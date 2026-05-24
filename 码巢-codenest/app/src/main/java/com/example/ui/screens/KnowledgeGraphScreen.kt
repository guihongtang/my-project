package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CuratedData
import com.example.data.HoneycombNode
import com.example.data.NodeCategory
import com.example.ui.CodeNestViewModel
import com.example.ui.components.HexagonShape

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KnowledgeGraphScreen(
    viewModel: CodeNestViewModel,
    modifier: Modifier = Modifier
) {
    val completedNodes by viewModel.completedNodesState.collectAsStateWithLifecycle()
    var selectedCategoryFilter by remember { mutableStateOf<NodeCategory?>(null) }
    var activeNodeDetail by remember { mutableStateOf<HoneycombNode?>(null) }

    // Map initial state overridden by database completion values
    val currentNodes = remember(completedNodes) {
        CuratedData.initialNodes.map { node ->
            val isCompleted = completedNodes.any { it.nodeId == node.id }
            if (isCompleted) {
                node.copy(state = "COMPLETED")
            } else {
                node
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "蜂窝知识图谱",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "已激活: ${completedNodes.size} / ${CuratedData.initialNodes.size}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Filter horizontal flow with modern custom tags
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    maxItemsInEachRow = 5
                ) {
                    val isAllSelected = selectedCategoryFilter == null
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isAllSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = 1.dp,
                                color = if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedCategoryFilter = null }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "全部",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAllSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                        )
                    }

                    CuratedData.categories.forEach { cat ->
                        val isSelected = selectedCategoryFilter == cat
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
                                .clickable { selectedCategoryFilter = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }

            // Visual Honeycomb Grid
            val filteredNodes = currentNodes.filter { node ->
                selectedCategoryFilter == null || node.category == selectedCategoryFilter
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredNodes, key = { it.id }) { node ->
                    HoneycombHexNode(
                        node = node,
                        onClick = { activeNodeDetail = node }
                    )
                }
            }
        }

        // Expanded Bottom Sheet style slide-up dialog for details
        AnimatedVisibility(
            visible = activeNodeDetail != null,
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            activeNodeDetail?.let { node ->
                val isCompleted = completedNodes.any { it.nodeId == node.id }
                NodeDetailCard(
                    node = node,
                    isCompleted = isCompleted,
                    onClose = { activeNodeDetail = null },
                    onCompleteNode = {
                        viewModel.completeKnowledgeNode(node.id, node.title, node.category.name)
                        activeNodeDetail = null
                    }
                )
            }
        }
    }
}

@Composable
fun HoneycombHexNode(
    node: HoneycombNode,
    onClick: () -> Unit
) {
    val nodeColor = remember(node.state) {
        when (node.state) {
            "COMPLETED" -> Color(0xFF16A34A) // Sleek Green
            "AVAILABLE" -> Color(0xFF005CBB) // Sleek Primary Blue
            else -> Color(0xFF74777F)        // Sleek Secondary Grey
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(HexagonShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(2.dp, nodeColor, HexagonShape),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(6.dp)
            ) {
                Icon(
                    imageVector = when (node.category) {
                        NodeCategory.CS_BASICS -> Icons.Default.Menu
                        NodeCategory.FRONTEND -> Icons.Default.Face
                        NodeCategory.BACKEND -> Icons.Default.Send
                        NodeCategory.DATABASE -> Icons.Default.List
                        NodeCategory.DEVOPS -> Icons.Default.Share
                        NodeCategory.ADVANCED -> Icons.Default.Star
                    },
                    contentDescription = null,
                    tint = nodeColor,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = node.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun NodeDetailCard(
    node: HoneycombNode,
    isCompleted: Boolean,
    onClose: () -> Unit,
    onCompleteNode: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.65f)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Close bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = node.category.label,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                IconButton(
                    onClick = onClose,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = node.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = node.description.ifEmpty { "系统化深度解析该核心知识点，配套工业级项目架构实践课程。" },
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "蜂巢技术核心讲义点",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Scrollable Lessons
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                node.details.forEachIndexed { i, detail ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "要点 ${i+1}: $detail",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Read",
                            tint = if (isCompleted) Color(0xFF16A34A) else MaterialTheme.colorScheme.onSurfaceVariant.copy(0.18f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Complete lesson action button
            Button(
                onClick = onCompleteNode,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCompleted) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary,
                    contentColor = if (isCompleted) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (isCompleted) "重新温习本蜂巢讲义" else "我已理解，点亮本模块蜂穴 (+150 XP)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
