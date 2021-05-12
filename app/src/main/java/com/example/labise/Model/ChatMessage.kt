package com.example.labise.Model

class ChatMessage {

        var content: String? = null
        var date: String? = null
        var id: String? = null
        var is_read : Boolean? = null
        var sender_email : String? = null
        var name: String? = null
        var type : String? = null


        var photoUrl: String? = null
        var imageUrl: String? = null

        // Empty constructor needed for Firestore serialization
        constructor()

        constructor(content: String?, date : String?, id : String?, is_read : Boolean, name: String?, sender_email: String?, type: String?, photoUrl : String?) {
            this.content = content
            this.date = date
            this.id = id
            this.is_read = is_read
            this.sender_email = sender_email
            this.name = name
            this.type = type

            this.photoUrl = photoUrl
            this.imageUrl = imageUrl
        }
}