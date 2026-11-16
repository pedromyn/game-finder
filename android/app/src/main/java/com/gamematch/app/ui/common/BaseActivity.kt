package com.gamematch.app.ui.common

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

/** Base das telas: botão "voltar" da barra superior e mensagens rápidas (Snackbar). */
abstract class BaseActivity : AppCompatActivity() {

    protected fun showBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    /** Abre uma tela apagando as anteriores (usado ao entrar e ao sair: "voltar" não retorna à sessão anterior). */
    protected fun startFresh(intent: Intent) {
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }

    protected fun snackbar(message: String?) {
        Snackbar.make(findViewById(android.R.id.content), message.orEmpty(), Snackbar.LENGTH_SHORT).show()
    }
}
