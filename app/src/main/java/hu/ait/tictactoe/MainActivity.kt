package hu.ait.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import hu.ait.tictactoe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    enum class Turn {
        NOUGHT,
        CROSS
    }

    private var noughtsTime = 0L
    private var crossesTime = 0L
    private var noughtsTimer: CountDownTimer? = null
    private var crossesTimer: CountDownTimer? = null

    private var firstTurn = Turn.CROSS
    private var currentTurn = Turn.CROSS

    private var crossesScore = 0
    private var noughtsScore = 0

    private var boardList = mutableListOf<Button>()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initBoard()
        startTimer(currentTurn)
    }

    private fun initBoard() {
        boardList.add(binding.a1)
        boardList.add(binding.a2)
        boardList.add(binding.a3)
        boardList.add(binding.b1)
        boardList.add(binding.b2)
        boardList.add(binding.b3)
        boardList.add(binding.c1)
        boardList.add(binding.c2)
        boardList.add(binding.c3)
    }

    fun boardTapped(view: View) {
        if (view !is Button)
            return
        addToBoard(view)

        if (checkForVictory(NOUGHT)) {
            noughtsScore++
            result("Noughts Win!")
        } else if (checkForVictory(CROSS)) {
            crossesScore++
            result("Crosses Win!")
        }

        if (fullBoard()) {
            result("Draw")
        } else {
            switchPlayer()
        }
    }

    private fun switchPlayer() {
        currentTurn = if (currentTurn == Turn.NOUGHT) Turn.CROSS else Turn.NOUGHT
        startTimer(currentTurn)
    }

    private fun checkForVictory(s: String): Boolean {
        // Horizontal Victory
        if (match(binding.a1, s) && match(binding.a2, s) && match(binding.a3, s))
            return true
        if (match(binding.b1, s) && match(binding.b2, s) && match(binding.b3, s))
            return true
        if (match(binding.c1, s) && match(binding.c2, s) && match(binding.c3, s))
            return true

        // Vertical Victory
        if (match(binding.a1, s) && match(binding.b1, s) && match(binding.c1, s))
            return true
        if (match(binding.a2, s) && match(binding.b2, s) && match(binding.c2, s))
            return true
        if (match(binding.a3, s) && match(binding.b3, s) && match(binding.c3, s))
            return true

        // Diagonal Victory
        if (match(binding.a1, s) && match(binding.b2, s) && match(binding.c3, s))
            return true
        if (match(binding.a3, s) && match(binding.b2, s) && match(binding.c1, s))
            return true

        return false
    }

    private fun match(button: Button, symbol: String): Boolean = button.text == symbol

    private fun result(title: String) {
        stopTimers()

        val message = "\nNoughts $noughtsScore\n\nCrosses $crossesScore"
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Reset") { _, _ ->
                resetBoard()
            }
            .setCancelable(false)
            .show()
    }

    private fun resetBoard() {
        stopTimers()

        for (button in boardList) {
            button.text = ""
        }

        firstTurn = if (firstTurn == Turn.NOUGHT) Turn.CROSS else Turn.NOUGHT
        currentTurn = firstTurn
        setTurnLabel()
        startTimer(currentTurn)
    }

    private fun fullBoard(): Boolean {
        for (button in boardList) {
            if (button.text == "")
                return false
        }
        return true
    }

    private fun addToBoard(button: Button) {
        if (button.text != "") {
            return
        }

        val symbol: String
        val textColor: Int

        if (currentTurn == Turn.NOUGHT) {
            symbol = NOUGHT
            textColor = resources.getColor(R.color.o_color)
        } else {
            symbol = CROSS
            textColor = resources.getColor(R.color.x_color)
        }

        button.text = symbol
        button.setTextColor(textColor)
        setTurnLabel()
        stopTimers()
    }

    private fun startTimer(turn: Turn) {
        val currentTime = System.currentTimeMillis()
        if (turn == Turn.NOUGHT) {
            noughtsTime = currentTime
            noughtsTimer?.cancel()
            noughtsTimer = createTimer(turn)
            noughtsTimer?.start()
            setTurnLabel()
        } else {
            crossesTime = currentTime
            crossesTimer?.cancel()
            crossesTimer = createTimer(turn)
            crossesTimer?.start()
            setTurnLabel()
        }
    }

    private fun createTimer(turn: Turn): CountDownTimer {
        return object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val elapsedTime = (System.currentTimeMillis() - if (turn == Turn.NOUGHT) noughtsTime else crossesTime) / 1000
                updateTimerDisplay(turn, elapsedTime)
            }

            override fun onFinish() {
            }
        }
    }

    private fun stopTimers() {
        noughtsTimer?.cancel()
        crossesTimer?.cancel()
    }

    private fun setTurnLabel() {
        val playerSymbol = if (currentTurn == Turn.NOUGHT) NOUGHT else CROSS
        val turnText = "Player $playerSymbol"
        binding.turnTV.text = turnText
    }

    private fun updateTimerDisplay(turn: Turn, elapsedTime: Long) {
        val playerSymbol = if (turn == Turn.NOUGHT) NOUGHT else CROSS
        val timerText = "Player $playerSymbol: $elapsedTime s"
        binding.timerTV.text = timerText
    }

    companion object {
        const val NOUGHT = "O"
        const val CROSS = "X"
    }
}
