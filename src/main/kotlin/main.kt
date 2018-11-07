import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.util.Date

val moshi = Moshi.Builder()
        .add(Date::class.java, CustomDateFormatAdapter())
        .build()
val listType = Types.newParameterizedType(List::class.java, Issue::class.java)
val adapter: JsonAdapter<List<Issue>> = moshi.adapter(listType)
val property: Property by lazy { initializeProperty() }
val FETCH_COUNT = 100
val DAY_MILLIS = 1000 * 60 * 60 * 24

fun initializeProperty(): Property {
    return Property(System.getenv(Env.HOST),
            System.getenv(Env.GITLAB_TOKEN),
            System.getenv(Env.SLACK_WEB_HOOK_URL),
            System.getenv(Env.PROJECT_ID).toInt(),
            System.getenv(Env.LIMIT).toInt())
}

fun main(args: Array<String>) {
    assertionSystemEnv()
    println("Env OK! Start fetch from ${property.host}")
    FuelManager.instance.basePath = property.host
    // TODO: Coroutineに置き換え
    fetch {
        println("All issue size: ${it.size}")
        val remindIssues = calculateRemindIssues(it)
        postToSlack(remindIssues)
    }
}

fun assertionSystemEnv() {
    requireNotNull(System.getenv(Env.HOST)) { "GitLabのホストを ${Env.HOST} に設定してください。" }
    requireNotNull(System.getenv(Env.GITLAB_TOKEN)) { "GitLabのトークンを ${Env.GITLAB_TOKEN} に設定してください。" }
    requireNotNull(System.getenv(Env.SLACK_WEB_HOOK_URL)) { "SlackのWebHook URLを ${Env.SLACK_WEB_HOOK_URL} に設定してください。" }
    requireNotNull(System.getenv(Env.PROJECT_ID)) { "プロジェクトのIDを ${Env.PROJECT_ID} に設定してください。" }
    requireNotNull(System.getenv(Env.LIMIT)) { "リマインドを促し始める日数を ${Env.LIMIT} に設定してください。" }
    require(System.getenv(Env.PROJECT_ID).toIntOrNull() != null) { "${Env.PROJECT_ID} には数字を設定してください。" }
    require(System.getenv(Env.LIMIT).toIntOrNull() != null) { "${Env.LIMIT} には数字を設定してください。" }
}

fun fetch(issueList: ArrayList<Issue> = arrayListOf(), page: Int = 1, callback: (ArrayList<Issue>) -> Unit) {
    val (_, _, result) = "/api/v4/projects/${property.projectId}/issues"
            .httpGet(listOf("state" to "opened", "page" to page, "per_page" to FETCH_COUNT))
            .header("PRIVATE-TOKEN" to property.gitlabToken)
            .responseString()
    result.fold({ json ->
        val issues = adapter.fromJson(json)?: run {
            println("Failed deserialized Issue from JSON.")
            return System.exit(1)
        }
        issueList.addAll(issues)
        when (issues.size < FETCH_COUNT) {
            true -> callback(issueList)
            false -> fetch(issueList, page + 1, callback)
        }
    }, {
        println(it.localizedMessage)
        return System.exit(1)
    })
}

fun calculateRemindIssues(issueList: ArrayList<Issue>): RemindIssues {
    val time = Date().time
    return issueList
            .filter { it.dueDate != null } // due dateが設定されていないものは除外
            .sortedByDescending { it.dueDate } // 時系列にソート
            .filter { (it.dueDate!!.time - time) / DAY_MILLIS < property.limit } // 締切が遠いものは除外
            .partition { (time - it.dueDate!!.time) > 0 } // 締切を過ぎているものと分ける
            .run { RemindIssues(this.first, this.second) }
}

fun postToSlack(remindIssues: RemindIssues) {
    val moshi = Moshi.Builder()
            .add(MessageJsonAdapter.FACTORY)
            .add(ListAttachmentJsonAdapter.FACTORY)
            .add(AttachmentJsonAdapter.FACTORY)
            .build()

    val listType = Types.newParameterizedType(Message::class.java)
    val adapter: JsonAdapter<Message> = moshi.adapter(listType)

    // TODO: 処理が重複してるのでまとめる
    // TODO: 0件だった場合ハッピーな文言を送る
    // TODO: エラーハンドリング
    createOverdueMessage(remindIssues.overdueIssues)?.let {
        Fuel.post(property.slackWebHookUrl).body(adapter.toJson(it)).response()
    }
    createUpcomingMessage(remindIssues.upcomingIssues)?.let {
        Fuel.post(property.slackWebHookUrl).body(adapter.toJson(it)).response()
    }
}

fun createUpcomingMessage(issueList: List<Issue>): Message? {
    val message = Message()
    message.text = "締切が${property.limit}日以内のIssue"
    message.attachments = arrayListOf()
    issueList.ifEmpty { return null }
            .forEach {
                val attachment = Attachment()
                attachment.title = it.title
                attachment.title_link = it.webUrl
                attachment.footer = it.labels.flat()
                attachment.text = "<@${it.author?.username}>"
                attachment.color = "warning"
                attachment.ts = (it.dueDate?.time?.div(1000))?.toInt() ?: 0
                message.attachments!!.add(attachment)
            }
    return message
}

fun createOverdueMessage(issueList: List<Issue>): Message? {
    val message = Message()
    message.text = "締切を過ぎてるIssue"
    message.attachments = arrayListOf()
    issueList.ifEmpty { return null }
            .forEach {
                val attachment = Attachment()
                attachment.title = it.title
                attachment.title_link = it.webUrl
                attachment.footer = it.labels.flat()
                attachment.text = "<@${it.author?.username}>"
                attachment.color = "danger"
                attachment.ts = (it.dueDate?.time?.div(1000))?.toInt() ?: 0
                message.attachments!!.add(attachment)
            }
    return message
}

fun List<String>.flat(): String {
    return this
            .ifEmpty { listOf("") }
            .reduce { acc, s -> "$acc, $s" }
}