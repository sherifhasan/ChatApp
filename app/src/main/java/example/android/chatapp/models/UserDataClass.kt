package example.android.chatapp.models

/**
 * Created by sheri on 2/10/2018.
 */
class UserDataClass {
    var name: String? = null
    var email: String? = null
    var id: String? = null
    var description: String? = null
    var photoUrl: String? = null

    constructor(name: String?, email: String?, id: String?, description: String?, photoUrl: String?) {
        this.name = name
        this.email = email
        this.id = id
        this.description = description
        this.photoUrl = photoUrl
    }
}