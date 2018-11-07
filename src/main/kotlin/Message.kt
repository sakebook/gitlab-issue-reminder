class Message {
    var text: String? = null
    var attachments: ArrayList<Attachment>? = null
}

class Attachment {
    var author_icon: String? = null
    var author_link: String? = null
    var author_name: String? = null
    var color: String? = null
    var fallback: String? = null
//    var fields: ArrayList<Field> = arrayListOf()
    var footer: String? = null
    var footer_icon: String? = null
    var image_url: String? = null
    var pretext: String? = null
    var text: String? = null
    var thumb_url: String? = null
    var title: String? = null
    var title_link: String? = null
    var ts: Int? = null
}

class Field {
    var short: Boolean? = null
    var title: String? = null
    var value: String? = null
}