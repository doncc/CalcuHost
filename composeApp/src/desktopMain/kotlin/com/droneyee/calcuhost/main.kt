package com.droneyee.calcuhost

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import calcuhost.composeapp.generated.resources.Res
import calcuhost.composeapp.generated.resources.allDrawableResources
import calcuhost.composeapp.generated.resources.calculate_256dp_5f6368
import org.jetbrains.compose.resources.getResourceUri
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

fun main() = application {



    Window(
        onCloseRequest = ::exitApplication,
        title = "无人机续航计算工具 v1.0.0-2025.3.27",
        state = WindowState(size = DpSize(1400.dp, 800.dp)),
        icon = painterResource(Res.drawable.calculate_256dp_5f6368)
    ) {
        App()
    }
}

