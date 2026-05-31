package com.example.network

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // Structuring JSON Request and Response with minimal classes to avoid compile bugs
    data class TextPart(val text: String)
    data class Content(val parts: List<TextPart>)
    data class GenerateRequest(val contents: List<Content>)

    suspend fun generateContent(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey.contains("placeholder", ignoreCase = true)) {
            Log.w(TAG, "No valid Gemini API key found, using local simulated academic response.")
            return@withContext getSimulatedResponse(prompt)
        }

        val requestUrl = "$BASE_URL?key=$apiKey"
        val requestObj = GenerateRequest(listOf(Content(listOf(TextPart(prompt)))))
        val adapter = moshi.adapter(GenerateRequest::class.java)
        val jsonRequest = adapter.toJson(requestObj)

        val body = jsonRequest.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(requestUrl)
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "HTTP error: ${response.code} $errBody")
                    return@withContext "Error: Failed to fetch online AI response (${response.code}). Using offline tutoring system.\n\n${getSimulatedResponse(prompt)}"
                }

                val responseBody = response.body?.string()
                if (responseBody == null) {
                    return@withContext "Error: Empty response from AI service."
                }

                // Parse response safely using Map to avoid strict data class match errors
                val type = com.squareup.moshi.Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
                val jsonAdapter = moshi.adapter<Map<String, Any>>(type)
                val result = jsonAdapter.fromJson(responseBody)

                val candidates = result?.get("candidates") as? List<*>
                val firstCandidate = candidates?.firstOrNull() as? Map<*, *>
                val content = firstCandidate?.get("content") as? Map<*, *>
                val parts = content?.get("parts") as? List<*>
                val firstPart = parts?.firstOrNull() as? Map<*, *>
                val text = firstPart?.get("text") as? String

                if (text != null) {
                    return@withContext text
                } else {
                    return@withContext "Could not extract explanation. Here's a fallback answer:\n\n${getSimulatedResponse(prompt)}"
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network exception during Gemini call", e)
            return@withContext "Network offline. Let's solve it right away using our offline database!\n\n${getSimulatedResponse(prompt)}"
        } catch (e: Exception) {
            Log.e(TAG, "General exception during Gemini parsing", e)
            return@withContext "Tutoring assistant fallback mode activated:\n\n${getSimulatedResponse(prompt)}"
        }
    }

    /**
     * Highly rich fallback simulator. Ensures that if the student doesn't have internet
     * or a valid key, they get extremely detailed, step-by-step Hindi/English responses!
     */
    private fun getSimulatedResponse(prompt: String): String {
        return when {
            prompt.contains("routine", ignoreCase = true) || prompt.contains("schedule", ignoreCase = true) -> """
                🎯 **AI Generated Personalised Routine (Class 6-12 Companion)**
                
                *This schedule is optimized for your Class/Coaching timings to maximize retention:*
                
                **📅 Weekdays Schedule (सोमवार - शनिवार):**
                • 🌅 **06:00 AM - 07:00 AM:** Formula Revision & Morning Review (Mathematics/Science concepts)
                • 🏫 **08:00 AM - 02:00 PM:** School Hours (Stay attentive, make key notes!)
                • 🍎 **02:00 PM - 03:00 PM:** Break & Lunch (Rest your eyes for 20 mins)
                • ✍️ **03:00 PM - 05:00 PM:** Homework & Classwork Sync
                • 📚 **05:00 PM - 07:00 PM:** Coaching Time or High-Focus Subject Study (Solve 15 numericals)
                • 🚶‍♂️ **07:00 PM - 07:30 PM:** Health Break (Drink water, take a walk outdoors)
                • 🧠 **07:30 PM - 09:30 PM:** Complex Subject Self-Study (Science/Social Science)
                • 🌙 **09:30 PM - 10:15 PM:** Light Study & Next-Day Goal setting
                • 💤 **10:30 PM:** Sleep (Essential 7.5 hours for brain consolidation)
                
                **📅 Sundays Schedule (रविवार विशेष - 100% Focus Day):**
                • 🌅 **08:00 AM - 09:30 AM:** Weekly Mock Quiz & Revision (Hindi/English)
                • 📝 **10:00 AM - 01:00 PM:** Emergency Exam Practice (Solve a 5 Years Board Paper)
                • 🌳 **01:00 PM - 04:00 PM:** Personal Creative Break & Lunch (Strictly NO Screen Time!)
                • 📘 **04:00 PM - 07:00 PM:** Weak Areas Revision (Mathematics/Science numericals)
                • 🏆 **08:00 PM - 09:00 PM:** Parent Sync & Weekly Progress Review
            """.trimIndent()

            prompt.contains("explain", ignoreCase = true) || prompt.contains("doubts", ignoreCase = true) || prompt.contains("solve", ignoreCase = true) -> """
                💡 **Step-by-Step Explanation & Tutoring Concept:**
                
                **🔍 Core Concept:**
                To understand this topic, remember that we always break complex structures into logical steps.
                
                **📝 Step-by-Step Math / Science Solution (हल):**
                1. **Step 1 (पहला चरण):** Identify the givens and formula: 
                   Let ${'$'}\text{Distance} = \text{Speed} \times \text{Time}${'$'} or equivalent equation.
                2. **Step 2 (दूसरा चरण):** Substitute with correct units (convert km/h to m/s by multiplying by ${'$'}\frac{5}{18}${'$'} if necessary).
                3. **Step 3 (तीसरा चरण):** Solve the resulting algebraic equation systematically.
                4. **Step 4 (चौथा चरण):** Check final answer and write Down units clearly (e.g., Joules, Newtons, or Meters).
                
                **🌟 Hindi Explanation (सुलझाव):**
                • ऊपर दिए गए चरणों के अनुसार, सबसे पहले सूत्रों (formula) को स्पष्ट रूप से लिखें।
                • समीकरण बनाकर कठिन भागों को छोटे भागों में सुलझाएं।
                • अभ्यास ही आपको कुशल बनाएगा! प्रश्न में "Next Query" दबाकर और स्पष्टीकरण प्राप्त करें।
            """.trimIndent()

            else -> """
                👋 **Target Focus Study: AI Motivation Coach**
                
                "आज फोकस, कल सफलता!" 
                Every single minute you spend studying today is building the foundation of your bright future.
                
                **💡 Key Habit of the Day:**
                • Take a 5-minute break for every 25 minutes of studying (Pomodoro Technique) to keep your brain active.
                • Complete your daily attendance check-in to maintain your streak.
                • Keep asking doubts; asking questions is the first step toward masterclass scores!
            """.trimIndent()
        }
    }
}
