package example.android.chatapp.models

/**
 * Created by sheri on 2/10/2018.
 */
class ChatHeadsDataClass {

    var name: String? = null
    var imgUri: String? = null
    var lastMessageTime: String? = null
    var lastMessage: String? = null

    constructor(name: String?, imgUri: String?, lastMessageTime: String?, lastMessage: String?) {
        this.name = name
        this.imgUri = imgUri
        this.lastMessageTime = lastMessageTime
        this.lastMessage = lastMessage
    }
}