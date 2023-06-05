import com.google.firebase.Timestamp
class Timesheet() {
    var date: Timestamp? = null
    var startTime: Timestamp? = null
    var endTime: Timestamp? = null
    var category: String? = null
    var entryDescription: String? = null
    var user: String? = null

    constructor(
        date: Timestamp?,
        startTime: Timestamp?,
        endTime: Timestamp?,
        category: String?,
        description: String?,
        user: String?
    ) : this() {
        this.date = date
        this.startTime = startTime
        this.endTime = endTime
        this.category = category
        this.entryDescription = description
        this.user = user
    }
}
