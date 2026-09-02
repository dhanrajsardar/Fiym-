package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BroQuotes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * 100% Pure Jetpack Compose Interactive Floating Fiym Orb.
 * Lightweight, zero background artifact / shadow, non-blocking to underlying UI touches.
 */
@Composable
fun InAppFloatingOrb(
    onOpenChat: () -> Unit,
    onOpenGuide: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isBubbleOpen by remember { mutableStateOf(false) }
    var currentThought by remember { mutableStateOf(BroQuotes.getRandomQuote()) }
    val coroutineScope = rememberCoroutineScope()

    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    var targetYaw by remember { mutableFloatStateOf(0f) }
    var targetPitch by remember { mutableFloatStateOf(0f) }
    val animatedYaw by animateFloatAsState(targetValue = targetYaw, animationSpec = spring(stiffness = 400f), label = "yaw")
    val animatedPitch by animateFloatAsState(targetValue = targetPitch, animationSpec = spring(stiffness = 400f), label = "pitch")

    var isBlinking by remember { mutableStateOf(false) }
    var isHappy by remember { mutableStateOf(false) }

    // Natural idle blinking loop
    LaunchedEffect(Unit) {
        while (true) {
            delay((2800..5500).random().toLong())
            isBlinking = true
            delay(130)
            isBlinking = false
        }
    }

    // Breathing float animation
    val infiniteTransition = rememberInfiniteTransition(label = "orb_breath")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_offset"
    )
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_pulse"
    )

    fun triggerTapAction() {
        isHappy = true
        isBubbleOpen = !isBubbleOpen
        if (isBubbleOpen) {
            currentThought = BroQuotes.getRandomQuote()
        }
        coroutineScope.launch {
            delay(1800)
            isHappy = false
        }
    }

    Box(
        modifier = modifier
            .wrapContentSize()
            .offset { IntOffset(dragOffsetX.roundToInt(), dragOffsetY.roundToInt()) }
    ) {
        Column(
            horizontalAlignment = Alignment.End
        ) {
            // Speech / Thought Bubble (Clean White Speech Bubble matching reference design)
            AnimatedVisibility(
                visible = isBubbleOpen,
                enter = scaleIn(spring(stiffness = 350f)) + fadeIn(tween(200)),
                exit = scaleOut(spring(stiffness = 350f)) + fadeOut(tween(150))
            ) {
                Card(
                    modifier = Modifier
                        .widthIn(min = 210.dp, max = 285.dp)
                        .padding(bottom = 6.dp)
                        .clickable {
                            // Tapping bubble refreshes quote with an animated reaction
                            currentThought = BroQuotes.getRandomQuote()
                            isHappy = true
                            coroutineScope.launch {
                                delay(1200)
                                isHappy = false
                            }
                        },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    border = BorderStroke(1.dp, Color(0xFFF0EAE6))
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 15.dp)
                    ) {
                        Text(
                            text = currentThought,
                            fontSize = 15.sp,
                            lineHeight = 21.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C1108)
                        )
                    }
                }
            }

            // Pure Compose Mascot Sphere (100% transparent back, no black background, draggable & clickable)
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .offset(y = floatOffset.dp)
                    .scale(scalePulse)
                    .clip(CircleShape)
                    .clickable {
                        triggerTapAction()
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                targetYaw = 0f
                                targetPitch = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffsetX += dragAmount.x
                                dragOffsetY += dragAmount.y
                                targetYaw = (dragAmount.x / 15f).coerceIn(-0.85f, 0.85f)
                                targetPitch = (dragAmount.y / 15f).coerceIn(-0.85f, 0.85f)
                            },
                            onDragEnd = {
                                targetYaw = 0f
                                targetPitch = 0f
                            },
                            onDragCancel = {
                                targetYaw = 0f
                                targetPitch = 0f
                            }
                        )
                    }
                    .testTag("floating_fiym_orb"),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val cx = w / 2f
                    val cy = h / 2f
                    val radius = (minOf(w, h) / 2f) - 2f

                    // 1. 3D Glossy Periwinkle/Lavender Sphere
                    val bodyBrush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE4EDFF), // Specular light reflection
                            Color(0xFFADC3FF), // Mascot body lavender-blue
                            Color(0xFF7A95E8)  // 3D bottom shading
                        ),
                        center = Offset(
                            cx - radius * 0.35f + (animatedYaw * radius * 0.2f),
                            cy - radius * 0.35f + (animatedPitch * radius * 0.2f)
                        ),
                        radius = radius * 1.35f
                    )
                    drawCircle(brush = bodyBrush, radius = radius, center = Offset(cx, cy))

                    // 2. Specular Top Highlight Reflection Oval
                    val specX = cx - radius * 0.32f + (animatedYaw * radius * 0.1f)
                    val specY = cy - radius * 0.52f + (animatedPitch * radius * 0.1f)
                    val specW = radius * 0.62f
                    val specH = radius * 0.32f
                    drawOval(
                        color = Color.White.copy(alpha = 0.65f),
                        topLeft = Offset(specX - specW / 2f, specY - specH / 2f),
                        size = Size(specW, specH)
                    )

                    // 3. Face Elements with 3D Yaw and Pitch
                    val faceCx = cx + (animatedYaw * radius * 0.55f)
                    val faceCy = cy + (animatedPitch * radius * 0.45f)
                    val eyeSpacing = radius * 0.38f
                    val eyeRadius = radius * 0.26f

                    val leftEyePerspective = (1.0f + (animatedYaw * 0.35f)).coerceIn(0.25f, 1.2f)
                    val rightEyePerspective = (1.0f - (animatedYaw * 0.35f)).coerceIn(0.25f, 1.2f)

                    val leftEyeX = faceCx - (eyeSpacing * leftEyePerspective)
                    val rightEyeX = faceCx + (eyeSpacing * rightEyePerspective)
                    val eyesY = faceCy - (radius * 0.05f)

                    // Blush Cheeks
                    val cheekAlpha = ((1.0f - kotlin.math.abs(animatedYaw) * 0.7f) * 0.35f).coerceIn(0f, 0.4f)
                    val cheekY = faceCy + radius * 0.22f
                    drawCircle(
                        color = Color(0xFFFF6B8B).copy(alpha = cheekAlpha),
                        radius = radius * 0.16f,
                        center = Offset(leftEyeX, cheekY)
                    )
                    drawCircle(
                        color = Color(0xFFFF6B8B).copy(alpha = cheekAlpha),
                        radius = radius * 0.16f,
                        center = Offset(rightEyeX, cheekY)
                    )

                    // Blinking Eyes
                    if (isBlinking) {
                        drawLine(
                            color = Color(0xFF181818),
                            start = Offset(leftEyeX - eyeRadius * 0.55f, eyesY),
                            end = Offset(leftEyeX + eyeRadius * 0.55f, eyesY),
                            strokeWidth = 5f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color(0xFF181818),
                            start = Offset(rightEyeX - eyeRadius * 0.55f, eyesY),
                            end = Offset(rightEyeX + eyeRadius * 0.55f, eyesY),
                            strokeWidth = 5f,
                            cap = StrokeCap.Round
                        )
                    } else if (isHappy) {
                        // Happy curved ^ ^ eyes
                        val happyPathL = Path().apply {
                            moveTo(leftEyeX - eyeRadius * 0.55f, eyesY + 2f)
                            quadraticTo(leftEyeX, eyesY - eyeRadius * 0.65f, leftEyeX + eyeRadius * 0.55f, eyesY + 2f)
                        }
                        val happyPathR = Path().apply {
                            moveTo(rightEyeX - eyeRadius * 0.55f, eyesY + 2f)
                            quadraticTo(rightEyeX, eyesY - eyeRadius * 0.65f, rightEyeX + eyeRadius * 0.55f, eyesY + 2f)
                        }
                        drawPath(happyPathL, color = Color(0xFF181818), style = Stroke(width = 5f, cap = StrokeCap.Round))
                        drawPath(happyPathR, color = Color(0xFF181818), style = Stroke(width = 5f, cap = StrokeCap.Round))

                        // Cute Smile
                        val mouthPath = Path().apply {
                            moveTo(faceCx - radius * 0.18f, faceCy + radius * 0.16f)
                            quadraticTo(faceCx, faceCy + radius * 0.36f, faceCx + radius * 0.18f, faceCy + radius * 0.16f)
                            close()
                        }
                        drawPath(mouthPath, color = Color(0xFF181818), style = Fill)
                    } else {
                        // Big cute open 3D round eyes
                        val pupilShiftX = (animatedYaw * 4f).coerceIn(-4f, 4f)
                        val pupilShiftY = (animatedPitch * 4f).coerceIn(-4f, 4f)

                        // Left Eye
                        val lEyeR = eyeRadius * leftEyePerspective
                        drawCircle(color = Color.White, radius = lEyeR, center = Offset(leftEyeX, eyesY))
                        drawCircle(color = Color(0xFF809CE6), radius = lEyeR, center = Offset(leftEyeX, eyesY), style = Stroke(width = 1.5f))
                        drawCircle(
                            color = Color(0xFF101524),
                            radius = lEyeR * 0.78f,
                            center = Offset(leftEyeX + pupilShiftX, eyesY + pupilShiftY)
                        )
                        // White glint
                        drawCircle(
                            color = Color.White,
                            radius = lEyeR * 0.28f,
                            center = Offset(leftEyeX + pupilShiftX - lEyeR * 0.25f, eyesY + pupilShiftY - lEyeR * 0.25f)
                        )

                        // Right Eye
                        val rEyeR = eyeRadius * rightEyePerspective
                        drawCircle(color = Color.White, radius = rEyeR, center = Offset(rightEyeX, eyesY))
                        drawCircle(color = Color(0xFF809CE6), radius = rEyeR, center = Offset(rightEyeX, eyesY), style = Stroke(width = 1.5f))
                        drawCircle(
                            color = Color(0xFF101524),
                            radius = rEyeR * 0.78f,
                            center = Offset(rightEyeX + pupilShiftX, eyesY + pupilShiftY)
                        )
                        // White glint
                        drawCircle(
                            color = Color.White,
                            radius = rEyeR * 0.28f,
                            center = Offset(rightEyeX + pupilShiftX - rEyeR * 0.25f, eyesY + pupilShiftY - rEyeR * 0.25f)
                        )

                        // Subtle smile
                        val mouthPath = Path().apply {
                            moveTo(faceCx - radius * 0.12f, faceCy + radius * 0.20f)
                            quadraticTo(faceCx, faceCy + radius * 0.28f, faceCx + radius * 0.12f, faceCy + radius * 0.20f)
                        }
                        drawPath(mouthPath, color = Color(0xFF181818), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
                    }
                }
            }
        }
    }
}
