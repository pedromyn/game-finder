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

    @Transactional(readOnly = true)
    fun find(userName: String): ProfileResponse = load(userName).toResponse()

    @Transactional
    fun updateGenres(userName: String, request: UpdateProfileRequest): ProfileResponse {
        val profile = load(userName)
        profile.favoriteGenres.clear()
        profile.favoriteGenres += request.favoriteGenres.map { it.trim() }.filter { it.isNotEmpty() }
        return profile.toResponse()
    }

    @Transactional
    fun updatePhoto(userName: String, file: MultipartFile): ProfileResponse {
        val type = file.contentType
        if (file.isEmpty || type == null || !type.startsWith("image/")) throw badRequest("Envie um arquivo de imagem.")
        val profile = load(userName)
        profile.photo = file.bytes
        profile.photoContentType = type
        profile.photoVersion++
        return profile.toResponse()
    }

    @Transactional(readOnly = true)
    fun photo(userName: String): ProfilePhoto {
        val profile = load(userName)
        val bytes = profile.photo ?: throw notFound("$userName ainda não tem foto.")
        return ProfilePhoto(bytes, profile.photoContentType ?: "image/jpeg")
    }

    @Transactional
    fun like(userName: String, gameId: Long): ProfileResponse {
        val game = games.findGame(gameId)
        val profile = load(userName)
        if (profile.likedGames.none { it.id == gameId }) profile.likedGames += game
        return profile.toResponse()
    }

    @Transactional
    fun unlike(userName: String, gameId: Long): ProfileResponse {
        val profile = load(userName)
        profile.likedGames.removeIf { it.id == gameId }
        return profile.toResponse()
    }

    /** Perfis só existem depois do cadastro (POST /auth/register). */
    private fun load(userName: String): Profile = profiles.findById(userName.trim())
        .orElseThrow { notFound("Perfil $userName não encontrado.") }
}
