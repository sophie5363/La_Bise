package com.example.labise.Model

class ChatContact {
    var email: String? = null
    var name: String? = null

    // Empty constructor needed for Firestore serialization
    constructor()

    constructor(email: String?, name: String?) {
        this.email = email
        this.name = name
    }

    companion object{

    }
}