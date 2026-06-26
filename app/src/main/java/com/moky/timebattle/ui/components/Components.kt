package com.moky.timebattle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moky.timebattle.ui.components.icons.checkIcon
import com.moky.timebattle.ui.components.icons.userIcon
import com.moky.timebattle.ui.theme.AbyssBlack
import com.moky.timebattle.ui.theme.CarbonGrey
import com.moky.timebattle.ui.theme.DarkRed
import com.moky.timebattle.ui.theme.DeepGrey
import com.moky.timebattle.ui.theme.DimWhite
import com.moky.timebattle.ui.theme.LifeRed
import com.moky.timebattle.ui.theme.MutedWhite
import com.moky.timebattle.ui.theme.StrokeLight
import com.moky.timebattle.ui.theme.StrokeRed
import com.moky.timebattle.ui.theme.WarmWhite

enum class ButtonVariant { Primary, Outline, Ghost }

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    enabled: Boolean = true
) {
    val (bg, border, contentColor) = when (variant) {
        ButtonVariant.Primary -> Triple(LifeRed, LifeRed, WarmWhite)
        ButtonVariant.Outline -> Triple(Color.Transparent, StrokeRed, LifeRed)
        ButtonVariant.Ghost -> Triple(Color.Transparent, StrokeLight, MutedWhite)
    }
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (enabled) bg else bg.copy(alpha = 0.4f))
            .border(1.dp, if (enabled) border else border.copy(alpha = 0.4f), shape)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 11.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                color = if (enabled) contentColor else contentColor.copy(alpha = 0.4f),
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            )
        )
    }
}

@Composable
fun AppInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true
) {
    val shape = RoundedCornerShape(7.dp)
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, StrokeLight, shape)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        textStyle = TextStyle(
            color = WarmWhite,
            fontSize = 13.sp,
            lineHeight = 18.sp
        ),
        singleLine = singleLine,
        cursorBrush = SolidColor(DarkRed),
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium.copy(color = DimWhite),
                        fontSize = 13.sp
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun AppChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    val (bg, border, textColor) = if (selected) {
        Triple(LifeRed, LifeRed, WarmWhite)
    } else {
        Triple(Color.Transparent, StrokeLight, MutedWhite)
    }
    Box(
        modifier = modifier
            .clip(shape)
            .background(bg)
            .border(1.dp, border, shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = textColor,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
            )
        )
    }
}

@Composable
fun TaskTag(
    tag: String,
    isSystem: Boolean,
    modifier: Modifier = Modifier
) {
    val bg = if (isSystem) LifeRed.copy(alpha = 0.1f) else StrokeLight.copy(alpha = 0.2f)
    val textColor = if (isSystem) LifeRed else MutedWhite
    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(3.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = tag,
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                letterSpacing = 1.sp,
                fontSize = 7.sp
            )
        )
    }
}

@Composable
fun TaskCard(
    title: String,
    description: String,
    reward: String,
    isSystem: Boolean,
    actionText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, StrokeLight, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .padding(16.dp)
    ) {
        TaskTag(tag = if (isSystem) "SYS" else "USR", isSystem = isSystem)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = reward,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp)
            )
            Box(
                modifier = Modifier
                    .border(1.dp, StrokeRed, RoundedCornerShape(6.dp))
                    .padding(horizontal = 14.dp, vertical = 5.dp)
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LifeRed,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
fun InProgressItem(
    title: String,
    reward: String,
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .border(1.dp, StrokeLight, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = checkIcon(),
                    contentDescription = null,
                    tint = LifeRed,
                    modifier = Modifier.size(15.dp)
                )
            } else {
                Icon(
                    imageVector = userIcon(),
                    contentDescription = null,
                    tint = MutedWhite,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = WarmWhite,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = reward,
                style = MaterialTheme.typography.labelSmall.copy(color = LifeRed, fontSize = 9.sp),
                modifier = Modifier.padding(top = 1.dp)
            )
        }
        Text(
            text = "›",
            style = MaterialTheme.typography.bodyLarge.copy(color = MutedWhite)
        )
    }
}

@Composable
fun TimerBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 2.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(DeepGrey, RoundedCornerShape(1.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(height)
                .background(
                    Brush.horizontalGradient(
                        listOf(DarkRed, LifeRed)
                    ),
                    RoundedCornerShape(1.dp)
                )
        )
    }
}

@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
            color = MutedWhite,
            letterSpacing = 2.sp,
            fontSize = 8.sp
        ),
        modifier = modifier
    )
}

@Composable
fun ToastItem(
    message: String,
    modifier: Modifier = Modifier,
    leading: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CarbonGrey, RoundedCornerShape(8.dp))
            .border(1.dp, StrokeLight, RoundedCornerShape(8.dp))
            .padding(start = 0.dp, top = 10.dp, end = 14.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(28.dp)
                .background(LifeRed)
        )
        leading?.invoke()
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, lineHeight = 14.sp)
        )
    }
}
