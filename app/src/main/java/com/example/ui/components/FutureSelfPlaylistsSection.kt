package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * EXACT pixel-perfect implementation of "YOUR FUTURE SELF PLAYLISTS" section
 * matching the user provided design image (WhatsApp Image 2026-08-14 at 8.38.34 AM.jpeg).
 */
@Composable
fun FutureSelfPlaylistsSection(
    onOpenPlaylist: () -> Unit,
    onCreatePlaylist: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        // 1. Section Header: "YOUR FUTURE SELF PLAYLISTS" + "+" button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "YOUR FUTURE SELF PLAYLISTS",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = Color(0xFF2C202B)
            )

            IconButton(
                onClick = onCreatePlaylist,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("btn_add_future_playlist")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Playlist",
                    tint = Color(0xFF2C202B),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Horizontal Cards Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            // Card 1: "The Mansion by the Water"
            item {
                MansionByTheWaterCard(
                    onClick = onOpenPlaylist
                )
            }

            // Card 2: "Create Playlist"
            item {
                CreatePlaylistDashedCard(
                    onClick = onCreatePlaylist
                )
            }
        }
    }
}

/**
 * Left Card: "The Mansion by the Water" with pink ripple background,
 * "• for you" badge, dark bird silhouette, serif title, and dot pagination indicator.
 */
@Composable
fun MansionByTheWaterCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(168.dp)
            .height(230.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(Color(0xFFFDE8F0))
            .clickable { onClick() }
            .testTag("playlist_mansion_card")
    ) {
        // Canvas rendering the marble ripple curves, bird silhouette, and accent dots
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawMansionCardBackground()
        }

        // Foreground content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top: "• for you" Pill Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFDF0F5).copy(alpha = 0.9f))
                    .padding(horizontal = 9.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4A3442))
                    )
                    Text(
                        text = "for you",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A3442)
                    )
                }
            }

            // Bottom: Serif Title + Dot pagination
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "The Mansion\nby the Water",
                    fontFamily = FontFamily.Serif,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF2C1D2A)
                )

                // Dot pagination: 1 active solid dot, 4 outline dots, + "1/5"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Dot 1 (Active)
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4A3442))
                    )

                    // Dots 2, 3, 4, 5 (Inactive)
                    repeat(4) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF5CAD9))
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "1/5",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B5363)
                    )
                }
            }
        }
    }
}

/**
 * Draws the wavy ripple concentric loops, subtle celestial dots, and flying dove silhouette
 */
private fun DrawScope.drawMansionCardBackground() {
    val w = size.width
    val h = size.height

    val swirlColor = Color(0xFFF7C8DA)
    val dotColor = Color(0xFF946F82)
    val birdColor = Color(0xFF2E1C2B)

    // 1. Concentric wavy ripple rings
    val swirlCenter1 = Offset(w * 0.72f, h * 0.32f)
    val swirlCenter2 = Offset(w * 0.22f, h * 0.45f)

    drawCircle(
        color = swirlColor,
        radius = 24.dp.toPx(),
        center = swirlCenter1,
        style = Stroke(width = 1.5.dp.toPx())
    )
    drawCircle(
        color = swirlColor,
        radius = 48.dp.toPx(),
        center = swirlCenter1,
        style = Stroke(width = 1.5.dp.toPx())
    )
    drawCircle(
        color = swirlColor,
        radius = 76.dp.toPx(),
        center = swirlCenter1,
        style = Stroke(width = 1.5.dp.toPx())
    )

    drawCircle(
        color = swirlColor,
        radius = 32.dp.toPx(),
        center = swirlCenter2,
        style = Stroke(width = 1.5.dp.toPx())
    )
    drawCircle(
        color = swirlColor,
        radius = 60.dp.toPx(),
        center = swirlCenter2,
        style = Stroke(width = 1.5.dp.toPx())
    )

    // Center focal dots
    drawCircle(color = dotColor, radius = 2.5.dp.toPx(), center = swirlCenter1)
    drawCircle(color = dotColor, radius = 2.5.dp.toPx(), center = swirlCenter2)
    drawCircle(color = dotColor, radius = 2.dp.toPx(), center = Offset(w * 0.78f, h * 0.18f))

    // 2. Flying Bird Silhouette
    val birdCenter = Offset(w * 0.46f, h * 0.48f)
    val birdPath = Path().apply {
        val cx = birdCenter.x
        val cy = birdCenter.y
        val scale = 0.95f

        moveTo(cx + 22f * scale, cy - 8f * scale) // Head / beak pointing right
        cubicTo(
            cx + 26f * scale, cy - 5f * scale,
            cx + 20f * scale, cy + 2f * scale,
            cx + 12f * scale, cy + 6f * scale
        ) // Throat to breast
        cubicTo(
            cx + 2f * scale, cy + 12f * scale,
            cx - 14f * scale, cy + 16f * scale,
            cx - 24f * scale, cy + 14f * scale
        ) // Underbelly to tail
        cubicTo(
            cx - 18f * scale, cy + 6f * scale,
            cx - 12f * scale, cy + 1f * scale,
            cx - 8f * scale, cy - 4f * scale
        ) // Forked tail inner
        cubicTo(
            cx - 18f * scale, cy - 14f * scale,
            cx - 6f * scale, cy - 22f * scale,
            cx + 6f * scale, cy - 16f * scale
        ) // Wing curve
        cubicTo(
            cx + 12f * scale, cy - 12f * scale,
            cx + 17f * scale, cy - 10f * scale,
            cx + 22f * scale, cy - 8f * scale
        ) // Back to head
        close()
    }

    drawPath(path = birdPath, color = birdColor)
}

/**
 * Right Card: "Create Playlist" with dashed/dotted border,
 * center white circular button with "+", and "Create Playlist" text.
 */
@Composable
fun CreatePlaylistDashedCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(168.dp)
            .height(230.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(Color(0xFFFAF6F9))
            .clickable { onClick() }
            .testTag("btn_create_playlist_card"),
        contentAlignment = Alignment.Center
    ) {
        // Dashed border
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 1.5.dp.toPx()
            val cornerRadius = 26.dp.toPx()
            val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)

            drawRoundRect(
                color = Color(0xFFE5D5E1),
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(size.width - strokeWidth, size.height - strokeWidth),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                style = Stroke(
                    width = strokeWidth,
                    pathEffect = dashPathEffect
                )
            )
        }

        // Center Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // White circular button with "+"
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .shadow(2.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Playlist",
                    tint = Color(0xFF7A6B78),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Create Playlist",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF6A5967)
            )
        }
    }
}
