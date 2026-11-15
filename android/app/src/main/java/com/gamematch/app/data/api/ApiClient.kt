package com.gamematch.app.data.api

import com.gamematch.app.BuildConfig
import com.google.gson.JsonParser
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/** Instância única do Retrofit usada por todo o app. */
object ApiClient {
    val api: GameMatchApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GameMatchApi::class.java)
    }
}

/**
 * Executa uma chamada à API e devolve um [Result]. Em caso de falha, a exceção
 * já vem com uma mensagem pronta para mostrar ao usuário.
 *
 * Uso: `apiCall { getRecommendations() }.onSuccess { ... }.onFailure { ... }`
 */
suspend fun <T> apiCall(request: suspend GameMatchApi.() -> T): Result<T> =
    try {
        Result.success(ApiClient.api.request())
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        Result.failure(Exception(serverMessage(e) ?: "O servidor respondeu com erro ${e.code()}."))
    } catch (e: IOException) {
        Result.failure(Exception("Não foi possível conectar à API. Verifique se o backend está rodando."))
    } catch (e: Exception) {
        Result.failure(Exception("Ocorreu um erro inesperado."))
    }

/** Lê o campo "message" do JSON de erro do Spring, se houver. */
private fun serverMessage(e: HttpException): String? = runCatching {
    val body = e.response()?.errorBody()?.string().orEmpty()
    JsonParser.parseString(body).asJsonObject.get("message").asString.takeIf { it.isNotBlank() }
}.getOrNull()
