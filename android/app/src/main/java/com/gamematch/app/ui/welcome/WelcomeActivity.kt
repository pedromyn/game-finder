package com.gamematch.app.ui.welcome

import android.content.Intent
import android.os.Bundle
import com.gamematch.app.data.UserSession
import com.gamematch.app.databinding.ActivityWelcomeBinding
import com.gamematch.app.ui.auth.AuthActivity
import com.gamematch.app.ui.common.BaseActivity
import com.gamematch.app.ui.home.HomeActivity

/** Tela 1 (boas-vindas): escolhe entre entrar ou criar perfil. Quem já está logado vai direto para o início. */
class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (UserSession.userName(this) != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        val binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnLogin.setOnClickListener { startActivity(AuthActivity.newIntent(this, register = false)) }
        binding.btnRegister.setOnClickListener { startActivity(AuthActivity.newIntent(this, register = true)) }
    }
}
