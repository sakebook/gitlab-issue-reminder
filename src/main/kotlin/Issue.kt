import com.squareup.moshi.Json
import java.util.Date

data class Issue(
        val title: String,
        val state: String,
        @field:Json(name = "due_date") val dueDate: Date?
)