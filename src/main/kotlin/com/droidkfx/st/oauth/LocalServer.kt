package com.droidkfx.st.oauth

import com.droidkfx.st.config.CallbackServerConfig
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.html.respondHtml
import io.ktor.server.netty.Netty
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.CompletableDeferred
import kotlinx.html.body
import kotlinx.html.h3
import kotlinx.html.head
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.title
import java.io.File
import java.io.FileInputStream
import java.net.URLDecoder
import java.security.KeyStore


class LocalServer(
    val callbackServerConfig: CallbackServerConfig
) {
    data class Result(val code: String?, val session: String?, val state: String?, val error: String?)

    private val logger = logger {}

    private val result = CompletableDeferred<Result>()
    private var server: EmbeddedServer<*, *>? = null

    fun awaitReponse(): CompletableDeferred<Result> {
        logger.trace { "awaitReponse" }
        if (server != null) {
            logger.debug { "Server already running" }
            return result
        }

        logger.debug { "Starting local server on port ${callbackServerConfig.port}" }
        server = embeddedServer(Netty, configure = { envConfig() }) {
            routing {
                get(callbackServerConfig.callbackPath) {
                    logger.debug { "Received callback" }
                    val code = URLDecoder.decode(call.request.queryParameters["code"], "UTF-8")
                    val session = call.request.queryParameters["session"]
                    val error = call.request.queryParameters["error"]
                    val state = call.request.queryParameters["state"]

                    // Show a simple page to user
                    call.respondHtml(HttpStatusCode.OK) {
                        head {
                            title {
                                +"DroidTrader - Oauth Page"
                            }
                        }
                        body {
                            h3 {
                                if (error == null) {
                                    +"Authorization complete"
                                } else {
                                    +"Authorization failed"
                                    span {
                                        +"Error: $error"
                                    }
                                }
                            }
                            p {
                                +"You can close this page at any time."
                            }
                        }
                    }

                    // Complete the result and stop the server shortly after
                    if (!result.isCompleted) {
                        logger.debug { "oauth callback result: $result" }
                        result.complete(Result(code, session, state, error))
                    }
                    // stop asynchronously to let response flush
                    // small delay avoids abrupt connection close
                    call.application.monitor.subscribe(ApplicationStopped) {
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
        logger.debug { "Local server started" }

        return result
    }

    fun stop() {
        logger.debug { "Stopping local server" }
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
