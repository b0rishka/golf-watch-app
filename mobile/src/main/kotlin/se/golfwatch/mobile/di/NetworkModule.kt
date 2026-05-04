package se.golfwatch.mobile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient =
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                // Register for both application/json and text/plain —
                // raw.githubusercontent.com serves JSON files as text/plain.
                val lenientJson = Json { ignoreUnknownKeys = true }
                json(lenientJson)
                json(lenientJson, contentType = ContentType.Text.Plain)
            }
        }
}
