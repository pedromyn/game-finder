package com.gamematch.app.data

import android.content.Context
import androidx.core.content.edit

/**
 * Lembra o nome de quem está usando o app (o perfil na API é identificado pelo nome).
 * Fica salvo em SharedPreferences, então continua valendo depois de fechar o app.
 */
object UserSession {
    private const val PREFS = "session"
    private const val KEY_USER_NAME = "user_name"

    fun userName(context: Context): String? =
        prefs(context).getString(KEY_USER_NAME, null)

    fun setUserName(context: Context, userName: String) =
        prefs(context).edit { putString(KEY_USER_NAME, userName) }

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
