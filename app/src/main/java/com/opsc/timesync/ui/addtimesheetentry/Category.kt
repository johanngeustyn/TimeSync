package com.opsc.timesync.ui.addtimesheetentry

data class Category(val id: String, val name: String) {
    override fun toString(): String {
        return name
    }
}
