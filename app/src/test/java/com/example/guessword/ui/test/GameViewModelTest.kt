package com.example.guessword.ui.test

import com.example.guessword.data.MAX_NO_OF_WORDS
import com.example.guessword.data.SCORE_INCREASE
import com.example.guessword.data.getUnscrambledWord
import com.example.guessword.ui.GameViewModel
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

class GameViewModelTest {
    private val viewModel = GameViewModel()

    @Test
    fun gameViewModel_Initialization_FirstWordLoaded() {

        val gameUiState = viewModel.uiState.value
        val unScrambledWord = getUnscrambledWord(gameUiState.currentScrambledWord)

        Assert.assertNotEquals(unScrambledWord, gameUiState.currentScrambledWord)
        Assert.assertTrue(gameUiState.currentWordCount == 1)
        Assert.assertTrue(gameUiState.score == 0)
        Assert.assertFalse(gameUiState.isGuessedWordWrong)
        Assert.assertFalse(gameUiState.isGameOver)
    }

    @Test
    fun gameViewModel_IncorrectGuess_ErrorFlagSet() {
        val incorrectPlayerWord = "and"

        viewModel.updateUserGuess(incorrectPlayerWord)
        viewModel.checkUserGuess()

        val currentGameUiState = viewModel.uiState.value
        Assert.assertEquals(0, currentGameUiState.score)
        Assert.assertTrue(currentGameUiState.isGuessedWordWrong)
    }

    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset() {
        var currentGameUiState = viewModel.uiState.value
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()
        currentGameUiState = viewModel.uiState.value

        Assert.assertFalse(currentGameUiState.isGuessedWordWrong)
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
    }

    @Test
    fun gameViewModel_WordSkipped_ScoreUnchangedAndWordCountIncreased() {
        var currentGameUiState = viewModel.uiState.value
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()
        currentGameUiState = viewModel.uiState.value
        val lastWordCount = currentGameUiState.currentWordCount

        viewModel.skipWord()
        currentGameUiState = viewModel.uiState.value
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
        assertEquals(lastWordCount + 1, currentGameUiState.currentWordCount)
    }

    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdatedCorrectly() {
        var expectedScore = 0
        var currentGameUiState = viewModel.uiState.value
        var correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

        repeat(MAX_NO_OF_WORDS) {
            expectedScore += SCORE_INCREASE
            viewModel.updateUserGuess(correctPlayerWord)
            viewModel.checkUserGuess()
            currentGameUiState = viewModel.uiState.value
            correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
            Assert.assertEquals(expectedScore, currentGameUiState.score)
        }
        assertEquals(MAX_NO_OF_WORDS, currentGameUiState.currentWordCount)
        Assert.assertTrue(currentGameUiState.isGameOver)
    }

    companion object {
        private const val SCORE_AFTER_FIRST_CORRECT_ANSWER = SCORE_INCREASE
    }
}
