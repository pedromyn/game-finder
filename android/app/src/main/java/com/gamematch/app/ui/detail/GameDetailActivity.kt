package com.gamematch.app.ui.detail

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.gamematch.app.R
import com.gamematch.app.data.api.apiCall
import com.gamematch.app.data.model.Game
import com.gamematch.app.databinding.ActivityGameDetailBinding
import com.gamematch.app.ui.common.BaseActivity
import com.gamematch.app.ui.common.ScreenState
import com.gamematch.app.ui.common.loadCover
import com.gamematch.app.ui.common.modeLabel
import kotlinx.coroutines.launch

/** Tela 5: detalhes de um jogo (GET /games/{id}), com Intents implícitas para loja e compartilhamento. */
class GameDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityGameDetailBinding
    private lateinit var state: ScreenState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showBackButton()
        state = ScreenState(binding.state, binding.content)
        load()
    }

    private fun load() {
        val id = intent.getLongExtra(EXTRA_GAME_ID, -1)
        state.loading()
        lifecycleScope.launch {
            apiCall { getGame(id) }
                .onSuccess(::show)
                .onFailure { state.error(it.message, ::load) }
        }
    }

    private fun show(game: Game) {
        bind(game)
        state.content()
    }

    private fun bind(game: Game) = with(binding) {
        val score = intent.getIntExtra(EXTRA_SCORE, -1)
        ivCover.loadCover(game.imageUrl)
        tvName.text = game.name
        tvScore.isVisible = score >= 0
        tvScore.text = getString(R.string.detail_compatibility, score)
        tvDescription.text = game.description
        tvGenres.text = game.genres.joinToString(", ")
        tvPlatforms.text = game.platforms.joinToString(", ")
        tvMode.text = modeLabel(game.mode)
        tvLength.text = getString(if (game.shortGame) R.string.length_short else R.string.length_long)
        btnStore.setOnClickListener { openStore(game) }
        btnShare.setOnClickListener { share(game) }
    }

    /** Intent implícita: abre a página da loja no navegador (ou no app da loja, se instalado). */
    private fun openStore(game: Game) {
        launch(Intent(Intent.ACTION_VIEW, Uri.parse(game.storeUrl)))
    }

    /** Intent implícita: compartilha o jogo como texto em qualquer app compatível. */
    private fun share(game: Game) {
        val send = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text, game.name, game.storeUrl))
        launch(Intent.createChooser(send, getString(R.string.share_chooser)))
    }

    private fun launch(implicitIntent: Intent) {
        try {
            startActivity(implicitIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.error_no_app, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val EXTRA_GAME_ID = "game_id"
        private const val EXTRA_SCORE = "score"

        fun newIntent(context: Context, gameId: Long, score: Int): Intent =
            Intent(context, GameDetailActivity::class.java)
                .putExtra(EXTRA_GAME_ID, gameId)
                .putExtra(EXTRA_SCORE, score)
    }
}
