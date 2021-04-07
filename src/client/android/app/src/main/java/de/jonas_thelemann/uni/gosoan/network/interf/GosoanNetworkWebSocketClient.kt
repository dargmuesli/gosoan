package de.jonas_thelemann.uni.gosoan.network.interf

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import timber.log.Timber
import java.net.URI
import java.util.concurrent.ConcurrentLinkedQueue

class GosoanNetworkWebSocketClient constructor(override val serverUri: URI) :
    WebSocketClient(serverUri), GosoanNetworkInterface {

    override val isGosoanOpen: Boolean get() = isOpen
    override val queue: ConcurrentLinkedQueue<ByteArray> = ConcurrentLinkedQueue()

    override var exception: Exception? = null

    override fun onOpen(handshakedata: ServerHandshake?) {
        Timber.i("WebSocket connection opened.")
        dequeue()
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

    override fun onError(e: Exception?) {
        if (e != null) {
            exception = e
        }
    }

    override fun start() {
        exception = null

        try {
            connect()
        } catch (e: Exception) {
            exception = e
        }
    }

    override fun stop() {
        close()
    }

    override fun sendBytes(byteArray: ByteArray) {
        send(byteArray.plus(0x0A))
    }
}