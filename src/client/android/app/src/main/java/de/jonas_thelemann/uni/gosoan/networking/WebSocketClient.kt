package de.jonas_thelemann.uni.gosoan.networking

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import timber.log.Timber
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketClient @Inject constructor(serverURI: URI) : WebSocketClient(serverURI) {
    override fun onOpen(handshakedata: ServerHandshake?) {
        Timber.i("WebSocket connection opened.")
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Timber.i(
            "WebSocket connection closed by %s. Code: %s. Reason: %s.", (if (remote) "remote peer" else "us"), code, reason
        )
    }

    override fun onMessage(message: String?) {
        Timber.i("WebSocket received message: $message")
    }

    override fun onError(ex: Exception?) {
        if (ex != null) {
            throw ex
        }
    }
}