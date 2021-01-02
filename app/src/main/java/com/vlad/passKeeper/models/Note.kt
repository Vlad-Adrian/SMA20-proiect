package com.vlad.passKeeper.models

import java.io.Serializable

data class Note(
    val title: String = "",
    val note: String = "",
) : Serializable