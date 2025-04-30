package com.droneyee.calcuhost.chartview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun LineChartDsV3(
    data: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.Blue,
    pointColor: Color = Color.Red,
    axisColor: Color = Color.Black,
    xAxisRange: Pair<Float, Float> = 0f to data.size.toFloat(),
    yAxisRange: Pair<Float, Float> = 0f to data.size.toFloat(),
    xAxisLabel: String = "X Axis",
    yAxisLabel: String = "Y Axis",
    title: String = "Line Chart",
    padding: Float = 50f,
    animationDuration: Int = 1000
) {
    var animatedData by remember { mutableStateOf(data.map { 0f }) }
    var animationProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(data) {
        animatedData = data.map { 0f }
        animationProgress = 0f
        for (i in 0 until animationDuration step 16) {
            animationProgress = i.toFloat() / animationDuration
            animatedData = data.map { it * animationProgress }
            delay(16)
        }
        animatedData = data
    }

    Box(modifier = modifier.background(Color.White).padding(16.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width - padding * 2
            val canvasHeight = size.height - padding * 2

            // Draw X and Y axis
            drawLine(
                axisColor,
                Offset(padding, size.height - padding),
                Offset(size.width - padding, size.height - padding)
            )
            drawLine(axisColor, Offset(padding, size.height - padding), Offset(padding, padding))

            // Draw data points and lines
            val xStep = canvasWidth / (xAxisRange.second - xAxisRange.first)
            val yStep = canvasHeight / (yAxisRange.second - yAxisRange.first)
            val path = Path()
            animatedData.forEachIndexed { index, value ->
                val x = padding + index * xStep
                val y = size.height - padding - value * yStep
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
                drawCircle(pointColor, 5f, Offset(x, y))
            }
            drawPath(path, lineColor, style = Stroke(2f))
        }

        // Draw X axis label
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = (padding / 2).dp)
        ) {
            Text(
                text = xAxisLabel,
                style = TextStyle(
                    color = axisColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Draw Y axis label
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .padding(start = (padding / 2).dp)
        ) {
            Text(
                text = yAxisLabel,
                style = TextStyle(
                    color = axisColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier
                    .align(Alignment.Center)
                    .rotate(-90f)
            )
        }

        // Draw title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = (padding / 2).dp)
        ) {
            Text(
                text = title,
                style = TextStyle(
                    color = axisColor,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

