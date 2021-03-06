package de.jonas_thelemann.uni.gosoan.network

import com.google.flatbuffers.FlatBufferBuilder
import com.google.gson.Gson
import de.jonas_thelemann.uni.gosoan.BuildConfig
import de.jonas_thelemann.uni.gosoan.generated.GosoanSensorEventFB
import de.jonas_thelemann.uni.gosoan.model.GosoanDataFormat
import de.jonas_thelemann.uni.gosoan.model.GosoanSensor
import de.jonas_thelemann.uni.gosoan.model.GosoanSensorEvent
import de.jonas_thelemann.uni.gosoan.model.GosoanTransmissionMethod
import de.jonas_thelemann.uni.gosoan.network.interf.GosoanNetworkTcpClient
import de.jonas_thelemann.uni.gosoan.network.interf.GosoanNetworkWebSocketClient
import de.jonas_thelemann.uni.gosoan.repository.NetworkInterfaceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GosoanNetworkClient @Inject constructor(private val networkInterfaceRepository: NetworkInterfaceRepository) {
    private val gson = Gson()

    @ExperimentalUnsignedTypes
    fun send(gosoanSensorEvent: GosoanSensorEvent) {
        val transmissionConfiguration = networkInterfaceRepository.getTransmissionConfiguration(
            GosoanSensor.getId(
                gosoanSensorEvent.sensorName,
                gosoanSensorEvent.sensorType
            )
        )
            ?: return
        val byteArray = when (transmissionConfiguration.dataFormat) {
            GosoanDataFormat.FlatBuffers -> getGosoanSensorEventAsFlatBuffersByteArray(
                gosoanSensorEvent
            )
            GosoanDataFormat.JSON -> gson.toJson(gosoanSensorEvent).toByteArray()
        }

        runBlocking {
            transmissionConfiguration.networkInterface.enqueue(byteArray)
        }
    }

    fun setupNetworkClient(
        gosoanSensor: GosoanSensor,
        serverIp: String,
        dataFormat: GosoanDataFormat,
        transmissionMethod: GosoanTransmissionMethod
    ) {
        val port = when (transmissionMethod) {
            GosoanTransmissionMethod.TCP ->
                when (dataFormat) {
                    GosoanDataFormat.FlatBuffers -> BuildConfig.SERVER_PORT_TCP_FLATBUFFERS
                    GosoanDataFormat.JSON -> BuildConfig.SERVER_PORT_TCP_JSON
                }
            GosoanTransmissionMethod.WebSocket ->
                when (dataFormat) {
                    GosoanDataFormat.FlatBuffers -> BuildConfig.SERVER_PORT_WEBSOCKET_FLATBUFFERS
                    GosoanDataFormat.JSON -> BuildConfig.SERVER_PORT_WEBSOCKET_JSON
                }
        }

        var gosoanNetworkInterface =
            networkInterfaceRepository.getGosoanNetworkInterface(serverIp, port)

        if (gosoanNetworkInterface == null) {
            gosoanNetworkInterface = when (transmissionMethod) {
                GosoanTransmissionMethod.TCP -> GosoanNetworkTcpClient(URI("tcp://$serverIp:$port"))
                GosoanTransmissionMethod.WebSocket -> GosoanNetworkWebSocketClient(URI("ws://$serverIp:$port"))
            }

            networkInterfaceRepository.addNetworkInterface(gosoanNetworkInterface)

            CoroutineScope(Dispatchers.IO).launch {
                gosoanNetworkInterface.start()
            }
        }

        networkInterfaceRepository.addSensorIdTransmissionConfigurationMapping(
            gosoanSensor.getId(),
            dataFormat,
            gosoanNetworkInterface
        )
    }

    fun teardownNetworkClients() {
        networkInterfaceRepository.clear()
    }
}

@ExperimentalUnsignedTypes
fun getGosoanSensorEventAsFlatBuffersByteArray(
    gosoanSensorEvent: GosoanSensorEvent
): ByteArray {
    val fb = FlatBufferBuilder(128)
    val sensorNameOffset = fb.createString(gosoanSensorEvent.sensorName)
    val valuesOffset = GosoanSensorEventFB.createValuesVector(fb, gosoanSensorEvent.values)
    val gosoanSensorEventOffset = GosoanSensorEventFB.createGosoanSensorEventFB(
        fb,
        gosoanSensorEvent.sensorType,
        sensorNameOffset,
        valuesOffset,
        gosoanSensorEvent.accuracy,
        gosoanSensorEvent.timestamp
    )
    fb.finish(gosoanSensorEventOffset)
    return fb.sizedByteArray()
}