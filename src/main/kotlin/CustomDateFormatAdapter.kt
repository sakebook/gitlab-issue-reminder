import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.text.SimpleDateFormat
import java.util.*

class CustomDateFormatAdapter: JsonAdapter<Date>() {

    private val dateFormat = "yyyy-MM-dd"
    private val sdFormat = SimpleDateFormat(dateFormat, Locale.JAPAN)

    @Synchronized
    @Throws(Exception::class)
    override fun fromJson(reader: JsonReader): Date?  {
        if (reader.peek() == JsonReader.Token.NULL) return reader.nextNull()
        val string = reader.nextString()
        return sdFormat.parse(string)

    }

    @Synchronized
    @Throws(Exception::class)
    override fun toJson(writer: JsonWriter, value: Date?) {
        writer.value(sdFormat.format(value))
    }

}