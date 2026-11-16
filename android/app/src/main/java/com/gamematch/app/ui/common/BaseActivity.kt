package com.gamematch.app.ui.common

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

    protected fun snackbar(message: String?) {
        Snackbar.make(findViewById(android.R.id.content), message.orEmpty(), Snackbar.LENGTH_SHORT).show()
    }
}
