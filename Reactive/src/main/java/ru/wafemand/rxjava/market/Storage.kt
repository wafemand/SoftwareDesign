package ru.wafemand.rxjava.market

import com.mongodb.rx.client.*
import org.bson.Document
import rx.Observable

class Storage {
    private fun database(): MongoDatabase = client.getDatabase("rxtest")

    private fun users(): MongoCollection<Document> = database().getCollection("users")

    private fun products(): MongoCollection<Document> = database().getCollection("products")

    fun addUser(user: Document): Observable<Success> = users().insertOne(user)

    fun addProduct(product: Document): Observable<Success> = products().insertOne(product)

    fun getUser(login: String): Observable<User> = users()
            .find()
            .toObservable()
            .filter { doc: Document -> doc["login"] == login }
            .map { doc -> User(doc) }

    val products: Observable<Product>
        get() = products().find().toObservable().map { doc: Document -> Product(doc) }

    companion object {
        private val client = createMongoClient()
        private fun createMongoClient(): MongoClient {
            return MongoClients.create("mongodb://localhost:27017")
        }
    }
}