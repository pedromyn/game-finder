package com.gamematch.api.service

import com.gamematch.api.dto.ProfilePhoto
import com.gamematch.api.dto.ProfileResponse
import com.gamematch.api.dto.UpdateProfileRequest
import com.gamematch.api.dto.toResponse
import com.gamematch.api.model.Profile
import com.gamematch.api.repository.GameRepository
import com.gamematch.api.repository.ProfileRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class ProfileService(
    private val profiles: ProfileRepository,
    private val games: GameRepository,
) {

    /** Devolve o perfil; se ainda não existir, devolve um perfil vazio (sem salvar). */
    @Transactional(readOnly = true)
    fun find(userName: String): ProfileResponse {
        val key = key(userName)
        return profiles.findById(key).orElseGet { Profile(userName = key) }.toResponse()
    }

    @Transactional
    fun updateGenres(userName: String, request: UpdateProfileRequest): ProfileResponse {
        val profile = loadOrCreate(userName)
        profile.favoriteGenres.clear()
        profile.favoriteGenres += request.favoriteGenres.map { it.trim() }.filter { it.isNotEmpty() }
        return profile.toResponse()
    }

    @Transactional
    fun updatePhoto(userName: String, file: MultipartFile): ProfileResponse {
        val type = file.contentType
        if (file.isEmpty || type == null || !type.startsWith("image/")) throw badRequest("Envie um arquivo de imagem.")
        val profile = loadOrCreate(userName)
        profile.photo = file.bytes
        profile.photoContentType = type
        profile.photoVersion++
        return profile.toResponse()
    }

    @Transactional(readOnly = true)
    fun photo(userName: String): ProfilePhoto {
        val profile = profiles.findById(key(userName)).orElse(null)
        val bytes = profile?.photo ?: throw notFound("$userName ainda não tem foto.")
        return ProfilePhoto(bytes, profile.photoContentType ?: "image/jpeg")
    }

    @Transactional
    fun like(userName: String, gameId: Long): ProfileResponse {
        val game = games.findGame(gameId)
        val profile = loadOrCreate(userName)
        if (profile.likedGames.none { it.id == gameId }) profile.likedGames += game
        return profile.toResponse()
    }

    @Transactional
    fun unlike(userName: String, gameId: Long): ProfileResponse {
        val profile = loadOrCreate(userName)
        profile.likedGames.removeIf { it.id == gameId }
        return profile.toResponse()
    }

    private fun loadOrCreate(userName: String): Profile {
        val key = key(userName)
        return profiles.findById(key).orElseGet { profiles.save(Profile(userName = key)) }
    }

    private fun key(userName: String): String =
        userName.trim().ifEmpty { throw badRequest("Informe o nome do usuário.") }
}
