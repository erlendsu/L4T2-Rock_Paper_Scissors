package com.auas.aad.rockpaperscissors.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.auas.aad.rockpaperscissors.R
import com.auas.aad.rockpaperscissors.model.Game
import kotlinx.android.synthetic.main.result.view.*

class GameAdapter(private val games: List<Game>) : RecyclerView.Adapter<GameAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.result,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return games.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(games[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val context: Context = itemView.context.applicationContext
        fun bind(game: Game) {
            when (game.result) {
                Game.Result.LOSS -> itemView.tvTitleResult.text = context.getString(R.string.loss)
                Game.Result.DRAW -> itemView.tvTitleResult.text = context.getString(R.string.draw)
                Game.Result.WIN -> itemView.tvTitleResult.text = context.getString(R.string.win)
            }
            itemView.tvDateResult.text = game.date.toString()
            itemView.ivComputerResult.setImageDrawable(context.getDrawable(game.computerChoice))
            itemView.ivYouResult.setImageDrawable(context.getDrawable(game.playerChoice))
        }
    }
}