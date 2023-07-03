package com.opsc.timesync.ui.home

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

class Timesheet(
    var date: Timestamp? = null,
    var startTime: Timestamp? = null,
    var endTime: Timestamp? = null,
    var category: DocumentReference? = null,
    var categoryName: String? = null,
    var entryDescription: String? = null,
    var user: String? = null,
    var photoUrl: String? = null
)