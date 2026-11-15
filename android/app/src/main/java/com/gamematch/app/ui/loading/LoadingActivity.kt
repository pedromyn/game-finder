package com.gamematch.app.ui.loading

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.IntentCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.gamematch.app.R
import com.gamematch.app.data.api.apiCall
import com.gamematch.app.data.model.PreferencesRequest
import com.gamematch.app.databinding.ActivityLoadingBinding
import com.gamematch.app.ui.common.BaseActivity
import com.gamematch.app.ui.common.ScreenState
import com.gamematch.app.ui.result.ResultActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Tela 3: envia as preferências (POST /recommendations) e mostra o progresso. */
class LoadingActivity : BaseActivity() {

    private lateinit var binding: ActivityLoadingBinding
    private lateinit var state: ScreenState
    private lateinit var preferences: PreferencesRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        state = ScreenState(binding.state)

        preferences = IntentCompat.getSerializableExtra(intent, EXTRA_PREFERENCES, PreferencesRequest::class.java)
            ?: return finish()
        binding.btnBack.setOnClickListener { finish() }
        requestRecommendation()
    }

    private fun requestRecommendation() {
        binding.btnBack.isVisible = false
        state.loading(getString(R.string.loading_recommendation))
        lifecycleScope.launch {
            // A API local responde em milissegundos; a pausa deixa o usuário perceber o carregamento.
            delay(MIN_LOADING_MS)
            apiCall { createRecommendation(preferences) }
                .onSuccess {
                    startActivity(ResultActivity.newIntent(this@LoadingActivity, it.id))
                    finish() // "voltar" no resultado retorna às preferências, não a esta tela
                }
                .onFailure {
                    state.error(it.message, ::requestRecommendation)
                    binding.btnBack.isVisible = true
                }
        }
    }

    companion object {
        private const val EXTRA_PREFERENCES = "preferences"
        private const val MIN_LOADING_MS = 900L

        fun newIntent(context: Context, preferences: PreferencesRequest): Intent =
            Intent(context, LoadingActivity::class.java).putExtra(EXTRA_PREFERENCES, preferences)
    }
}
