val property: Property by lazy { initializeProperty() }

fun initializeProperty(): Property {
    return Property(System.getenv(Env.HOST),
            System.getenv(Env.TOKEN),
            System.getenv(Env.PROJECT_ID).toInt(),
            System.getenv(Env.LIMIT).toInt())
}

fun main(args: Array<String>) {
    assertionSystemEnv()
}

fun assertionSystemEnv() {
    requireNotNull(System.getenv(Env.HOST)) { "GitLabのホストを ${Env.HOST} に設定してください。" }
    requireNotNull(System.getenv(Env.TOKEN)) { "GitLabのトークンを ${Env.TOKEN} に設定してください。" }
    requireNotNull(System.getenv(Env.PROJECT_ID)) { "プロジェクトのIDを ${Env.PROJECT_ID} に設定してください。" }
    requireNotNull(System.getenv(Env.LIMIT)) { "リマインドを促し始める日数を ${Env.LIMIT} に設定してください。" }
    require(System.getenv(Env.PROJECT_ID).toIntOrNull() != null) { "${Env.PROJECT_ID} には数字を設定してください。" }
    require(System.getenv(Env.LIMIT).toIntOrNull() != null) { "${Env.LIMIT} には数字を設定してください。" }
}