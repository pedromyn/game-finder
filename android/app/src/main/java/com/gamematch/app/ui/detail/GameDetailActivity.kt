package com.gamematch.app.ui.detail

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.lifecycleScope
import com.gamematch.app.R
import com.gamematch.app.data.UserSession
import com.gamematch.app.data.api.apiCall
import com.gamematch.app.data.model.Game
import com.gamematch.app.databinding.ActivityGameDetailBinding
import com.gamematch.app.ui.common.BaseActivity
import com.gamematch.app.ui.common.ScreenState
import com.gamematch.app.ui.common.loadImage
import com.gamematch.app.ui.common.modeLabel
import kotlinx.coroutines.launch

/**
 * Tela 7: detalhes de um jogo (GET /games/{id}), com Intents implícitas para loja e compartilhamento
 * e o botão de curtir, que salva o jogo no perfil (PUT/DELETE /profiles/{nome}/likes/{id}).
 */
class GameDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityGameDetailBinding
    private lateinit var state: ScreenState
    private val userName by lazy { UserSession.userName(this) }
    private var liked = false

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
        loadLikeState(game)
    }

    private fun bind(game: Game) = with(binding) {
        val score = intent.getIntExtra(EXTRA_SCORE, NO_SCORE)
        ivCover.loadImage(game.imageUrl)
        tvName.text = game.name
        tvScore.isVisible = score != NO_SCORE
        tvScore.text = getString(R.string.detail_compatibility, score)
        tvDescription.text = game.description
        tvGenres.text = game.genres.joinToString(", ")
        tvPlatforms.text = game.platforms.joinToString(", ")
        tvMode.text = modeLabel(game.mode)
        tvLength.text = getString(if (game.shortGame) R.string.length_short else R.string.length_long)
        btnStore.setOnClickListener { openStore(game) }
        btnShare.setOnClickListener { share(game) }
        btnLike.setOnClickListener { toggleLike(game) }
    }

    /** Descobre se o jogo já foi curtido. Sem usuário na sessão (ou sem API), o coração não aparece. */
    private fun loadLikeState(game: Game) {
        val name = userName ?: return
        lifecycleScope.launch {
            apiCall { getProfile(name) }.onSuccess { profile ->
                setLiked(profile.likedGames.any { it.id == game.id })
                binding.btnLike.isVisible = true
            }
        }
    }

    private fun toggleLike(game: Game) {
        val name = userName ?: return
        val like = !liked
        binding.btnLike.isEnabled = false
        lifecycleScope.launch {
            apiCall { if (like) likeGame(name, game.id) else unlikeGame(name, game.id) }
                .onSuccess {
                    setLiked(like)
                    snackbar(getString(if (like) R.string.message_liked else R.string.message_unliked))
                }
                .onFailure { snackbar(it.message) }
            binding.btnLike.isEnabled = true
        }
    }

    private fun setLiked(value: Boolean) = with(binding.btnLike) {
        liked = value
        setImageResource(if (value) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
        val color = ContextCompat.getColor(context, if (value) R.color.like else R.color.text_secondary)
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
        contentDescription = getString(if (value) R.string.action_unlike else R.string.action_like)
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
        private const val NO_SCORE = -1

        /** [score] é a compatibilidade; fora de um ranking (ex.: vindo do perfil), não é mostrada. */
        fun newIntent(context: Context, gameId: Long, score: Int = NO_SCORE): Intent =
            Intent(context, GameDetailActivity::class.java)
                .putExtra(EXTRA_GAME_ID, gameId)
                .putExtra(EXTRA_SCORE, score)
    }
}
