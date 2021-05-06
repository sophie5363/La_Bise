package com.example.labise.Model

class ChatUtilisateur {
    var nom : String? = null
    var prenom : String? = null
    var conversations : List<ChatConversationUtilisateur>? = null

    constructor()

    constructor(nom : String?, prenom : String?, conversation: List<ChatConversationUtilisateur>?) {
        this.nom = nom
        this.prenom = prenom
        this.conversations = conversation
    }
}