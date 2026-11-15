package com.gamematch.api.model

import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id

@Entity
class Game(
    @Id
    var id: Long = 0,
    var name: String = "",
    @Column(length = 500)
    var description: String = "",
    @ElementCollection(fetch = FetchType.EAGER)
    var genres: MutableSet<String> = mutableSetOf(),
    @ElementCollection(fetch = FetchType.EAGER)
    var platforms: MutableSet<String> = mutableSetOf(),
    @Enumerated(EnumType.STRING)
    var mode: GameMode = GameMode.SOLO,
    /** true quando a campanha (ou a partida) é curta. */
    var shortGame: Boolean = false,
    var imageUrl: String = "",
    var storeUrl: String = "",
)
