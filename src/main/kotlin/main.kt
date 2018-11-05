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

fun initializeProperty(): Property {
    return Property(System.getenv(Env.HOST),
            System.getenv(Env.TOKEN),
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
    }
}

fun assertionSystemEnv() {
    requireNotNull(System.getenv(Env.HOST)) { "GitLabのホストを ${Env.HOST} に設定してください。" }
    requireNotNull(System.getenv(Env.TOKEN)) { "GitLabのトークンを ${Env.TOKEN} に設定してください。" }
    requireNotNull(System.getenv(Env.PROJECT_ID)) { "プロジェクトのIDを ${Env.PROJECT_ID} に設定してください。" }
    requireNotNull(System.getenv(Env.LIMIT)) { "リマインドを促し始める日数を ${Env.LIMIT} に設定してください。" }
    require(System.getenv(Env.PROJECT_ID).toIntOrNull() != null) { "${Env.PROJECT_ID} には数字を設定してください。" }
    require(System.getenv(Env.LIMIT).toIntOrNull() != null) { "${Env.LIMIT} には数字を設定してください。" }
}

fun fetch(issueList: ArrayList<Issue> = arrayListOf(), page: Int = 1, callback: (ArrayList<Issue>) -> Unit) {
    val (_, _, result) = "/api/v4/projects/${property.projectId}/issues"
            .httpGet(listOf("state" to "opened", "page" to page, "per_page" to FETCH_COUNT))
            .header("PRIVATE-TOKEN" to property.token)
            .responseString()
    result.fold({ json ->
        val issues = adapter.fromJson(json)?: return System.exit(1)
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