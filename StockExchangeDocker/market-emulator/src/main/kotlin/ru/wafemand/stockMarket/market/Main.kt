package ru.wafemand.stockMarket.market

import ru.wafemand.stockMarket.repository.MongoStockRepository

/**
 * Usage: <port> <databaseConnectionString>
 */
fun main(args: Array<String>) =
    StockMarketService(
        MongoStockRepository(args[1])
    ).start(args[0].toInt())