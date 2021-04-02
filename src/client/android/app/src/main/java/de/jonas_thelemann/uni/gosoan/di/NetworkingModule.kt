package de.jonas_thelemann.uni.gosoan.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.jonas_thelemann.uni.gosoan.BuildConfig
import de.jonas_thelemann.uni.gosoan.networking.WebSocketClient
import java.net.URI
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {
    @Singleton
    @Provides
    fun provideWebSocketClient(): WebSocketClient =
        WebSocketClient(URI("ws://" + BuildConfig.SERVER_IP + ":" + BuildConfig.SERVER_PORT_WEBSOCKET + "/echo"))
}