package com.gamematch.app.ui.common

import android.view.View
import androidx.core.view.isVisible
import com.gamematch.app.databinding.ViewStateBinding

/**
 * Alterna uma tela entre carregando, erro, mensagem e conteúdo.
 * Usa o layout compartilhado view_state.xml; [content] é a view que só aparece quando os dados chegam.
 */
class ScreenState(
    private val state: ViewStateBinding,
    private val content: View? = null,
) {

    fun loading(message: String? = null) = show(progress = true, message = message)

    /** Mostra o erro com um botão para tentar de novo. */
    fun error(message: String?, onRetry: () -> Unit) {
        show(message = message, retry = true)
        state.btnRetry.setOnClickListener { onRetry() }
    }

    /** Mostra só um texto (ex.: lista vazia). */
    fun message(message: String) = show(message = message)

    fun content() {
        state.root.isVisible = false
        content?.isVisible = true
    }

    private fun show(progress: Boolean = false, message: String? = null, retry: Boolean = false) {
        content?.isVisible = false
        state.root.isVisible = true
        state.progress.isVisible = progress
        state.tvMessage.isVisible = message != null
        state.tvMessage.text = message
        state.btnRetry.isVisible = retry
    }
}
