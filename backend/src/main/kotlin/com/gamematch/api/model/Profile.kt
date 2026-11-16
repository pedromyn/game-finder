package com.gamematch.api.model

import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.ManyToMany

/** Perfil de um usuário, criado no cadastro (nome + senha). O nome é o identificador. */
@Entity
class Profile(
    @Id
    var userName: String = "",
    /** Senha guardada só como hash BCrypt (nunca em texto puro e nunca enviada no JSON). */
    var passwordHash: String? = null,
    @ElementCollection(fetch = FetchType.EAGER)
    var favoriteGenres: MutableSet<String> = mutableSetOf(),
    @ManyToMany(fetch = FetchType.EAGER)
    var likedGames: MutableSet<Game> = mutableSetOf(),
    @Lob
    var photo: ByteArray? = null,
    var photoContentType: String? = null,
    /** Aumenta a cada foto nova; o app usa na URL para não mostrar a foto antiga do cache. */
    var photoVersion: Long = 0,
)
