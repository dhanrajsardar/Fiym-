package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.WealthIdentityStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OathScreen(
    identityStore: WealthIdentityStore,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isSealed by identityStore.isOathSealed.collectAsState()
    val savedName by identityStore.userName.collectAsState()
    val personalOaths by identityStore.personalOaths.collectAsState()
    val sealedDate by identityStore.sealedDate.collectAsState()
    val brokenRecords by identityStore.brokenRecords.collectAsState()

    var userNameInput by remember(savedName) { mutableStateOf(savedName) }
    var showAddOathDialog by remember { mutableStateOf(false) }
    var newOathText by remember { mutableStateOf("") }
    var showBreakOathDialog by remember { mutableStateOf(false) }
    var selectedBrokenOath by remember { mutableStateOf("") }
    var breakReasonText by remember { mutableStateOf("") }

    // Signature path strokes
    val signaturePoints = remember { mutableStateListOf<List<Offset>>() }
    var currentStroke = remember { mutableStateListOf<Offset>() }

    val coreFoundationOaths = remember {
        listOf(
            "I will not give up on the life I have chosen to build.",
            "I will not break my discipline for temporary comfort.",
            "I will face challenges instead of running from them.",
            "I understand that failure is not a reason to quit."
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_feat_oath_1786842491879),
                            contentDescription = "The Oath Icon",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "The Oath",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF1E2026)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E2026)
                        )
                    }
                },
                actions = {
                    if (isSealed) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SEALED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFD6EAF8),
                        Color(0xFFEAF3FA),
                        Color(0xFFFDE4EE),
                        Color(0xFFFFF6F1),
                        Color(0xFFFFF9F5)
                    )
                )
            )
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isSealed) {
                // ================= DRAFTING & SEALING FLOW =================
                // 1. Header Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF231934))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "THE OATH",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = Color(0xFFE1BEE7)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "A promise to the person I am becoming.",
                                fontSize = 13.5.sp,
                                color = Color.White,
                                fontStyle = FontStyle.Italic
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.ic_feat_oath_1786842491879),
                            contentDescription = null,
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                    }
                }

                // 2. Name Input Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Your Identity & Oath Name:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5E5470)
                        )
                        OutlinedTextField(
                            value = userNameInput,
                            onValueChange = { userNameInput = it },
                            placeholder = { Text("Enter your full name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF7E57C2),
                                unfocusedBorderColor = Color(0xFFE2DCED)
                            )
                        )
                    }
                }

                // 3. Core Foundation Oaths
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "I, ${userNameInput.ifBlank { "..." }}, make this oath to myself:",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E2026)
                        )

                        coreFoundationOaths.forEach { vow ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF673AB7),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = vow,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = Color(0xFF333333),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // 4. Personal Oaths Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "My Personal Oaths",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E2026)
                            )
                            TextButton(onClick = { showAddOathDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFF673AB7)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Oath", color = Color(0xFF673AB7), fontWeight = FontWeight.Bold)
                            }
                        }

                        if (personalOaths.isEmpty()) {
                            Text(
                                text = "No personal vows added yet. Tap '+ Add Oath' to add your unique sacred commitments.",
                                fontSize = 13.sp,
                                color = Color(0xFF888888),
                                fontStyle = FontStyle.Italic
                            )
                        } else {
                            personalOaths.forEachIndexed { index, vow ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF7F6FB), RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "✓",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF673AB7)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = vow,
                                            fontSize = 13.5.sp,
                                            lineHeight = 19.sp,
                                            color = Color(0xFF262626)
                                        )
                                    }
                                    IconButton(
                                        onClick = { identityStore.removePersonalOath(index) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFE57373),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Witness & Signature Pad
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF7FE)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Witness & Signature",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF311B92)
                        )

                        Text(
                            text = "\"I stand as a witness to this promise.\nI make these commitments knowingly and willingly. I understand that every time I break them, I must face myself—not make excuses.\"",
                            fontSize = 13.5.sp,
                            lineHeight = 19.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF4527A0)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sign on the pad below with finger:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF5E5470)
                            )
                            if (signaturePoints.isNotEmpty() || currentStroke.isNotEmpty()) {
                                TextButton(onClick = {
                                    signaturePoints.clear()
                                    currentStroke.clear()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = Color(0xFFE53935)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Clear Signature", color = Color(0xFFE53935), fontSize = 11.sp)
                                }
                            }
                        }

                        // Drawing Canvas
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFD1C4E9), RoundedCornerShape(14.dp))
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            currentStroke.clear()
                                            currentStroke.add(offset)
                                        },
                                        onDrag = { change, _ ->
                                            change.consume()
                                            currentStroke.add(change.position)
                                        },
                                        onDragEnd = {
                                            if (currentStroke.isNotEmpty()) {
                                                signaturePoints.add(currentStroke.toList())
                                                currentStroke.clear()
                                            }
                                        },
                                        onDragCancel = {
                                            currentStroke.clear()
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                // Draw completed strokes
                                signaturePoints.forEach { stroke ->
                                    if (stroke.size > 1) {
                                        val path = Path().apply {
                                            moveTo(stroke.first().x, stroke.first().y)
                                            for (i in 1 until stroke.size) {
                                                lineTo(stroke[i].x, stroke[i].y)
                                            }
                                        }
                                        drawPath(
                                            path = path,
                                            color = Color(0xFF1A237E),
                                            style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                        )
                                    }
                                }
                                // Draw ongoing stroke
                                if (currentStroke.size > 1) {
                                    val path = Path().apply {
                                        moveTo(currentStroke.first().x, currentStroke.first().y)
                                        for (i in 1 until currentStroke.size) {
                                            lineTo(currentStroke[i].x, currentStroke[i].y)
                                        }
                                    }
                                    drawPath(
                                        path = path,
                                        color = Color(0xFF1A237E),
                                        style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                    )
                                }
                            }

                            if (signaturePoints.isEmpty() && currentStroke.isEmpty()) {
                                Text(
                                    text = "✍️ Draw your signature here",
                                    color = Color(0xFFB39DDB),
                                    fontSize = 14.sp,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Witness: Self",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF673AB7)
                            )
                            Text(
                                text = "Signed by: ${userNameInput.ifBlank { "You" }}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF333333)
                            )
                        }
                    }
                }

                // 6. Seal CTA Button
                Button(
                    onClick = {
                        if (userNameInput.isBlank()) {
                            Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        identityStore.sealTheOath("signed", userNameInput)
                        Toast.makeText(context, "THE OATH HAS BEEN SEALED!", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("btn_seal_the_oath"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A148C)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SEAL THE OATH",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = Color.White
                    )
                }
            } else {
                // ================= SEALED CERTIFICATE VIEW =================
                // Certificate Parchment Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color(0xFFD4AF37), RoundedCornerShape(24.dp))
                        .shadow(8.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF7))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header
                        Image(
                            painter = painterResource(id = R.drawable.ic_feat_oath_1786842491879),
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )

                        Text(
                            text = "FIYM",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 4.sp,
                            color = Color(0xFF8D6E63)
                        )

                        Text(
                            text = "CERTIFICATE OF PERSONAL OATH",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = Color(0xFF2C241E),
                            textAlign = TextAlign.Center
                        )

                        HorizontalDivider(
                            modifier = Modifier.width(140.dp),
                            thickness = 1.5.dp,
                            color = Color(0xFFD4AF37)
                        )

                        Text(
                            text = "This certifies that",
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF795548)
                        )

                        Text(
                            text = savedName.uppercase(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp,
                            color = Color(0xFF311B92)
                        )

                        Text(
                            text = "has willingly made a sacred commitment to the person they choose to become.",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp,
                            color = Color(0xFF4E342E)
                        )

                        // Vow List
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F6EE)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "THE SACRED OATHS:",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = Color(0xFF8D6E63)
                                )

                                coreFoundationOaths.forEach { vow ->
                                    Row(verticalAlignment = Alignment.Top) {
                                        Text("✓", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(vow, fontSize = 12.5.sp, color = Color(0xFF2C241E), fontWeight = FontWeight.Medium)
                                    }
                                }

                                personalOaths.forEach { vow ->
                                    Row(verticalAlignment = Alignment.Top) {
                                        Text("✓", fontWeight = FontWeight.Bold, color = Color(0xFF673AB7), fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(vow, fontSize = 12.5.sp, color = Color(0xFF2C241E), fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }

                        // Witness and Signatures
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(
                                    text = "Witness: Self",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5D4037)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Date: $sealedDate",
                                    fontSize = 11.sp,
                                    color = Color(0xFF8D6E63)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "✍️ $savedName",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic,
                                    fontFamily = FontFamily.Cursive,
                                    color = Color(0xFF1A237E)
                                )
                                HorizontalDivider(modifier = Modifier.width(100.dp), thickness = 1.dp, color = Color(0xFF5D4037))
                                Text(
                                    text = "Authorized Signature",
                                    fontSize = 10.sp,
                                    color = Color(0xFF8D6E63)
                                )
                            }
                        }

                        // Gold Seal Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFF8E1),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MilitaryTech,
                                    contentDescription = null,
                                    tint = Color(0xFFF57F17),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "STATUS: SEALED & SOVEREIGN",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFF57F17),
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }

                // Breaking an Oath Section (Accountability Flow)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Accountability Checkpoint",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E2026)
                            )
                        }

                        Text(
                            text = "Did you slip or break an oath? Face yourself with total honesty instead of hiding from it.",
                            fontSize = 12.5.sp,
                            color = Color(0xFF666666)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showBreakOathDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Acknowledge Slip",
                                    fontSize = 12.sp,
                                    color = Color(0xFFE53935),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    identityStore.unsealForRewrite()
                                    Toast.makeText(context, "Oath opened for revisions", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
                            ) {
                                Text(
                                    text = "Rewrite My Oath",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Broken Records Log
                        if (brokenRecords.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Accountability Reflections Log (${brokenRecords.size}):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5E5470)
                            )
                            brokenRecords.take(3).forEach { record ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "Vow: ${record.oathText}",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFC62828)
                                        )
                                        Text(
                                            text = "\"${record.reason}\"",
                                            fontSize = 11.sp,
                                            fontStyle = FontStyle.Italic,
                                            color = Color(0xFF37474F)
                                        )
                                        Text(
                                            text = record.date,
                                            fontSize = 9.5.sp,
                                            color = Color(0xFF78909C)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Add Personal Oath Dialog
    if (showAddOathDialog) {
        AlertDialog(
            onDismissRequest = { showAddOathDialog = false },
            title = { Text("Add Personal Oath", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Write a sacred vow to yourself:",
                        fontSize = 13.sp,
                        color = Color(0xFF666666)
                    )
                    OutlinedTextField(
                        value = newOathText,
                        onValueChange = { newOathText = it },
                        placeholder = { Text("e.g. I will not let fear make my decisions.") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newOathText.isNotBlank()) {
                            identityStore.addPersonalOath(newOathText.trim())
                            newOathText = ""
                            showAddOathDialog = false
                            Toast.makeText(context, "Added new oath", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddOathDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Acknowledge Broken Oath Dialog
    if (showBreakOathDialog) {
        val allVows = coreFoundationOaths + personalOaths
        AlertDialog(
            onDismissRequest = { showBreakOathDialog = false },
            title = {
                Text("Acknowledge Broken Oath", fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "You broke an oath. Before you continue, acknowledge what happened with zero excuses.",
                        fontSize = 13.sp,
                        color = Color(0xFF37474F)
                    )

                    Text(
                        "Which oath did you break?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2026)
                    )

                    allVows.forEach { vow ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedBrokenOath = vow },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedBrokenOath == vow,
                                onClick = { selectedBrokenOath = vow },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFC62828))
                            )
                            Text(text = vow, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        }
                    }

                    Text(
                        "Why did you break it? (Honest Reflection):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2026)
                    )

                    OutlinedTextField(
                        value = breakReasonText,
                        onValueChange = { breakReasonText = it },
                        placeholder = { Text("What emotional trigger or excuse caused the slip?") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedBrokenOath.isNotBlank() && breakReasonText.isNotBlank()) {
                            identityStore.recordBrokenOath(selectedBrokenOath, breakReasonText)
                            showBreakOathDialog = false
                            selectedBrokenOath = ""
                            breakReasonText = ""
                            Toast.makeText(context, "Accountability acknowledged. Renewing resolve...", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Please select an oath and write your reflection", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("Renew My Oath")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBreakOathDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
