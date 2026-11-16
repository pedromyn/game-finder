package com.gamematch.api.service

import com.gamematch.api.dto.AuthRequest
import com.gamematch.api.dto.ProfileResponse
import com.gamematch.api.dto.toResponse
import com.gamematch.api.model.Profile
import com.gamematch.api.repository.ProfileRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/** Cadastro e login. A senha é guardada como hash BCrypt. */
@Service
class AuthService(private val profiles: ProfileRepository) {

    private val encoder = BCryptPasswordEncoder()

    @Transactional
    fun register(request: AuthRequest): ProfileResponse {
        val userName = request.userName.trim()
        if (userName.isEmpty()) throw badRequest("Informe o nome do usuário.")
        if (request.password.length < MIN_PASSWORD) {
            throw badRequest("A senha precisa ter pelo menos $MIN_PASSWORD caracteres.")
        }

        // Perfis antigos (de antes do cadastro existir) ainda não têm senha: o cadastro os "assume".
        val existing = profiles.findById(userName).orElse(null)
        if (existing?.passwordHash != null) throw conflict("Já existe um perfil com o nome $userName.")

        val profile = existing ?: Profile(userName = userName)
        profile.passwordHash = encoder.encode(request.password)
        return profiles.save(profile).toResponse()
    }

    @Transactional(readOnly = true)
    fun login(request: AuthRequest): ProfileResponse {
        val profile = profiles.findById(request.userName.trim()).orElse(null)
        val hash = profile?.passwordHash
        // Mesma mensagem para nome inexistente e senha errada, para não revelar quais nomes existem.
        if (hash == null || !encoder.matches(request.password, hash)) {
            throw unauthorized("Nome ou senha incorretos.")
        }
        return profile.toResponse()
    }

    companion object {
        const val MIN_PASSWORD = 4
    }
}
