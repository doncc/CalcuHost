package com.droneyee.calcuhost

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.droneyee.calcuhost.database.ConfigItem
import com.mohamedrejeb.calf.ui.web.rememberWebViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat


val storageViewModel = StorageViewModel()
val appViewModel: AppViewModel = AppViewModel()

@Composable
fun App() {

    val (items, setItems) = remember { mutableStateOf(emptyList<String>()) }
    //电池总能量
    val (totalBatteryEnergy, setTotalBatteryEnergy) = remember { mutableDoubleStateOf(0.0) }
    //起飞所需单轴提供的升力
    val (liftPerMotorAtTakeoff, setLiftPerMotorAtTakeoff) = remember { mutableStateOf("") }
    //续航时间
    val (enduranceTime, setEnduranceTime) = remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(Unit) {
        appViewModel.totalBatteryEnergy.collect { wh ->
            setTotalBatteryEnergy(wh)
        }
    }

    LaunchedEffect(Unit) {
        appViewModel.liftPerMotorAtTakeoff.collect { kg ->
            setLiftPerMotorAtTakeoff(kg)
        }
    }
    LaunchedEffect(Unit) {
        appViewModel.enduranceTime.collect { time ->
            setEnduranceTime(time)
        }
    }
    LaunchedEffect(Unit) {
        storageViewModel.configs.collect { configs ->
            // 处理 configs 的变化
            // 例如，更新 items 或其他逻辑
            setItems(configs.map { it.config_name })
        }
    }

    MaterialTheme {
        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()
        val (showInfoDialog, setShowInfoDialog) = remember { mutableStateOf(false) } // 新增对话框状态

//        // 添加 LaunchedEffect 来在启动时打开抽屉
//        LaunchedEffect(Unit) {
//            scope.launch {
//                scaffoldState.drawerState.open()
//            }
//        }

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = { Text("无人机续航计算工具") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                scaffoldState.drawerState.open()
                            }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Toggle Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            setShowInfoDialog(true)
                        }) {
                            Icon(Icons.Default.Info, contentDescription = "Info")
                        }
                    })
            }, drawerContent = {
                ui_DrawerContent(scaffoldState, scope)
                //关闭抽屉
                //scaffoldState.drawerState.close()
            }) { innerPadding ->
            ui_MainContent(items, totalBatteryEnergy, liftPerMotorAtTakeoff, enduranceTime)

            ui_SelfInfoDialog(showInfoDialog, setShowInfoDialog)
        }

    }
}

@Composable
fun ui_MainContent(
    items: List<String>,
    totalBatteryEnergy: Double,
    liftPerMotorAtTakeoff: String,
    enduranceTime: Double
) {

    val state = rememberWebViewState(url = "https://www.baidu.com")
    LaunchedEffect(Unit) {
        // Enable JavaScript
        state.settings.javaScriptEnabled = true
        println("hi!")
    }

    Row(Modifier.fillMaxWidth()) {

        //编辑区
        Column {
            //输入区
            Row {
                ui_BatteryInfo(
                    modifier = Modifier.padding(9.dp)
                        .border(BorderStroke(1.dp, Color.Black))
                        .width(intrinsicSize = IntrinsicSize.Min)
                        .height(250.dp)
                )

                Column(modifier = Modifier.width(intrinsicSize = IntrinsicSize.Min)) {

                    ui_UAVInfo(
                        modifier = Modifier.padding(9.dp)
                            .border(BorderStroke(1.dp, Color.Black)),
                    )
                    ui_LoadInfo(
                        modifier = Modifier.padding(9.dp)
                            .border(BorderStroke(1.dp, Color.Black)),
                    )
                }
            }

            //结果区
            Row {
                //首页，二维图
//                val configItems by storageViewModel.configItems.collectAsState()
//                ui_GraphInfo(configItems)

                //计算结果
                ui_CalcuResultInfo(
                    modifier = Modifier.padding(9.dp)
                        .width(intrinsicSize = IntrinsicSize.Min)
                        .border(BorderStroke(1.dp, Color.Black))
                        .height(250.dp),
                    totalBatteryEnergy = totalBatteryEnergy,
                    liftPerMotorAtTakeoff = liftPerMotorAtTakeoff,
                    enduranceTime = enduranceTime
                )
            }

//            WebView(
//                state = state,
//                modifier = Modifier.fillMaxWidth().fillMaxHeight())

        }

        //动力套配置区
        Column {
            //使内容居中
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //下拉菜单
                ui_DropdownMenu(items, { onCheckChangeText ->
                    storageViewModel.getAllConfigItemsWithConfigName(
                        onCheckChangeText
                    )
                })
            }

            //首页，右侧预览图表
            val configItems by storageViewModel.configItems.collectAsState()
            ui_TableInfo(
                configItems,
                liftPerMotorAtTakeoff
            )
        }
    }

}

@Composable
fun ui_SelfInfoDialog(showInfoDialog: Boolean, setShowInfoDialog: (Boolean) -> Unit) {

    // 个人信息对话框
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { setShowInfoDialog(false) },
            title = { Text("无人机续航计算工具") },
            text = {
                Column {
                    Text(
                        "Power by Darkbug\n" +
                                "Github: https://github.com/doncc\n" +
                                "\n" +
                                "v1.0.0 2025.3.27\n" +
                                "\n" +
                                "Release Note:\n" +
                                "1.可根据输入数值，动态计算续航时间\n" +
                                "2.可自定义动力套的数值匹配表"
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { setShowInfoDialog(false) }) {
                    Text("确定")
                }
            }
        )
    }
}

@Composable
fun ui_DropdownMenu(items: List<String>, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(if (items.isEmpty()) "请选择" else items[0]) }

    Text("选择动力套件：")

    Box(modifier = Modifier.padding(16.dp)) {
        Box(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .background(Color.LightGray, RoundedCornerShape(4.dp))
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selectedOptionText)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown, // 确保你有一个箭头图标资源
                    contentDescription = "Arrow",
                    modifier = Modifier.size(16.dp).rotate(if (expanded) 180f else 0f)
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(200.dp)
                .background(Color.White, RoundedCornerShape(4.dp)),
            offset = DpOffset(x = 0.dp, y = 0.dp)
        ) {
            items.forEach { label ->
                DropdownMenuItem(onClick = {
                    selectedOptionText = label
                    expanded = false
                    onOptionSelected(label)
                }) {
                    Text(text = label)
                }
            }
        }
    }
}

@Composable
fun ui_DrawerContent(scaffoldState: ScaffoldState, scope: CoroutineScope) {

    val BASE_LIST_OF_ITEMS = listOf("拉力(kg)", "功率(W)")
    val (items, setItems) = remember { mutableStateOf(emptyList<String>()) }
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    val (newItemText, setNewItemText) = remember { mutableStateOf("") }
    val (selectedItem, setSelectedItem) = remember { mutableStateOf("") }
    val (tableData, setTableData) = remember {
        mutableStateOf(
            listOf(
                BASE_LIST_OF_ITEMS
            )
        )
    }
    val (showDeleteButtons, setShowDeleteButtons) = remember { mutableStateOf(false) } // 新增状态变量
    var itemTitleName by remember { mutableStateOf("") }//右侧detail详情页title名称
    var configIdMap by remember { mutableStateOf(emptyMap<Int, Long>()) }

    // 创建 Snackbar 状态管理
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    // 添加 LaunchedEffect 来监听 configs 和 configItems 的变化
    LaunchedEffect(Unit) {
        storageViewModel.configs.collect { configs ->
            // 处理 configs 的变化
            // 例如，更新 items 或其他逻辑
            setItems(configs.map { it.config_name })
        }
    }
    LaunchedEffect(Unit) {
        storageViewModel.configItems.collect { configItems ->

            // 假设 configItems 是一个 List<ConfigItem>，并且每个 ConfigItem 有一个 thrust 和 power 属性
            val fetchedData = configItems.map { listOf(it.thrust.toString(), it.power.toString()) }
            // 合并 titleData 和 fetchedData
            val titleData = listOf(BASE_LIST_OF_ITEMS)
            setTableData(titleData + fetchedData)

            // 增加映射表
            configIdMap =
                configItems.mapIndexed { index, id -> index + 1 to id.id }
                    .toMap()
        }
    }

    // 在 ui_DrawerContent 函数中添加状态变量
    var showCheckboxes by remember { mutableStateOf(false) }
    //使用 Map 来存储每个 item 的选中状态
    val (itemCheckedStates, setItemCheckedStates) = remember { mutableStateOf(mapOf<String, Boolean>()) }

    //View
    Row(modifier = Modifier.fillMaxWidth()) {

        Column(
            modifier = Modifier.width(IntrinsicSize.Max).fillMaxHeight()
                .background(Color.LightGray).padding(3.dp)
        ) {
            //抽屉左侧布局
            items.forEach { item ->
                Row(modifier = Modifier.width(IntrinsicSize.Max)) {
                    if (showCheckboxes) {
                        //添加一个多选框
                        Checkbox(
                            checked = itemCheckedStates[item] ?: false, // 默认不选中
                            onCheckedChange = { isChecked ->
                                setItemCheckedStates(itemCheckedStates + (item to isChecked)) // 更新 Map 中当前 item 的选中状态

                            }
                        )
                    }

                    TextButton(
                        modifier = Modifier.width(230.dp), onClick = {
                            setSelectedItem(item)
                            //标题名称
                            itemTitleName = item

                            storageViewModel.getAllConfigItemsWithConfigName(item);
                        }) {
                        Text(item)
                    }
                }
            }

            //添加按钮
            if (!showCheckboxes) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !showCheckboxes,
                    onClick = { setShowDialog(true) }) {
                    Text("添加菜单项")
                }
            }

            //删除按钮
            if (showCheckboxes) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        // 获取所有选中的 item 的 key
                        val selectedItems = itemCheckedStates.filter { it.value }.keys.toList()
                        storageViewModel.removeConfigItemByItemName(selectedItems)
                    }) {
                    Text("删除")
                }
            }

            //删除菜单项按钮
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    //当点击删除菜单按钮时，添加菜单按钮置灰不可点击，同时显示item列表的每个多选框，可组合选择多个item，并获取到对应的id
                    showCheckboxes = !showCheckboxes
                }) {
                Text(if (!showCheckboxes) "删除菜单项" else "取消删除选择")
            }

        }

        Column(modifier = Modifier.padding(horizontal = 9.dp)) {
            //抽屉右侧布局
            if (selectedItem.isNotEmpty()) {
                Text(
                    itemTitleName + "的配置",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // 展示表格数据区域
                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    itemsIndexed(tableData) { index, row ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            row.forEachIndexed { colIndex, cell ->
                                if (index == 0) {
                                    Text(cell, modifier = Modifier.weight(1f))
                                } else {
                                    TextField(
                                        modifier = Modifier.weight(1f),
                                        value = cell,
                                        onValueChange = { newValue ->
                                            // 使用正则表达式验证输入是否为数字或带小数点的数字
                                            if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                                val updatedData = tableData.toMutableList()
                                                updatedData[index] =
                                                    updatedData[index].toMutableList().apply {
                                                        this[colIndex] = newValue
                                                    }
                                                setTableData(updatedData)
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(1.dp).background(Color.Black))
                                }
                            }
                            if (index != 0 && showDeleteButtons) { // 修改条件判断
                                IconButton(onClick = {

                                    val id: Long? = configIdMap.get(index)
                                    if (id == null) {
                                        snackbarMessage = "执行错误，停止执行"
                                        showSnackbar = true
                                        return@IconButton
                                    }

                                    storageViewModel.removeConfigItemById(
                                        id,
                                        itemTitleName
                                    );

                                    val isSuccessful =
                                        storageViewModel.removeConfigWithFeedbackState.value
                                    snackbarMessage =
                                        if (isSuccessful) "删除数据成功！" else "删除数据失败！"
                                    showSnackbar = true

                                    if (isSuccessful) {
                                        val updatedData = tableData.toMutableList().apply {
                                            removeAt(index)
                                        }
                                        setTableData(updatedData)
                                    }

                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Row")
                                }
                            }
                        }
                    }
                }

                //抽屉右侧表格底部功能按钮区域
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f), onClick = {
                            val updatedData =
                                tableData.toMutableList().apply {
                                    add(listOf("", ""))
                                }
                            setTableData(updatedData)

                        }, enabled = !showDeleteButtons
                    ) {
                        Text("增加数据")
                    }
                    Button(
                        modifier = Modifier.weight(1f), onClick = {
                            setShowDeleteButtons(!showDeleteButtons) // 切换删除按钮显示状态
                        }) {
                        Text(if (showDeleteButtons) "完成删除" else "删除数据")
                    }
                    Button(
                        modifier = Modifier.weight(1f), onClick = {
                            // 保存表格按钮

                            val configName = itemTitleName
                            val thrusts = tableData.drop(1).map { it[0].toIntOrNull() ?: 0 }
                                .toIntArray()
                            val powers =
                                tableData.drop(1).map { it[1].toDoubleOrNull() ?: 0.0 }
                                    .toDoubleArray()
                            storageViewModel.addConfigWithFeedback(
                                configName,
                                thrusts,
                                powers
                            )

                            val isSuccessful =
                                storageViewModel.addConfigWithFeedbackState.value
                            snackbarMessage = if (isSuccessful) "保存数据成功！" else "保存数据失败！"
                            showSnackbar = true

                        }, enabled = !showDeleteButtons
                    ) {
                        Text("保存数据")
                    }
                }
                //Snackbar提示结果
                if (showSnackbar) {
                    Snackbar(
                        modifier = Modifier.padding(8.dp),
                        action = {
                            TextButton(onClick = { showSnackbar = false }) {
                                Text("Dismiss")
                            }
                        }
                    ) {
                        Text(snackbarMessage)
                    }
                }
                // 使用 LaunchedEffect 在 Snackbar 显示后自动消失
                LaunchedEffect(key1 = showSnackbar) {
                    delay(5000) // 5秒后自动消失
                    showSnackbar = false
                }

            }
        }
    }

    //对话框展示部分
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { setShowDialog(false) },
            title = { Text("添加新菜单项") },
            text = {
                TextField(
                    value = newItemText, onValueChange = setNewItemText
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newItemText.isNotBlank()) {
                        setItems(items + newItemText)
                        setNewItemText("")
                        setShowDialog(false)
                    }
                }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { setShowDialog(false) }) {
                    Text("取消")
                }
            })
    }

    //业务
    storageViewModel.getAllConfig()
}

@Composable
fun ui_BatteryInfo(modifier: Modifier) {
    /*电池信息*/

    Column(
        //电池方面信息
        modifier = modifier.padding(9.dp)
    ) {
        Text("电池方面信息")
        Row {
            ui_InputUnit(
                title = "型号",
                unitName = "s",
                inputType = InputType.Int,
                onValueChange = {
                    appViewModel.setBatterySpecPerDrone(it.toIntOrNull() ?: 0)
                }
            )

            ui_InputUnit(
                title = "单块电池规格",
                unitName = "mAh",
                inputType = InputType.Int,
                onValueChange = {
                    appViewModel.setBatteryCapacitymAh(it.toIntOrNull() ?: 0)
                }
            )

            ui_InputUnit(
                title = "电池数量",
                unitName = "块",
                inputType = InputType.Int,
                onValueChange = {
                    appViewModel.setBatteryCount(it.toIntOrNull() ?: 0)
                }
            )
        }
        Row {
            ui_InputUnit(
                title = "单块电池平均电压",
                unitName = "V",
                inputType = InputType.Double,
                onValueChange = {
                    appViewModel.setAverageBatteryVoltage(it.toDoubleOrNull() ?: 0.0)
                }
            )

            ui_InputUnit(
                title = "电池能量转换效率",
                unitName = "%(范围0~100%)",
                inputType = InputType.Int,
                onValueChange = {
                    appViewModel.setBatteryEfficiency(it.toIntOrNull() ?: 0)
                }
            )
        }
    }


}

@Composable
fun ui_UAVInfo(
    modifier: Modifier,
) {
//无人机方面信息
    Column(
        modifier = modifier.padding(9.dp)
    ) {
        Text("无人机方面信息")
        Row {
            ui_InputUnit(
                title = "空机+电池重量",
                unitName = "kg",
                inputType = InputType.Double,
                onValueChange = {
                    appViewModel.setEmptyWeightWithBattery(it.toDoubleOrNull() ?: 0.0)
                }
            )

            ui_InputUnit(
                title = "无人机轴数",
                unitName = "轴",
                inputType = InputType.Int,
                onValueChange = {
                    appViewModel.setDroneMotorCount(it.toIntOrNull() ?: 0)
                }
            )
            ui_InputUnit(
                title = "无人机机身功耗",
                unitName = "W",
                inputType = InputType.Double,
                onValueChange = {
                    appViewModel.setDroneBodyPowerConsumption(it.toDoubleOrNull() ?: 0.0)
                }
            )
        }
    }

}

@Composable
fun ui_LoadInfo(modifier: Modifier) {
    //载荷方面信息
    Column(
        modifier = modifier.padding(9.dp)
    ) {
        Text("载荷方面信息")
        Row {
            ui_InputUnit(
                title = "载荷重量",
                unitName = "kg",
                inputType = InputType.Double,
                onValueChange = {
                    appViewModel.setPayloadWeight(it.toDoubleOrNull() ?: 0.0)
                }
            )

            ui_InputUnit(
                title = "设计挂载非标件重量",
                unitName = "kg",
                inputType = InputType.Double,
                onValueChange = {
                    appViewModel.setDesignNonStandardMountWeight(it.toDoubleOrNull() ?: 0.0)
                }
            )
            ui_InputUnit(
                title = "挂载载荷功耗",
                unitName = "W",
                inputType = InputType.Double,
                onValueChange = {
                    appViewModel.setMountedPayloadPowerConsumption(it.toDoubleOrNull() ?: 0.0)
                }
            )
        }
    }
}

@Composable
fun ui_CalcuResultInfo(
    modifier: Modifier,
    totalBatteryEnergy: Double,
    liftPerMotorAtTakeoff: String,
    enduranceTime: Double
) {


    Column(
        //计算结果信息
        modifier = modifier.padding(12.dp)
    ) {
        Text("计算结果：")

        Row {
            Text("电池总能量：")
            Text(text = totalBatteryEnergy.toString())
            Text("Wh")
        }

        Row {
            Text("起飞所需单轴提供的升力：")
            Text(
                if (liftPerMotorAtTakeoff.isEmpty() || liftPerMotorAtTakeoff == "0.0") "0.0" else decimalFormat.format(
                    liftPerMotorAtTakeoff.toDouble()
                ).toString()
            )
            Text("kg")
        }

        Row {
            Text("续航时间：")
            Text(
                if (enduranceTime == 0.0 ||
                    enduranceTime == Double.POSITIVE_INFINITY ||
                    enduranceTime.isNaN()
                ) "0.0" else decimalFormat.format(enduranceTime)
            )
            Text("min")
        }
    }
}

@Composable
fun ui_InputUnit(
    text: String = "",
    title: String,
    unitName: String,
    enabled: Boolean = true,
    inputType: InputType,
    onValueChange: ((String) -> Unit)? = null
) {

    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(9.dp)
    ) {
        Text(title)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = if (text.isNotEmpty()) text else inputText,
                modifier = Modifier.width(80.dp),
                singleLine = true,
                enabled = enabled,
                keyboardOptions = KeyboardOptions(
                    keyboardType = when (inputType) {
                        InputType.Int -> KeyboardType.Number
                        InputType.Double -> KeyboardType.Decimal
                    }
                ),
                onValueChange = { value ->
                    when (inputType) {
                        InputType.Int -> {
                            val filteredText = value.filter { it.isDigit() }
                            inputText = filteredText
                            onValueChange?.invoke(filteredText)
                        }

                        InputType.Double -> {
                            // 处理以小数点开头的情况，自动补零
                            var processedText = value
                            if (processedText.startsWith('.')) {
                                processedText = "0$processedText"
                            }
                            // 允许数字和小数点，且最多一个小数点
                            if (processedText.all { it.isDigit() || it == '.' } &&
                                processedText.count { it == '.' } <= 1) {
                                inputText = processedText
                                onValueChange?.invoke(processedText)
                            }
                        }
                    }

                }
            )
            Text(unitName)
        }
    }
}

// 定义输入类型枚举
enum class InputType {
    Int,
    Double
}

val decimalFormat = DecimalFormat("#.0")

@Composable
fun ui_GraphInfo(configItems: List<ConfigItem>) {
    Column(
        modifier = Modifier.padding(9.dp)
            .border(BorderStroke(1.dp, Color.Black))
            .width(350.dp)
            .height(250.dp)
    ) {
        Text("拉力和功率关系图", modifier = Modifier.padding(8.dp))
        
        // 准备数据
        val thrusts = configItems.map { it.thrust.toFloat() }
        val powers = configItems.map { it.power.toFloat() }
        
        // 计算最大值用于Y轴刻度
        val maxPower = powers.maxOrNull() ?: 0f
        val maxThrust = thrusts.maxOrNull() ?: 0f
        
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Y轴（功率）
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("功率(W)")
                Text("0")
            }
            
            // X轴（拉力）
            Row(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0")
                Text("拉力(g)")
                Text(maxThrust.toString())
            }
            
            // 绘制折线
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                
                // 绘制折线
                val path = Path()
                configItems.forEachIndexed { index, item ->
                    val x = (item.thrust / maxThrust).toFloat() * width
                    val y = height - (item.power / maxPower).toFloat() * height
                    
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                
                // 设置线条样式
                drawPath(
                    path = path,
                    color = Color.Blue,
                    style = Stroke(width = 2f)
                )
                
                // 绘制数据点
                configItems.forEach { item ->
                    val x = (item.thrust / maxThrust).toFloat() * width
                    val y = height - (item.power / maxPower).toFloat() * height
                    drawCircle(
                        color = Color.Red,
                        radius = 4f,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}

@Composable
fun ui_TableInfo(
    configItems: List<ConfigItem>,
    liftPerMotorAtTakeoff: String
) {

    // 将 liftPerMotorAtTakeoff 从 kg 转换为 g
    val liftPerMotorAtTakeoffInGrams = liftPerMotorAtTakeoff.toDoubleOrNull()?.times(1000) ?: 0.0

    // 找到最接近的 thrust 值
    var closestThrust: ConfigItem? = null
    var smallestDifference = Double.MAX_VALUE

    for (configItem in configItems) {
        val difference = Math.abs(configItem.thrust - liftPerMotorAtTakeoffInGrams)
        if (difference < smallestDifference) {
            smallestDifference = difference
            closestThrust = configItem
        }
    }

    val (nearThrust, setNearThrust) = remember { mutableLongStateOf(0) }

    // 记录最接近的值
    closestThrust?.let {
        // 在这里可以记录或显示最接近的 thrust 值
        println("最接近的 thrust 值: ${it.thrust}g")
        setNearThrust(it.thrust)
    }

    LazyColumn(
        modifier = Modifier.padding(9.dp).border(BorderStroke(1.dp, Color.Black))
    ) {
        // 表头
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(4.dp)
            ) {
                Text("拉力(g)", modifier = Modifier.weight(1f))
                Text("功率(W)", modifier = Modifier.weight(1f))
            }
        }


        // 表格内容
        itemsIndexed(configItems) { index, configItem ->
            val isMatched = configItem.thrust == nearThrust
            val backgroundColor = if (isMatched) Color.Green else Color.Transparent

            if (isMatched) {
                val getPower = configItem.power.toDouble()
                appViewModel.setPower(getPower)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .background(backgroundColor)
            ) {
                Text(
                    configItem.thrust.toString(),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    configItem.power.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

