package com.gamematch.api.model

import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.ManyToMany

/** Perfil de um usuário, identificado pelo nome digitado na tela de boas-vindas. */
@Entity
class Profile(
    @Id
    var userName: String = "",
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
