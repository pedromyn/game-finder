package com.gamematch.app.ui.common

import androidx.appcompat.app.AppCompatActivity

/** Base das telas: concentra o comportamento do botão "voltar" da barra superior. */
abstract class BaseActivity : AppCompatActivity() {

    protected fun showBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
