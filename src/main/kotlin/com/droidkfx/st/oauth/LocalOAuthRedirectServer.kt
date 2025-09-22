package com.droidkfx.st.oauth

import com.droidkfx.st.config.CallbackServerConfig
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CompletableDeferred
import java.awt.Desktop
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.KeyStore


class LocalOAuthRedirectServer(
    val callbackServerConfig: CallbackServerConfig
) {
    data class Result(val code: String?, val session: String?, val state: String?, val error: String?)

    private val result = CompletableDeferred<Result>()
    private var server: EmbeddedServer<*, *>? = null

    fun awaitReponse(): CompletableDeferred<Result> {
        if (server != null) return result

        server = embeddedServer(Netty, configure = { envConfig() }) {
            routing {
                get(callbackServerConfig.callbackPath) {
                    val code = URLDecoder.decode(call.request.queryParameters["code"])
                    val session = call.request.queryParameters["session"]
                    val error = call.request.queryParameters["error"]
                    val state = call.request.queryParameters["state"]

                    // Show a simple page to user
                    call.respondText(
                        """
                        <html>
                          <body>
                            <h3>${if (error == null) "Authorization complete" else "Authorization failed"}</h3>
                            <p>This page will close automatically or you can close it at any time!.</p>
                            ${if (error != null) "<p>Error: $error</p>" else """
                                  <script type="text/javascript">
                                // This script will attempt to close the current window after 5 seconds (5000 milliseconds).
                                setTimeout(function() {
                                    window.close();
                                }, 5000); 
                            </script>
                            """.trimIndent()}
                          </body>
                        </html>
                        """.trimIndent(),
                        io.ktor.http.ContentType.Text.Html
                    )

                    // Complete the result and stop the server shortly after
                    if (!result.isCompleted) {
                        result.complete(Result(code, session, state, error))
                    }
                    // stop asynchronously to let response flush
                    // small delay avoids abrupt connection close
                    call.application.environment.monitor.subscribe(ApplicationStopped) {
                        // no-op
                    }
                    // Stop the server on a separate thread
                    Thread {
                        Thread.sleep(200)
                        stop()
                    }.start()
                }
            }
        }.start(false)

        return result
    }

    fun stop() {
        server?.stop(gracePeriodMillis = 200, timeoutMillis = 1000)
        server = null
    }

    private fun ApplicationEngine.Configuration.envConfig() {
        val keyStore = KeyStore.getInstance(callbackServerConfig.sslCertType).apply {
            FileInputStream(File(callbackServerConfig.sslCertLocation)).use {
                load(it, callbackServerConfig.sslCertPassword.toCharArray())
            }
        }
        sslConnector(
            keyStore = keyStore,
            keyAlias = callbackServerConfig.sslCertAlias,
            keyStorePassword = { callbackServerConfig.sslCertPassword.toCharArray() },
            privateKeyPassword = { callbackServerConfig.sslCertPassword.toCharArray() }) {
            port = callbackServerConfig.port
            keyStorePath = File(callbackServerConfig.sslCertLocation)
        }
    }
}
