package de.jonas_thelemann.uni.gosoan

import com.google.gson.Gson
import de.jonas_thelemann.uni.gosoan.model.GosoanSensorEvent
import de.jonas_thelemann.uni.gosoan.network.getGosoanSensorEventAsFlatBuffersByteArray
import org.junit.BeforeClass
import org.junit.Test
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths
import java.security.SecureRandom
import kotlin.random.Random

class TestDataGenerator {
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
                        getRandomString(random, random.nextInt(9, 50)),
                        floatArrayOf(
                            random.nextFloat() + random.nextInt(0, 25),
                            random.nextFloat() + random.nextInt(
                                0,
                                25
                            )
                        ),
                        random.nextInt(0, 25),
                        1618618440000 + random.nextInt(0, 1000),
                    )
                )
            }
        }

        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        // https://www.baeldung.com/kotlin/random-alphanumeric-string
        private fun getRandomString(random: Random, length: Int): String {
            val bytes = ByteArray(length)
            random.nextBytes(bytes)

            return (bytes.indices)
                .map {
                    charPool[random.nextInt(charPool.size)]
                }.joinToString("")
        }
    }

    @Test
    fun gson() {
        val gson = Gson()
        val path = Paths.get("../../../test/test_data_json")

        Files.deleteIfExists(path)
        Files.createFile(path)

        for (gosoanSensorEvent in gosoanSensorEvents) {
            val byteArray = gson.toJson(gosoanSensorEvent).toByteArray()

            FileOutputStream(path.toFile(), true).use { fos ->
                fos.write(ByteBuffer.allocate(4).putInt(byteArray.size).array().plus(byteArray))
            }
        }
    }

    @ExperimentalUnsignedTypes
    @Test
    fun flatBuffers() {
        val path = Paths.get("../../../test/test_data_flatbuffers")

        Files.deleteIfExists(path)
        Files.createFile(path)

        for (gosoanSensorEvent in gosoanSensorEvents) {
            val byteArray = getGosoanSensorEventAsFlatBuffersByteArray(gosoanSensorEvent)

            FileOutputStream(path.toFile(), true).use { fos ->
                fos.write(ByteBuffer.allocate(4).putInt(byteArray.size).array().plus(byteArray))
            }
        }
    }
}