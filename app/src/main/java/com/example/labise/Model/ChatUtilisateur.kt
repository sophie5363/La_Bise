package com.example.labise.Model

class ChatUtilisateur {
    var nom : String? = null
    var prenom : String? = null
    var imageURL : String? = null

    constructor()

    constructor(nom : String?, prenom : String?, imageURL : String?) {
        this.nom = nom
        this.prenom = prenom
        this.imageURL = imageURL
    }
}