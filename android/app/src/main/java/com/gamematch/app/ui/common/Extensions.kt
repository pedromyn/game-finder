package com.gamematch.app.ui.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.load
import com.gamematch.app.R
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** Carrega uma imagem da internet no ImageView, mostrando [placeholder] enquanto carrega (ou se falhar). */
fun ImageView.loadImage(url: String, @DrawableRes placeholder: Int = R.drawable.bg_cover_placeholder) {
    load(url) {
        crossfade(true)
        placeholder(placeholder)
        error(placeholder)    }
}

/** Traduz o modo vindo da API (SOLO, ONLINE, AMBOS) para um texto de tela. */
fun Context.modeLabel(mode: String): String = getString(
    when (mode) {
        "SOLO" -> R.string.mode_solo
        "ONLINE" -> R.string.mode_online
        else -> R.string.mode_both
    },
)

/** Gêneros disponíveis no app (lista única, usada nas preferências e no perfil). */
fun Context.genreOptions(): Array<String> = resources.getStringArray(R.array.genres)

private val displayFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

/** Converte a data ISO da API (2026-11-15T19:15:18) para dd/MM/yyyy HH:mm. */
fun formatDate(isoDate: String): String =
    runCatching { LocalDateTime.parse(isoDate).format(displayFormat) }.getOrDefault(isoDate)

/**
 * Lê uma imagem escolhida pelo usuário e devolve um JPEG reduzido (lado menor ~[maxSize] px),
 * para o upload ser leve. Devolve null se não conseguir ler. Chamar fora da thread principal.
 */
fun Context.readImageAsJpeg(uri: Uri, maxSize: Int = 512): ByteArray? = runCatching {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }

    var sampleSize = 1
    while (minOf(bounds.outWidth, bounds.outHeight) / (sampleSize * 2) >= maxSize) sampleSize *= 2

    val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
    val bitmap = contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
        ?: return null
    ByteArrayOutputStream().use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it)
        it.toByteArray()
    }
}.getOrNull()
