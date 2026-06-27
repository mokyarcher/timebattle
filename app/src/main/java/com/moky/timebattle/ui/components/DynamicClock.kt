package com.moky.timebattle.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import com.moky.timebattle.ui.theme.LifeRed
import kotlin.math.cos
import kotlin.math.sin

private const val TWO_PI = 2 * Math.PI.toFloat()

@Composable
fun DynamicClock(
    modifier: Modifier = Modifier,
    primaryColor: Color = LifeRed,
    secondaryAlpha: Float = 0.45f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "clock")
    val secondRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing)
        ),
        label = "secondHand"
    )
    val minuteRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3600000, easing = LinearEasing)
        ),
        label = "minuteHand"
    )

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val rOuter = size.width * 0.38f
        val rInner = size.width * 0.355f
        val strokeMain = size.width * 0.006f
        val strokeThin = size.width * 0.004f
        val dotR = size.width * 0.015f
        val centerDashR = size.width * 0.03f

        // Outer ring (main)
        drawCircle(
            color = primaryColor,
            radius = rOuter,
            center = Offset(cx, cy),
            style = Stroke(width = strokeMain * 1.6f)
        )

        // Inner ring (secondary, dashed)
        drawDashedCircle(
            cx = cx,
            cy = cy,
            radius = rInner,
            dashLength = size.width * 0.01f,
            gapLength = size.width * 0.045f,
            color = primaryColor.copy(alpha = secondaryAlpha),
            strokeWidth = strokeThin
        )

        // Main ticks: top, bottom, left, right
        val tickMainStart = rOuter - size.width * 0.04f
        val tickMainEnd = rOuter - size.width * 0.01f
        listOf(0f, 90f, 180f, 270f).forEach { angleDeg ->
            val angle = Math.toRadians((angleDeg - 90).toDouble()).toFloat()
            val cosA = cos(angle)
            val sinA = sin(angle)
            drawLine(
                color = primaryColor,
                start = Offset(cx + cosA * tickMainStart, cy + sinA * tickMainStart),
                end = Offset(cx + cosA * tickMainEnd, cy + sinA * tickMainEnd),
                strokeWidth = strokeMain * 1.6f,
                cap = StrokeCap.Round
            )
        }

        // Secondary ticks: diagonals
        val tickSecStart = rOuter - size.width * 0.035f
        val tickSecEnd = rOuter - size.width * 0.015f
        listOf(30f, 60f, 120f, 150f, 210f, 240f, 300f, 330f).forEach { angleDeg ->
            val angle = Math.toRadians((angleDeg - 90).toDouble()).toFloat()
            val cosA = cos(angle)
            val sinA = sin(angle)
            drawLine(
                color = primaryColor.copy(alpha = secondaryAlpha),
                start = Offset(cx + cosA * tickSecStart, cy + sinA * tickSecStart),
                end = Offset(cx + cosA * tickSecEnd, cy + sinA * tickSecEnd),
                strokeWidth = strokeThin,
                cap = StrokeCap.Round
            )
        }

        // Minute hand
        withTransform({ rotate(secondRotation, Offset(cx, cy)) }) {
            drawLine(
                color = primaryColor,
                start = Offset(cx, cy + size.width * 0.04f),
                end = Offset(cx, cy - rOuter + size.width * 0.085f),
                strokeWidth = strokeMain * 1.8f,
                cap = StrokeCap.Round
            )
        }

        // Second hand (dashed upper part, solid lower part)
        withTransform({ rotate(minuteRotation, Offset(cx, cy)) }) {
            drawLine(
                color = primaryColor.copy(alpha = secondaryAlpha),
                start = Offset(cx, cy - size.width * 0.085f),
                end = Offset(cx, cy - rOuter + size.width * 0.055f),
                strokeWidth = strokeThin,
                cap = StrokeCap.Round
            )
            drawLine(
                color = primaryColor,
                start = Offset(cx, cy),
                end = Offset(cx, cy + size.width * 0.06f),
                strokeWidth = strokeThin,
                cap = StrokeCap.Round
            )
        }

        // Center dot
        drawCircle(
            color = primaryColor,
            radius = dotR,
            center = Offset(cx, cy)
        )

        // Center dashed ring
        drawDashedCircle(
            cx = cx,
            cy = cy,
            radius = centerDashR,
            dashLength = size.width * 0.0075f,
            gapLength = size.width * 0.015f,
            color = primaryColor.copy(alpha = 0.7f),
            strokeWidth = strokeThin
        )
    }
}

private fun DrawScope.drawDashedCircle(
    cx: Float,
    cy: Float,
    radius: Float,
    dashLength: Float,
    gapLength: Float,
    color: Color,
    strokeWidth: Float
) {
    val circumference = TWO_PI * radius
    val segmentLength = dashLength + gapLength
    val segments = (circumference / segmentLength).toInt()
    val actualDash = (dashLength / segmentLength) * (360f / segments)

    repeat(segments) { index ->
        val startAngle = -90f + index * (360f / segments)
        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = actualDash,
            useCenter = false,
            topLeft = Offset(cx - radius, cy - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth)
        )
    }
}
