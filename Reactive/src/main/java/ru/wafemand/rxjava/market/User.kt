package ru.wafemand.rxjava.market

import org.bson.Document

class User(doc: Document) {
    val id: String = doc["_id"].toString()
    val name: String = doc.getString("name")
    val login: String = doc.getString("login")
    val currency: Currency = Currency.valueOf(doc.getString("currency"))

    override fun toString(): String = "User{id=$id, name='$name', login='$login', currency=$currency}"
}