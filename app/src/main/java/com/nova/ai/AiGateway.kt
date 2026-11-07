package com.nova.ai

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

/**
 * Small HTTPS-only adapter for the NOVA backend contract.
 * POST {baseUrl}/v1/chat with {"message":"..."}; expect {"reply":"..."}.
 * Provider credentials stay on the server and are never shipped in the APK.
 */
object AiGateway {
    private val executor = Executors.newSingleThreadExecutor()

    fun send(baseUrl: String, message: String, callback: (String?) -> Unit) {
        executor.execute {
            var connection: HttpURLConnection? = null
            val reply = try {
                val url = URL("${baseUrl.trimEnd('/')}/v1/chat")
                require(url.protocol == "https") { "HTTPS is required" }
                connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 12_000
                    readTimeout = 20_000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    setRequestProperty("Accept", "application/json")
                }
                val payload = JSONObject().put("message", message).toString()
                connection.outputStream.use { it.write(payload.toByteArray(StandardCharsets.UTF_8)) }
                if (connection.responseCode !in 200..299) null
                else {
                    val body = connection.inputStream.bufferedReader().use { it.readText() }
                    JSONObject(body).optString("reply").takeIf { it.isNotBlank() }
                }
            } catch (_: Exception) {
                null
            } finally {
                connection?.disconnect()
            }
            callback(reply)
        }
    }
}
