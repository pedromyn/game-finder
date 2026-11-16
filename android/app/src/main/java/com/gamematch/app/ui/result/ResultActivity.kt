package com.gamematch.app.ui.result

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gamematch.app.R
import com.gamematch.app.adapter.RankedGameAdapter
import com.gamematch.app.data.api.apiCall
import com.gamematch.app.data.model.Recommendation
import com.gamematch.app.databinding.ActivityResultBinding
import com.gamematch.app.ui.common.BaseActivity
import com.gamematch.app.ui.common.ScreenState
import com.gamematch.app.ui.common.modeLabel
import com.gamematch.app.ui.detail.GameDetailActivity
import com.gamematch.app.ui.history.HistoryActivity
import kotlinx.coroutines.launch

/** Tela 6: ranking de jogos de uma recomendação (GET /recommendations/{id}). */
class ResultActivity : BaseActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var state: ScreenState

    private val adapter = RankedGameAdapter { ranked ->
        startActivity(GameDetailActivity.newIntent(this, ranked.game.id, ranked.score))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showBackButton()
        state = ScreenState(binding.state, binding.content)

        binding.rvGames.layoutManager = LinearLayoutManager(this)
        binding.rvGames.adapter = adapter
        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        load()
    }

    private fun load() {
        val id = intent.getLongExtra(EXTRA_RECOMMENDATION_ID, -1)
        state.loading()
        lifecycleScope.launch {
            apiCall { getRecommendation(id) }
                .onSuccess(::show)
                .onFailure { state.error(it.message, ::load) }
        }
    }

    private fun show(recommendation: Recommendation) {
        binding.tvTitle.text = recommendation.title
        val summary = getString(
            R.string.result_summary,
            recommendation.platform,
            recommendation.genres.joinToString(", "),
            modeLabel(recommendation.mode),
        )
        binding.tvSummary.text =
            if (recommendation.shortOnly) getString(R.string.result_summary_short, summary) else summary
        adapter.submitList(recommendation.results)
        state.content()
    }

    companion object {
        private const val EXTRA_RECOMMENDATION_ID = "recommendation_id"

        fun newIntent(context: Context, recommendationId: Long): Intent =
            Intent(context, ResultActivity::class.java).putExtra(EXTRA_RECOMMENDATION_ID, recommendationId)
    }
}
