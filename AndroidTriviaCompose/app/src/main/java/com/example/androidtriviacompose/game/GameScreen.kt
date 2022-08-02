package com.example.androidtriviacompose.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.androidtriviacompose.gameover.GameOverScreen
import com.example.androidtriviacompose.gamewon.GameWonScreen
import com.example.androidtriviacompose.game.QuestionRepository.Question
import com.example.androidtriviacompose.R
import com.example.androidtriviacompose.Routes

/**
 * Our singleton instance of [QuestionRepository] that we use to retrieve the [Question] we ask the
 * user (using the [QuestionRepository.nextQuestion] method) and use to check the answer the user
 * chooses with its [QuestionRepository.checkAnswer] method. Each [Question] instance we get holds
 * the question text in its [Question.text] field and the list of four suggested answers in its
 * [Question.answers] field.
 */
val questionRepository: QuestionRepository = QuestionRepository().also { it.initialize() }

/**
 * This is the screen that displays each [Question] asked the user, along with a radio group of
 * radio buttons that allow the user to choose one of four suggested answers to the question. At
 * the bottom of the screen there is a "Submit" button which will check the answer using the
 * [QuestionRepository.checkAnswer] method and navigate to the [GameOverScreen] if the method
 * returns `false`. If it returns `true` it checks the [QuestionRepository.gameWon] flag of
 * [questionRepository] and if it is `true` (the user has answered three questions correctly)
 * it navigates to the [GameWonScreen]. Otherwise it fetches the next [Question] using the
 * `nextQuestion` parameter of [GameScreenContent] (a lambda which sets the `nextQuestionToAsk`
 * [Question] to the [Question] returned by the [QuestionRepository.nextQuestion] method of
 * [questionRepository]) which causes the [GameScreenContent] composable to recompose to display
 * the new [Question].
 *
 * @param modifier a [Modifier] instance that our caller could use to modify our Composables (but
 * they don't do so, so the default [Modifier] is used instead).
 * @param navController the [NavHostController] we use to navigate to the [GameWonScreen] or to the
 * [GameOverScreen].
 */
@Preview
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val initialQuestion = questionRepository.nextQuestion()
    var nextQuestionToAsk: Question by remember {
        mutableStateOf(initialQuestion)
    }
    GameScreenContent(
        modifier = modifier,
        navController = navController,
        questionRepository = questionRepository,
        questionToAsk = nextQuestionToAsk,
        nextQuestion = { nextQuestionToAsk = questionRepository.nextQuestion() }
    )
}

/**
 * This is the content displayed by the [GameScreen] Composable, the indirection allows us to hoist
 * state to [GameScreen]. Its content consists of a column whose `modifier` adds a padding of 8dp
 * to the [Modifier] that is passed to [GameScreenContent] and adds [Modifier.verticalScroll] to
 * allow its contents to scroll. The `horizontalAlignment` parameter (horizontal alignment of its
 * children) is set to centered by [Alignment.CenterHorizontally]. The contents of the column
 * consists of an [Image], a [QuestionContent] composable to display our [Question] parameter
 * [questionToAsk], and a [Button] labeled with a "Submit" [Text] which when clicked will use the
 * [QuestionRepository.checkAnswer] method to check whether the user answered the question correctly
 * and if they did it will check if the [QuestionRepository.gameWon] flag indicated that the user
 * won the game and if so use [navController] to navigate to the [GameWonScreen], otherwise it will
 * call the [nextQuestion] lambda to move on the next question. If the user did not answer the
 * question correctly it will use [navController] to navigate to the [GameOverScreen] (setting the
 * `selectedId` to -1 and calling [QuestionRepository.initialize] to get ready for the next question
 * is necessary too).
 *
 * @param modifier a [Modifier] instance that our caller could use to modify our Composables (but
 * they don't do so, so the default [Modifier] is used instead).
 * @param navController the [NavHostController] we use to navigate to the [GameWonScreen] or to the
 * [GameOverScreen].
 * @param questionRepository the [QuestionRepository] singleton we use to keep track of questions,
 * answers, check the user's answers for correctness, and decide if the user has won the game.
 * @param questionToAsk the current [Question] with its answers that we should display in our
 * [QuestionContent].
 * @param nextQuestion the lambda we should call when the user answers a [Question] correctly to
 * fetch the next question from the [QuestionRepository].
 */
@Composable
fun GameScreenContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    questionRepository: QuestionRepository,
    questionToAsk: Question = dummy,
    nextQuestion: () -> Unit
) {
    /**
     * Which [RadioButton] of [QuestionContent] is currently selected, -1 indicates none selected.
     */
    var selectedId by remember {
        mutableStateOf(-1)
    }
    Column(
        modifier = modifier.padding(8.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.android_category_simple),
            contentDescription = null
        )
        QuestionContent(
            modifier = modifier,
            selectedId = selectedId,
            question = questionToAsk,
            changeSelection = { selectedId = it }
        )
        Button(onClick = {
            if (questionRepository.checkAnswer(questionToAsk.answers[selectedId])) {
                selectedId = -1
                if (questionRepository.gameWon) {
                    questionRepository.initialize()
                    navController.navigate(Routes.GameWon.route)
                }
                nextQuestion()
            } else {
                selectedId = -1
                questionRepository.initialize()
                navController.navigate(Routes.GameOver.route)
            }
        }
        ) {
            Text(
                text = stringResource(id = R.string.submit_button),
                fontSize = 18.sp
            )
        }
    }
}

/**
 * The Composable which displays a [Question], as well as 4 possible answers with [RadioButton]'s
 * which allow the user to select one of them. Its content consists of a [Column] with a `modifier`
 * of [Modifier.selectableGroup] to group its contents together for accessibility use, and the
 * content of the column consists of a [Text] displaying the [Question.text] question of our
 * [question] parameter, along with 4 [Row]'s to display the 4 answers of [Question.answers] in
 * [Text]'s with a [RadioButton] in each [Row] that the user can click to select that answer by
 * calling the [changeSelection] lambda with the ID of the [Row] (the [Text] in each [Row] is also
 * clickable to enble the answer to be selected by clicking on it as well).
 *
 * @param modifier a [Modifier] instance that our caller could use to modify our Composables. The
 * default [Modifier] passed to [GameScreenContent] is the same default [Modifier] used by
 * [GameScreen] with no modifications.
 * @param selectedId which of our [RadioButton] composables are currently selected, with -1
 * indicating that none are selected.
 * @param question the current [Question] and answers we should be diplaying.
 * @param changeSelection the lambda that each [RadioButton] should call with its ID when it is
 * clicked to change the [selectedId] to point to it.
 */
@Composable
fun QuestionContent(
    modifier: Modifier = Modifier,
    selectedId: Int = -1,
    question: Question = dummy,
    changeSelection: (Int) -> Unit = {}
) {
    Column(
        modifier = modifier.selectableGroup()
    ) {
        Text(
            text = question.text,
            fontSize = 30.sp
        )
        Row {
            val id = 0
            RadioButton(
                selected = selectedId == id,
                onClick = { changeSelection(id) }
            )
            Text(
                modifier = modifier
                    .padding(top = 12.dp)
                    .clickable { changeSelection(id) },
                text = question.answers[id]
            )
        }
        Row {
            val id = 1
            RadioButton(
                selected = selectedId == id,
                onClick = { changeSelection(id) }
            )
            Text(
                modifier = modifier
                    .padding(top = 12.dp)
                    .clickable { changeSelection(id) },
                text = question.answers[id]
            )
        }
        Row {
            val id = 2
            RadioButton(
                selected = selectedId == id,
                onClick = { changeSelection(id) }
            )
            Text(
                modifier = modifier
                    .padding(top = 12.dp)
                    .clickable { changeSelection(id) },
                text = question.answers[id]
            )
        }
        Row {
            val id = 3
            RadioButton(
                selected = selectedId == id,
                onClick = { changeSelection(id) }
            )
            Text(
                modifier = modifier
                    .padding(top = 12.dp)
                    .clickable { changeSelection(id) },
                text = question.answers[id]
            )
        }
    }
}

/**
 * A dummy [Question] to use for preview only.
 */
private val dummy = Question(
    text = "What color is the Android mascot?",
    answers = listOf("Blue", "Green", "Yellow", "Red")
)

