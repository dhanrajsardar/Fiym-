package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.ManifestationItem
import com.example.data.local.WealthIdentityStore
import kotlin.math.max
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManifestationsScreen(
    identityStore: WealthIdentityStore,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val manifestations by identityStore.manifestations.collectAsState()

    // View state
    var isCreateMode by remember { mutableStateOf(false) }
    var isGridView by remember { mutableStateOf(false) } // Default to Interactive Draggable Vision Board
    var itemToEdit by remember { mutableStateOf<ManifestationItem?>(null) }
    var selectedManifestationForView by remember { mutableStateOf<ManifestationItem?>(null) }

    BackHandler {
        if (selectedManifestationForView != null) {
            selectedManifestationForView = null
        } else if (isCreateMode) {
            isCreateMode = false
            itemToEdit = null
        } else {
            onBack()
        }
    }

    // Exact background gradient matching reference aesthetic:
    // Soft airy pale lilac/lavender -> Gentle powdery rose pink -> Warm peach apricot sunset glow
    val backgroundBrush = Brush.verticalGradient(
        0.00f to Color(0xFFEBE4F0), // Top: Pale lavender-gray mist
        0.20f to Color(0xFFF3E6EE), // Upper: Gentle lilac petal
        0.45f to Color(0xFFF8E5EC), // Mid: Soft powdery pink glow
        0.70f to Color(0xFFFDE6DC), // Lower-mid: Delicate blush peach
        0.88f to Color(0xFFFCDCC6), // Lower: Warm apricot glow
        1.00f to Color(0xFFFCD4BC)  // Bottom: Warm peach-gold sunset
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        if (isCreateMode) {
            // --- STEP 2: CREATE / EDIT MANIFESTATION BOARD SCREEN ---
            CreateManifestationBoardView(
                initialItem = itemToEdit,
                onBack = {
                    isCreateMode = false
                    itemToEdit = null
                },
                onSave = { title, imageUri, resId ->
                    if (itemToEdit != null) {
                        identityStore.updateManifestation(
                            id = itemToEdit!!.id,
                            newTitle = title,
                            newImageUri = imageUri?.toString(),
                            newDrawableResId = resId
                        )
                        Toast.makeText(context, "Manifestation updated", Toast.LENGTH_SHORT).show()
                    } else {
                        // Place near top-center of vision board
                        val nextX = (30 + (manifestations.size % 2) * 160).toFloat()
                        val nextY = (60 + (manifestations.size / 2) * 220).toFloat()
                        val randomTilt = if (manifestations.size % 2 == 0) -2.5f else 2.5f
                        identityStore.addManifestation(
                            title = title,
                            imageUri = imageUri?.toString(),
                            drawableResId = resId,
                            posX = nextX,
                            posY = nextY,
                            rotation = randomTilt
                        )
                        Toast.makeText(context, "✨ Called in: $title", Toast.LENGTH_SHORT).show()
                    }
                    isCreateMode = false
                    itemToEdit = null
                }
            )
        } else {
            // --- STEP 1: MANIFESTATIONS INTERACTIVE VISION BOARD SCREEN ---
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = {
                            Column(
                                modifier = Modifier.padding(start = 2.dp)
                            ) {
                                Text(
                                    text = "Manifestations",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF28212B)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isGridView) "EVERYTHING YOU'VE CALLED IN" else "DRAG & PLACE ANYWHERE ON BOARD ✦",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        letterSpacing = 1.3.sp,
                                        color = Color(0xFF8C7B8E)
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier.testTag("back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color(0xFF28212B),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        actions = {
                            // Toggle View Mode (Canvas vs Grid)
                            IconButton(
                                onClick = { isGridView = !isGridView },
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("toggle_view_mode")
                            ) {
                                Icon(
                                    imageVector = if (isGridView) Icons.Default.PanTool else Icons.Default.GridView,
                                    contentDescription = if (isGridView) "Switch to Vision Board" else "Switch to Grid",
                                    tint = Color(0xFF6B5A6E),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Auto-arrange / Reset button
                            IconButton(
                                onClick = {
                                    identityStore.resetPositions()
                                    Toast.makeText(context, "✦ Board Re-arranged", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("auto_arrange_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Auto Arrange Board",
                                    tint = Color(0xFF6B5A6E),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            // Circular outlined (+) button to create new manifestation
                            Box(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .border(1.2.dp, Color(0xFFA693A6), CircleShape)
                                    .clickable {
                                        itemToEdit = null
                                        isCreateMode = true
                                    }
                                    .testTag("add_manifestation_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Create Manifestation",
                                    tint = Color(0xFFA693A6),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                }
            ) { innerPadding ->
                if (manifestations.isEmpty()) {
                    // Minimal empty state
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .clickable { isCreateMode = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "Your board is calm & clear",
                                fontFamily = FontFamily.Serif,
                                fontSize = 20.sp,
                                color = Color(0xFF5A4E60)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap (+) in the corner to call in your first manifestation.\nYou can move, drag, and freely position them anywhere on your board!",
                                fontSize = 13.5.sp,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF9E8F9E)
                            )
                        }
                    }
                } else if (isGridView) {
                    // 2-Column Staggered Grid View
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = innerPadding.calculateTopPadding() + 8.dp,
                            bottom = 40.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalItemSpacing = 14.dp,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(manifestations, key = { it.id }) { item ->
                            val hasImage = item.drawableResId != null || item.imageUri != null
                            if (hasImage) {
                                ImageManifestationCard(
                                    item = item,
                                    onClick = { selectedManifestationForView = item },
                                    onEdit = {
                                        itemToEdit = item
                                        isCreateMode = true
                                    },
                                    onDelete = { identityStore.deleteManifestation(item.id) },
                                    onFavorite = {
                                        identityStore.toggleFavorite(
                                            title = item.title,
                                            content = "Manifestation: ${item.title} called in on ${item.date}",
                                            category = "Manifestation"
                                        )
                                        Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else {
                                TextOnlyManifestationCard(
                                    item = item,
                                    onClick = { selectedManifestationForView = item },
                                    onEdit = {
                                        itemToEdit = item
                                        isCreateMode = true
                                    },
                                    onDelete = { identityStore.deleteManifestation(item.id) },
                                    onFavorite = {
                                        identityStore.toggleFavorite(
                                            title = item.title,
                                            content = "Manifestation: ${item.title} called in on ${item.date}",
                                            category = "Manifestation"
                                        )
                                        Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                } else {
                    // --- INTERACTIVE DRAGGABLE VISION BOARD CANVAS ---
                    InteractiveDraggableBoardCanvas(
                        manifestations = manifestations,
                        contentPadding = innerPadding,
                        onUpdatePosition = { id, posX, posY, rotation, bringToFront ->
                            identityStore.updateManifestationPosition(
                                id = id,
                                posX = posX,
                                posY = posY,
                                rotation = rotation,
                                bringToFront = bringToFront
                            )
                        },
                        onCardClick = { selectedManifestationForView = it },
                        onEdit = {
                            itemToEdit = it
                            isCreateMode = true
                        },
                        onDelete = { identityStore.deleteManifestation(it.id) },
                        onFavorite = {
                            identityStore.toggleFavorite(
                                title = it.title,
                                content = "Manifestation: ${it.title} called in on ${it.date}",
                                category = "Manifestation"
                            )
                            Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Clean & peaceful detail dialog
        selectedManifestationForView?.let { item ->
            val hasImage = item.drawableResId != null || item.imageUri != null
            AlertDialog(
                onDismissRequest = { selectedManifestationForView = null },
                containerColor = Color.White,
                shape = RoundedCornerShape(24.dp),
                title = null,
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (hasImage) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0xFF1E1724))
                            ) {
                                if (item.drawableResId != null) {
                                    Image(
                                        painter = painterResource(id = item.drawableResId),
                                        contentDescription = item.title,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else if (item.imageUri != null) {
                                    AsyncImage(
                                        model = Uri.parse(item.imageUri),
                                        contentDescription = item.title,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(10.dp)
                                        .background(Color(0x99000000), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = item.date,
                                        color = Color.White,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        } else {
                            Text(
                                text = item.date,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF8E8294)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        Text(
                            text = item.title,
                            fontFamily = FontFamily.Serif,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF28212B)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Embody this reality in the present tense. It is already done.",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = Color(0xFF7A6D82)
                        )
                    }
                },
                confirmButton = {
                    Row {
                        TextButton(
                            onClick = {
                                val currentItem = selectedManifestationForView
                                selectedManifestationForView = null
                                itemToEdit = currentItem
                                isCreateMode = true
                            }
                        ) {
                            Text("Edit", color = Color(0xFF9E748F))
                        }
                        TextButton(
                            onClick = { selectedManifestationForView = null }
                        ) {
                            Text(
                                text = "Embody & Close",
                                color = Color(0xFF9E748F),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            )
        }
    }
}

/**
 * Interactive Draggable Board Canvas:
 * Allows user to touch and drag text notes and images freely anywhere across the canvas!
 */
@Composable
fun InteractiveDraggableBoardCanvas(
    manifestations: List<ManifestationItem>,
    contentPadding: PaddingValues,
    onUpdatePosition: (id: String, posX: Float, posY: Float, rotation: Float?, bringToFront: Boolean) -> Unit,
    onCardClick: (ManifestationItem) -> Unit,
    onEdit: (ManifestationItem) -> Unit,
    onDelete: (ManifestationItem) -> Unit,
    onFavorite: (ManifestationItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Calculate maximum bottom position so canvas can scroll if cards are moved down
    val maxCardY = manifestations.maxOfOrNull { it.posY } ?: 400f
    val canvasHeightDp = max(1100f, maxCardY + 450f).dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = contentPadding.calculateTopPadding())
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(canvasHeightDp)
        ) {
            // Subtle aesthetic background pinboard dot texture
            Canvas(modifier = Modifier.fillMaxSize()) {
                val dotSpacing = 32.dp.toPx()
                val dotColor = Color(0x1F8B708B)
                var y = 20f
                while (y < size.height) {
                    var x = 20f
                    while (x < size.width) {
                        drawCircle(
                            color = dotColor,
                            radius = 1.2.dp.toPx(),
                            center = Offset(x, y)
                        )
                        x += dotSpacing
                    }
                    y += dotSpacing
                }
            }

            // Render each manifestation as a draggable card
            manifestations.forEach { item ->
                DraggableManifestationItem(
                    item = item,
                    onDragComplete = { newX, newY ->
                        onUpdatePosition(item.id, newX, newY, null, true)
                    },
                    onClick = { onCardClick(item) },
                    onEdit = { onEdit(item) },
                    onDelete = { onDelete(item) },
                    onFavorite = { onFavorite(item) },
                    onRotate = { deltaRot ->
                        val newRot = (item.rotation + deltaRot) % 360f
                        onUpdatePosition(item.id, item.posX, item.posY, newRot, true)
                    }
                )
            }
        }
    }
}

/**
 * Individual Draggable Card Component with real-time responsive touch gestures,
 * elevation lift feedback, and rotation.
 */
@Composable
fun DraggableManifestationItem(
    item: ManifestationItem,
    onDragComplete: (Float, Float) -> Unit,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onFavorite: () -> Unit,
    onRotate: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var isDragging by remember { mutableStateOf(false) }
    var currentX by remember(item.id, item.posX) { mutableFloatStateOf(item.posX) }
    var currentY by remember(item.id, item.posY) { mutableFloatStateOf(item.posY) }

    val animatedElevation by animateFloatAsState(
        targetValue = if (isDragging) 16f else 3f,
        animationSpec = spring(),
        label = "dragElevation"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isDragging) 1.05f else 1.0f,
        animationSpec = spring(),
        label = "dragScale"
    )

    val hasImage = item.drawableResId != null || item.imageUri != null
    val cardWidth = if (hasImage) 168.dp else 165.dp

    Box(
        modifier = modifier
            .offset {
                val xPx = with(density) { currentX.dp.roundToPx() }
                val yPx = with(density) { currentY.dp.roundToPx() }
                IntOffset(xPx, yPx)
            }
            .zIndex(if (isDragging) 999f else item.zIndex)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
                rotationZ = item.rotation + (if (isDragging) -1.5f else 0f)
                shadowElevation = animatedElevation
                clip = false
            }
            .pointerInput(item.id) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                    },
                    onDragEnd = {
                        isDragging = false
                        onDragComplete(currentX, currentY)
                    },
                    onDragCancel = {
                        isDragging = false
                        onDragComplete(currentX, currentY)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val dxDp = with(density) { dragAmount.x.toDp().value }
                        val dyDp = with(density) { dragAmount.y.toDp().value }
                        currentX = max(4f, currentX + dxDp)
                        currentY = max(4f, currentY + dyDp)
                    }
                )
            }
            .width(cardWidth)
    ) {
        if (hasImage) {
            ImageVisionBoardCard(
                item = item,
                isDragging = isDragging,
                onClick = onClick,
                onEdit = onEdit,
                onDelete = onDelete,
                onFavorite = onFavorite,
                onRotate = onRotate
            )
        } else {
            TextVisionBoardCard(
                item = item,
                isDragging = isDragging,
                onClick = onClick,
                onEdit = onEdit,
                onDelete = onDelete,
                onFavorite = onFavorite,
                onRotate = onRotate
            )
        }
    }
}

/**
 * Text Card for Vision Board with Pinboard Stationery Aesthetic
 */
@Composable
fun TextVisionBoardCard(
    item: ManifestationItem,
    isDragging: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onFavorite: () -> Unit,
    onRotate: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDragging) 12.dp else 3.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color(0x226A516E),
                spotColor = Color(0x226A516E)
            )
            .clip(RoundedCornerShape(20.dp))
            .border(
                0.8.dp,
                if (isDragging) Color(0xFFBA96B3) else Color(0x35E0D2E0),
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .testTag("manifestation_card_${item.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFAF6F9) // Delicate warm stationary linen
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // Header: Date + 3-dots Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.date,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF96879B)
                )

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color(0xFFA597A8),
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    DraggableActionMenu(
                        expanded = menuExpanded,
                        onDismiss = { menuExpanded = false },
                        onEdit = onEdit,
                        onFavorite = onFavorite,
                        onDelete = onDelete,
                        onRotate = {
                            menuExpanded = false
                            onRotate(4f)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Manifestation Text (Serif, dark charcoal)
            Text(
                text = item.title,
                fontFamily = FontFamily.Serif,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF2E2633),
                lineHeight = 23.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtle bottom sparkle indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "✦",
                    fontSize = 10.sp,
                    color = Color(0xFFC7B3C6)
                )
            }
        }
    }
}

/**
 * Image Card for Vision Board with Polaroid Framing Aesthetic
 */
@Composable
fun ImageVisionBoardCard(
    item: ManifestationItem,
    isDragging: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onFavorite: () -> Unit,
    onRotate: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.74f)
            .shadow(
                elevation = if (isDragging) 14.dp else 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color(0x28493352),
                spotColor = Color(0x28493352)
            )
            .clip(RoundedCornerShape(20.dp))
            .border(
                0.8.dp,
                if (isDragging) Color(0xFFBA96B3) else Color(0x35E0D2E0),
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .testTag("manifestation_card_${item.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1724))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main Image
            if (item.drawableResId != null) {
                Image(
                    painter = painterResource(id = item.drawableResId),
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else if (item.imageUri != null) {
                AsyncImage(
                    model = Uri.parse(item.imageUri),
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Top-left Date Badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(Color(0x99000000), RoundedCornerShape(8.dp))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text(
                    text = item.date,
                    color = Color.White,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Top-right 3-dots Menu
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                }

                DraggableActionMenu(
                    expanded = menuExpanded,
                    onDismiss = { menuExpanded = false },
                    onEdit = onEdit,
                    onFavorite = onFavorite,
                    onDelete = onDelete,
                    onRotate = {
                        menuExpanded = false
                        onRotate(4f)
                    }
                )
            }

            // Bottom Title with dark gradient scrim
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0x99000000), Color(0xEE000000))
                        )
                    )
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Text(
                    text = item.title,
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DraggableActionMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onFavorite: () -> Unit,
    onDelete: () -> Unit,
    onRotate: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier.background(Color.White)
    ) {
        DropdownMenuItem(
            text = { Text("Edit") },
            onClick = {
                onDismiss()
                onEdit()
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color(0xFF6A5A70),
                    modifier = Modifier.size(18.dp)
                )
            }
        )
        DropdownMenuItem(
            text = { Text("Rotate Card") },
            onClick = onRotate,
            leadingIcon = {
                Icon(
                    Icons.Default.RotateRight,
                    contentDescription = null,
                    tint = Color(0xFF6A5A70),
                    modifier = Modifier.size(18.dp)
                )
            }
        )
        DropdownMenuItem(
            text = { Text("Add to Favorites") },
            onClick = {
                onDismiss()
                onFavorite()
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFE91E63),
                    modifier = Modifier.size(18.dp)
                )
            }
        )
        DropdownMenuItem(
            text = { Text("Delete", color = Color(0xFFD32F2F)) },
            onClick = {
                onDismiss()
                onDelete()
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(18.dp)
                )
            }
        )
    }
}

// --- TYPE A: TEXT-ONLY MANIFESTATION CARD (For Grid View) ---
@Composable
fun TextOnlyManifestationCard(
    item: ManifestationItem,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = Color(0x1A6A516E),
                spotColor = Color(0x1A6A516E)
            )
            .clip(RoundedCornerShape(22.dp))
            .border(0.6.dp, Color(0x28E0D2E0), RoundedCornerShape(22.dp))
            .clickable { onClick() }
            .testTag("manifestation_card_${item.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xF7FDFBFC)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Top Row: Date on left, 3-dots on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.date,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF8E8294)
                )

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color(0xFFA597A8),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    ManifestationActionMenu(
                        expanded = menuExpanded,
                        onDismiss = { menuExpanded = false },
                        onEdit = onEdit,
                        onFavorite = onFavorite,
                        onDelete = onDelete
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Manifestation Text (Serif, dark charcoal)
            Text(
                text = item.title,
                fontFamily = FontFamily.Serif,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF2E2633),
                lineHeight = 26.sp
            )
        }
    }
}

// --- TYPE B: IMAGE MANIFESTATION CARD (For Grid View) ---
@Composable
fun ImageManifestationCard(
    item: ManifestationItem,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.72f)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = Color(0x22493352),
                spotColor = Color(0x22493352)
            )
            .clip(RoundedCornerShape(22.dp))
            .clickable { onClick() }
            .testTag("manifestation_card_${item.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1724))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main Image
            if (item.drawableResId != null) {
                Image(
                    painter = painterResource(id = item.drawableResId),
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else if (item.imageUri != null) {
                AsyncImage(
                    model = Uri.parse(item.imageUri),
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Top-left Date Badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .background(Color(0x99000000), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = item.date,
                    color = Color.White,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Top-right 3-dots Menu
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
            ) {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                ManifestationActionMenu(
                    expanded = menuExpanded,
                    onDismiss = { menuExpanded = false },
                    onEdit = onEdit,
                    onFavorite = onFavorite,
                    onDelete = onDelete
                )
            }

            // Bottom Title with dark gradient scrim
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0x99000000), Color(0xEE000000))
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    text = item.title,
                    fontFamily = FontFamily.Serif,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ManifestationActionMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier.background(Color.White)
    ) {
        DropdownMenuItem(
            text = { Text("Edit") },
            onClick = {
                onDismiss()
                onEdit()
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color(0xFF6A5A70),
                    modifier = Modifier.size(18.dp)
                )
            }
        )
        DropdownMenuItem(
            text = { Text("Add to Favorites") },
            onClick = {
                onDismiss()
                onFavorite()
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFE91E63),
                    modifier = Modifier.size(18.dp)
                )
            }
        )
        DropdownMenuItem(
            text = { Text("Delete", color = Color(0xFFD32F2F)) },
            onClick = {
                onDismiss()
                onDelete()
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(18.dp)
                )
            }
        )
    }
}

// --- STEP 2: CREATE / EDIT YOUR MANIFESTATION BOARD (References 2, 3, 4) ---
@Composable
fun CreateManifestationBoardView(
    initialItem: ManifestationItem? = null,
    onBack: () -> Unit,
    onSave: (String, Uri?, Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var titleInput by remember { mutableStateOf(initialItem?.title ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(initialItem?.imageUri?.let { Uri.parse(it) }) }
    var selectedPresetResId by remember { mutableStateOf(initialItem?.drawableResId) }
    var showInspirationDialog by remember { mutableStateOf(false) }

    // Gallery Picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            selectedPresetResId = null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        // Simple Top-left Back Arrow
        IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("create_back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF28212B),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Large Centered Heading
        Text(
            text = "Create your\nmanifestation board",
            fontFamily = FontFamily.Serif,
            fontSize = 32.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF453E4A),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // "start here ✦" with downward hand-drawn arrow (shown when no image selected)
        if (selectedImageUri == null && selectedPresetResId == null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "start here ✦",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF9E8F9E)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Graceful subtle curved arrow pointing downward
                Canvas(
                    modifier = Modifier.size(width = 24.dp, height = 36.dp)
                ) {
                    val arrowPath = Path().apply {
                        moveTo(size.width * 0.45f, 2f)
                        cubicTo(
                            size.width * 0.75f, size.height * 0.35f,
                            size.width * 0.25f, size.height * 0.65f,
                            size.width * 0.5f, size.height - 4f
                        )
                    }
                    drawPath(
                        path = arrowPath,
                        color = Color(0xFFBCAEB8),
                        style = Stroke(width = 1.8f, cap = StrokeCap.Round)
                    )
                    // Arrowhead
                    val headPath = Path().apply {
                        moveTo(size.width * 0.3f, size.height - 10f)
                        lineTo(size.width * 0.5f, size.height - 2f)
                        lineTo(size.width * 0.7f, size.height - 10f)
                    }
                    drawPath(
                        path = headPath,
                        color = Color(0xFFBCAEB8),
                        style = Stroke(width = 1.8f, cap = StrokeCap.Round)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Floating Card: Manifestation Creation Board
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = Color(0x185E4663),
                    spotColor = Color(0x185E4663)
                )
                .clip(RoundedCornerShape(26.dp)),
            color = Color.White,
            shape = RoundedCornerShape(26.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Image Preview if selected
                if (selectedImageUri != null || selectedPresetResId != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.58f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFF1E1724))
                    ) {
                        if (selectedPresetResId != null) {
                            Image(
                                painter = painterResource(id = selectedPresetResId!!),
                                contentDescription = "Selected Dream Vision",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Selected Vision Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Circular close button (X) on top-right of image
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF886A86))
                                .clickable {
                                    selectedImageUri = null
                                    selectedPresetResId = null
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove Image",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Borderless text input directly on board
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    if (titleInput.isEmpty()) {
                        Text(
                            text = "What did you call in?",
                            fontSize = 17.sp,
                            color = Color(0xFFAFA6B2),
                            fontFamily = FontFamily.Default
                        )
                    }

                    BasicTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        textStyle = TextStyle(
                            fontSize = 17.sp,
                            color = Color(0xFF28212B),
                            fontFamily = FontFamily.Default
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manifestation_input_text")
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom Actions Row inside Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left icons: Camera/Presets and Gallery
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                showInspirationDialog = true
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("camera_icon_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CameraAlt,
                                contentDescription = "Camera / Presets",
                                tint = Color(0xFF685B6F),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                galleryLauncher.launch("image/*")
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("gallery_icon_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Image,
                                contentDescription = "Gallery",
                                tint = Color(0xFF685B6F),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Right Send / Submit Action Button (Circular Mauve with White Paper-Plane)
                    val isReadyToSubmit = titleInput.isNotBlank() || selectedImageUri != null || selectedPresetResId != null
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                if (isReadyToSubmit) Color(0xFF9E748F) else Color(0xFFDCD2DC)
                            )
                            .clickable(enabled = isReadyToSubmit) {
                                val finalTitle = titleInput.ifBlank { "Manifested Vision" }
                                onSave(finalTitle, selectedImageUri, selectedPresetResId)
                            }
                            .testTag("submit_manifestation_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    // Inspiration Presets Modal for quick vision choices (Ferrari, Villa, Mansion)
    if (showInspirationDialog) {
        AlertDialog(
            onDismissRequest = { showInspirationDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(22.dp),
            title = {
                Text(
                    text = "Vision Inspirations",
                    fontFamily = FontFamily.Serif,
                    fontSize = 19.sp,
                    color = Color(0xFF28212B)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF9F5F8),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedPresetResId = R.drawable.manifest_dream_car_1786842283241
                                selectedImageUri = null
                                if (titleInput.isBlank()) titleInput = "Dream car"
                                showInspirationDialog = false
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.manifest_dream_car_1786842283241),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Dream car", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Ferrari Red Supercar", fontSize = 12.sp, color = Color(0xFF8B7D93))
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF9F5F8),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedPresetResId = R.drawable.manifest_villa_1786842298224
                                selectedImageUri = null
                                if (titleInput.isBlank()) titleInput = "Dream villa"
                                showInspirationDialog = false
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.manifest_villa_1786842298224),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Dream villa", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Luxury Infinity Pool Estate", fontSize = 12.sp, color = Color(0xFF8B7D93))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showInspirationDialog = false }) {
                    Text("Close", color = Color(0xFF9E748F))
                }
            }
        )
    }
}
