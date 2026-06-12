package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.GenerationConfig
import com.example.api.Part
import com.example.api.RetrofitClient
import com.example.api.ThinkingConfig
import com.example.data.AppModel
import com.example.data.PrivacyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.BuildConfig

class MainViewModel : ViewModel() {
    private val repository = PrivacyRepository()

    val targetApps = repository.targetApps
    val deviceProfiles = repository.deviceProfiles
    val selectedProfile = repository.selectedProfile

    private val _aiAnalysisOutput = MutableStateFlow("Tap the Analyze button below to run the AI FSM bot check.")
    val aiAnalysisOutput: StateFlow<String> = _aiAnalysisOutput.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    val privacyRiskScore: Int
        get() {
            val highRiskCount = targetApps.value.count { it.accessesHardwareIds && !it.isEnabledForSpoofing }
            return 100 - (highRiskCount * 20).coerceIn(0, 100)
        }

    fun toggleAppSpoofing(packageName: String) {
        repository.toggleAppSpoofing(packageName)
    }

    fun selectProfile(profile: com.example.data.DeviceProfile) {
        repository.selectProfile(profile)
    }

    fun runAiAnalysis() {
        viewModelScope.launch {
            _isAnalyzing.value = true
            _aiAnalysisOutput.value = "Analyzing current configuration with Gemini 3.1 Pro Preview..."
            
            val apps = targetApps.value
            val profile = selectedProfile.value
            val spoofedAppsCount = apps.count { it.isEnabledForSpoofing }
            
            val prompt = """
                You are a privacy expert AI assistant (AI FSM Bot). Assess the user's current device spoofing config:
                - Privacy Score: ${privacyRiskScore}/100
                - Spoofed Target Apps: $spoofedAppsCount out of ${apps.size}
                - Selected Mock Device Profile: ${profile?.model ?: "Unknown"} by ${profile?.manufacturer ?: "Unknown"}
                
                Simulate a Play Integrity / SafetyNet check based on this profile and tell the user if their spoofed configuration is likely to trigger detection mechanisms in high-security apps like Snapchat. Provide a short, 3-4 sentence risk report. Use a High Thinking level to analyze the implications of spoofing high-risk identifiers.
            """.trimIndent()

            try {
                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    generationConfig = GenerationConfig(thinkingConfig = ThinkingConfig(thinkingLevel = "HIGH"))
                )
                val response = RetrofitClient.service.generateContent(
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    request = request
                )
                val resultText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                _aiAnalysisOutput.value = resultText ?: "Analysis complete, but no result generated."
            } catch (e: Exception) {
                _aiAnalysisOutput.value = "Error during analysis: ${e.message}"
            } finally {
                _isAnalyzing.value = false
            }
        }
    }
}
