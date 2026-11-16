package com.by.syncope28.arnotes

object SceneKeys {
    const val EXTRA_SCENE_ID = "SCENE_ID"
    const val EXTRA_SCENE_NAME = "SCENE_NAME"
    const val EXTRA_DARK_THEME = "DARK_THEME"
    const val EXTRA_DYNAMIC_COLOR = "DYNAMIC_COLOR"

    const val PREF_APPEARANCE = "appearance"
    const val PREF_DARK_THEME = "dark_theme"
    const val PREF_DYNAMIC_COLOR = "dynamic_color"

    const val PREF_SCENE_PREFIX = "scene_"
    const val PREF_SCENE_NAME = "SCENE_NAME"
    const val PREF_NOTES = "notes"
    const val PREF_NOTE_IDS = "note_ids"
    const val PREF_NOTE_TEXT_PREFIX = "note_text_"

    const val SCENE_LINK_PREFIX = "https://arnotes.com/scene/"
    const val NOTE_LINK_PATTERN = "${SCENE_LINK_PREFIX}{sceneId}/note/{noteId}"
    const val SHARE_MIME_TYPE = "text/plain"
}
