package com.vlad.passKeeper.models

abstract class ListItem {

    val TYPE_NAME: Int = 0
    val TYPE_GENERAL: Int = 1

    abstract fun getType(): Int
}