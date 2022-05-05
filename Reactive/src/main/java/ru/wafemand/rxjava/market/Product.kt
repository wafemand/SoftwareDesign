package ru.wafemand.rxjava.market

import org.bson.Document

class Product(doc: Document) {
    val id: String = doc.getString("_id")
    val title: String = doc.getString("title")
    val usdPrice: Double = doc.getDouble("usdPrice")

    fun toString(currency: Currency): String = "Product{id=$id, title='$title', usdPrice=${convertFromUsd(usdPrice, currency)}, currency=$currency"
}