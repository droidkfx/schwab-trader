package com.droidkfx.st.schwab.oauth

import com.droidkfx.st.config.CallbackServerConfig
import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.http.HttpStatusCode
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.html.respondHtml
import io.ktor.server.netty.Netty
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    private val callbackServerConfig: ReadOnlyValueDataBinding<CallbackServerConfig>
) {
    data class Result(val code: String?, val session: String?, val state: String?, val error: String?)

    private val logger = logger {}

    private var result = CompletableDeferred<Result>()
    private var server: EmbeddedServer<*, *>? = null

    fun awaitResponse(): CompletableDeferred<Result> {
        logger.trace { "awaitResponse" }
        if (server != null) {
            logger.debug { "Server already running" }
            return result
        }

        result = CompletableDeferred()
        logger.debug { "Starting local server on port ${callbackServerConfig.value.port}" }
        server = embeddedServer(Netty, configure = { envConfig() }) {
            routing {
                get(callbackServerConfig.value.callbackPath) {
                    logger.debug { "Received callback" }
                    val code = call.request.queryParameters["code"]?.let { URLDecoder.decode(it, "UTF-8") }
                    val session = call.request.queryParameters["session"]
                    val error = call.request.queryParameters["error"]
                    val state = call.request.queryParameters["state"]

                    // Show a simple page to the user
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
                        val value = Result(code, session, state, error)
                        logger.debug { "oauth callback result: $value" }
                        result.complete(value)
                    }
                    // stop asynchronously to let response flush
                    // small delay avoids abrupt connection close
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(200)
                        stop()
                    }
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
        val cfg = callbackServerConfig.value
        val keyStore = KeyStore.getInstance(cfg.sslCertType).apply {
            FileInputStream(File(cfg.sslCertLocation)).use {
                load(it, cfg.sslCertPassword.toCharArray())
            }
        }
        sslConnector(
            keyStore = keyStore,
            keyAlias = cfg.sslCertAlias,
            keyStorePassword = { cfg.sslCertPassword.toCharArray() },
            privateKeyPassword = { cfg.sslCertPassword.toCharArray() }) {
            port = cfg.port
            keyStorePath = File(cfg.sslCertLocation)
        }
    }
}
