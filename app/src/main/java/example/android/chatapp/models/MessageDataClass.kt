package example.android.chatapp.models

/**
 * Created by sheri on 2/10/2018.
 */
class MessageDataClass {
    var msgBody: String? = null
    var msgPublisher: String? = null
    var msgPublisherId: String? = null
    var photoUrl: String? = null

    constructor(msgBody: String?, msgPublisher: String?, msgPublisherId: String?, photoUrl: String?) {
        this.msgBody = msgBody
        this.msgPublisher = msgPublisher
        this.msgPublisherId = msgPublisherId
        this.photoUrl = photoUrl
    }
}