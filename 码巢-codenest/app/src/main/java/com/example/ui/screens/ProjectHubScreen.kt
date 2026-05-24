package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CuratedData
import com.example.data.ProjectItem
import com.example.ui.CodeNestViewModel
import com.example.ui.Screen
import com.example.ui.components.StarRatingBar

@Composable
fun ProjectHubScreen(
    viewModel: CodeNestViewModel,
    modifier: Modifier = Modifier
) {
    val activeSelection = viewModel.selectedProject
    val enrolledProjects by viewModel.userProjectsState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (activeSelection == null) {
            // Project Catalog View
            ProjectCategoryCatalog(
                onSelectProject = { viewModel.selectedProject = it },
                enrolledList = enrolledProjects
            )
        } else {
            // Selected Active Project Detail Workflow View
            val enrolledState = enrolledProjects.find { it.projectCode == activeSelection.code }
            ProjectRoadmapDetail(
                project = activeSelection,
                currentMilestoneIndex = enrolledState?.currentMilestone ?: 1,
                currentStepIndex = enrolledState?.currentStep ?: 1,
                isCompleted = enrolledState?.status == "COMPLETED",
                onBack = { viewModel.selectedProject = null },
                onAdvanceStep = { isLast ->
                    viewModel.enrollOrAdvanceProject(
                        projectCode = activeSelection.code,
                        projectTitle = activeSelection.title,
                        stepIndex = enrolledState?.currentStep ?: 1,
                        isLastStep = isLast
                    )
                },
                onLoadCodeToSandbox = { code, lang ->
                    viewModel.editorCode = code
                    viewModel.selectedLanguage = lang
                    viewModel.compilerOutput = "代码加载就绪。点击 [Run Code] 或 [AI 编译审查] 解析。"
                    viewModel.currentScreen = Screen.Sandbox
                }
            )
        }
    }
}

@Composable
fun ProjectCategoryCatalog(
    onSelectProject: (ProjectItem) -> Unit,
    enrolledList: List<com.example.data.UserProject>
) {
    var selectedLevelFilter by remember { mutableStateOf(1) } // Default level 1

    val levels = listOf(
        1 to "萌芽期 (基础)",
        2 to "生长期 (框架)",
        3 to "成长期 (全栈)",
        5 to "精通期 (系统)"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Headers
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "全网高规格实战仓库",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp
                )
            )
            Text(
                text = "以蜂窝级项目驱动。60个从基础到工业级实战进阶，被动掌握架构蜕变。",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    lineHeight = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Level Selection slider row constructed with custom premium themed pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(levels) { (num, name) ->
                    val isSelected = selectedLevelFilter == num
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedLevelFilter = num }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }

        // Project Catalog items
        val catalogList = remember(selectedLevelFilter) {
            CuratedData.curatedProjects.filter { it.levelNum == selectedLevelFilter }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(catalogList) { project ->
                val isEnrolled = enrolledList.any { it.projectCode == project.code }
                val isCompleted = enrolledList.any { it.projectCode == project.code && it.status == "COMPLETED" }

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectProject(project) }
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = project.code,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontFamily = FontFamily.Monospace
                                )
                            )

                            if (isCompleted) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFDCFCE7))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text("已通关", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                }
                            } else if (isEnrolled) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFFFEDD5))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text("开发中", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEA580C))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = project.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = project.subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Horizontal tech badges
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            project.techStack.forEach { tech ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = tech,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StarRatingBar(rating = project.difficulty)

                            Text(
                                text = "实战课时: ${project.hours} 小时",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun ProjectRoadmapDetail(
    project: ProjectItem,
    currentMilestoneIndex: Int,
    currentStepIndex: Int,
    isCompleted: Boolean,
    onBack: () -> Unit,
    onAdvanceStep: (isLast: Boolean) -> Unit,
    onLoadCodeToSandbox: (code: String, lang: String) -> Unit
) {
    var activeTab by remember { mutableStateOf(0) } // 0: Overview, 1: Milestones, 2: Recap

    Column(modifier = Modifier.fillMaxSize()) {
        // Back Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${project.code}: ${project.title}",
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Custom High-tech Tab bar
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Text("项目概览", modifier = Modifier.padding(14.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Text("分步实现", modifier = Modifier.padding(14.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Text("复盘面试题", modifier = Modifier.padding(14.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            when (activeTab) {
                0 -> {
                    // Overview Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(20.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFEA580C))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = project.levelName,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    }
                                    Text(
                                        text = project.title,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontSize = 18.sp
                                        )
                                    )
                                    Text(
                                        text = project.description,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                            lineHeight = 20.sp
                                        )
                                    )
                                }
                            }
                        }

                        item {
                            Text(
                                text = "核心技术栈与架构设计",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                project.techStack.forEach { tech ->
                                    Box(
                                        modifier = Modifier
                                            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = tech,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "完成本实训你将被动掌握:",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            listOf(
                                "极速建立多端或后台工程架构，规范配置解耦",
                                "完整设计支持高并发抗灾机制及事务锁定的数据表",
                                "深入对接 AI 教练，学会全周期自然指令结对编程"
                            ).forEach { gain ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = gain,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Milestones Flow Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(project.milestones) { milestone ->
                            val isCompletedMilestone = isCompleted || milestone.order < currentMilestoneIndex
                            val isActiveMilestone = !isCompleted && milestone.order == currentMilestoneIndex

                            val (cardBorderColor, containerColor) = when {
                                isCompletedMilestone -> Color(0xFF16A34A) to MaterialTheme.colorScheme.surface
                                isActiveMilestone -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.outline to MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, cardBorderColor, RoundedCornerShape(20.dp)),
                                colors = CardDefaults.cardColors(containerColor = containerColor),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "里程碑 ${milestone.order}: ${milestone.title}",
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isActiveMilestone) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                            fontSize = 14.sp,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Icon(
                                            imageVector = if (isCompletedMilestone) Icons.Default.CheckCircle else Icons.Default.Info,
                                            contentDescription = null,
                                            tint = if (isCompletedMilestone) Color(0xFF16A34A) else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = milestone.description,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isActiveMilestone) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )

                                    if (isActiveMilestone) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f))
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = "当前里程验收指标:",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = milestone.testCriteria,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    val lang = when {
                                                        project.techStack.any { it.contains("Python", true) } -> "python"
                                                        project.techStack.any { it.contains("JS", true) || it.contains("React", true) } -> "javascript"
                                                        project.techStack.any { it.contains("HTML", true) } -> "html"
                                                        else -> "java"
                                                    }
                                                    onLoadCodeToSandbox(milestone.starterCode, lang)
                                                },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                                ),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("装载至沙盒", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = {
                                                    val isLast = milestone.order == project.milestones.size
                                                    onAdvanceStep(isLast)
                                                },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primary,
                                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                                ),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text("我已完成 (+100 XP)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Recap & Interview Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "CodeNest 大厂真题技术推演",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        items(listOf(
                            "Q1: 如何在本项目场景中，防止超卖或防止数据并发踩踏？" to "A: 高频场景中，关系型数据库行锁极易导致排队崩溃。我们的最佳做法是通过 Redis-Lua 集中化自减，让多端数据判空读写呈单并发原子状态。",
                            "Q2: 双 Token (Access vs Refresh) 相比单 Token 具备什么绝对优势？" to "A: 单 Token 若有效期长，泄露则终身泄露。Access Token 有效期设为15分钟极短，即使泄露由于时效转瞬即逝也危害极小，依靠 HttpOnly 加密的 Refresh Token 在底层安静代签自愈，提供流畅完美的体验。"
                        )) { (q, a) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = q,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = a,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
