package com.gamematch.app.ui.common

import android.content.Context
import android.widget.ImageView
import coil.load
import com.gamematch.app.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** Carrega a capa de um jogo a partir da URL, com fundo de placeholder. */
fun ImageView.loadCover(url: String) {
    load(url) {
        crossfade(true)
        placeholder(R.drawable.bg_cover_placeholder)
        error(R.drawable.bg_cover_placeholder)
    }
}

/** Traduz o modo vindo da API (SOLO, ONLINE, AMBOS) para um texto de tela. */
fun Context.modeLabel(mode: String): String = getString(
    when (mode) {
        "SOLO" -> R.string.mode_solo
        "ONLINE" -> R.string.mode_online
        else -> R.string.mode_both
    },
)

private val displayFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

/** Converte a data ISO da API (2026-11-15T19:15:18) para dd/MM/yyyy HH:mm. */
fun formatDate(isoDate: String): String =
    runCatching { LocalDateTime.parse(isoDate).format(displayFormat) }.getOrDefault(isoDate)
