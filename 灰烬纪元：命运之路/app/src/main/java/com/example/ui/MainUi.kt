package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.viewmodel.ActiveScreen
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.HubTab
import kotlin.random.Random

// Visual Colors aligned with Dark Medieval Western Fantasy (Immersive UI theme)
val SlateBackground = Color(0xFF0F0F0D)  // Deep Obsidian/Jet Black
val ParchmentDark = Color(0xFF1A1A17)    // Dark Charcoal / Base Header/Footer wood
val CardParchmentBase = Color(0xFF1E1C18)// Stoned Charcoal parchment surface
val ParchmentLight = Color(0xFFE0D8B0)   // Silk Warm Gold Text
val EmberGold = Color(0xFFB8860B)        // Muted Imperial Dark Gold Accent
val AmberYellow = Color(0xFFFFD700)      // Bright Solar Gold Highlights
val CrimsonBlood = Color(0xFF8B0000)     // Deep Dried Crimson / Wound Red
val DeepManaGreen = Color(0xFF4169E1)    // Enchanted Mana Royal Blue Bar (formerly Sacred Green)
val ItemGray = Color(0xFF26221B)         // Walnut soil / Subcard background shade
val AccentBorder = Color(0xFF3D3321)     // High-context border gold dust rim

@Composable
fun MainUiView(viewModel: GameViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        // Shared Ambient Dark Radial Light Glow Effect (radial-gradient(#2A241B to #0F0F0D))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF2A241B), Color(0xFF0F0F0D)),
                        radius = 1600f
                    )
                )
        )

        // Screen routing State Machine
        when (viewModel.currentScreen) {
            ActiveScreen.WELCOME -> WelcomeScreen(viewModel)
            ActiveScreen.CHARACTER_CREATE -> CharacterCreatorScreen(viewModel)
            ActiveScreen.MAIN_HUB -> MainHubDashboard(viewModel)
        }
    }
}

// ------------------------------------------------------------
// 1. Welcome / Save Slot Select View
// ------------------------------------------------------------
@Composable
fun WelcomeScreen(viewModel: GameViewModel) {
    val slots by viewModel.dbSaveSlots.collectAsState()
    var deleteConfirmSlotId by remember { mutableStateOf<Int?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Main Logo/Title Group
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "游戏标志",
                tint = EmberGold,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "灰烬纪元：命运之路",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AmberYellow,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Ash Epoch: Path of Fate",
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = ParchmentLight,
                letterSpacing = 1.sp
            )
        }

        // Mid Content: Slated sheepskin Save game folders
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "— 选择你的宿命之卷 —",
                fontSize = 14.sp,
                color = ParchmentLight,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Loop 3 slot numbers
            for (slotId in 1..3) {
                val currentSlot = slots.find { it.slotId == slotId }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ParchmentDark)
                        .border(1.dp, if (currentSlot != null) EmberGold else ItemGray, RoundedCornerShape(8.dp))
                        .clickable { viewModel.loadGameSlot(slotId) }
                        .padding(16.dp)
                ) {
                    if (currentSlot != null) {
                        // Display save attributes 
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(EmberGold)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = currentSlot.characterName,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "LV.${currentSlot.level}",
                                        fontSize = 12.sp,
                                        color = AmberYellow,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                            .background(Color(0x33FFA800), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "流派：${currentSlot.pathName} • 种族：${currentSlot.raceName}",
                                    fontSize = 13.sp,
                                    color = ParchmentLight
                                )
                                Text(
                                    text = "历险地点：${getRegionNameById(currentSlot.currentRegionId)} • 财富：${currentSlot.gold} 金币",
                                    fontSize = 12.sp,
                                    color = ParchmentLight,
                                    fontWeight = FontWeight.Light
                                )
                            }
                            // Delete button
                            IconButton(
                                onClick = { deleteConfirmSlotId = slotId },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "删除存档",
                                    tint = CrimsonBlood
                                )
                            }
                        }
                    } else {
                        // Empty Slot Indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "空存档槽",
                                tint = ParchmentLight,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "档案卷 ${slotId}：[未觉醒虚境]",
                                    fontSize = 16.sp,
                                    color = ParchmentLight,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "点击此处，建立全新的宿命神迹...",
                                    fontSize = 12.sp,
                                    color = ParchmentLight,
                                    fontWeight = FontWeight.Light
                                )
                            }
                        }
                    }
                }
            }
        }

        // Welcome Footer Credits
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "『天命指引前行，黑潮终将平息』",
                fontSize = 11.sp,
                color = ParchmentLight,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "版本 v1.4.2 — 100% 独立单机离线状态引擎",
                fontSize = 10.sp,
                color = ItemGray
            )
        }
    }

    // Delete slot confirmation dialog
    if (deleteConfirmSlotId != null) {
        AlertDialog(
            onDismissRequest = { deleteConfirmSlotId = null },
            title = { Text("绝灭卷宗？", color = Color.White) },
            text = { Text("你确定要永久撕毁档案 ${deleteConfirmSlotId} 的天命旅途吗？此行为无法撤销。", color = ParchmentLight) },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteConfirmSlotId?.let { viewModel.deleteSaveSlot(it) }
                        deleteConfirmSlotId = null
                    }
                ) {
                    Text("毁弃封存", color = CrimsonBlood, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmSlotId = null }) {
                    Text("留存思索", color = Color.White)
                }
            },
            containerColor = ParchmentDark,
            titleContentColor = Color.White
        )
    }
}

// ------------------------------------------------------------
// 2. Character Creation Stream (命运殿堂)
// ------------------------------------------------------------
@Composable
fun CharacterCreatorScreen(viewModel: GameViewModel) {
    var step by remember { mutableStateOf(1) } // 1=Race Selection, 2=Path Selection, 3=Attributes Allocation
    
    var tempName by remember { mutableStateOf("") }
    var selectedRace by remember { mutableStateOf(Race.HUMAN) }
    var selectedPath by remember { mutableStateOf(StylePath.MAGIC_PATH) }
    var portraitIdx by remember { mutableStateOf(0) }
    
    // Sliders & Custom visual details state
    var sliderSkin by remember { mutableStateOf(0.4f) }
    var sliderHair by remember { mutableStateOf(0.2f) }
    var hasTearsAndScars by remember { mutableStateOf(false) }
    
    // Initial Stat point pool
    var pointsToAllocate by remember { mutableStateOf(5) }
    val baseAttributes = remember(selectedRace) {
        mutableStateMapOf(
            "STR" to 10 + selectedRace.strBonus,
            "DEX" to 10 + selectedRace.dexBonus,
            "CON" to 10 + selectedRace.conBonus,
            "INT" to 10 + selectedRace.intBonus,
            "WIL" to 10 + selectedRace.wilBonus,
            "SPR" to 10 + selectedRace.sprBonus,
            "PER" to 10 + selectedRace.perBonus,
            "CHA" to 10 + selectedRace.chaBonus
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        // Creator Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (step > 1) {
                        step -= 1
                    } else {
                        viewModel.currentScreen = ActiveScreen.WELCOME
                    }
                }
            ) {
                Icon(Icons.Default.ArrowBack, "返回", tint = Color.White)
            }
            Text(
                text = "命运殿堂 • 塑魂仪 (${step}/3)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AmberYellow
            )
            Text(
                text = "SLOT ${viewModel.selectedSlotId}",
                fontSize = 12.sp,
                color = ParchmentLight,
                modifier = Modifier
                    .background(Color.DarkGray, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large creation tabs
        when (step) {
            1 -> {
                // RACE CHOOSE
                Column(modifier = Modifier.weight(1f)) {
                    Text("第一步：选择出身种族（Race Selection）", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("点击星座宿愿，了解各族的天赋异禀：", fontSize = 12.sp, color = ParchmentLight)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Race selection buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Race.values().forEach { r ->
                            val isSelected = selectedRace == r
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) EmberGold else ParchmentDark)
                                    .border(1.dp, if (isSelected) AmberYellow else ItemGray, RoundedCornerShape(8.dp))
                                    .clickable { selectedRace = r }
                                    .padding(vertical = 12.dp)
                            ) {
                                Text(
                                    text = r.displayName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Detailed Card
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ParchmentDark)
                            .border(1.dp, ItemGray, RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        item {
                            Text("【${selectedRace.displayName} • 背景渊源】", fontSize = 16.sp, color = AmberYellow, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(selectedRace.description, fontSize = 13.sp, color = Color.White, lineHeight = 18.sp)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("【初始行装】", fontSize = 12.sp, color = EmberGold, fontWeight = FontWeight.Bold)
                            Text(selectedRace.basicClothes, fontSize = 13.sp, color = ParchmentLight)

                            Spacer(modifier = Modifier.height(16.dp))
                            Text("【神迹天赋与奇术】", fontSize = 12.sp, color = EmberGold, fontWeight = FontWeight.Bold)
                            selectedRace.traits.forEach { tr ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Star, "天赋", tint = AmberYellow, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(tr, fontSize = 12.sp, color = Color.White)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            Text("【外观捏脸细调】", fontSize = 14.sp, color = AmberYellow)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text("肤色冷暖调: ${(sliderSkin*100).toInt()}%", fontSize = 11.sp, color = ParchmentLight)
                            Slider(value = sliderSkin, onValueChange = { sliderSkin = it }, colors = SliderDefaults.colors(thumbColor = EmberGold, activeTrackColor = EmberGold))
                            
                            Text("发色明暗比: ${(sliderHair*100).toInt()}%", fontSize = 11.sp, color = ParchmentLight)
                            Slider(value = sliderHair, onValueChange = { sliderHair = it }, colors = SliderDefaults.colors(thumbColor = EmberGold, activeTrackColor = EmberGold))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = hasTearsAndScars,
                                    onCheckedChange = { hasTearsAndScars = it },
                                    colors = CheckboxDefaults.colors(checkedColor = EmberGold)
                                )
                                Text("附加中世纪战损刀疤/刺青纹路", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
            2 -> {
                // PATH CHOOSE
                Column(modifier = Modifier.weight(1f)) {
                    Text("第二步：选择修炼流派（Class Path）", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("流派锁定后事关你后世战术格斗风格，请细加甄别选配：", fontSize = 12.sp, color = ParchmentLight)
                    Spacer(modifier = Modifier.height(16.dp))

                    StylePath.values().forEach { path ->
                        val isSelected = selectedPath == path
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0x22E28514) else ParchmentDark)
                                .border(1.dp, if (isSelected) EmberGold else ItemGray, RoundedCornerShape(8.dp))
                                .clickable { selectedPath = path }
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when (path) {
                                            StylePath.MAGIC_PATH -> Icons.Default.Star
                                            StylePath.CULTIVATION_PATH -> Icons.Default.Refresh
                                            StylePath.BODY_TEMPERING_PATH -> Icons.Default.Person
                                        },
                                        contentDescription = path.displayName,
                                        tint = EmberGold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = path.displayName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, "已选", tint = AmberYellow)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = path.description,
                                fontSize = 12.sp,
                                color = ParchmentLight,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "核心要素：主 [${path.primaryAttr}] • 辅 [${path.secondaryAttr}]",
                                fontSize = 12.sp,
                                color = AmberYellow,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
            3 -> {
                // ATTRIBUTE POINTS ALLOCATION
                Column(modifier = Modifier.weight(1f)) {
                    Text("第三步：塑命神授与命名", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("可分配自由魂授点，为你的英雄命名记录碑帖：", fontSize = 12.sp, color = ParchmentLight)
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        placeholder = { Text("输入宿命勇者姓名 (默认：命定之子)", color = Color.Gray) },
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ParchmentDark),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = ParchmentDark,
                            unfocusedContainerColor = ParchmentDark,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = EmberGold,
                            focusedIndicatorColor = EmberGold
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("天授自由值：", fontSize = 14.sp, color = Color.White)
                        Text(
                            text = "$pointsToAllocate 点",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmberGold,
                            modifier = Modifier
                                .background(Color(0xFF3E2D23), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        val itemsList = baseAttributes.keys.toList()
                        items(itemsList) { attrKey ->
                            val value = baseAttributes[attrKey] ?: 10
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ParchmentDark)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    val fullName = when (attrKey) {
                                        "STR" -> "力量 (STR) - 物理近斩 & 负重"
                                        "DEX" -> "敏捷 (DEX) - 先攻回转 & 闪避"
                                        "CON" -> "体质 (CON) - 体魄生命上限"
                                        "INT" -> "智力 (INT) - 秘法术伤上限"
                                        "WIL" -> "意志 (WIL) - 控御恐惧抗性"
                                        "SPR" -> "灵力 (SPR) - 真罡元气亲和"
                                        "PER" -> "感知 (PER) - 阱防勘测视野"
                                        "CHA" -> "魅力 (CHA) - 酒肆社交好感"
                                        else -> attrKey
                                    }
                                    Text(fullName, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Minus
                                    IconButton(
                                        onClick = {
                                            val currentBase = 10 + (when (attrKey) {
                                                "STR" -> selectedRace.strBonus
                                                "DEX" -> selectedRace.dexBonus
                                                "CON" -> selectedRace.conBonus
                                                "INT" -> selectedRace.intBonus
                                                "WIL" -> selectedRace.wilBonus
                                                "SPR" -> selectedRace.sprBonus
                                                "PER" -> selectedRace.perBonus
                                                "CHA" -> selectedRace.chaBonus
                                                else -> 0
                                            })
                                            if (value > currentBase) {
                                                baseAttributes[attrKey] = value - 1
                                                pointsToAllocate += 1
                                            }
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("-", color = Color.LightGray, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Text(
                                        "$value",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberYellow,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )

                                    // Plus
                                    IconButton(
                                        onClick = {
                                            if (pointsToAllocate > 0) {
                                                baseAttributes[attrKey] = value + 1
                                                pointsToAllocate -= 1
                                            }
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Add, "加", tint = Color.LightGray)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Footer Button
        Button(
            onClick = {
                if (step < 3) {
                    step += 1
                } else {
                    // Create and enter Hub tab
                    viewModel.createNewCharacter(tempName, selectedRace, selectedPath, portraitIdx)
                    baseAttributes.forEach { (k, v) ->
                        viewModel.attributes[k] = v
                    }
                    viewModel.attributePointsLeft = pointsToAllocate
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = EmberGold),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_button"),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (step < 3) "凝结魂气，继续前进" else "铸魂归位，觉醒降临！",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

// ------------------------------------------------------------
// 3. Main Board HUD Dashboard
// ------------------------------------------------------------
@Composable
fun MainHubDashboard(viewModel: GameViewModel) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        containerColor = SlateBackground,
        topBar = { TopStatusBar(viewModel) },
        bottomBar = { BottomNavBar(viewModel) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Screen switching animations 
            AnimatedContent(
                targetState = viewModel.currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "hub_tabs"
            ) { targetTab ->
                Box(modifier = Modifier.fillMaxSize()) {
                    when (targetTab) {
                        HubTab.WORLD_MAP -> WorldMapTab(viewModel)
                        HubTab.TAVERN_NPC -> TavernTab(viewModel)
                        HubTab.COMBAT_ARENA -> CombatTab(viewModel)
                        HubTab.BLACKSMITH_FORGE -> ForgeTab(viewModel)
                        HubTab.HERO_PROFILE -> HeroProfileTab(viewModel)
                        HubTab.MANUAL_GUIDE -> GameGuideTab(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomGridProgressBar(
    progress: Float,
    gradientColors: List<Color>,
    trackColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(trackColor)
    ) {
        val coercedProgress = progress.coerceIn(0f, 1f)
        if (coercedProgress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(coercedProgress)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Brush.horizontalGradient(gradientColors))
            )
        }
    }
}

// Top dashboard ribbon (Immersive UI Style with Fantasy Flare)
@Composable
fun TopStatusBar(viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParchmentDark)
            .statusBarsPadding()
            .border(width = 1.dp, color = AccentBorder)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile & Stats
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Circular Portrait Mock (High Elf / Archer design inspired)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SlateBackground)
                        .border(2.dp, EmberGold, CircleShape)
                ) {
                    Icon(
                        imageVector = when (viewModel.heroPath) {
                            StylePath.MAGIC_PATH -> Icons.Default.Star
                            StylePath.CULTIVATION_PATH -> Icons.Default.Refresh
                            StylePath.BODY_TEMPERING_PATH -> Icons.Default.Person
                        },
                        contentDescription = "Avatar",
                        tint = AmberYellow,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = viewModel.heroName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberYellow,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LV.${viewModel.heroLevel}",
                            fontSize = 10.sp,
                            color = SlateBackground,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier
                                .background(AmberYellow, RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${viewModel.heroRace.displayName} · ${viewModel.heroPath.displayName}",
                        fontSize = 11.sp,
                        color = ParchmentLight.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Save Progress and Close Game Option
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        viewModel.saveGameProgress()
                        viewModel.triggerLog("💾 天命卷帙已自动校对封存！")
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Check, "实时保存", tint = AmberYellow)
                }
                
                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = {
                        viewModel.currentScreen = ActiveScreen.WELCOME
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.ExitToApp, "退入星空", tint = ParchmentLight.copy(alpha = 0.6f))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Health, Spirit, and Money ribbons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Health bar with Red Flame Gradient
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "体力 (HP)", 
                        fontSize = 10.sp, 
                        color = CrimsonBlood, 
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${viewModel.playerHpCurrent}/${viewModel.playerHpMax}", 
                        fontSize = 10.sp, 
                        color = ParchmentLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                CustomGridProgressBar(
                    progress = viewModel.playerHpCurrent.toFloat() / viewModel.playerHpMax,
                    gradientColors = listOf(Color(0xFF500000), Color(0xFF8B0000)),
                    trackColor = Color(0xFF2D2820),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
            }

            // Mana / Energy bar with Royal Blue Gradient
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (viewModel.heroPath == StylePath.CULTIVATION_PATH) "真气 (Qi)" else "神识 (MP)",
                        fontSize = 10.sp,
                        color = DeepManaGreen,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${viewModel.playerMpCurrent}/${viewModel.playerMpMax}", 
                        fontSize = 10.sp, 
                        color = ParchmentLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                CustomGridProgressBar(
                    progress = viewModel.playerMpCurrent.toFloat() / viewModel.playerMpMax,
                    gradientColors = listOf(Color(0xFF1C2951), Color(0xFF4169E1)),
                    trackColor = Color(0xFF2D2820),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                )
            }

            // Wealth Wallet
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xFF26221B), RoundedCornerShape(8.dp))
                    .border(width = 1.dp, color = AccentBorder, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text("🪙", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${viewModel.heroGold}G", 
                    fontSize = 11.sp, 
                    color = AmberYellow, 
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Bottom styled action button nodes (Immersive UI Style)
@Composable
fun BottomNavBar(viewModel: GameViewModel) {
    NavigationBar(
        containerColor = Color(0xFF121210), // Obsidian Dark Slate for navigation
        tonalElevation = 8.dp,
        modifier = Modifier.border(width = 1.dp, color = ItemGray)
    ) {
        val tabList = listOf(
            Triple(HubTab.WORLD_MAP, "埃瑟兰大图", Icons.Default.Home),
            Triple(HubTab.TAVERN_NPC, "酒肆关系", Icons.Default.Favorite),
            Triple(HubTab.COMBAT_ARENA, "格斗战棋", Icons.Default.Warning),
            Triple(HubTab.BLACKSMITH_FORGE, "神兵熔炉", Icons.Default.Settings),
            Triple(HubTab.HERO_PROFILE, "英雄境界", Icons.Default.Person),
            Triple(HubTab.MANUAL_GUIDE, "传古手札", Icons.Default.Info)
        )

        tabList.forEach { (tab, title, icon) ->
            val isSelected = viewModel.currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { viewModel.currentTab = tab },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isSelected) AmberYellow else ParchmentLight.copy(alpha = 0.5f)
                    )
                },
                label = {
                    Text(
                        text = title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) AmberYellow else ParchmentLight.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFF26221B) // Elegant dark indicator pill
                )
            )
        }
    }
}

// Helper region naming mapping
fun getRegionNameById(id: String): String {
    return when (id) {
        "dawnhaven" -> "晨曦之镇"
        "silvercrown" -> "银冠城邦"
        "ironspine" -> "铁脊山脉"
        "redwaste" -> "赤荒平原"
        "emerald_labyrinth" -> "翡翠迷境"
        "black_wave_waste" -> "黑潮废土"
        else -> "未知秘境"
    }
}

// ------------------------------------------------------------
// Tab 3.1: World Map View & Dynamic Weather Cycle Engine
// ------------------------------------------------------------
@Composable
fun WorldMapTab(viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Daily Sky Indicators Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CardParchmentBase)
                .border(1.dp, AccentBorder, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📖 历险日历板 (天数: ${viewModel.daysElapsed})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberYellow
                    )
                    Text(
                        text = "时段: ${viewModel.currentTimeOfDay.displayName}",
                        fontSize = 12.sp,
                        color = EmberGold,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Day night icon
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(ItemGray, RoundedCornerShape(6.dp))
                            .border(1.dp, AccentBorder, RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (viewModel.currentTimeOfDay == TimeOfDay.DAY) Icons.Default.Star else Icons.Default.Lock,
                            contentDescription = "sky",
                            tint = EmberGold
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "天气当前：${viewModel.currentWeather.displayName} (${viewModel.currentWeather.desc})",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "光环与战斗修正：移动扣AP增加 ${viewModel.currentWeather.movementPenalty} | 先攻变幅: ${viewModel.currentTimeOfDay.hitModifier}%",
                            fontSize = 11.sp,
                            color = ParchmentLight.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Active triggers weather shift
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            viewModel.incrementTimeStep(1)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ItemGray),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("冥想打坐 (时宿轮转)", fontSize = 12.sp, color = ParchmentLight)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "季节:【${viewModel.currentSeason.displayName}】",
                        fontSize = 12.sp,
                        color = AmberYellow,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Active Alert Warning for beast invasions count
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (viewModel.isBeastTideActive) CrimsonBlood else Color(0xFF101925))
                .border(
                    1.dp,
                    if (viewModel.isBeastTideActive) AmberYellow else AccentBorder,
                    RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "beast",
                        tint = if (viewModel.isBeastTideActive) AmberYellow else Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (viewModel.isBeastTideActive) "🚨 全域警报：大型黑潮兽灾袭来！" else "📡 萨满灵骨预兆（兽力监测）",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (viewModel.isBeastTideActive)
                        "此时任意探索触发极高倍率的【区域领主级 BOSS】大战！击退巨兽可获取传说神话大料！"
                    else
                        "距离下一波大荒巨兽在主大平原觉醒，还剩约 [${viewModel.beastTideCountdown}] 轮历险结印步数...",
                    fontSize = 11.sp,
                    color = ParchmentLight
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("🗺️ 【埃瑟兰大陆 · 部落与城帮快移】", fontSize = 15.sp, color = AmberYellow, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        // List 6 geographical centers
        MapRegion.values().forEach { r ->
            val isCurrent = viewModel.currentRegion == r
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardParchmentBase)
                    .border(
                        1.dp,
                        if (isCurrent) AmberYellow else AccentBorder,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(r.themeColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = r.displayName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "危级: ${r.dangerLevel}",
                            fontSize = 11.sp,
                            color = ParchmentLight,
                            modifier = Modifier
                                .background(Color(0x22FFFFFF), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }

                    if (isCurrent) {
                        Text(
                            text = "📌 当前在此",
                            fontSize = 11.sp,
                            color = EmberGold,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Button(
                            onClick = {
                                viewModel.travelToRegion(r)
                                viewModel.triggerLog("🐎 你率引行囊旅行至 【${r.displayName}】！")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (r.dangerLevel <= viewModel.heroLevel * 8 + 5) EmberGold else Color.DarkGray),
                            modifier = Modifier.height(30.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                        ) {
                            Text("长途起驾", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = r.description,
                    fontSize = 12.sp,
                    color = ParchmentLight,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("📬 区域历险委托版（Quest Active）", fontSize = 15.sp, color = AmberYellow, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        // Display regional quests
        val currentQuests = viewModel.questLogByRegion.value.filter { it.location == viewModel.currentRegion.id }
        if (currentQuests.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardParchmentBase)
                    .border(1.dp, AccentBorder, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("此地暂时风平浪静，无贴纸揭榜委派宿愿。", fontSize = 12.sp, color = ParchmentLight.copy(alpha = 0.7f))
            }
        } else {
            currentQuests.forEach { q ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(CardParchmentBase)
                        .border(1.dp, AccentBorder, RoundedCornerShape(6.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = q.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(q.desc, fontSize = 11.sp, color = ParchmentLight, lineHeight = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "酬劳：${q.goldReward}G | 历练：+${q.expReward}",
                            fontSize = 11.sp,
                            color = AmberYellow
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { viewModel.toggleQuestStatus(q) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (q.status) {
                                "未接取" -> EmberGold
                                "进行中" -> Color.Gray
                                "可交付" -> Color(0xFF4CAF50)
                                else -> Color.DarkGray
                            }
                        ),
                        modifier = Modifier.height(30.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = when (q.status) {
                                "未接取" -> "揭榜"
                                "进行中" -> "递交中.."
                                "可交付" -> "交付"
                                else -> "已尘封"
                            },
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------
// Tab 3.2: Tavern NPC & Relations & Complete Marriage Simulator
// ------------------------------------------------------------
@Composable
fun TavernTab(viewModel: GameViewModel) {
    val context = LocalContext.current
    var activeChatNpcId by remember { mutableStateOf<String?>(null) }
    var giftMenuNpcId by remember { mutableStateOf<String?>(null) }
    
    val npcLists = remember {
        listOf(
            NPC(id = "alaric", name = "阿拉里克", title = "银冠圣殿骑士长", race = Race.HUMAN, initialOpinion = 10, location = "silvercrown", voiceLines = listOf("为了帝国的余晖，誓死捍卫裂谷！", "圣光啊，请指引这片被黑潮焦灼的人世格调。"), lovedGifts = listOf("饰铠", "铁剑"), dislikedGifts = listOf("烈酒")),
            NPC(id = "moira", name = "莫伊拉", title = "钢脉熔铁炉匠师", race = Race.DWARF, initialOpinion = 5, location = "ironspine", voiceLines = listOf("呼啦！想要我给你修补佩剑？拿上玄原铁矿石来！", "来！干完这一杯黑啤，看你个头见长！"), lovedGifts = listOf("烈酒", "原石"), dislikedGifts = listOf("饰铠")),
            NPC(id = "thrall", name = "萨尔·碎颅者", title = "赤荒战魂大萨满", race = Race.ORC, initialOpinion = 0, location = "redwaste", voiceLines = listOf("荣耀高过血肉。我的先祖在大平原烈风中哭喊。", "萨满图腾能洗去世俗卑污者体表的暗毒。"), lovedGifts = listOf("骨饰", "兽骨"), dislikedGifts = listOf("草药")),
            NPC(id = "elenwe", name = "爱莲薇", title = "古树迷叶长生祭司", race = Race.ELF, initialOpinion = 15, location = "emerald_labyrinth", voiceLines = listOf("人类，古木之歌正如同我们的容颜般消退凋落..", "你需要来些月华星芒凝结的甘露滋养意志吗？"), lovedGifts = listOf("魔法石", "月光瓶"), dislikedGifts = listOf("刀枪"))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("🍻 【星光炉火酒馆 • 英杰风云榜】", fontSize = 16.sp, color = AmberYellow, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("游戏拥有好感关联系统，送礼、言语应答或共同迎敌可以提升关系至【灵魂伴侣】进而缔结婚约：", fontSize = 11.sp, color = ParchmentLight)
        Spacer(modifier = Modifier.height(16.dp))

        npcLists.forEach { npc ->
            val points = viewModel.npcRelations[npc.id] ?: 0
            val activeRating = when {
                points >= 76 -> "灵魂伴侣 (Soulmate) 💍"
                points >= 51 -> "死党挚友 (Best Friend)"
                points >= 26 -> "亲密友人 (Trustworthy)"
                points >= 1 -> "友善之交"
                points >= -50 -> "中立冷淡"
                else -> "敌对追杀 💀"
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ParchmentDark)
                    .border(width = 1.dp, color = ItemGray, shape = RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = npc.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = npc.title,
                                fontSize = 11.sp,
                                color = EmberGold,
                                modifier = Modifier
                                    .background(Color(0x33DE780D), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "驻留地：${getRegionNameById(npc.location)} • 本族：${npc.race.displayName}",
                            fontSize = 11.sp,
                            color = ParchmentLight
                        )
                    }

                    // Relation meter size
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1E2A38), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "好感度: $points",
                            fontSize = 12.sp,
                            color = if (points > 0) Color(0xFF4CAF50) else Color.LightGray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "关系定位：$activeRating",
                    fontSize = 12.sp,
                    color = AmberYellow,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // NPC control deck
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { activeChatNpcId = npc.id },
                        colors = ButtonDefaults.buttonColors(containerColor = ItemGray),
                        modifier = Modifier
                            .height(28.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, "对话", tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("倾囊闲谈", fontSize = 11.sp, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Button(
                        onClick = { giftMenuNpcId = npc.id },
                        colors = ButtonDefaults.buttonColors(containerColor = ItemGray),
                        modifier = Modifier
                            .height(28.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, "赠送", tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("馈赠薄礼", fontSize = 11.sp, color = Color.White)
                        }
                    }

                    // soulmate marriage proposal simulator trigger
                    if (points >= 76) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                viewModel.triggerLog("💍 仪式启动！你与【${npc.name}】在晨曦镇大主神圣壁炉前正式结发为夫妻！获得永久伴侣BUFF（濒死攻击+20%防御提升）！")
                                viewModel.npcRelations[npc.id] = 100 // Peak Soul connection
                                viewModel.saveGameProgress()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonBlood),
                            modifier = Modifier
                                .height(28.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Favorite, "婚礼", tint = Color.White, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("宿命联姻 💖", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Active Chat dialogues logic
        if (activeChatNpcId != null) {
            val npc = npcLists.find { it.id == activeChatNpcId }
            if (npc != null) {
                AlertDialog(
                    onDismissRequest = { activeChatNpcId = null },
                    title = { Text("与 [${npc.name}] 对话中...", color = Color.White) },
                    text = {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black, RoundedCornerShape(6.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "“ ${npc.voiceLines.random()} ”",
                                    fontSize = 13.sp,
                                    color = EmberGold,
                                    lineHeight = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("选择你的对答契机：", fontSize = 12.sp, color = ParchmentLight)
                        }
                    },
                    confirmButton = {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = {
                                    viewModel.npcRelations[npc.id] = ((viewModel.npcRelations[npc.id] ?: 0) + 12).coerceAtMost(100)
                                    viewModel.triggerLog("【对答得体】与 ${npc.name} 意气相投，好感+12点。")
                                    activeChatNpcId = null
                                    viewModel.saveGameProgress()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ItemGray),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("『迎合风道』：世人多卑微，惟英杰之气与你相通。", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    viewModel.npcRelations[npc.id] = ((viewModel.npcRelations[npc.id] ?: 0) + 15).coerceAtMost(100)
                                    viewModel.triggerLog("【倾囊相托】豪情承诺拯救翡翠古树万年元气！${npc.name} 十分动容，好感+15点。")
                                    activeChatNpcId = null
                                    viewModel.saveGameProgress()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ItemGray),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("『壮哉豪气』：既然黑潮将至，就由我的重铁佩剑荡平虚妄！", fontSize = 12.sp)
                            }
                        }
                    },
                    containerColor = ParchmentDark,
                    titleContentColor = Color.White
                )
            }
        }

        // Gift drop down menus logic
        if (giftMenuNpcId != null) {
            val npc = npcLists.find { it.id == giftMenuNpcId }
            if (npc != null) {
                AlertDialog(
                    onDismissRequest = { giftMenuNpcId = null },
                    title = { Text("向 [${npc.name}] 递呈珍品", color = Color.White) },
                    text = {
                        Column {
                            Text("你包裹行囊中的物品如下，选择投其所好的礼物将引起巨大触动：", fontSize = 12.sp, color = ParchmentLight)
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            viewModel.backpack.value.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.giftToNpc(npc.id, item.name)
                                            giftMenuNpcId = null
                                        }
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🎁 ${item.name} (数量: ${item.count})", color = Color.White, fontSize = 12.sp)
                                    Text("递交 >>", color = AmberYellow, fontSize = 12.sp)
                                }
                            }
                            if (viewModel.backpack.value.isEmpty()) {
                                Text("行口袋中无适用品。快去大地图快移探索、采集矿石草药或打擂夺宝吧！", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { giftMenuNpcId = null }) { Text("折回行口袋子", color = Color.White) }
                    },
                    containerColor = ParchmentDark
                )
            }
        }
    }
}

// ------------------------------------------------------------
// Tab 3.3: Combat Battlefield View (Standard Grid Arena)
// ------------------------------------------------------------
@Composable
fun CombatTab(viewModel: GameViewModel) {
    val combatActive = viewModel.inCombatMode
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (!combatActive) {
            // Out of battle - Select Battle Arena Mode
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "角力",
                    tint = EmberGold,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "— 命运角斗战棋机 —",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberYellow
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "在此可以激发神识，开启一个严格回合制的 10×10 网格模拟死决：",
                    fontSize = 12.sp,
                    color = ParchmentLight,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                if (viewModel.lastCombatRewardSummary.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF221A17))
                            .border(1.dp, EmberGold, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = viewModel.lastCombatRewardSummary,
                            fontSize = 12.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Alert overlay for beast tide boss state
                val isTide = viewModel.isBeastTideActive
                
                Button(
                    onClick = { viewModel.buildTacticalCombat(isBossSelection = false) },
                    colors = ButtonDefaults.buttonColors(containerColor = EmberGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("⚔️ 开启普通野怪遭遇战 (收获金铁原石)", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.buildTacticalCombat(isBossSelection = true) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isTide) Color.Red else CrimsonBlood),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = if (isTide) "👹【黑潮天命BOSS】极意决死战 (双倍暴装!!)" else "🐲 试炼挑战领主BOSS 决胜",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // IN COMBAT - 10x10 isometric style tactical grid
            val player = viewModel.combatPlayer
            val enemy = viewModel.combatEnemy
            
            if (player != null && enemy != null) {
                // Main stats duel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Player hp simple
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${player.name}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("HP: ${player.currentHp}/${player.maxHp}", fontSize = 11.sp, color = CrimsonBlood)
                        Spacer(modifier = Modifier.height(2.dp))
                        LinearProgressIndicator(
                            progress = { (player.currentHp.toFloat() / player.maxHp).coerceIn(0f, 1f) },
                            color = CrimsonBlood,
                            trackColor = Color.DarkGray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("VS", fontSize = 14.sp, color = AmberYellow, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(16.dp))
                    // Enemy hp simple
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${enemy.name}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                        Text("HP: ${enemy.currentHp}/${enemy.maxHp}", fontSize = 11.sp, color = CrimsonBlood, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(2.dp))
                        LinearProgressIndicator(
                            progress = { (enemy.currentHp.toFloat() / enemy.maxHp).coerceIn(0f, 1f) },
                            color = CrimsonBlood,
                            trackColor = Color.DarkGray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // AP gauge count
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF221E1B), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("🔋 我方行动点 (AP): ${player.ap}/10", fontSize = 11.sp, color = AmberYellow, fontWeight = FontWeight.Bold)
                    Text("🛡️ 战役轮次: ${viewModel.combatRoundCount}", fontSize = 11.sp, color = ParchmentLight)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 10x10 grid canvas map 
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateBackground)
                        .border(1.dp, ItemGray)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (y in 0 until 10) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                for (x in 0 until 10) {
                                    val cell = viewModel.combatGrid.value.find { it.x == x && it.y == y }
                                    val isUser = player.x == x && player.y == y
                                    val isOpp = enemy.x == x && enemy.y == y
                                    
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .background(
                                                color = when {
                                                    isUser -> player.spriteColor
                                                    isOpp -> enemy.spriteColor
                                                    else -> cell?.terrain?.color ?: SlateBackground
                                                }
                                            )
                                            .border(0.5.dp, Color(0x33FFFFFF))
                                            .clickable {
                                                if (!isUser && !isOpp) {
                                                    viewModel.moveCombatPlayer(x, y)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        when {
                                            isUser -> {
                                                Text("🧙", fontSize = 14.sp)
                                            }
                                            isOpp -> {
                                                Text(if (enemy.name.contains("BOSS")) "👺" else "👾", fontSize = 14.sp)
                                            }
                                            cell?.terrain == TerrainType.WALL_ROCK -> {
                                                Text("🪨", fontSize = 12.sp)
                                            }
                                            cell?.terrain == TerrainType.LAVA_FIRE -> {
                                                Text("🔥", fontSize = 10.sp)
                                            }
                                            cell?.terrain == TerrainType.ICE_FLOE -> {
                                                Text("❄️", fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Targeted Body Parts Dashboard (STONESHARPD INJURY ENGINE 复刻)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ParchmentDark, RoundedCornerShape(8.dp))
                        .border(1.dp, ItemGray)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎯 【部位校准瞄准器 - 击碎判定】", fontSize = 11.sp, color = AmberYellow, fontWeight = FontWeight.Bold)
                        Text("肢损对敌效能产生削弱", fontSize = 9.sp, color = ParchmentLight)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // horizontal selector scroll of 7 parts
                    Row(
                        modifier = ModifierHorizontalScrollable(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BodyPart.values().forEachIndexed { index, part ->
                            val isChosen = viewModel.selectedTargetPartIdx == index
                            val partHp = enemy.bodyParts[index]
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isChosen) EmberGold else Color(0xFF1F1B19))
                                    .border(1.dp, if (isChosen) AmberYellow else Color.Gray, RoundedCornerShape(4.dp))
                                    .clickable { viewModel.selectedTargetPartIdx = index }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(part.displayName, fontSize = 10.sp, color = if (isChosen) Color.Black else Color.White, fontWeight = FontWeight.Bold)
                                    Text("${partHp.currentHp.toInt()}/${partHp.maxHp.toInt()}", fontSize = 9.sp, color = if (isChosen) Color.Black else ParchmentLight)
                                    if (partHp.isCrippled) {
                                        Text("残❌", fontSize = 8.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Combat console logs
                Spacer(modifier = Modifier.height(6.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black)
                        .padding(6.dp)
                ) {
                    items(viewModel.combatLog.value) { log ->
                        Text(text = log, fontSize = 11.sp, color = ParchmentLight, modifier = Modifier.padding(vertical = 1.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tactical action buttons deck
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.executeCombatPhysAttack() },
                        colors = ButtonDefaults.buttonColors(containerColor = EmberGold),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text("🗡️ 砍刹 (6 AP)", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val activeSkill = viewModel.customSkills.value.firstOrNull()
                            if (activeSkill != null) {
                                viewModel.executeCombatSkill(activeSkill)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonBlood),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text("🔮 释招 (${viewModel.customSkills.value.firstOrNull()?.apCost ?: 5} AP)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.drinkCombatPotion() },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepManaGreen),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text("💊 吃药 (4 AP)", color = Color.White, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.passTurn() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text("🛡️ 防守", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// Custom horizontal scrolling wrapper helper since we keep code in single modules beautiful
@Composable
fun ModifierHorizontalScrollable(): Modifier {
    return Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
}

// ------------------------------------------------------------
// Tab 3.4: Blacksmith Forge Upgrade Menu
// ------------------------------------------------------------
@Composable
fun ForgeTab(viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("⚒️ 【格罗姆尼尔古神兵熔炉】", fontSize = 16.sp, color = AmberYellow, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Text("矮人莫伊拉燃起炉火！收集大地图矿石，以百分百匠人锤意强化武器甲片或维护装备耐用：", fontSize = 11.sp, color = ParchmentLight)
        
        Spacer(modifier = Modifier.height(16.dp))

        // Scrap Ore Materials Counter panel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardParchmentBase, RoundedCornerShape(8.dp))
                .border(1.dp, AccentBorder, RoundedCornerShape(8.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, "原石", tint = EmberGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("采集到的玄铁原石：", fontSize = 14.sp, color = ParchmentLight)
            }
            Text(
                text = "${viewModel.oreMaterials} 块",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AmberYellow,
                modifier = Modifier
                    .background(ItemGray, RoundedCornerShape(4.dp))
                    .border(1.dp, AccentBorder, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("可强化装备（最高可进阶至 +5，失败时将由于回火退级，量力而行）：", fontSize = 12.sp, color = ParchmentLight.copy(alpha = 0.8f), modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))

        // Upgradeable items loop
        listOf(GearSlot.WEAPON, GearSlot.TORSO).forEach { slot ->
            val gear = viewModel.equippedGears[slot]
            if (gear != null) {
                val neededOre = 2 + gear.upgradeLevel * 2
                val neededGold = 50 + gear.upgradeLevel * 50
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardParchmentBase)
                        .border(1.dp, AccentBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = gear.getFullName(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = gear.rarity.color
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "装备部位：${slot.displayName} • 基础系数: ${gear.baseValue} (计算后实际: ${gear.getEffectiveStat()})",
                                fontSize = 11.sp,
                                color = ParchmentLight
                            )
                        }

                        Button(
                            onClick = { viewModel.upgradeGear(slot) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmberGold),
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("强化 (+${gear.upgradeLevel}级)", color = SlateBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AccentBorder))
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("强化所需：$neededOre 玄原石 + $neededGold G", fontSize = 11.sp, color = AmberYellow)
                        Text(
                            text = "预估熔击成功率: ${(100 - gear.upgradeLevel * 15).coerceIn(20, 100)}%",
                            fontSize = 11.sp,
                            color = ParchmentLight.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { viewModel.repairGears() },
            colors = ButtonDefaults.buttonColors(containerColor = ItemGray),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
        ) {
            Text("🔧 全套铁砧保养修理 (消耗 40G)", color = Color.White)
        }
    }
}

// ------------------------------------------------------------
// Tab 3.5: Hero Profile / Level Breakthrough system / inventory
// ------------------------------------------------------------
@Composable
fun HeroProfileTab(viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("👤 【英雄属性与修行境界】", fontSize = 16.sp, color = AmberYellow, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        // Level details & breakthrough panel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CardParchmentBase)
                .border(1.dp, AccentBorder, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("境界等级: 【${viewModel.heroLevel}级】", fontSize = 16.sp, color = AmberYellow, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("修炼流派: ${viewModel.heroPath.subTitle}", fontSize = 12.sp, color = ParchmentLight)
                    }

                    Button(
                        onClick = { viewModel.attemptBreakthrough() },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonBlood),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("💥 灵气突破境界", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("历练修行EXP：${viewModel.heroExp}/${viewModel.expNeededForNextLevel}", fontSize = 11.sp, color = ParchmentLight.copy(alpha = 0.9f))
                Spacer(modifier = Modifier.height(4.dp))
                CustomGridProgressBar(
                    progress = viewModel.heroExp.toFloat() / viewModel.expNeededForNextLevel,
                    gradientColors = listOf(AmberYellow, EmberGold),
                    trackColor = Color(0xFF2D2820),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Remaining assign points
        if (viewModel.attributePointsLeft > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1E2F20))
                    .border(1.dp, Color(0xFF2E4F32), RoundedCornerShape(4.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "🌟 天授元气富余！有 ${viewModel.attributePointsLeft} 点属性没有分配，快去点击下栏增加神力：",
                    fontSize = 12.sp,
                    color = ParchmentLight,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Stats attributes looping
        Text("【基礎身骨数据属性面板】", fontSize = 13.sp, color = AmberYellow, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))

        viewModel.attributes.forEach { (key, valValue) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(CardParchmentBase)
                    .border(1.dp, AccentBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (key) {
                        "STR" -> "力量 (STR): $valValue (物理近斩与负重加层)"
                        "DEX" -> "敏捷 (DEX): $valValue (格斗先攻率与回避率)"
                        "CON" -> "体质 (CON): $valValue (提供额外血限骨骼强度)"
                        "INT" -> "智力 (INT): $valValue (秘法印记伤害，提高神识)"
                        "WIL" -> "意志 (WIL) - $valValue"
                        "SPR" -> "灵力 (SPR) - $valValue"
                        "PER" -> "感知 (PER) - $valValue"
                        else -> "$key: $valValue"
                    },
                    fontSize = 12.sp,
                    color = ParchmentLight
                )

                if (viewModel.attributePointsLeft > 0) {
                    IconButton(
                        onClick = { viewModel.allocateStat(key) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.AddCircle, "配点", tint = EmberGold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("🎒 【勇者便携备战快捷行囊】", fontSize = 15.sp, color = AmberYellow, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // Inventory items display
        viewModel.backpack.value.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(CardParchmentBase)
                    .border(1.dp, AccentBorder, RoundedCornerShape(6.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("📦 ${item.name} (存量: ${item.count})", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(item.desc, fontSize = 11.sp, color = ParchmentLight.copy(alpha = 0.8f))
                }

                Button(
                    onClick = {
                        if (item.id.contains("hp") || item.id.contains("ale")) {
                            viewModel.playerHpCurrent = (viewModel.playerHpCurrent + 35).coerceAtMost(viewModel.playerHpMax)
                            item.count -= 1
                            viewModel.triggerLog("🩹 玩家灌下了 [${item.name}]，生命获得激越疗合回复！")
                            viewModel.backpack.value = viewModel.backpack.value.filter { it.count > 0 }
                            viewModel.saveGameProgress()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ItemGray),
                    modifier = Modifier.height(28.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text("服下回复", color = ParchmentLight, fontSize = 11.sp)
                }
            }
        }
        if (viewModel.backpack.value.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardParchmentBase)
                    .border(1.dp, AccentBorder, RoundedCornerShape(8.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("行囊虚无。可在旅途长途快速跑动移动时采掘到草药。", color = ParchmentLight.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }
    }
}

// ------------------------------------------------------------
// Tab 3.6: Game Rules Manual Guidelines Booklet
// ------------------------------------------------------------
@Composable
fun GameGuideTab(viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("📖 《灰烬纪元：天命古抄传记手札》", fontSize = 18.sp, color = AmberYellow, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = ParchmentDark),
            modifier = Modifier.fillMaxWidth().border(1.dp, ItemGray, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("【核心必读：战斗部位命中系统 - 石碑铭刻】", fontSize = 14.sp, color = EmberGold, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. 《灰烬纪元》引入硬核【7大身体部位损伤】机制。每个肢体部位拥有完全独立的生命池与毁废状态特征。\n\n" +
                           "2. 头部头部一旦归零，角色与敌兵即刻立即死亡暴毙！物理重砍有高概率附加致盲目眩、击昏断。但头部位置由于权重小不易击中，直接砍劈命中率极具挫折减免惩罚。\n\n" +
                           "3. 手臂受伤残废将折损攻击威力与盾防；腿部肌肉拉裂或残废大幅阻滞每一步AP挪移点，难以再进行翻滚格挡。\n\n" +
                           "4. 战场网格 10x10 等距操作流。多看清地形：避开暴风熔岩火山池（火海会吞吐火毒伤），利用高低崖台落差（高处打低处加15%增功与暴击命中）。",
                    fontSize = 12.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = ParchmentDark),
            modifier = Modifier.fillMaxWidth().border(1.dp, ItemGray, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("【兽潮暴发与神兵淬火秘闻】", fontSize = 14.sp, color = EmberGold, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• 兽穴倒计数面板：在地图多区域快移移动均扣损日历，当巨兽狂躁倒计沦为0，将触发席卷整座大陆的‘天命巨兽潮’！大地图各处涌现主宰BOSS，此时迎击能缴获带有‘神授’名号的前后缀唯一饰品和神话利刃。\n\n" +
                           "• 原石玄铁炉铁强化：修理需要金币，装备强化能给任何武器附加多重稀有随机词缀。强化+1至+5成功概率逐渐衰弱，需谨慎锻力。",
                    fontSize = 12.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "策划致辞：本作为您呈现纯粹的欧美奇幻CRPG硬核精髓。拒绝市面充值快打氪金机制，祝你早日攻破黑潮裂痕！",
            fontSize = 11.sp,
            color = ParchmentLight,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
