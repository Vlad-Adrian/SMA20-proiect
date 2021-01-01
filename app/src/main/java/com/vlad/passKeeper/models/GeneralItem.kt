package com.vlad.passKeeper.models

class GeneralItem : ListItem() {

    var password: Password? = null

    override fun getType(): Int {
        return TYPE_GENERAL
    }
}