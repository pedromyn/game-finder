package com.gamematch.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gamematch.app.R
import com.gamematch.app.data.model.RankedGame
import com.gamematch.app.databinding.ItemRankedGameBinding
import com.gamematch.app.ui.common.loadCover

/** Lista do ranking: um card por jogo com capa, nome e compatibilidade. */
class RankedGameAdapter(
    private val onClick: (RankedGame) -> Unit,
) : ListAdapter<RankedGame, RankedGameAdapter.ViewHolder>(Diff) {

    inner class ViewHolder(private val binding: ItemRankedGameBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ranked: RankedGame, position: Int) = with(binding) {
            val context = root.context
            ivCover.loadCover(ranked.game.imageUrl)
            tvPosition.text = context.getString(R.string.ranking_position, position + 1)
            tvName.text = ranked.game.name
            tvGenres.text = ranked.game.genres.joinToString(" · ")
            tvScore.text = context.getString(R.string.score_percent, ranked.score)
            root.setOnClickListener { onClick(ranked) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemRankedGameBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), position)

    private object Diff : DiffUtil.ItemCallback<RankedGame>() {
        override fun areItemsTheSame(old: RankedGame, new: RankedGame) = old.game.id == new.game.id
        override fun areContentsTheSame(old: RankedGame, new: RankedGame) = old == new
    }
}
