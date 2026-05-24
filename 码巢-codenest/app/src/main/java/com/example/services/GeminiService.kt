package com.example.services

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object GeminiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    suspend fun getGeminiResponse(prompt: String, systemPrompt: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "请在 AI Studio 的 Secrets 面板中配置有效的 GEMINI_API_KEY。"
        }

        // Clean and escape helper
        fun escape(str: String): String {
            return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
        }

        val jsonPrompt = escape(prompt)
        val systemPromptPart = if (systemPrompt != null) {
            val jsonSys = escape(systemPrompt)
            ", \"systemInstruction\": { \"parts\": [ { \"text\": \"$jsonSys\" } ] }"
        } else ""

        val requestJson = "{ \"contents\": [ { \"parts\": [ { \"text\": \"$jsonPrompt\" } ] } ]$systemPromptPart }"
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = requestJson.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBodyStr = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext "调用 AI 失败(HTTP ${response.code}): $responseBodyStr"
                }

                // Parse the response using a robust extraction locator
                val textMarker = "\"text\":"
                val startIndex = responseBodyStr.indexOf(textMarker)
                if (startIndex != -1) {
                    val quoteStart = responseBodyStr.indexOf("\"", startIndex + textMarker.length)
                    if (quoteStart != -1) {
                        var quoteEnd = -1
                        var searchIndex = quoteStart + 1
                        while (searchIndex < responseBodyStr.length) {
                            val nextQuote = responseBodyStr.indexOf("\"", searchIndex)
                            if (nextQuote == -1) break
                            if (responseBodyStr[nextQuote - 1] != '\\') {
                                quoteEnd = nextQuote
                                break
                            } else {
                                var backslashCount = 0
                                var slashIndex = nextQuote - 1
                                while (slashIndex >= 0 && responseBodyStr[slashIndex] == '\\') {
                                    backslashCount++
                                    slashIndex--
                                }
                                if (backslashCount % 2 == 0) {
                                    quoteEnd = nextQuote
                                    break
                                }
                            }
                            searchIndex = nextQuote + 1
                        }

                        if (quoteEnd != -1) {
                            val rawText = responseBodyStr.substring(quoteStart + 1, quoteEnd)
                            return@withContext rawText.replace("\\\\", "\\")
                                .replace("\\\"", "\"")
                                .replace("\\n", "\n")
                                .replace("\\r", "\r")
                                .replace("\\t", "\t")
                        }
                    }
                }
                "AI 响应格式解析异常，返回内容如下:\n$responseBodyStr"
            }
        } catch (e: Exception) {
            "无法链接到谷歌 Gemini 智能教练端:\n${e.localizedMessage ?: e.message ?: "网络超时"}"
        }
    }
}
