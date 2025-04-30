package com.droneyee.calcuhost

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.jetbrains.compose.ui.tooling.preview.Preview

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "CalcuHost",
        state = WindowState(size = DpSize(1100.dp, 800.dp))
    ) {
        App()
    }
}


@Composable
@Preview
fun ui_ui_TableInfoPreview() {
    ui_TableInfo()
}
