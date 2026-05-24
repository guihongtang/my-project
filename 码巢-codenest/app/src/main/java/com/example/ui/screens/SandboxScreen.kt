package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CodeNestViewModel

/**
 * Lightweight, high-fidelity custom syntax highlighter for code sandbox editor
 */
class SimpleSyntaxHighlighter(val language: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        val builder = AnnotatedString.Builder()
        
        val keywords = when (language) {
            "python" -> listOf(
                "def ", "class ", "return ", "import ", "from ", "as ", "pass", "if ", "elif ",
                "else:", "else ", "for ", "while ", "in ", "and ", "or ", "not ", "is ", "None", "self"
            )
            "javascript" -> listOf(
                "function ", "const ", "let ", "var ", "return ", "import ", "export ", "from ",
                "class ", "if ", "else ", "for ", "while ", "await ", "async ", "new ", "true", "false", "this"
            )
            else -> listOf(
                "import ", "package ", "class ", "public ", "private ", "protected ", "void ",
                "fun ", "val ", "var ", "return ", "if ", "else ", "this ", "throw ", "try ", "catch"
            )
        }

        var i = 0
        while (i < originalText.length) {
            var matched = false
            
            // Highlight single line comments
            if (originalText.startsWith("#", i) || originalText.startsWith("//", i)) {
                val lineEnd = originalText.indexOf('\n', i)
                val commentLen = if (lineEnd != -1) lineEnd - i else originalText.length - i
                builder.pushStyle(SpanStyle(color = Color(0xFF94A3B8))) // Gentle Slate Slate
                builder.append(originalText.substring(i, i + commentLen))
                builder.pop()
                i += commentLen
                matched = true
                continue
            }

            // Highlight strings
            if (originalText[i] == '"' || originalText[i] == '\'') {
                val quoteChar = originalText[i]
                val nextQuote = originalText.indexOf(quoteChar, i + 1)
                val stringLen = if (nextQuote != -1) nextQuote - i + 1 else originalText.length - i
                builder.pushStyle(SpanStyle(color = Color(0xFF4ADE80))) // Soft Mint Green
                builder.append(originalText.substring(i, i + stringLen))
                builder.pop()
                i += stringLen
                matched = true
                continue
            }

            // Highlight keywords
            for (kw in keywords) {
                if (originalText.startsWith(kw, i)) {
                    builder.pushStyle(SpanStyle(color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold)) // Vivid Soft Blue
                    builder.append(kw)
                    builder.pop()
                    i += kw.length
                    matched = true
                    break
                }
            }

            if (!matched) {
                builder.append(originalText[i].toString())
                i++
            }
        }

        return TransformedText(builder.toAnnotatedString(), OffsetMapping.Identity)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SandboxScreen(
    viewModel: CodeNestViewModel,
    modifier: Modifier = Modifier
) {
    var activeWorkspaceTab by remember { mutableStateOf(0) } // 0: Editor Canvas, 1: AI Chat Assistant
    val highlighter = remember(viewModel.selectedLanguage) { SimpleSyntaxHighlighter(viewModel.selectedLanguage) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Workspace subheader
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "实战沙盒 / CodeSandbox",
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp
                )
            }

            // High-tech Language Badge Selector
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = viewModel.selectedLanguage.uppercase(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Segmented Control Tabs (Editor vs Chat Coach)
        TabRow(
            selectedTabIndex = activeWorkspaceTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = activeWorkspaceTab == 0,
                onClick = { activeWorkspaceTab = 0 },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("沙盒编辑器", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Tab(
                selected = activeWorkspaceTab == 1,
                onClick = { activeWorkspaceTab = 1 },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("AI 结对教练", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Inner Tab Contents
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (activeWorkspaceTab == 0) {
                // Coding Playground Canvas view
                Column(modifier = Modifier.fillMaxSize()) {
                    // Editor input
                    Box(
                        modifier = Modifier
                            .weight(0.55f)
                            .fillMaxWidth()
                            .background(Color(0xFF0C0A19))
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            // Line Number Column gutters
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(36.dp)
                                    .background(Color(0xFF070512))
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val lines = viewModel.editorCode.split("\n")
                                for (i in 1..lines.size.coerceAtMost(30)) {
                                    Text(
                                        text = "$i",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.25f),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp)
                                    )
                                }
                            }

                            // Editable editor field
                            TextField(
                                value = viewModel.editorCode,
                                onValueChange = { viewModel.editorCode = it },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .horizontalScroll(rememberScrollState()),
                                textStyle = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    lineHeight = 18.sp
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                visualTransformation = highlighter,
                                placeholder = {
                                    Text(
                                        "请输入你的代码...",
                                        color = Color.White.copy(alpha = 0.25f),
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            )
                        }
                    }

                    // Action controls rows
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.runLocalCodeSimulator() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            enabled = !viewModel.isCompiling,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("本地运行", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.runAiCodeReview() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            enabled = !viewModel.isCompiling,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AI 编译审查", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    // Compiler output panel Console
                    Box(
                        modifier = Modifier
                            .weight(0.45f)
                            .fillMaxWidth()
                            .background(Color(0xFF070A0F))
                            .padding(12.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TERMINAL CONSOLE OUTPUT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B),
                                    fontFamily = FontFamily.Monospace
                                )

                                if (viewModel.isCompiling) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = viewModel.compilerOutput,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = if (viewModel.compilerOutput.contains("错误") || viewModel.compilerOutput.contains("Error")) Color(0xFFFF6B6B) else Color(0xFF4ADE80),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            } else {
                // AI Tutor Mentor view (Tab 1)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    val listState = rememberLazyListState()
                    val chatHistory by viewModel.aiMessages

                    // Scrollable messages list
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(chatHistory) { msg ->
                            val isUser = msg.sender == "user"
                            val containerColor = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            val borderColor = if (isUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outline

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .background(containerColor, RoundedCornerShape(16.dp))
                                        .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = if (isUser) "全栈拓荒者" else "NEST AI COACH",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 9.sp,
                                            color = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        Text(
                                            text = msg.content,
                                            color = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }

                        if (viewModel.isAiTyping) {
                            item {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(12.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        strokeWidth = 2.dp
                                    )
                                    Text(
                                        text = "AI 正在分析并撰写重构讲义...",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }

                    // Quick prompt suggestions FlowRow row
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "分析复杂度" to "请帮我分析当前沙盒编辑区内代码的时间复杂度和空间复杂度。",
                            "查找Bug" to "帮我看看编辑区内的代码有什么逻辑漏洞或者语法 Bug吗？",
                            "优化重构" to "推荐一些重构技巧，帮助我让这段代码读起来更干净、更高效。"
                        ).forEach { (label, action) ->
                            SuggestionChip(
                                onClick = {
                                    viewModel.aiChatInput = action
                                    viewModel.sendChatMessage()
                                },
                                label = { Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                border = SuggestionChipDefaults.suggestionChipBorder(
                                    enabled = true,
                                    borderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                        }
                    }

                    // User Message input bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = viewModel.aiChatInput,
                            onValueChange = { viewModel.aiChatInput = it },
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp),
                            placeholder = { Text("提问 AI 导师...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), fontSize = 13.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            ),
                            maxLines = 3,
                            shape = RoundedCornerShape(12.dp)
                        )

                        IconButton(
                            onClick = { viewModel.sendChatMessage() },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}
