package com.by.syncope28.arnotes

data class AppUiState(
    val darkTheme: Boolean? = null,
    val dynamicColor: Boolean = false,
    val nickname: String = "",
    val profileFromLink: Boolean = false,
    val profileMessage: Int? = null
)
