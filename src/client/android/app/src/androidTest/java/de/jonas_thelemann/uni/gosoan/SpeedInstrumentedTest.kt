package de.jonas_thelemann.uni.gosoan

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import de.jonas_thelemann.uni.gosoan.model.GosoanSensorEvent
import de.jonas_thelemann.uni.gosoan.network.getGosoanSensorEventAsFlatBuffersByteArray
import de.jonas_thelemann.uni.gosoan.network.interf.GosoanNetworkInterface
import de.jonas_thelemann.uni.gosoan.network.interf.GosoanNetworkTcpClient
import de.jonas_thelemann.uni.gosoan.network.interf.GosoanNetworkWebSocketClient
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.net.URI
import java.security.SecureRandom
import kotlin.random.Random
import kotlin.system.measureTimeMillis

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class SpeedInstrumentedTest {
    companion object {
        private val gosoanSensorEvents = mutableListOf<GosoanSensorEvent>()

        @BeforeClass
        @JvmStatic
        fun setup() {
            val random = Random(0)

            for (i in 0 until 99999) {
                gosoanSensorEvents.add(
                    GosoanSensorEvent(
                        random.nextInt(0, 100),
                        getRandomString(random.nextInt(9, 50)),
                        floatArrayOf(
                            random.nextFloat() + random.nextInt(0, 25),
                            random.nextFloat() + random.nextInt(0, 25)
                        ),
                        random.nextInt(0, 25),
                        1618618440000 + random.nextInt(0, 1000),
                    )
                )
            }
        }

        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        // https://www.baeldung.com/kotlin/random-alphanumeric-string
        private fun getRandomString(length: Int): String {
            val random = SecureRandom()
            val bytes = ByteArray(length)
            random.nextBytes(bytes)

            return (bytes.indices)
                .map {
                    charPool[random.nextInt(charPool.size)]
                }.joinToString("")
        }
    }

    @ExperimentalUnsignedTypes
    fun getFlatBuffers(): List<ByteArray> {
        val byteArrays = mutableListOf<ByteArray>()

        for (gosoanSensorEvent in gosoanSensorEvents) {
            byteArrays.add(getGosoanSensorEventAsFlatBuffersByteArray(gosoanSensorEvent))
        }

        return byteArrays
    }

    fun getJson(gson: Gson): List<ByteArray> {
        val byteArrays = mutableListOf<ByteArray>()

        for (gosoanSensorEvent in gosoanSensorEvents) {
            byteArrays.add(gson.toJson(gosoanSensorEvent).toByteArray())
        }

        return byteArrays
    }

    @Test
    fun json() {
        assertEquals(-1, measureTimeMillis {
            getJson(Gson())
        })
    }

    @ExperimentalUnsignedTypes
    @Test
    fun flatBuffers() {
        assertEquals(-1, measureTimeMillis {
            getFlatBuffers()
        })
    }

    fun measureSendBytes(
        gosoanNetworkInterface: GosoanNetworkInterface,
        byteArrays: List<ByteArray>
    ): Long {
        gosoanNetworkInterface.start()

        val measurement = measureTimeMillis {
            for (byteArray in byteArrays) {
                if (!gosoanNetworkInterface.sendBytes(byteArray)) {
                    fail()
                }
            }
        }

        gosoanNetworkInterface.stop()

        return measurement
    }

    @Test
    fun webSocketsJson() {
        assertEquals(
            -1, measureSendBytes(
                GosoanNetworkWebSocketClient(URI("ws://${BuildConfig.DEFAULT_SERVER_IP_EMULATOR}:${BuildConfig.SERVER_PORT_WEBSOCKET_JSON}")),
                getJson(Gson())
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun webSocketsFlatBuffers() {
        assertEquals(
            -1, measureSendBytes(
                GosoanNetworkWebSocketClient(URI("ws://${BuildConfig.DEFAULT_SERVER_IP_EMULATOR}:${BuildConfig.SERVER_PORT_WEBSOCKET_FLATBUFFERS}")),
                getFlatBuffers()
            )
        )
    }

    @Test
    fun tcpJson() {
        assertEquals(
            -1, measureSendBytes(
                GosoanNetworkTcpClient(URI("tcp://${BuildConfig.DEFAULT_SERVER_IP_EMULATOR}:${BuildConfig.SERVER_PORT_TCP_JSON}")),
                getJson(Gson())
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun tcpFlatBuffers() {
        assertEquals(
            -1, measureSendBytes(
                GosoanNetworkTcpClient(URI("tcp://${BuildConfig.DEFAULT_SERVER_IP_EMULATOR}:${BuildConfig.SERVER_PORT_TCP_FLATBUFFERS}")),
                getFlatBuffers()
            )
        )
    }
}