package com.gamematch.app.ui.history

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gamematch.app.R
import com.gamematch.app.adapter.HistoryAdapter
import com.gamematch.app.data.api.apiCall
import com.gamematch.app.data.model.Recommendation
import com.gamematch.app.data.model.UpdateRecommendationRequest
import com.gamematch.app.databinding.ActivityHistoryBinding
import com.gamematch.app.ui.common.BaseActivity
import com.gamematch.app.ui.common.ScreenState
import com.gamematch.app.ui.result.ResultActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import retrofit2.HttpException

/** Tela 6: histórico (GET /recommendations) com renomear/favoritar (PUT) e apagar (DELETE). */
class HistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var state: ScreenState
    private var items: List<Recommendation> = emptyList()

    private val adapter = HistoryAdapter(
        onOpen = { startActivity(ResultActivity.newIntent(this, it.id)) },
        onToggleFavorite = ::toggleFavorite,
        onRename = ::askNewTitle,
        onDelete = ::confirmDelete,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showBackButton()
        state = ScreenState(binding.state, binding.rvHistory)

        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter
        load()
    }

    private fun load() {
        state.loading()
        lifecycleScope.launch {
            apiCall { getRecommendations() }
                .onSuccess(::show)
                .onFailure { state.error(it.message, ::load) }
        }
    }

    private fun show(list: List<Recommendation>) {
        items = list
        adapter.submitList(list)
        if (list.isEmpty()) state.message(getString(R.string.history_empty)) else state.content()
    }

    private fun toggleFavorite(item: Recommendation) {
        val message = if (item.favorite) R.string.message_unfavorited else R.string.message_favorited
        update(item, UpdateRecommendationRequest(favorite = !item.favorite), message)
    }

    private fun askNewTitle(item: Recommendation) {
        val input = EditText(this).apply {
            setText(item.title)
            setSelectAllOnFocus(true) // digitar já substitui o título antigo
            hint = getString(R.string.rename_hint)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        }
        val container = FrameLayout(this).apply {
            setPadding(resources.getDimensionPixelSize(R.dimen.dialog_padding))
            addView(input)
        }
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.rename_title)
            .setView(container)
            .setNegativeButton(R.string.action_cancel, null)
            .setPositiveButton(R.string.action_save) { _, _ ->
                val title = input.text.toString().trim()
                if (title.isNotEmpty() && title != item.title) {
                    update(item, UpdateRecommendationRequest(title = title), R.string.message_renamed)
                }
            }
            .show()
    }

    /** PUT /recommendations/{id}: troca o item alterado na lista e avisa o usuário. */
    private fun update(item: Recommendation, changes: UpdateRecommendationRequest, successMessage: Int) {
        lifecycleScope.launch {
            apiCall { updateRecommendation(item.id, changes) }
                .onSuccess { updated ->
                    show(items.map { if (it.id == updated.id) updated else it })
                    snackbar(getString(successMessage))
                }
                .onFailure { snackbar(it.message) }
        }
    }

    private fun confirmDelete(item: Recommendation) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_title)
            .setMessage(getString(R.string.delete_message, item.title))
            .setNegativeButton(R.string.action_cancel, null)
            .setPositiveButton(R.string.action_delete) { _, _ -> delete(item) }
            .show()
    }

    /** DELETE /recommendations/{id}. */
    private fun delete(item: Recommendation) {
        lifecycleScope.launch {
            apiCall {
                val response = deleteRecommendation(item.id)
                if (!response.isSuccessful) throw HttpException(response)
            }
                .onSuccess {
                    show(items.filter { it.id != item.id })
                    snackbar(getString(R.string.message_deleted))
                }
                .onFailure { snackbar(it.message) }
        }
    }
}
