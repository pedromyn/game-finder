package com.gamematch.app.data

import android.content.Context
import androidx.core.content.edit

/**
 * Sessão: guarda o nome de quem entrou (login ou cadastro). O perfil na API é identificado pelo nome.
 * Fica salvo em SharedPreferences, então a pessoa continua logada depois de fechar o app.
 */
object UserSession {
    private const val PREFS = "session"
    private const val KEY_USER_NAME = "user_name"

    fun userName(context: Context): String? =
        prefs(context).getString(KEY_USER_NAME, null)

    fun setUserName(context: Context, userName: String) =
        prefs(context).edit { putString(KEY_USER_NAME, userName) }

    /** Sair: esquece o usuário logado. */
    fun clear(context: Context) = prefs(context).edit { remove(KEY_USER_NAME) }

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
