package de.jonas_thelemann.uni.gosoan.network.interf

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.runBlocking
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import timber.log.Timber
import java.net.URI
import java.util.concurrent.ConcurrentLinkedQueue

class GosoanNetworkWebSocketClient constructor(override val serverUri: URI) :
    WebSocketClient(serverUri), GosoanNetworkInterface {

    override val isGosoanOpen: Boolean get() = isOpen
    override val queue: ConcurrentLinkedQueue<ByteArray> = ConcurrentLinkedQueue()

    override var dataSent: Int = 0
    override var exception: Exception? = null

    private val handler: Handler = Handler(Looper.getMainLooper())
    private val reconnectTask = Runnable {
        reconnect()
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        Timber.i("WebSocket connection opened.")
        exception = null

        runBlocking {
            dequeue()
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Timber.i(
            "WebSocket connection closed by %s. Code: %s. Reason: %s.",
            (if (remote) "remote peer" else "us"),
            code,
            reason
        )
        exception = Exception("WebSocket closed")
        handler.postDelayed(reconnectTask, 1000)
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
        connectBlocking()
    }

    override fun stop() {
        closeBlocking()
        handler.removeCallbacks(reconnectTask)
    }

    @Synchronized
    override fun sendBytes(byteArray: ByteArray): Boolean {
        return try {
            send(byteArray)
            true
        } catch (e: java.lang.Exception) {
            close()
            exception = e
            handler.postDelayed(reconnectTask, 1000)
            false
        }
    }
}