package com.moky.timebattle.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private const val DefaultStrokeWidth = 1.25f

private fun PathBuilder.circle(cx: Float, cy: Float, r: Float) {
    moveTo(cx, cy - r)
    arcTo(r, r, 0f, true, true, cx, cy + r)
    arcTo(r, r, 0f, true, true, cx, cy - r)
    close()
}

fun clockIcon(color: Color = Color.White): ImageVector = ImageVector.Builder(
    name = "Clock",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(color),
        strokeLineWidth = DefaultStrokeWidth,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        circle(12f, 12f, 9f)
        moveTo(12f, 3f); lineTo(12f, 7f)
        moveTo(12f, 12f); lineTo(12f, 7f)
        moveTo(12f, 12f); lineTo(16f, 14f)
    }
}.build()

fun homeIcon(color: Color = Color.White): ImageVector = ImageVector.Builder(
    name = "Home",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(color),
        strokeLineWidth = DefaultStrokeWidth,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        circle(12f, 12f, 9f)
    }
    path(
        fill = SolidColor(color),
        stroke = SolidColor(Color.Transparent)
    ) {
        circle(12f, 12f, 2f)
    }
}.build()

fun checkIcon(color: Color = Color.White): ImageVector = ImageVector.Builder(
    name = "Check",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(color),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(20f, 6f); lineTo(9f, 17f); lineTo(4f, 12f)
    }
}.build()

fun taskIcon(color: Color = Color.White): ImageVector = ImageVector.Builder(
    name = "Task",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(color),
        strokeLineWidth = DefaultStrokeWidth,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(9f, 11f); lineTo(12f, 14f); lineTo(22f, 4f)
        moveTo(21f, 12f); verticalLineTo(19f)
        arcTo(2f, 2f, 0f, false, true, 19f, 21f); horizontalLineTo(5f)
        arcTo(2f, 2f, 0f, false, true, 3f, 19f); verticalLineTo(5f)
        arcTo(2f, 2f, 0f, false, true, 5f, 3f); horizontalLineToRelative(11f)
    }
}.build()

fun tradeIcon(color: Color = Color.White): ImageVector = ImageVector.Builder(
    name = "Trade",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(color),
        strokeLineWidth = DefaultStrokeWidth,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(17f, 1f); lineTo(21f, 5f); lineTo(17f, 9f)
        moveTo(3f, 11f); verticalLineTo(9f)
        arcTo(4f, 4f, 0f, false, true, 7f, 5f); horizontalLineToRelative(14f)
        moveTo(7f, 23f); lineTo(3f, 19f); lineTo(7f, 15f)
        moveTo(21f, 13f); verticalLineToRelative(2f)
        arcTo(4f, 4f, 0f, false, true, 17f, 19f); horizontalLineTo(3f)
    }
}.build()

fun groupIcon(color: Color = Color.White): ImageVector = ImageVector.Builder(
    name = "Group",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(color),
        strokeLineWidth = DefaultStrokeWidth,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(17f, 21f); verticalLineToRelative(-2f)
        arcTo(4f, 4f, 0f, false, false, 13f, 15f); horizontalLineTo(5f)
        arcTo(4f, 4f, 0f, false, false, 1f, 19f); verticalLineToRelative(2f)
        moveTo(9f, 7f)
        arcTo(4f, 4f, 0f, true, true, 9f, 15f); arcTo(4f, 4f, 0f, true, true, 9f, 7f)
        moveTo(23f, 21f); verticalLineToRelative(-2f)
        arcTo(4f, 4f, 0f, false, false, 20f, 15.13f)
        moveTo(16f, 3.13f)
        arcTo(4f, 4f, 0f, false, true, 16f, 10.88f)
    }
}.build()

fun bellIcon(color: Color = Color.White): ImageVector = ImageVector.Builder(
    name = "Bell",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(color),
        strokeLineWidth = DefaultStrokeWidth,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(18f, 8f)
        arcTo(6f, 6f, 0f, false, false, 6f, 8f)
        curveToRelative(0f, 7f, -3f, 9f, -3f, 9f); horizontalLineToRelative(18f)
        reflectiveCurveToRelative(-3f, -2f, -3f, -9f)
        moveTo(13.73f, 21f)
        arcTo(2f, 2f, 0f, false, true, 10.27f, 21f)
    }
}.build()

fun userIcon(color: Color = Color.White): ImageVector = ImageVector.Builder(
    name = "User",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(color),
        strokeLineWidth = DefaultStrokeWidth,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(20f, 21f); verticalLineToRelative(-2f)
        arcTo(4f, 4f, 0f, false, false, 16f, 15f); horizontalLineTo(8f)
        arcTo(4f, 4f, 0f, false, false, 4f, 19f); verticalLineToRelative(2f)
        moveTo(12f, 7f)
        arcTo(4f, 4f, 0f, true, true, 12f, 15f); arcTo(4f, 4f, 0f, true, true, 12f, 7f)
    }
}.build()

fun heartIcon(color: Color = Color.White): ImageVector = ImageVector.Builder(
    name = "Heart",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(color),
        strokeLineWidth = DefaultStrokeWidth,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(20.84f, 4.61f)
        arcTo(5.5f, 5.5f, 0f, false, false, 13.06f, 4.61f); lineTo(12f, 5.67f)
        lineTo(10.94f, 4.61f)
        arcTo(5.5f, 5.5f, 0f, false, false, 3.16f, 12.39f); lineTo(4.22f, 13.45f)
        lineTo(12f, 21.23f); lineTo(19.78f, 13.45f); lineTo(20.84f, 12.39f)
        arcTo(5.5f, 5.5f, 0f, false, false, 20.84f, 4.61f)
    }
}.build()

fun chartIcon(color: Color = Color.White): ImageVector = ImageVector.Builder(
    name = "Chart",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(color),
        strokeLineWidth = DefaultStrokeWidth,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(12f, 20f); verticalLineTo(10f)
        moveTo(18f, 20f); verticalLineTo(4f)
        moveTo(6f, 20f); verticalLineTo(16f)
    }
}.build()

fun backIcon(color: Color = Color.White): ImageVector = ImageVector.Builder(
    name = "Back",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(color),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(15f, 18f); lineTo(9f, 12f); lineTo(15f, 6f)
    }
}.build()

fun infoIcon(color: Color = Color.White): ImageVector = ImageVector.Builder(
    name = "Info",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(color),
        strokeLineWidth = 1.5f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        circle(12f, 12f, 10f)
        moveTo(12f, 8f); verticalLineTo(12f)
        moveTo(12f, 16f); lineTo(12.01f, 16f)
    }
}.build()

fun syncIcon(color: Color = Color.White): ImageVector = ImageVector.Builder(
    name = "Sync",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(color),
        strokeLineWidth = DefaultStrokeWidth,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(21.5f, 5f); verticalLineTo(9f); horizontalLineTo(17.5f)
        moveTo(2.5f, 19f); verticalLineTo(15f); horizontalLineTo(6.5f)
        moveTo(4.24f, 9.75f)
        arcTo(7f, 7f, 0f, false, true, 15.66f, 5.4f); lineTo(18.5f, 8.5f)
        moveTo(5.5f, 15.5f); lineTo(8.34f, 18.6f)
        arcTo(7f, 7f, 0f, false, false, 19.76f, 14.25f)
    }
}.build()

fun logoIcon(size: Float = 96f, color: Color = Color.White): ImageVector = ImageVector.Builder(
    name = "Logo",
    defaultWidth = size.dp,
    defaultHeight = size.dp,
    viewportWidth = size,
    viewportHeight = size
).apply {
    val cx = size / 2f
    val cy = size / 2f
    val r1 = size * 0.4375f
    val r2 = size * 0.3125f
    val strokeMain = size * 0.0156f
    val strokeThin = size * 0.0078f
    val dotR = size * 0.026f
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(color),
        strokeLineWidth = strokeMain,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        circle(cx, cy, r1)
    }
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(color.copy(alpha = 0.35f)),
        strokeLineWidth = strokeThin,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        circle(cx, cy, r2)
    }
    path(
        fill = SolidColor(Color.Transparent),
        stroke = SolidColor(color),
        strokeLineWidth = strokeMain * 0.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(cx, cy - r1 + strokeMain); lineTo(cx, cy - r1 + strokeMain + size * 0.125f)
        moveTo(cx, cy); lineTo(cx, cy - size * 0.125f)
        moveTo(cx, cy); lineTo(cx + size * 0.125f, cy + size * 0.0625f)
    }
    path(
        fill = SolidColor(color),
        stroke = SolidColor(Color.Transparent)
    ) {
        circle(cx, cy, dotR)
    }
}.build()
