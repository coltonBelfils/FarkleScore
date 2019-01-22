package com.coalbell.farklescrore

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.HapticFeedbackConstants
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val GAME_HISTORY_KEY = "gameHistoryKey"
        const val SHARED_PREFERENCES_SETTINGS_KEY = "sharedPreferencesSettingsKey"
        const val MIN_BOARD_SCORE_KEY = "minBoardScoreKey"
    }

    private lateinit var history: HistoryStackGame
    private lateinit var currentTurn: HistoryStackTurn
    private var minBoardScore: Int = 500

    private val oneButton: FarkleButton = FarkleButton(R.id.oneButton, R.id.oneCount, "One", 100, 1, 0)
    private val fiveButton: FarkleButton = FarkleButton(R.id.fiveButton, R.id.fiveCount, "Five", 50, 1, 0)
    private val threeKindButton: FarkleButton = FarkleButton(R.id.threeKindButton, R.id.threeKindCount, "Three of a Kind", -1, 3, 0)
    private val fourKindButton: FarkleButton = FarkleButton(R.id.fourKindButton, R.id.fourKindCount, "Four of a Kind", 1000, 4, 0)
    private val fiveKindButton: FarkleButton = FarkleButton(R.id.fiveKindButton, R.id.fiveKindCount, "Five of a Kind", 2000, 5, 0)
    private val sixKindButton: FarkleButton = FarkleButton(R.id.sixKindButton, R.id.sixKindCount, "Six of a Kind", 3000, 6, 0)
    private val straightButton: FarkleButton = FarkleButton(R.id.straightButton, R.id.straightCount, "Straight", 1500, 6, 0)
    private val threePairsButton: FarkleButton = FarkleButton(R.id.threePairButton, R.id.threePairCount, "Three Pair", 1500, 6, 0)
    private val twoTripletsButton: FarkleButton = FarkleButton(R.id.twoTripletsButton, R.id.twoTripletsCount, "Two Triplets", 2000, 6, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        history = savedInstanceState?.getParcelable(GAME_HISTORY_KEY) ?: run {
            HistoryStackGame().apply {
                push(HistoryStackTurn())
            }
        }
        getSharedPreferences(SHARED_PREFERENCES_SETTINGS_KEY, Context.MODE_PRIVATE).apply {
            minBoardScore = getInt(MIN_BOARD_SCORE_KEY, 500)
        }
        currentTurn = history.peek()!!
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbarFeedback)
        resetButtonValues()
        readIn()
        updateOutput()

        findViewById<Button>(R.id.undoButton).setOnLongClickListener {
            history = HistoryStackGame().apply {
                currentTurn = HistoryStackTurn()
                push(currentTurn)
            }
            resetButtonValues()
            updateOutput()
            true
        }
        findViewById<TextView>(R.id.threeKindSliderNumber).text = (threeKindSlider.progress + 1).toString()
        threeKindButton.value = (threeKindSlider.progress + 1) * 100
        findViewById<SeekBar>(R.id.threeKindSlider).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                findViewById<SeekBar>(R.id.threeKindSlider).performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                findViewById<TextView>(R.id.threeKindSliderNumber).text = (progress + 1).toString()
                threeKindButton.value = (progress + 1) * 100
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelable(GAME_HISTORY_KEY, history)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reset -> {
                history = HistoryStackGame().apply {
                    currentTurn = HistoryStackTurn()
                    push(currentTurn)
                }
                resetButtonValues()
                updateOutput()
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_feedback -> {
                startActivity(Intent(this, FeedbackActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(v: View?) {
        onClick(v?.id, true)
    }

    private fun onClick(id: Int?, write: Boolean) {
        when (id) {
            oneButton.id -> clickHandler(oneButton, write)
            fiveButton.id -> clickHandler(fiveButton, write)
            threeKindButton.id -> clickHandler(threeKindButton, write)
            fourKindButton.id -> clickHandler(fourKindButton, write)
            fiveKindButton.id -> clickHandler(fiveKindButton, write)
            sixKindButton.id -> clickHandler(sixKindButton, write)
            straightButton.id -> clickHandler(straightButton, write)
            threePairsButton.id -> clickHandler(threePairsButton, write)
            twoTripletsButton.id -> clickHandler(twoTripletsButton, write)
            R.id.endTurn -> {
                if(currentTurn.isEmpty()) {
                    var value = 0
                    var dice = 0
                    //dialog
                    val view = layoutInflater.inflate(R.layout.activity_pickup_dialog, null)
                    val inScore = view.findViewById<EditText>(R.id.pickupInputScore)
                    val inDice = view.findViewById<EditText>(R.id.pickupInputDice)

                    AlertDialog.Builder(this).also {

                        it.setView(view)
                        inScore.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {
                                if (TextUtils.isDigitsOnly(s)) {
                                    value = inScore.text.toString().toInt()
                                }
                            }

                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                            }

                        })
                        inDice.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {
                                if (TextUtils.isDigitsOnly(s)) {
                                    dice = inDice.text.toString().toInt()
                                }
                            }

                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            }

                        })
                        it.setPositiveButton(R.string.okay) {d, _ ->
                            if(value < 0 || value % 50 != 0) {
                                Toast.makeText(it.context, "Invalid score, score should be divisible by 50", Toast.LENGTH_LONG).show()
                            } else if(dice !in 0..6) {
                                Toast.makeText(it.context, "Invalid dice left, dice left should be between 0 and 6", Toast.LENGTH_LONG).show()
                            } else if(!inScore.text.isEmpty() && !inDice.text.isEmpty()) {
                                clickHandler(FarkleButton(-1, -1, "customInput", value, dice, 0), true)
                                d.dismiss()
                            }
                        }
                        it.setNegativeButton(R.string.cancal) {d, _ ->
                            d.dismiss()
                        }
                        val dialog = it.create()
                        dialog.show()
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                            if(value < 0 || value % 50 != 0) {
                                Toast.makeText(it.context, "Invalid score, score should be divisible by 50, value is $value", Toast.LENGTH_LONG).show()
                            } else if(dice !in 0..6) {
                                Toast.makeText(it.context, "Invalid dice left, dice left should be between 0 and 6", Toast.LENGTH_LONG).show()
                            } else if(!inScore.text.isEmpty() && !inDice.text.isEmpty()) {
                                clickHandler(FarkleButton(-1, -1, "customInput", value, 6 - dice, 0), true)
                                dialog.dismiss()
                            }
                        }
                    }
                } else {
                    if ((currentTurn.currentTurnScore + history.currentTotalScore) < minBoardScore) {
                        Toast.makeText(
                            this, "You must have at least $minBoardScore total points to end your turn", Toast.LENGTH_LONG).show()
                    } else {
                        history.currentTotalScore += currentTurn.currentTurnScore
                        currentTurn = HistoryStackTurn()
                        history.push(currentTurn)
                        resetButtonValues()
                        totalScore.text = history.currentTotalScore.toString()
                        turnScore.text = currentTurn.currentTurnScore.toString()
                        diceLeft.text = 6.toString()
                        updateOutput()
                    }
                }
            }
            R.id.farkle -> {
                history.peek()?.farkle = true
                currentTurn = HistoryStackTurn()
                history.push(currentTurn)
                resetButtonValues()
                turnScore.text = 0.toString()
                diceLeft.text = 6.toString()
            }
            R.id.undoButton -> undoHandler()
        }
    }

    private fun clickHandler(fBtn: FarkleButton, write: Boolean) {
        val numberDiceAvailable = ((currentTurn.currentDiceUsed / 6) * 6) + 6
        if(fBtn.name == "customInput" || !write || (currentTurn.currentDiceUsed + fBtn.diceNeeded) <= numberDiceAvailable) {
            if (write) {
                currentTurn.push(fBtn)
                fBtn.pressedCount++
                currentTurn.currentDiceUsed += fBtn.diceNeeded
                currentTurn.currentTurnScore += fBtn.value
            }
            updateOutput()

            if(fBtn.name != "customInput") findViewById<Button>(fBtn.textId).text = fBtn.pressedCount.toString()
        } else {
            Toast.makeText(this, "There are not enough dice left for a ${fBtn.name}", Toast.LENGTH_LONG).show()
        }
    }

    private fun undoHandler(): Boolean {
        val toUndo = currentTurn.pop() ?: run {
            history.pop()
            currentTurn = history.peek() ?: run {
                currentTurn = HistoryStackTurn()
                history.push(currentTurn)
                updateOutput()
                resetButtonValues()
                return false
            }
            history.currentTotalScore -= currentTurn.currentTurnScore
            updateOutput()
            resetButtonValues()
            return true
        }
        currentTurn.currentTurnScore -= toUndo.value
        currentTurn.currentDiceUsed -= toUndo.diceNeeded
        updateOutput()
        toUndo.pressedCount--
        return true
    }

    private fun readIn() {
        var read = currentTurn.deQueue()
        while (read != null) {
            clickHandler(read, false)
            read = currentTurn.deQueue()
        }
        currentTurn.resetQueue()
    }

    private fun updateOutput() {
        turnScore.text = currentTurn.currentTurnScore.toString()
        totalScore.text = history.currentTotalScore.toString()
        val numberDiceLeft = (((currentTurn.currentDiceUsed / 6) * 6) + 6) - currentTurn.currentDiceUsed
        diceLeft.text = if(currentTurn.isEmpty()) {
            endTurn.text = getString(R.string.pick_up_score)
            6.toString()
        } else {
            endTurn.text = getString(R.string.end_turn)
            numberDiceLeft.toString()
        }
    }

    private fun resetButtonValues() {
        oneButton.run {
            pressedCount = 0
            findViewById<Button>(textId).text = pressedCount.toString()
        }
        fiveButton.run {
            pressedCount = 0
            findViewById<Button>(textId).text = pressedCount.toString()
        }
        threeKindButton.run {
            pressedCount = 0
            findViewById<Button>(textId).text = pressedCount.toString()
        }
        fourKindButton.run {
            pressedCount = 0
            findViewById<Button>(textId).text = pressedCount.toString()
        }
        fiveKindButton.run {
            pressedCount = 0
            findViewById<Button>(textId).text = pressedCount.toString()
        }
        sixKindButton.run {
            pressedCount = 0
            findViewById<Button>(textId).text = pressedCount.toString()
        }
        straightButton.run {
            pressedCount = 0
            findViewById<Button>(textId).text = pressedCount.toString()
        }
        threePairsButton.run {
            pressedCount = 0
            findViewById<Button>(textId).text = pressedCount.toString()
        }
        twoTripletsButton.run {
            pressedCount = 0
            findViewById<Button>(textId).text = pressedCount.toString()
        }
    }
}
