package com.auas.aad.rockpaperscissors.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.auas.aad.rockpaperscissors.R
import com.auas.aad.rockpaperscissors.database.GameRepository
import com.auas.aad.rockpaperscissors.model.Game
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

const val HISTORY_REQUEST_CODE = 100

class MainActivity : AppCompatActivity() {
    private lateinit var gameRepository: GameRepository
    private val mainScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        gameRepository = GameRepository(this)
        initViews()
    }

    private fun initViews() {
        ivRock.setOnClickListener { onSelectGameInput(Game.Choice.ROCK) }
        ivPaper.setOnClickListener { onSelectGameInput(Game.Choice.PAPER) }
        ivScissors.setOnClickListener { onSelectGameInput(Game.Choice.SCISSORS) }
        updateStatistics()
    }

    private fun onSelectGameInput(player: Game.Choice) {
        val computer = Game.Choice.values()[(Game.Choice.values().indices).random()]
        val game = createGame(player, computer)
        showResult(game)
        insertGameInDB(game)
        updateStatistics()
    }

    private fun insertGameInDB(game: Game){
        mainScope.launch{
            withContext(Dispatchers.IO){
                gameRepository.insertGame(game)
            }
            updateStatistics()
        }
    }

    private fun updateStatistics() {
        mainScope.launch {
            var win = 0
            var draw = 0
            var lose = 0
            withContext(Dispatchers.IO) {
                win = gameRepository.getNumberOfWins()
                draw = gameRepository.getNumberOfDraws()
                lose = gameRepository.getNumberOfLosses()
            }
            tvStatisticInfo.text = getString(R.string.statistic_info, win, draw, lose)
        }
    }

    private fun createGame(player: Game.Choice, computer: Game.Choice): Game {
        return Game(
            getDrawable(player),
            getDrawable(computer),
            getResult(player, computer),
            Date()
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                HISTORY_REQUEST_CODE ->  updateStatistics()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_open_history -> {
                startActivityForResult(Intent(this, HistoryActivity::class.java), HISTORY_REQUEST_CODE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getResult(player: Game.Choice, computer: Game.Choice): Game.Result {
        return if (player == computer)
             Game.Result.DRAW
        else if (
            player == Game.Choice.ROCK && computer == Game.Choice.SCISSORS ||
            player == Game.Choice.PAPER && computer == Game.Choice.ROCK ||
            player == Game.Choice.SCISSORS && computer == Game.Choice.PAPER
        )  Game.Result.WIN
        else  Game.Result.LOSS
    }

    private fun showResult(game: Game) {
        ivComputer.setImageDrawable(getDrawable(game.computerChoice))
        ivYou.setImageDrawable(getDrawable(game.playerChoice))
        when (game.result) {
            Game.Result.WIN -> tvResult.text = getString(R.string.win)
            Game.Result.LOSS -> tvResult.text = getString(R.string.loss)
            Game.Result.DRAW -> tvResult.text = getString(R.string.draw)
        }
    }

    private fun getDrawable(choice: Game.Choice): Int {
        return when (choice) {
            Game.Choice.ROCK -> R.drawable.rock
            Game.Choice.PAPER -> R.drawable.paper
            Game.Choice.SCISSORS -> R.drawable.scissors
        }
    }
}