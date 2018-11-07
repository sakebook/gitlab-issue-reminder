import com.squareup.moshi.Json
import java.util.Date

data class Issue(
        val title: String,
        val state: String,
        @field:Json(name = "labels") val labels: List<String>,
        @field:Json(name = "web_url") val webUrl: String,
        @field:Json(name = "due_date") val dueDate: Date?,
        val author: Author?
)

data class Author(
    val avatar_url: Any,
    val id: Int,
    val name: String,
    val state: String,
    val username: String,
    val web_url: String
)


data class RemindIssues(val overdueIssues: List<Issue>, val upcomingIssues: List<Issue>)