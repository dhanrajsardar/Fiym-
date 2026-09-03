package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BroOrangePrimary

@Composable
fun PremiumFeatureButton(
    title: String,
    imageRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hapticEnabled: Boolean = true,
    iconTint: Color? = BroOrangePrimary,
    iconBackground: Color? = Color(0xFFF5F0FF),
    iconSize: Dp = 28.dp,
    containerColor: Color = Color.White,
    labelColor: Color = Color(0xFF1E1E2E),
    accentColor: Color = BroOrangePrimary,
    contentDescription: String? = null,
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState()

    var isHovered by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = when {
            !enabled -> 1f
            isPressed.value -> 0.94f
            isHovered -> 1.02f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 1800f),
        label = "buttonScale"
    )

    val elevation by animateFloatAsState(
        targetValue = when {
            !enabled -> 0f
            isPressed.value -> 0f
            isHovered -> 6f
            else -> 2f
        },
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 1200f),
        label = "buttonElevation"
    )

    val iconBgAlpha by animateFloatAsState(
        targetValue = when {
            !enabled -> 0.4f
            isPressed.value -> 1f
            isHovered -> 0.7f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 1000f),
        label = "iconBgAlpha"
    )

    LaunchedEffect(isPressed.value) {
        if (isPressed.value && enabled && hapticEnabled) {
            context.runOnHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }

    val combinedModifier = modifier
        .fillMaxWidth()
        .aspectRatio(0.88f)
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            shadowElevation = elevation
        }
        .clip(RoundedCornerShape(20.dp))
        .background(containerColor)
        .border(
            width = if (isFocused) 2.dp else 0.dp,
            color = if (isFocused) accentColor else Color.Transparent,
            shape = RoundedCornerShape(20.dp)
        )
        .padding(2.dp)
        .clip(RoundedCornerShape(18.dp))
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
        .focusable(interactionSource = interactionSource)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = { isHovered = true }
            )
        }
        .onFocusChanged { focusState ->
            isFocused = focusState.isFocused
        }

    Box(
        modifier = combinedModifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (iconBackground != null) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .graphicsLayer { alpha = iconBgAlpha }
                        .background(iconBackground.copy(alpha = iconBgAlpha)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = imageRes),
                        contentDescription = contentDescription ?: title,
                        modifier = Modifier.size(iconSize),
                        tint = iconTint ?: Color.Unspecified
                    )
                }
            } else {
                Icon(
                    painter = painterResource(id = imageRes),
                    contentDescription = contentDescription ?: title,
                    modifier = Modifier.size(iconSize),
                    tint = iconTint ?: Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (enabled) labelColor else labelColor.copy(alpha = 0.38f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                letterSpacing = 0.2.sp,
                textAlign = TextAlign.Center
            )
        }

        if (isFocused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .border(2.dp, accentColor, RoundedCornerShape(20.dp))
                    .padding(-2.dp)
            ) {}
        }
    }
}

internal fun android.content.Context.runOnHapticFeedback(constants: Int) {
    (this as? android.app.Activity)?.window?.decorView?.performHapticFeedback(constants)
}