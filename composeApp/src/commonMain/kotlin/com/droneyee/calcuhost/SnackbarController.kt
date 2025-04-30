package com.droneyee.calcuhost

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class SnackbarController {
    private var _message by mutableStateOf<String?>(null)
    val message: String? get() = _message

    fun showMessage(message: String) {
        _message = message
    }

    fun dismiss() {
        _message = null
    }
}

@Composable
fun SnackbarHost() {
    val controller = LocalSnackbarController.current
    val message = controller.message

    AnimatedVisibility(
        visible = message != null,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut()
    ) {
        message?.let {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(4.dp, RoundedCornerShape(4.dp))
                    .background(Color.DarkGray, RoundedCornerShape(4.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = it,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                // 自动关闭
                LaunchedEffect(Unit) {
                    delay(3000)
                    controller.dismiss()
                }
            }
        }
    }
}

// 在需要的地方提供实例（例如通过 CompositionLocal）
val LocalSnackbarController = staticCompositionLocalOf<SnackbarController> {
    error("No SnackbarController provided")
}