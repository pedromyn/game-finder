package com.gamematch.app.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import com.gamematch.app.R
import com.gamematch.app.databinding.ActivityWelcomeBinding
import com.gamematch.app.ui.common.BaseActivity
import com.gamematch.app.ui.history.HistoryActivity
import com.gamematch.app.ui.preferences.PreferencesActivity

/** Tela 1: pede o nome do usuário e inicia o fluxo. */
class WelcomeActivity : BaseActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etName.doAfterTextChanged { binding.tilName.error = null }
        binding.btnStart.setOnClickListener { start() }
        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    private fun start() {
        val name = binding.etName.text?.toString()?.trim().orEmpty()
        if (name.isEmpty()) {
            binding.tilName.error = getString(R.string.error_name_required)
            return
        }
        startActivity(PreferencesActivity.newIntent(this, name))
    }
}
