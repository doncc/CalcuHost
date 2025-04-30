package com.droneyee.calcuhost.database

internal class Database() {
    private var database: CalcuHostDatabase
    private var dbQuery: CalcuHostDatabaseQueries

    // 生成构造函数
    init {
        val databaseDriverFactory: DatabaseDriverFactory = getDatabaseDriverFactory()
        val driver = databaseDriverFactory.createDriver()
        //创建表
        CalcuHostDatabase.Schema.create(driver)
        database = CalcuHostDatabase.Companion(driver)
        dbQuery = database.calcuHostDatabaseQueries
    }

    /**
     * 获取所有数据库内的 Config 数据
     */
    internal fun getAllConfigs(): List<Config> {
        return dbQuery.selectAllConfigs().executeAsList()//::configs
    }

    /**
     * 添加新的配置数据及其对应的配置数据项，并返回操作是否成功的状态
     * @param configName 配置名称
     * @param thrusts 拉力数据数组
     * @param powers 功率数据数组
     * @return 操作是否成功
     */
    internal fun addConfigWithItemsAndFeedback(
        configName: String,
        thrusts: IntArray,
        powers: DoubleArray
    ): Boolean {
        return try {
            addConfigWithItems(configName, thrusts, powers)
            true
        } catch (e: Exception) {
            println(e.message.toString())
            false
        }
    }

    /**
     * 添加新的配置数据及其对应的配置数据项
     * @param configName 配置名称
     * @param thrusts 拉力数据数组
     * @param powers 功率数据数组
     */
    internal fun addConfigWithItems(
        configName: String,
        thrusts: IntArray,
        powers: DoubleArray
    ) {
        if (thrusts.size != powers.size) {
            throw IllegalArgumentException("拉力和功率数据数组长度必须相同")
        }
        database.transaction {
            // 查询 configName 对应的 id
            var configId =
                dbQuery.selectConfigByName(config_name = configName).executeAsOneOrNull()?.id

            var isExist = true
            //判断是否存在
            if (configId == null) {
                // 生成新的 Config 数据项并插入
                dbQuery.insertConfig(config_name = configName)
                configId = dbQuery.selectConfigByName(config_name = configName).executeAsOne().id
                isExist = false
            }

            if (isExist) {
                // 删除原来的 ConfigItem 数据
                dbQuery.deleteConfigItemsByConfigId(configId)
            }

            thrusts.forEachIndexed { index, thrust ->
                dbQuery.insertConfigItem(
                    config_id = configId,
                    power = powers[index].toDouble(),
                    thrust = thrust.toLong()
                )
            }
        }
    }

    /**
     * 通过输入的 configId 来获取查询数据库里的 ConfigItem
     * @param configId 配置ID
     * @return 查询到的 ConfigItem 列表
     */
    internal fun getConfigItemsByConfigId(configId: Long): List<ConfigItem> {
        return dbQuery.selectConfigItemsByConfigId(configId).executeAsList()
    }

    internal fun deleteConfigItemById(id: Long): Boolean {
        return try {
            dbQuery.deleteConfigItemById(id)
            return true
        } catch (e: Exception) {
            println(e.message.toString())
            return false
        }
    }

    internal fun deleteSelectedConfigInfo(configId: Long): Boolean {
        return try {
            database.transaction {
                dbQuery.deleteConfigItemsByConfigId(configId)
                dbQuery.deleteConfig(configId)
            }
            return true
        } catch (e: Exception) {
            println(e.message.toString())
            return false
        }
    }

}