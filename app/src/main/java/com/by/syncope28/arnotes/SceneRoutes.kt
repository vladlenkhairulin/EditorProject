package com.by.syncope28.arnotes

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object NoteList : NavKey

@Serializable
data class NoteDetail(val noteId: String) : NavKey

@Serializable
data class NoteEdit(val noteId: String? = null) : NavKey

@Serializable
data class NoteLink(val sceneId: String, val noteId: String) : NavKey
