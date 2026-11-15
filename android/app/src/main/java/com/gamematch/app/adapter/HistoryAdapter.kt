package com.gamematch.app.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gamematch.app.R
import com.gamematch.app.data.model.Recommendation
import com.gamematch.app.databinding.ItemHistoryBinding
import com.gamematch.app.ui.common.formatDate

/** Lista do histórico: um card por recomendação, com ações de favoritar, renomear e apagar. */
class HistoryAdapter(
    private val onOpen: (Recommendation) -> Unit,
    private val onToggleFavorite: (Recommendation) -> Unit,
    private val onRename: (Recommendation) -> Unit,
    private val onDelete: (Recommendation) -> Unit,
) : ListAdapter<Recommendation, HistoryAdapter.ViewHolder>(Diff) {

    inner class ViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Recommendation) = with(binding) {
            val context = root.context
            tvTitle.text = item.title
            tvSubtitle.text =
                context.getString(R.string.history_subtitle, item.userName, formatDate(item.createdAt))

            val top = item.results.firstOrNull()
            tvTopGame.isVisible = top != null
            if (top != null) {
                tvTopGame.text = context.getString(R.string.history_top_game, top.game.name, top.score)
            }

            btnFavorite.setImageResource(if (item.favorite) R.drawable.ic_star else R.drawable.ic_star_border)
            val starColor = if (item.favorite) R.color.star else R.color.text_secondary
            ImageViewCompat.setImageTintList(
                btnFavorite,
                ColorStateList.valueOf(ContextCompat.getColor(context, starColor)),
            )

            root.setOnClickListener { onOpen(item) }
            btnFavorite.setOnClickListener { onToggleFavorite(item) }
            btnRename.setOnClickListener { onRename(item) }
            btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    private object Diff : DiffUtil.ItemCallback<Recommendation>() {
        override fun areItemsTheSame(old: Recommendation, new: Recommendation) = old.id == new.id
        override fun areContentsTheSame(old: Recommendation, new: Recommendation) = old == new
    }
}
