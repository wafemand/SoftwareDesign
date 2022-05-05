package ru.wafemand.rxjava.market

import com.mongodb.rx.client.Success
import io.reactivex.netty.protocol.http.server.HttpServer
import kotlin.jvm.JvmStatic
import org.bson.Document
import java.lang.RuntimeException
import rx.Observable

object Server {
    private val storage = Storage()

    @JvmStatic
    fun main(args: Array<String>) = HttpServer
            .newServer(8080)
            .start { req, resp ->
                try {
                    val method = req.decodedPath.substring(1)
                    val response = handleReq(method, req.queryParameters)
                    resp.writeString(response)
                } catch (e: RuntimeException) {
                    resp.writeString(Observable.just(e.toString()))
                }
            }
            .awaitShutdown()

    private fun handleReq(method: String, parameters: Map<String, List<String>>): Observable<String> {
        return when (method) {
            "createUser" -> createUser(parameters)
            "getUserInfo" -> getUserInfo(parameters)
            "createProduct" -> createProduct(parameters)
            "getProductList" -> getProductList(parameters)
            else -> throw RuntimeException("Unknown api method")
        }
    }

    private fun extract(parameters: Map<String, List<String>>, key: String): String = parameters.getOrElse(key) {
        throw RuntimeException("Expected $key parameter in query")
    }[0]

    private fun createUser(parameters: Map<String, List<String>>): Observable<String> = storage.addUser(
            Document()
                    .append("name", extract(parameters, "name"))
                    .append("login", extract(parameters, "login"))
                    .append("currency", extract(parameters, "currency"))
    ).map { obj: Success? -> obj.toString() }


    private fun getUserInfo(parameters: Map<String, List<String>>): Observable<String> =
            storage.getUser(extract(parameters, "login")).map { obj: User? -> obj.toString() }


    private fun createProduct(parameters: Map<String, List<String>>): Observable<String> = storage.addProduct(
            Document()
                    .append("title", extract(parameters, "title"))
                    .append("usdPrice", extract(parameters, "usdPrice"))
    ).map { obj: Success? -> obj.toString() }


    private fun getProductList(parameters: Map<String, List<String>>): Observable<String> =
            storage.getUser(extract(parameters, "login")).flatMap { u: User -> storage.products.map { p: Product -> p.toString(u.currency) } }
}