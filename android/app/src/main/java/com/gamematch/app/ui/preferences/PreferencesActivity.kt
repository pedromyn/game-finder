package com.gamematch.app.ui.preferences

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import androidx.core.view.children
import com.gamematch.app.R
import com.gamematch.app.data.model.PreferencesRequest
import com.gamematch.app.databinding.ActivityPreferencesBinding
import com.gamematch.app.ui.common.BaseActivity
import com.gamematch.app.ui.loading.LoadingActivity
import com.google.android.material.snackbar.Snackbar

/** Tela 2: coleta as preferências (plataforma, gêneros, modo e duração). */
class PreferencesActivity : BaseActivity() {

    private lateinit var binding: ActivityPreferencesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showBackButton()

        val userName = intent.getStringExtra(EXTRA_USER_NAME).orEmpty()
        binding.tvGreeting.text = getString(R.string.preferences_greeting, userName)
        binding.btnRecommend.setOnClickListener { submit(userName) }
    }

    private fun submit(userName: String) {
        val genres = binding.gridGenres.children
            .filterIsInstance<CheckBox>()
            .filter { it.isChecked }
            .map { it.text.toString() }
            .toList()
        if (genres.isEmpty()) {
            Snackbar.make(binding.root, R.string.error_genre_required, Snackbar.LENGTH_SHORT).show()
            return
        }

        val preferences = PreferencesRequest(
            userName = userName,
            platform = binding.spPlatform.selectedItem.toString(),
            genres = genres,
            mode = if (binding.rbOnline.isChecked) "ONLINE" else "SOLO",
            shortOnly = binding.swShortOnly.isChecked,
        )
        startActivity(LoadingActivity.newIntent(this, preferences))
    }

    companion object {
        private const val EXTRA_USER_NAME = "user_name"

        fun newIntent(context: Context, userName: String): Intent =
            Intent(context, PreferencesActivity::class.java).putExtra(EXTRA_USER_NAME, userName)
    }
}
