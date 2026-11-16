package com.gamematch.app.ui.preferences

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.GridLayout
import androidx.lifecycle.lifecycleScope
import com.gamematch.app.R
import com.gamematch.app.data.api.apiCall
import com.gamematch.app.data.model.PreferencesRequest
import com.gamematch.app.databinding.ActivityPreferencesBinding
import com.gamematch.app.ui.common.BaseActivity
import com.gamematch.app.ui.common.genreOptions
import com.gamematch.app.ui.loading.LoadingActivity
import kotlinx.coroutines.launch

/** Tela 4: coleta as preferências (plataforma, gêneros, modo e duração). */
class PreferencesActivity : BaseActivity() {

    private lateinit var binding: ActivityPreferencesBinding
    private val genreCheckBoxes = mutableListOf<CheckBox>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showBackButton()

        val userName = intent.getStringExtra(EXTRA_USER_NAME).orEmpty()
        binding.tvGreeting.text = getString(R.string.preferences_greeting, userName)
        createGenreCheckBoxes()
        checkFavoriteGenres(userName)
        binding.btnRecommend.setOnClickListener { submit(userName) }
    }

    /** Um CheckBox por gênero, em duas colunas. */
    private fun createGenreCheckBoxes() {
        genreOptions().forEach { genre ->
            val checkBox = CheckBox(this).apply { text = genre }
            val params = GridLayout.LayoutParams().apply {
                width = 0
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
            binding.gridGenres.addView(checkBox, params)
            genreCheckBoxes += checkBox
        }
    }

    /** Já deixa marcados os gêneros favoritos do perfil. Se a API falhar, a tela segue normalmente. */
    private fun checkFavoriteGenres(userName: String) {
        lifecycleScope.launch {
            apiCall { getProfile(userName) }.onSuccess { profile ->
                genreCheckBoxes
                    .filter { it.text in profile.favoriteGenres }
                    .forEach { it.isChecked = true }
            }
        }
    }

    private fun submit(userName: String) {
        val genres = genreCheckBoxes.filter { it.isChecked }.map { it.text.toString() }
        if (genres.isEmpty()) {
            snackbar(getString(R.string.error_genre_required))
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
