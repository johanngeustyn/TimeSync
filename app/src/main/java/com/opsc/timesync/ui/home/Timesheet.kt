package com.opsc.timesync.ui.home

class Timesheet {

    val title: String = ""
    var id: String = ""
    var startDateTime: String = ""
    var endDateTime: String = ""
    var description: String = ""

    constructor()
    constructor(id: String, startDateTime: String, endDateTime: String, description: String) {
        this.id = id
        this.startDateTime = startDateTime
        this.endDateTime = endDateTime
        this.description = description
    }
}
