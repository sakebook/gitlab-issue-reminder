object Env {
    const val HOST = "DUE_DATE_REMINDER_HOST"
    const val PROJECT_ID = "DUE_DATE_REMINDER_PROJECT_ID"
    const val TOKEN = "DUE_DATE_REMINDER_TOKEN"
    const val LIMIT = "DUE_DATE_REMINDER_LIMIT"
}

data class Property(
        val host: String,
        val token: String,
        val projectId: Int,
        val limit: Int
)