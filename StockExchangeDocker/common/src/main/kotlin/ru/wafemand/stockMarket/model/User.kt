package ru.wafemand.stockMarket.model

data class User(
    var login: String,
    var name: String,
    var money: Double,
    var stocks: MutableMap<String, Int>
)