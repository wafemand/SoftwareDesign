package ru.wafemand.stockMarket.market

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.wafemand.stockMarket.model.Company
import ru.wafemand.stockMarket.repository.StockRepository
import kotlin.random.Random

class StockMarketService(
    private val repository: StockRepository,
) {
    fun start(port: Int) {
        embeddedServer(Netty, port) {
            routing {
                addCompany()
                getCompany()
                allCompanies()
                addStocks()
                buyStocks()
                sellStocks()
            }
        }.start(wait = true)
    }

    private fun Routing.addCompany() = get("/companies/add") {
        try {
            val parameters = this.context.request.queryParameters
            val companyName = parameters["company"]!!
            val startPrice = parameters["startPrice"]!!.toDouble()
            if (repository.getCompany(companyName) != null)
                throw Exception("Company already exists: $companyName")

            repository.addCompany(Company(companyName, 0, startPrice))
            call.response.status(HttpStatusCode.OK)
        } catch (e: Exception) {
            call.respondText(status = HttpStatusCode.BadRequest) { e.localizedMessage }
        }
    }

    private fun Routing.getCompany() = get("/companies/get") {
        try {
            val parameters = this.context.request.queryParameters
            val companyName = parameters["company"]!!
            this.call.respondText(status = HttpStatusCode.OK) {
                repository.getCompany(companyName).toString()
            }
        } catch (e: Exception) {
            call.respondText(status = HttpStatusCode.BadRequest) { e.localizedMessage }
        }
    }

    private fun Routing.allCompanies() = get("/companies/all") {
        try {
            this.call.respondText(status = HttpStatusCode.OK) {
                repository.getAllCompanies().joinToString("\n")
            }
        } catch (e: Exception) {
            call.respondText(status = HttpStatusCode.BadRequest) { e.localizedMessage }
        }
    }

    private fun Routing.addStocks() = get("/stocks/add") {
        try {
            val parameters = this.context.request.queryParameters
            val companyName = parameters["company"]!!
            val count = parameters["count"]!!.toInt()
            val company = repository.getCompany(companyName) ?: throw Exception("No such company: $companyName")
            repository.updateCompany(companyName, company.apply {
                this.stocksCount += count
                this.price = updatePrice(this.price)
            })
            call.response.status(HttpStatusCode.OK)
        } catch (e: Exception) {
            call.respondText(status = HttpStatusCode.BadRequest) { e.localizedMessage }
        }
    }

    private fun Routing.buyStocks() = get("/stocks/buy") {
        try {
            val parameters = this.context.request.queryParameters
            val companyName = parameters["company"]!!
            val count = parameters["count"]!!.toInt()
            val login = parameters["login"]!!

            val company = repository.getCompany(companyName) ?: throw Exception("No such company: $companyName")
            val user = repository.getUser(login) ?: throw Exception("No such user: $login")
            if (company.stocksCount < count)
                throw Exception("No stocks in market")
            if (user.money < count * company.price)
                throw Exception("Not enough money")
            repository.updateUser(login, user.apply {
                if (this.stocks.containsKey(companyName)) {
                    this.stocks[companyName] = this.stocks[companyName]!! + count
                } else {
                    this.stocks[companyName] = count
                }
                this.money -= count * company.price
            })
            repository.updateCompany(companyName, company.apply {
                this.stocksCount -= count
                this.price = updatePrice(this.price)
            })
            call.response.status(HttpStatusCode.OK)
        } catch (e: Exception) {
            call.respondText(status = HttpStatusCode.BadRequest) { e.localizedMessage }
        }
    }

    private fun Routing.sellStocks() = get("/stocks/sell") {
        try {
            val parameters = this.context.request.queryParameters
            val companyName = parameters["company"]!!
            val count = parameters["count"]!!.toInt()
            val login = parameters["login"]!!

            val company = repository.getCompany(companyName) ?: throw Exception("No such company: $companyName")
            val user = repository.getUser(login) ?: throw Exception("No such user: $login")
            if (!user.stocks.containsKey(companyName))
                throw Exception("User doesn't have stocks of $companyName")
            if (user.stocks[companyName]!! < count)
                throw Exception("User doesn't have enough stocks")
            repository.updateUser(login, user.apply {
                this.stocks[companyName] = this.stocks[companyName]!! - count
                this.money += count * company.price
            })
            repository.updateCompany(companyName, company.apply {
                this.stocksCount += count
                this.price = updatePrice(this.price)
            })
            call.response.status(HttpStatusCode.OK)
        } catch (e: Exception) {
            call.respondText(status = HttpStatusCode.BadRequest) { e.localizedMessage }
        }
    }

    private fun updatePrice(oldPrice: Double): Double {
        val direction = if (Random.nextBoolean()) 1 else -1
        val percent = Random.nextInt(0, 20).toDouble() / 100
        return oldPrice + direction * percent * oldPrice
    }
}