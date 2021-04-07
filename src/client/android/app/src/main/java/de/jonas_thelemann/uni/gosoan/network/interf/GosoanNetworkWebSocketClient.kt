package de.jonas_thelemann.uni.gosoan.network.interf

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import timber.log.Timber
import java.net.URI

class GosoanNetworkWebSocketClient constructor(override val serverUri: URI) :
    WebSocketClient(serverUri), GosoanNetworkInterface {

    override val isGosoanOpen: Boolean get() = isOpen

    override fun onOpen(handshakedata: ServerHandshake?) {
        Timber.i("WebSocket connection opened.")
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Timber.i(
            "WebSocket connection closed by %s. Code: %s. Reason: %s.",
            (if (remote) "remote peer" else "us"),
            code,
            reason
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

    override fun start() {
        println(serverUri)
        connect()
    }

    override fun stop() {
        close()
    }
}