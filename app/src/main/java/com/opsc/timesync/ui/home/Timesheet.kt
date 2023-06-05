class Timesheet() {
    var date: String = ""
    var startTime: String = ""
    var endTime: String = ""
    var entryDescription: String = ""
    var user: String = ""

    constructor(
        date: String,
        startTime: String,
        endTime: String,
        description: String,
        user: String
    ) : this() {
        this.date = date
        this.startTime = startTime
        this.endTime = endTime
        this.entryDescription = description
        this.user = user
    }
}
