package com.gamematch.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gamematch.app.data.model.Game
import com.gamematch.app.databinding.ItemLikedGameBinding
import com.gamematch.app.ui.common.loadImage

/** Jogos curtidos no perfil: card compacto com capa, nome e gêneros. */
class LikedGameAdapter(
    private val onClick: (Game) -> Unit,
) : ListAdapter<Game, LikedGameAdapter.ViewHolder>(Diff) {

    inner class ViewHolder(private val binding: ItemLikedGameBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(game: Game) = with(binding) {
            ivCover.loadImage(game.imageUrl)
            tvName.text = game.name
            tvGenres.text = game.genres.joinToString(" · ")
            root.setOnClickListener { onClick(game) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemLikedGameBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    private object Diff : DiffUtil.ItemCallback<Game>() {
        override fun areItemsTheSame(old: Game, new: Game) = old.id == new.id
        override fun areContentsTheSame(old: Game, new: Game) = old == new
    }
}
