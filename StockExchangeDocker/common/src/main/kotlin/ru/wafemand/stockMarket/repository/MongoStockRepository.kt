package ru.wafemand.stockMarket.repository

import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters
import org.bson.Document
import org.litote.kmongo.findOne
import ru.wafemand.stockMarket.model.Company
import ru.wafemand.stockMarket.model.User

class MongoStockRepository(
    connectionString: String,
) : StockRepository {
    companion object {
        const val DATABASE_NAME = "stockMarketDb"

        const val COMPANIES_COLLECTION = "companies"
        const val COMPANY_NAME = "name"
        const val COMPANY_STOCKS_COUNT = "stocksCount"
        const val COMPANY_STOCK_PRICE = "price"

        const val USERS_COLLECTION = "users"
        const val USERS_LOGIN = "login"
        const val USERS_NAME = "name"
        const val USERS_MONEY = "money"

        const val STOCKS_COLLECTION = "stocks"
        const val STOCKS_USER = "user"
        const val STOCKS_COMPANY = "company"
        const val STOCKS_COUNT = "count"
    }

    private val database = MongoClients.create(connectionString).getDatabase(DATABASE_NAME)

    init {
        val collections = database.listCollectionNames().toHashSet()
        if (!collections.contains(COMPANIES_COLLECTION))
            database.createCollection(COMPANIES_COLLECTION)
        if (!collections.contains(USERS_COLLECTION))
            database.createCollection(USERS_COLLECTION)
        if (!collections.contains(STOCKS_COLLECTION))
            database.createCollection(STOCKS_COLLECTION)
    }

    override fun getAllCompanies() = database.getCollection(COMPANIES_COLLECTION)
        .find().map {
            Company(
                it[COMPANY_NAME]!!.toString(),
                it[COMPANY_STOCKS_COUNT]!!.toString().toInt(),
                it[COMPANY_STOCK_PRICE]!!.toString().toDouble()
            )
        }.toList()

    override fun getCompany(name: String) = database.getCollection(COMPANIES_COLLECTION)
        .findOne(Filters.eq(COMPANY_NAME, name)).let {
            it ?: return@let null
            Company(
                name,
                it[COMPANY_STOCKS_COUNT]!!.toString().toInt(),
                it[COMPANY_STOCK_PRICE]!!.toString().toDouble()
            )
        }

    override fun addCompany(company: Company) {
        database.getCollection(COMPANIES_COLLECTION).insertOne(
            Document(mapOf(
                COMPANY_NAME to company.name,
                COMPANY_STOCKS_COUNT to company.stocksCount,
                COMPANY_STOCK_PRICE to company.price,
            ))
        )
    }

    override fun updateCompany(name: String, company: Company) {
        database.getCollection(COMPANIES_COLLECTION).replaceOne(
            Filters.eq(COMPANY_NAME, company.name),
            Document(mapOf(
                COMPANY_NAME to company.name,
                COMPANY_STOCKS_COUNT to company.stocksCount,
                COMPANY_STOCK_PRICE to company.price,
            ))
        )
    }

    override fun getAllUsers() = database.getCollection(USERS_COLLECTION)
        .find().map { u ->
            User(
                u[USERS_LOGIN]!!.toString(),
                u[USERS_NAME]!!.toString(),
                u[USERS_MONEY]!!.toString().toDouble(),
                database.getCollection(STOCKS_COLLECTION).find(
                    Filters.eq(STOCKS_USER, u[USERS_LOGIN]!!.toString())
                ).map {
                    it[STOCKS_COMPANY]!!.toString() to it[STOCKS_COUNT]!!.toString().toInt()
                }.toList().toMap().toMutableMap()
            )
        }.toList()

    override fun getUser(login: String) = database.getCollection(USERS_COLLECTION)
        .findOne(Filters.eq(USERS_LOGIN, login)).let { u ->
            u ?: return@let null
            User(
                u[USERS_LOGIN]!!.toString(),
                u[USERS_NAME]!!.toString(),
                u[USERS_MONEY]!!.toString().toDouble(),
                database.getCollection(STOCKS_COLLECTION).find(
                    Filters.eq(STOCKS_USER, u[USERS_LOGIN]!!.toString())
                ).map {
                    it[STOCKS_COMPANY]!!.toString() to it[STOCKS_COUNT]!!.toString().toInt()
                }.toList().toMap().toMutableMap()
            )
        }

    override fun addUser(user: User) {
        database.getCollection(USERS_COLLECTION).insertOne(
            Document(mapOf(
                USERS_LOGIN to user.login,
                USERS_NAME to user.name,
                USERS_MONEY to user.money
            ))
        )
    }

    override fun updateUser(login: String, user: User) {
        database.getCollection(USERS_COLLECTION).replaceOne(
            Filters.eq(USERS_LOGIN, user.login),
            Document(mapOf(
                USERS_LOGIN to user.login,
                USERS_NAME to user.name,
                USERS_MONEY to user.money
            ))
        )
        for (userStock in user.stocks) {
            val filters = Filters.and(
                Filters.eq(STOCKS_USER, login),
                Filters.eq(STOCKS_COMPANY, userStock.key),
            )
            if (database.getCollection(STOCKS_COLLECTION).findOne(filters) != null) {
                database.getCollection(STOCKS_COLLECTION).replaceOne(
                    filters,
                    Document(mapOf(
                        STOCKS_USER to login,
                        STOCKS_COMPANY to userStock.key,
                        STOCKS_COUNT to userStock.value
                    ))
                )
            } else {
                database.getCollection(STOCKS_COLLECTION).insertOne(
                    Document(mapOf(
                        STOCKS_USER to login,
                        STOCKS_COMPANY to userStock.key,
                        STOCKS_COUNT to userStock.value
                    ))
                )
            }
        }
    }

    override fun clearAll() = database.drop()
}