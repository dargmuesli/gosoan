package de.jonas_thelemann.uni.gosoan.network

import com.google.flatbuffers.FlatBufferBuilder
import com.google.gson.Gson
import de.jonas_thelemann.uni.gosoan.BuildConfig
import de.jonas_thelemann.uni.gosoan.generated.GosoanSensorEventFB
import de.jonas_thelemann.uni.gosoan.model.*
import de.jonas_thelemann.uni.gosoan.network.interf.GosoanNetworkInterface
import de.jonas_thelemann.uni.gosoan.network.interf.GosoanNetworkTcpClient
import de.jonas_thelemann.uni.gosoan.network.interf.GosoanNetworkWebSocketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GosoanNetworkClient @Inject constructor() {
    private val networkInterfaces = mutableListOf<GosoanNetworkInterface>()
    private val sensorIdTransmissionConfigurationMap =
        mutableMapOf<String, GosoanTransmissionConfiguration>()


    @ExperimentalUnsignedTypes
    fun send(gosoanSensorEvent: GosoanSensorEvent) {
        val transmissionConfiguration = sensorIdTransmissionConfigurationMap[GosoanSensor.getId(
            gosoanSensorEvent.sensorName,
            gosoanSensorEvent.sensorType
        )]
            ?: return
        val byteArray = when (transmissionConfiguration.dataFormat) {
            GosoanDataFormat.FlatBuffers -> getGosoanSensorEventAsFlatBuffersByteArray(
                gosoanSensorEvent
            )
            GosoanDataFormat.JSON -> Gson().toJson(gosoanSensorEvent).toByteArray()
        }

        transmissionConfiguration.networkInterface.enqueue(byteArray)
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

        var networkInterfaceUsed: GosoanNetworkInterface? = null

        for (networkInterface in networkInterfaces) {
            if (networkInterface.serverUri.host == serverIp && networkInterface.serverUri.port == port) {
                networkInterfaceUsed = networkInterface
                break
            }
        }

        if (networkInterfaceUsed == null) {
            networkInterfaceUsed = when (transmissionMethod) {
                GosoanTransmissionMethod.TCP -> GosoanNetworkTcpClient(URI("tcp://$serverIp:$port"))
                GosoanTransmissionMethod.WebSocket -> GosoanNetworkWebSocketClient(URI("http://$serverIp:$port"))
            }
        }

        networkInterfaces.add(networkInterfaceUsed)
        sensorIdTransmissionConfigurationMap[gosoanSensor.getId()] =
            GosoanTransmissionConfiguration(dataFormat, networkInterfaceUsed)
        CoroutineScope(Dispatchers.IO).launch {
            networkInterfaceUsed.start()
        }
    }

    fun teardownNetworkClients() {
        for (networkInterface in networkInterfaces) {
            networkInterface.stop()
        }

        networkInterfaces.clear()
        sensorIdTransmissionConfigurationMap.clear()
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