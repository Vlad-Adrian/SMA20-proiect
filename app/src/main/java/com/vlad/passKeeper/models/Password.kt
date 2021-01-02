package com.vlad.passKeeper.models

import java.io.Serializable

data class Password(
    val username: String = "",
    val pass: String? = "",
    val name: String = "",
    val link: String = "",
    val notes: String = ""
) : Serializable