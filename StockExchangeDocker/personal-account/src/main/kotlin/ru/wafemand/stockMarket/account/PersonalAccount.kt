package ru.wafemand.stockMarket.account

import io.ktor.http.*
import ru.wafemand.stockMarket.model.User
import ru.wafemand.stockMarket.repository.StockRepository
import ru.wafemand.stockMarket.http.request
import java.net.http.HttpResponse

class PersonalAccount(
    private val repository: StockRepository,
    private val stockMarketHost: String,
    private val stockMarketPort: Int
) {
    fun registerUser(login: String, name: String) {
        repository.addUser(User(login, name, 0.0, mutableMapOf()))
    }
    
    fun topUp(login: String, money: Double) {
        val user = repository.getUser(login) ?: throw Exception("No such user")
        repository.updateUser(login, user.apply { this.money += money })
    }
    
    fun getAllStocks(login: String): Map<String, Pair<Int, Double>> {
        val user = repository.getUser(login) ?: throw Exception("No such user")
        val result = mutableMapOf<String, Pair<Int, Double>>()
        for (stock in user.stocks) {
            result[stock.key] = stock.value to repository.getCompany(stock.key)!!.price
        }
        return result
    }
    
    fun getSumMoney(login: String): Double {
        val user = repository.getUser(login) ?: throw Exception("No such user")
        return user.money + getAllStocks(login).entries.sumOf { it.value.first * it.value.second }
    } 
    
    fun buyStocks(login: String, company: String, count: Int) {
        val response = request("/stocks/buy", 
            "login" to login,
            "company" to company,
            "count" to count
        )
        checkResult(response)
    }

    fun sellStocks(login: String, company: String, count: Int) {
        val response = request("/stocks/sell",
            "login" to login,
            "company" to company,
            "count" to count
        )
        checkResult(response)
    }
    
    private fun request(uri: String, vararg parameters: Pair<String, Any>) =
        request("$stockMarketHost:$stockMarketPort", uri, *parameters)

    private fun checkResult(response: HttpResponse<String>) {
        if (response.statusCode() != HttpStatusCode.OK.value) {
            throw Exception("Status ${response.statusCode()}: ${response.body()}")
        }
    }
}