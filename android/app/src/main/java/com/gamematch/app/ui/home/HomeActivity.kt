package com.gamematch.app.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.gamematch.app.R
import com.gamematch.app.data.UserSession
import com.gamematch.app.data.api.ApiClient
import com.gamematch.app.data.api.apiCall
import com.gamematch.app.databinding.ActivityHomeBinding
import com.gamematch.app.ui.common.BaseActivity
import com.gamematch.app.ui.common.loadImage
import com.gamematch.app.ui.history.HistoryActivity
import com.gamematch.app.ui.preferences.PreferencesActivity
import com.gamematch.app.ui.profile.ProfileActivity
import com.gamematch.app.ui.welcome.WelcomeActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

/** Tela 3: início da sessão (depois de entrar): atalhos para recomendar, perfil, histórico e sair. */
class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userName = UserSession.userName(this) ?: return logout()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvGreeting.text = getString(R.string.home_greeting, userName)
        binding.btnRecommend.setOnClickListener {
            startActivity(PreferencesActivity.newIntent(this, userName))
        }
        binding.btnProfile.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
        binding.ivAvatar.setOnClickListener { binding.btnProfile.performClick() }
        binding.btnHistory.setOnClickListener { startActivity(Intent(this, HistoryActivity::class.java)) }
        binding.btnLogout.setOnClickListener { confirmLogout() }
    }

    /** Recarrega a foto sempre que a tela aparece (ela pode ter sido trocada no perfil). */
    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            apiCall { getProfile(userName) }.onSuccess { profile ->
                ApiClient.photoUrl(profile)?.let { binding.ivAvatar.loadImage(it, R.drawable.ic_avatar_placeholder) }
            }
        }
    }

    private fun confirmLogout() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.logout_title)
            .setNegativeButton(R.string.action_cancel, null)
            .setPositiveButton(R.string.action_logout) { _, _ -> logout() }
            .show()
    }

    private fun logout() {
        UserSession.clear(this)
        startFresh(Intent(this, WelcomeActivity::class.java))
    }
}
