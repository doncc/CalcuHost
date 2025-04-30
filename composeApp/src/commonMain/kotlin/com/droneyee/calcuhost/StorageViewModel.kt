package com.droneyee.calcuhost

import com.droneyee.calcuhost.database.Config
import com.droneyee.calcuhost.database.ConfigItem
import com.droneyee.calcuhost.database.Database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StorageViewModel {
    private val platform = getPlatform()
    private val database = Database()

    private val _uiAddConfigState = MutableStateFlow<Boolean>(false)
    val addConfigWithFeedbackState: StateFlow<Boolean> get() = _uiAddConfigState

    private val _uiRemoveConfigState = MutableStateFlow<Boolean>(false)
    val removeConfigWithFeedbackState: StateFlow<Boolean> get() = _uiRemoveConfigState

    private val _configs = MutableStateFlow<List<Config>>(emptyList())
    val configs: StateFlow<List<Config>> get() = _configs

    private val _configItems = MutableStateFlow<List<ConfigItem>>(emptyList())
    val configItems: StateFlow<List<ConfigItem>> get() = _configItems


    fun greet(): String {
        return "Hello, ${platform.name}!"
    }

    fun addConfigWithFeedback(configName: String, thrusts: IntArray, powers: DoubleArray) {
        val isSuccessful =
            database.addConfigWithItemsAndFeedback(configName, thrusts, powers)
        _uiAddConfigState.value = isSuccessful
    }

    /**
     * 获取所有 Config 数据
     */
    fun getAllConfig() {
        val allConfigs = database.getAllConfigs()
        // 更新 _configs
        _configs.value = allConfigs
    }

    /**
     * 根据config_name获取所有 ConfigItem 数据
     */
    fun getAllConfigItemsWithConfigName(configName: String) {
        // 通过匹配 _configs 变量里的 configName 来查找对应的 configId
        val configId = _configs.value.find { it.config_name == configName }?.id ?: 0
        val allConfigsWithItems = database.getConfigItemsByConfigId(configId.toLong())

        // 对 allConfigsWithItems 按照 thrust 字段进行排序
        val sortedConfigItems = allConfigsWithItems.sortedBy { it.thrust }

        // 清空 _configItems 的值
        _configItems.value = emptyList()
        // 更新 _configItems
        _configItems.value = sortedConfigItems
    }

    fun removeConfigItemById(id: Long, itemTitleName: String) {
        val result = database.deleteConfigItemById(id)
        _uiRemoveConfigState.value = result
        if (result) {
            getAllConfigItemsWithConfigName(itemTitleName)
        }
    }

    /**
     * 根据选择的item项,删除所有和item相关的数据
     */
    fun removeConfigItemByItemName(selectedItems: List<String>) {
        for (id in selectedItems) {
            val configId = _configs.value.find { it.config_name == id }?.id ?: 0
            database.deleteSelectedConfigInfo(configId)
        }
        getAllConfig()
    }
}