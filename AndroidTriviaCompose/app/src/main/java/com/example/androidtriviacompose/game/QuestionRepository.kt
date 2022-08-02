package com.example.androidtriviacompose.game

/**
 * This class is used by [GameScreen] to retrieve a random [Question] (our [nextQuestion] method)
 * and to check the answer that the user chooses (our [checkAnswer] method).
 */
class QuestionRepository {
    /**
     * This Data class holds a question to ask the user in its [text] field, and a list of possible
     * answers in its [answers] field.
     */
    data class Question(
        /**
         * The question we are asking the user.
         */
        val text: String,
        /**
         * The list of possible answers to the question in our [text] field. The first answer
         * in the list is the correct one.
         */
        val answers: List<String>
    )

    /**
     * Our list of [Question] questions. The first answer in the `answers` field of every [Question]
     * is the correct one. We randomize the answers before showing the text. All questions must have
     * four answers. We'd want these to contain references to string resources so we could
     * internationalize. (Or better yet, don't define the questions in code...)
     */
    private val questions: MutableList<Question> = mutableListOf(
        Question(text = "What is Android Jetpack?",
            answers = listOf("All of these", "Tools", "Documentation", "Libraries")),
        Question(text = "What is the base class for layouts?",
            answers = listOf("ViewGroup", "ViewSet", "ViewCollection", "ViewRoot")),
        Question(text = "What layout do you use for complex screens?",
            answers = listOf("ConstraintLayout", "GridLayout", "LinearLayout", "FrameLayout")),
        Question(text = "What do you use to push structured data into a layout?",
            answers = listOf("Data binding", "Data pushing", "Set text", "An OnClick method")),
        Question(text = "What method do you use to inflate layouts in fragments?",
            answers = listOf("onCreateView", "onActivityCreated", "onCreateLayout", "onInflateLayout")),
        Question(text = "What's the build system for Android?",
            answers = listOf("Gradle", "Graddle", "Grodle", "Groyle")),
        Question(text = "Which class do you use to create a vector drawable?",
            answers = listOf("VectorDrawable", "AndroidVectorDrawable", "DrawableVector", "AndroidVector")),
        Question(text = "Which one of these is an Android navigation component?",
            answers = listOf("NavController", "NavCentral", "NavMaster", "NavSwitcher")),
        Question(text = "Which XML element lets you register an activity with the launcher activity?",
            answers = listOf("intent-filter", "app-registry", "launcher-registry", "app-launcher")),
        Question(text = "What do you use to mark a layout for data binding?",
            answers = listOf("<layout>", "<binding>", "<data-binding>", "<dbinding>"))
    )

    /**
     * The current question that we are asking.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var currentQuestion: Question

    /**
     * The shuffled `answers` list field of the [Question] field [currentQuestion].
     */
    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var answers: MutableList<String>

    /**
     * Flag that indicates that the user has won the game by answering 3 questions correctly.
     */
    var gameWon: Boolean = false

    /**
     * Index into our [questions] field which we use to choose the next [currentQuestion]
     */
    private var questionIndex = 0

    /**
     * The number of questions we ask in each round of the game (at most 3).
     */
    private val numQuestions = ((questions.size + 1) / 2).coerceAtMost(3)

    /**
     * Initializes our state for a new game. First we shuffle our [MutableList] of [Question] field
     * [questions], set our pointer to the next question to ask, [questionIndex] to 0, set our
     * [gameWon] flag to `false`, set the current question, [currentQuestion] to the [Question] in
     * [questions] at the [questionIndex] index pointer, copy the [Question.answers] list of answers
     * of [currentQuestion] to our [answers] field, and then shuffle [answers].
     */
    fun initialize() {
        questions.shuffle()
        questionIndex = 0
        gameWon = false
        currentQuestion = questions[questionIndex]
        answers = currentQuestion.answers.toMutableList()
        answers.shuffle()
    }

    /**
     * Returns a [Question] constructed to have the [Question.text] question of [currentQuestion] and
     * the [Question.answers] field containing the shuffled answers in our [answers] field.
     *
     * @return the next [Question] to ask the user in its [Question.text] field, with suggested
     * answers in its [Question.answers] field.
     */
    fun nextQuestion(): Question {
        return Question(text = currentQuestion.text, answers = answers)
    }

    /**
     * Checks whether its [String] parameter [answer] matches the correct answer which is at index
     * 0 in the [Question.answers] list of [currentQuestion], saving the result in its [Boolean]
     * variable `val okay`. It then increments the pointer to the next question contained in
     * [questionIndex] and branches on whether it is less than our field [numQuestions]:
     *  - if it is less than [numQuestions] we set our new current [Question] field [currentQuestion]
     *  to the [Question] at index [questionIndex] in our [questions] list, set our [answers] field
     *  to a copy of the [Question.answers] list of [currentQuestion] and then shuffle [answers].
     *  - if [questionIndex] is greater than or equal to [numQuestions] we set our [gameWon] flag
     *  to `true` (whether the last answer was correct or not notice, but that is handled by our
     *  callers checking our return value before they check the value of [gameWon]).
     *
     * Finally we return `okay` to the caller.
     *
     * @param answer the answer chosen by the user.
     * @return `true` if [answer] is the correct answer, `false` if it is not the correct answer.
     */
    fun checkAnswer(answer: String): Boolean {
        val okay: Boolean = answer == currentQuestion.answers[0]
        questionIndex++
        if (questionIndex < numQuestions) {
            currentQuestion = questions[questionIndex]
            answers = currentQuestion.answers.toMutableList()
            answers.shuffle()
        } else {
            gameWon = true
        }
        return okay
    }

}