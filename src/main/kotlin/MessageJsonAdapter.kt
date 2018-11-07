import com.squareup.moshi.*

class MessageJsonAdapter(moshi: Moshi): JsonAdapter<Message>() {
    private val adapter = ListAttachmentJsonAdapter(moshi)
    override fun fromJson(reader: JsonReader): Message? = null
    override fun toJson(writer: JsonWriter, value: Message?) {
        writer.beginObject()
        writer.name("text")
        writer.value(value?.text)
        writer.name("attachments")
        adapter.toJson(writer, value?.attachments)
        writer.endObject()
    }
    companion object {
        val FACTORY: Factory = Factory { type, _, moshi ->
            val listType = Types.newParameterizedType(Message::class.java)
            if (type == listType) {
                return@Factory MessageJsonAdapter(moshi)
            }
            null
        }
    }
}

class ListAttachmentJsonAdapter(moshi: Moshi): JsonAdapter<List<Attachment>>() {
    private val adapter = AttachmentJsonAdapter(moshi)
    override fun fromJson(reader: JsonReader): List<Attachment>? = null
    override fun toJson(writer: JsonWriter, value: List<Attachment>?) {
        writer.beginArray()
        value?.forEach {
            adapter.toJson(writer, it)
        }
        writer.endArray()
    }
    companion object {
        val FACTORY: Factory = Factory { type, _, moshi ->
            val listType = Types.newParameterizedType(List::class.java, Attachment::class.java)
            if (type == listType) {
                return@Factory ListAttachmentJsonAdapter(moshi)
            }
            null
        }
    }
}

class AttachmentJsonAdapter(moshi: Moshi): JsonAdapter<Attachment>() {
    val adapter: JsonAdapter<Attachment> = moshi.adapter(Attachment::class.java)
    override fun fromJson(reader: JsonReader): Attachment? = null
    override fun toJson(writer: JsonWriter, value: Attachment?) {
        adapter.toJson(writer, value)
    }
    companion object {
        val FACTORY: Factory = Factory { type, _, moshi ->
            val listType = Types.newParameterizedType(Attachment::class.java)
            if (type == listType) {
                return@Factory AttachmentJsonAdapter(moshi)
            }
            null
        }
    }
}
