package com.example.labise.Model

class ChatConversation {

    var conversationName : String? = null
    var nomUser1 : String? = null
    var nomUser2 : String? = null

    constructor()

    constructor(conversationName: String?, nomUser1: String?, nomUser2 : String? ) {
        this.conversationName = conversationName
        this.nomUser1 = nomUser1
        this.nomUser2 = nomUser2
    }
}