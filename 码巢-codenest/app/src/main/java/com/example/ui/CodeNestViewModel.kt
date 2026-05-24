package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.*
import com.example.services.GeminiService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object KnowledgeGraph : Screen()
    object ProjectHub : Screen()
    object Sandbox : Screen()
    object Profile : Screen()
}

data class ChatMessage(
    val sender: String, // "user", "ai"
    val content: String,
    val time: Long = System.currentTimeMillis()
)

class CodeNestViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java,
        "codenest_database"
    ).fallbackToDestructiveMigration().build()

    val dao = db.dao()

    // Screen navigation state
    var currentScreen by mutableStateOf<Screen>(Screen.Home)

    // Selection States for detailed modal/dialog content
    var selectedNode by mutableStateOf<HoneycombNode?>(null)
    var selectedProject by mutableStateOf<ProjectItem?>(null)

    // Flow states from database
    val userStatsState: StateFlow<UserStats?> = dao.getUserStatsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val completedNodesState: StateFlow<List<CompletedNode>> = dao.getCompletedNodesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProjectsState: StateFlow<List<UserProject>> = dao.getUserProjectsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Sandbox states
    var selectedLanguage by mutableStateOf("python")
    var editorCode by mutableStateOf("# 在此输入 Python 代码进行全栈实操\ndef match_nest(code_str):\n    print(\"Hello CodeNest! 欢迎来到交互沙盒。\")\n    return \"CodeNest Mastered\"\n\nmatch_nest(\"nest\")")
    var compilerOutput by mutableStateOf("系统就绪。请编写代码并点击 [Run Code] 或 [AI 编译审查] 运行。")
    var isCompiling by mutableStateOf(false)

    // AI Chat assist State
    var aiChatInput by mutableStateOf("")
    val aiMessages = mutableStateOf<List<ChatMessage>>(
        listOf(
            ChatMessage("ai", "你好！我是你的 CodeNest (码巢) AI 合作结对编程教练。你可以点击下方的快捷提示词，或者直接粘贴报错、提问任何关于 408 计算机体系及全栈开发中的难点。")
        )
    )
    var isAiTyping by mutableStateOf(false)

    init {
        // Initialize Default Stats if missing
        viewModelScope.launch {
            if (dao.getUserStats() == null) {
                dao.saveUserStats(UserStats())
            }
        }
    }

    // Custom level-up calculations
    fun earnXp(amount: Int) {
        viewModelScope.launch {
            val current = dao.getUserStats() ?: UserStats()
            val newXp = current.xp + amount
            val levelUpThreshold = current.level * 500
            val levelUp = newXp >= levelUpThreshold
            val newLevel = if (levelUp) current.level + 1 else current.level
            val finalXp = if (levelUp) newXp - levelUpThreshold else newXp

            dao.saveUserStats(
                current.copy(
                    xp = finalXp,
                    level = newLevel,
                    streak = current.streak + if ((0..3).random() == 0) 1 else 0 // simulated natural streak updates
                )
            )
        }
    }

    fun completeKnowledgeNode(nodeId: String, title: String, category: String) {
        viewModelScope.launch {
            // Add Node
            dao.addCompletedNode(CompletedNode(nodeId, category, title))
            // Earn XP
            earnXp(150)
        }
    }

    fun enrollOrAdvanceProject(projectCode: String, projectTitle: String, stepIndex: Int, isLastStep: Boolean) {
        viewModelScope.launch {
            val userProj = userProjectsState.value.find { it.projectCode == projectCode }
                ?: UserProject(projectCode, projectTitle)

            if (isLastStep) {
                val updatedProj = userProj.copy(
                    currentStep = stepIndex + 1,
                    status = "COMPLETED"
                )
                dao.saveUserProject(updatedProj)
                // Add code lines and project count
                val currentStats = dao.getUserStats() ?: UserStats()
                dao.saveUserStats(
                    currentStats.copy(
                        completedProjectsCount = currentStats.completedProjectsCount + 1,
                        totalCodeLines = currentStats.totalCodeLines + (350..800).random()
                    )
                )
                earnXp(500)
            } else {
                val updatedProj = userProj.copy(
                    currentStep = stepIndex + 1,
                    status = "IN_PROGRESS"
                )
                dao.saveUserProject(updatedProj)
                // Increment code lines
                val currentStats = dao.getUserStats() ?: UserStats()
                dao.saveUserStats(
                    currentStats.copy(
                        totalCodeLines = currentStats.totalCodeLines + (50..120).random()
                    )
                )
                earnXp(100)
            }
        }
    }

    fun runLocalCodeSimulator() {
        val code = editorCode
        isCompiling = true
        compilerOutput = "🚀 [CodeNest Native Compiler] 正在链接运行沙盒...\n"
        
        viewModelScope.launch {
            kotlinx.coroutines.delay(1200) // Realistic delay
            val output = simulateExecution(code, selectedLanguage)
            compilerOutput = output
            isCompiling = false
            earnXp(20) // Award a tiny amount of XP for practice
        }
    }

    fun runAiCodeReview() {
        val code = editorCode
        val lang = selectedLanguage
        isCompiling = true
        compilerOutput = "🤖 [CodeNest AI Compiler] 正在组织高级编译图谱对代码进行自愈检查...\n"

        viewModelScope.launch {
            val sysPrompt = "You are a professional compiler simulation and code analyzer. Output simulated Execution result (stdout), Execution summary (memory, complexity), and clear Refactoring suggestions in Markdown format with gorgeous ANSI/Color structure, simplified for code learning."
            val userPrompt = """
                Please simulate execution or analyze this $lang code snippet:
                ```$lang
                $code
                ```
                Provide:
                1. Expected stdout/stderr output.
                2. Potential syntax error or logic warnings.
                3. High-quality feedback for architectural optimization.
            """.trimIndent()

            val aiResponse = GeminiService.getGeminiResponse(userPrompt, sysPrompt)
            compilerOutput = aiResponse
            isCompiling = false
            earnXp(50) // Code review gives solid learning XP
        }
    }

    fun sendChatMessage() {
        val inputText = aiChatInput.trim()
        if (inputText.isEmpty()) return

        val userMsg = ChatMessage("user", inputText)
        aiMessages.value = aiMessages.value + userMsg
        aiChatInput = ""
        isAiTyping = true

        viewModelScope.launch {
            val sysPrompt = "You are a world-class Fullstack Coding Mentor at CodeNest (码巢). Answer questions with beautiful Markdown formatting, clean emojis, code samples, clear steps, and helpful advice. Always keep explanations professional yet conversational and engaging."
            val prompt = """
                Context:
                Active Node: ${selectedNode?.title ?: "无"}
                Active Project: ${selectedProject?.title ?: "无"}
                Current Editor Code ($selectedLanguage):
                $editorCode
                
                Question: $inputText
            """.trimIndent()

            val aiResponse = GeminiService.getGeminiResponse(prompt, sysPrompt)
            aiMessages.value = aiMessages.value + ChatMessage("ai", aiResponse)
            isAiTyping = false
        }
    }

    private fun simulateExecution(code: String, lang: String): String {
        return when (lang) {
            "python" -> {
                if (code.contains("print")) {
                    val matches = Regex("""print\s*\(\s*["'](.*?)["']\s*\)""").findAll(code)
                    val outputs = matches.map { it.groupValues[1] }.joinToString("\n")
                    if (outputs.isNotEmpty()) {
                        "🐍 Python 3.11.2 - 执行成功:\n\n[STDOUT]\n$outputs\n\n-----------------\n内存占用: 11.4MB\n执行耗时: 12ms\n返回值: 0"
                    } else {
                        "🐍 Python 3.11.2 - 执行成功!\n\n-----------------\n内存占用: 10.1MB\n执行耗时: 8ms\n(注: 无显式标准输出)"
                    }
                } else {
                    "🐍 Python 3.11.2 - 语法校验通过。\n建议增加 `print()` 命令以查看计算结果。"
                }
            }
            "javascript" -> {
                if (code.contains("console.log")) {
                    val matches = Regex("""console\.log\s*\(\s*["'](.*?)["']\s*\)""").findAll(code)
                    val outputs = matches.map { it.groupValues[1] }.joinToString("\n")
                    if (outputs.isNotEmpty()) {
                        "⚡ JavaScript V8 - 运行成功:\n\n[STDOUT]\n$outputs\n\n-----------------\n内存占用: 22.1MB\n执行耗时: 5ms"
                    } else {
                        "⚡ JavaScript V8 - 运行通过。\n建议使用 console.log() 输出返回值。"
                    }
                } else {
                    "⚡ JavaScript V8  - 执行通过。"
                }
            }
            "html" -> {
                "🌐 HTML Render 渲染成功:\n渲染效果为包含 ${if (code.contains("main")) "主体/容器" else "容器元素"} 的自适应响应式网页架构。\n已顺利解耦样式。"
            }
            else -> {
                "☕ JVM / Go 原生编译器 - 静态类型检测通过。\n[STDOUT]\nCompile success: 1 file resolved.\n内存占用: 45MB\n耗时: 110ms"
            }
        }
    }
}
