package com.by.syncope28.arnotes

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private lateinit var preferences: SharedPreferences
    private val _uiState = MutableStateFlow(
        AppUiState(profileFromLink = savedStateHandle[SceneKeys.PROFILE_FROM_LINK_KEY] ?: false)
    )
    val uiState = _uiState.asStateFlow()

    fun loadSettings(context: Context) {
        preferences = context.getSharedPreferences(SceneKeys.PREF_APPEARANCE, Context.MODE_PRIVATE)
        if (!preferences.contains(SceneKeys.NICKNAME_KEY)) {
            val savedNickname = context.getSharedPreferences(SceneKeys.PREF_PROFILE, Context.MODE_PRIVATE)
                .getString(SceneKeys.NICKNAME_KEY, null)
            if (savedNickname != null) {
                preferences.edit { putString(SceneKeys.NICKNAME_KEY, savedNickname) }
            }
        }
        refreshSettings()
    }

    fun refreshSettings(systemDarkTheme: Boolean? = null) {
        if (systemDarkTheme != null && preferences.contains(SceneKeys.PREF_DARK_THEME)) {
            val savedSystemTheme = if (preferences.contains(SceneKeys.PREF_SYSTEM_THEME_AT_OVERRIDE)) {
                preferences.getBoolean(SceneKeys.PREF_SYSTEM_THEME_AT_OVERRIDE, false)
            } else {
                null
            }
            if (savedSystemTheme != systemDarkTheme) {
                clearThemeOverride()
            }
        }

        _uiState.update {
            it.copy(
                darkTheme = readDarkTheme(),
                dynamicColor = preferences.getBoolean(SceneKeys.PREF_DYNAMIC_COLOR, false),
                nickname = savedStateHandle[SceneKeys.NICKNAME_KEY] ?: readNickname()
            )
        }
    }

    private fun readDarkTheme(): Boolean? {
        return if (preferences.contains(SceneKeys.PREF_DARK_THEME)) {
            preferences.getBoolean(SceneKeys.PREF_DARK_THEME, false)
        } else {
            null
        }
    }

    fun changeDarkTheme(value: Boolean, systemDarkTheme: Boolean) {
        preferences.edit {
            putBoolean(SceneKeys.PREF_DARK_THEME, value)
            putBoolean(SceneKeys.PREF_SYSTEM_THEME_AT_OVERRIDE, systemDarkTheme)
        }
        refreshSettings()
    }

    private fun clearThemeOverride() {
        preferences.edit {
            remove(SceneKeys.PREF_DARK_THEME)
            remove(SceneKeys.PREF_SYSTEM_THEME_AT_OVERRIDE)
        }
    }

    fun changeDynamicColor(value: Boolean) {
        preferences.edit { putBoolean(SceneKeys.PREF_DYNAMIC_COLOR, value) }
        refreshSettings()
    }

    fun changeNickname(value: String) {
        if (_uiState.value.profileFromLink) return
        val nickname = value.take(SceneKeys.MAX_NICKNAME_LENGTH)
        savedStateHandle[SceneKeys.NICKNAME_KEY] = nickname
        _uiState.update { it.copy(nickname = nickname, profileMessage = null) }
    }

    fun saveProfile() {
        val state = _uiState.value
        if (state.nickname.isBlank() || state.profileFromLink) return
        preferences.edit { putString(SceneKeys.NICKNAME_KEY, state.nickname) }
        savedStateHandle.remove<String>(SceneKeys.NICKNAME_KEY)
        _uiState.update { it.copy(profileMessage = R.string.profile_saved) }
    }

    fun profileLink(): String {
        val nickname = Uri.encode(_uiState.value.nickname)
        return "${SceneKeys.PROFILE_LINK}?${SceneKeys.NICKNAME_KEY}=$nickname"
    }

    fun openProfileLink(uri: Uri) {
        val nickname = uri.getQueryParameter(SceneKeys.NICKNAME_KEY).orEmpty()
        val valid = uri.scheme == SceneKeys.LINK_SCHEME && uri.host == SceneKeys.LINK_HOST && uri.path == SceneKeys.PROFILE_PATH && nickname.isNotBlank() && nickname.length <= SceneKeys.MAX_NICKNAME_LENGTH
        val linkedNickname = if (valid) nickname else ""
        savedStateHandle[SceneKeys.NICKNAME_KEY] = linkedNickname
        savedStateHandle[SceneKeys.PROFILE_FROM_LINK_KEY] = true
        _uiState.update {
            it.copy(
                nickname = linkedNickname, profileFromLink = true, profileMessage = if (valid) null else R.string.profile_link_error
            )
        }
    }

    fun closeProfileLink() {
        if (!_uiState.value.profileFromLink) return
        savedStateHandle.remove<String>(SceneKeys.NICKNAME_KEY)
        savedStateHandle.remove<Boolean>(SceneKeys.PROFILE_FROM_LINK_KEY)
        _uiState.update {
            it.copy(
                nickname = readNickname(), profileFromLink = false, profileMessage = null)
        }
    }

    private fun readNickname(): String = preferences.getString(SceneKeys.NICKNAME_KEY, "").orEmpty()
}
