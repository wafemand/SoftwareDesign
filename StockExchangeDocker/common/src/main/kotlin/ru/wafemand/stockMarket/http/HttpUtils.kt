package ru.wafemand.stockMarket.http

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun request(host: String, uri: String, vararg parameters: Pair<String, Any>): HttpResponse<String> {
    val url = "http://$host$uri?${parameters.joinToString("&") { "${it.first}=${it.second}" }}"
    return HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI(url)).GET().build(), HttpResponse.BodyHandlers.ofString())
}