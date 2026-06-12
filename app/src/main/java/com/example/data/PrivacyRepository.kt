package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppModel(
    val packageName: String,
    val appName: String,
    val isEnabledForSpoofing: Boolean,
    val accessesHardwareIds: Boolean
)

data class DeviceProfile(
    val manufacturer: String,
    val model: String,
    val board: String
)

class PrivacyRepository {
    private val _targetApps = MutableStateFlow(
        listOf(
            AppModel("com.snapchat.android", "Snapchat", false, true),
            AppModel("com.facebook.katana", "Facebook", false, true),
            AppModel("com.instagram.android", "Instagram", false, true),
            AppModel("com.whatsapp", "WhatsApp", false, false),
            AppModel("com.spotify.music", "Spotify", false, false)
        )
    )
    val targetApps: StateFlow<List<AppModel>> = _targetApps.asStateFlow()

    private val _deviceProfiles = MutableStateFlow(
        listOf(
            DeviceProfile("Google", "Pixel 8 Pro", "husky"),
            DeviceProfile("Samsung", "Galaxy S24 Ultra", "e3q"),
            DeviceProfile("OnePlus", "OnePlus 12", "op12")
        )
    )
    val deviceProfiles: StateFlow<List<DeviceProfile>> = _deviceProfiles.asStateFlow()

    private val _selectedProfile = MutableStateFlow<DeviceProfile?>(null)
    val selectedProfile: StateFlow<DeviceProfile?> = _selectedProfile.asStateFlow()

    fun toggleAppSpoofing(packageName: String) {
        _targetApps.value = _targetApps.value.map {
            if (it.packageName == packageName) it.copy(isEnabledForSpoofing = !it.isEnabledForSpoofing) else it
        }
    }

    fun selectProfile(profile: DeviceProfile) {
        _selectedProfile.value = profile
    }
}
