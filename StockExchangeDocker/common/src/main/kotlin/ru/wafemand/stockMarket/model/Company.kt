package ru.wafemand.stockMarket.model

data class Company(
    var name: String,
    var stocksCount: Int,
    var price: Double
)