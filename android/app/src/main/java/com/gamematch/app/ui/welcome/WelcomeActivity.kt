package com.gamematch.app.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import com.gamematch.app.R
import com.gamematch.app.data.UserSession
import com.gamematch.app.databinding.ActivityWelcomeBinding
import com.gamematch.app.ui.common.BaseActivity
import com.gamematch.app.ui.history.HistoryActivity
import com.gamematch.app.ui.preferences.PreferencesActivity
import com.gamematch.app.ui.profile.ProfileActivity

/** Tela 1: pede o nome do usuário e inicia o fluxo. */
class WelcomeActivity : BaseActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Preenche com o último nome usado.
        binding.etName.setText(UserSession.userName(this))
        binding.etName.doAfterTextChanged { binding.tilName.error = null }

        binding.btnStart.setOnClickListener {
            readName()?.let { startActivity(PreferencesActivity.newIntent(this, it)) }
        }
        binding.btnProfile.setOnClickListener {
            readName()?.let { startActivity(Intent(this, ProfileActivity::class.java)) }
        }
        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    /** Valida o nome digitado e o guarda na sessão. Devolve null (e mostra o erro) se estiver vazio. */
    private fun readName(): String? {
        val name = binding.etName.text?.toString()?.trim().orEmpty()
        if (name.isEmpty()) {
            binding.tilName.error = getString(R.string.error_name_required)
            return null
        }
        UserSession.setUserName(this, name)
        return name
    }
}
