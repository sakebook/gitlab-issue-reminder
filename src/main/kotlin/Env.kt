object Env {
    const val HOST = "DUE_DATE_REMINDER_HOST"
    const val PROJECT_ID = "DUE_DATE_REMINDER_PROJECT_ID"
    const val GITLAB_TOKEN = "DUE_DATE_REMINDER_GITLAB_TOKEN"
    const val LIMIT = "DUE_DATE_REMINDER_LIMIT"
    const val SLACK_TOKEN = "DUE_DATE_REMINDER_SLACK_TOKEN"
}

data class Property(
        val host: String,
        val gitlabToken: String,
        val slackToken: String,
        val projectId: Int,
        val limit: Int
)