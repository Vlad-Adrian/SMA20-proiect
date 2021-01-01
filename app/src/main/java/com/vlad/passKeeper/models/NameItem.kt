package com.vlad.passKeeper.models

class NameItem : ListItem() {

    var name: String? = null

    override fun getType(): Int {
        return TYPE_NAME
    }

}