package com.by.syncope28.arnotes

object SceneKeys {
    const val EXTRA_SCENE_ID = "SCENE_ID"
    const val EXTRA_SCENE_NAME = "SCENE_NAME"
    const val EXTRA_DARK_THEME = "DARK_THEME"
    const val EXTRA_DYNAMIC_COLOR = "DYNAMIC_COLOR"

    const val PREF_APPEARANCE = "appearance"
    const val PREF_PROFILE = "profile"
    const val NICKNAME_KEY = "nickname"
    const val PREF_DARK_THEME = "dark_theme_override"
    const val PREF_SYSTEM_THEME_AT_OVERRIDE = "system_theme_at_override"
    const val PREF_DYNAMIC_COLOR = "dynamic_color"

    const val PREF_SCENE_PREFIX = "scene_"
    const val PREF_SCENE_NAME = "SCENE_NAME"
    const val PREF_NOTES = "notes"
    const val PREF_NOTE_IDS = "note_ids"
    const val PREF_NOTE_TEXT_PREFIX = "note_text_"

    const val DRAFT_KEY = "draft"
    const val PROFILE_FROM_LINK_KEY = "profile_from_link"
    const val MAX_NICKNAME_LENGTH = 40

    const val LINK_SCHEME = "https"
    const val LINK_HOST = "arnotes.com"
    const val PROFILE_PATH = "/profile"
    const val SCENE = "scene"
    const val NOTE = "note"
    const val PROFILE_LINK = "$LINK_SCHEME://$LINK_HOST$PROFILE_PATH"
    const val SCENE_LINK_PREFIX = "$LINK_SCHEME://$LINK_HOST/$SCENE/"
    const val NOTE_LINK_PATTERN = "${SCENE_LINK_PREFIX}{sceneId}/$NOTE/{noteId}"
    const val SHARE_MIME_TYPE = "text/plain"

    const val LOG_TAG = "ARNotes"
    const val LOG_ON_CREATE = "onCreate"
    const val LOG_ON_START = "onStart"
    const val LOG_ON_RESUME = "onResume"
    const val LOG_ON_PAUSE = "onPause"
    const val LOG_ON_STOP = "onStop"
    const val LOG_ON_DESTROY = "onDestroy"
    const val LOG_OPEN_REQUESTED_SCENE = "openRequestedScene"
    const val LOG_LOAD_SCENE = "loadScene"
}
