package ru.wafemand.rxjava.market


enum class Currency {
    USD, EUR, RUB
}

fun convertFromUsd(value: Double, to: Currency): Double = when (to) {
    Currency.RUB -> value * 76.38
    Currency.USD -> value
    Currency.EUR -> value * 0.88
}