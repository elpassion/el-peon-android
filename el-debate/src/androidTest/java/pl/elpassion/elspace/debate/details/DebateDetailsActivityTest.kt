package pl.elpassion.elspace.debate.details

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.*
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.SingleSubject
import org.hamcrest.Matchers
import org.junit.*
import pl.elpassion.R
import pl.elpassion.elspace.common.*
import pl.elpassion.elspace.dabate.details.createDebateData
import pl.elpassion.elspace.dabate.details.createHttpException
import pl.elpassion.elspace.debate.comment.DebateCommentActivity
import pl.elpassion.elspace.debate.details.DebateDetailsActivity.Companion.intent

class DebateDetailsActivityTest {

    private val debateDetailsSubject = SingleSubject.create<DebateData>()
    private val sendVoteSubject = CompletableSubject.create()
    private val apiMock by lazy {
        mock<DebateDetails.Api>().apply {
            whenever(getDebateDetails(any())).thenReturn(debateDetailsSubject)
            whenever(vote(any(), any())).thenReturn(sendVoteSubject)
        }
    }

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = rule<DebateDetailsActivity>(autoStart = false)

    @Before
    fun setup() {
        DebateDetails.ApiProvider.override = { apiMock }
    }

    @Test
    fun shouldShowToolbarWithCorrectTitle() {
        startActivity()
        onId(R.id.toolbar)
                .isDisplayed()
                .hasChildWithText(R.string.debate_title)
    }

    @Test
    fun shouldExitScreenOnBackArrowClick() {
        startActivity()
        onToolbarBackArrow().click()
        Assert.assertTrue(rule.activity.isFinishing)
    }

    @Test
    fun shouldShowTopicInfo() {
        startActivity()
        onText(R.string.debate_details_info_topic).isDisplayed()
    }

    @Test
    fun shouldShowTopicReturnedFromApi() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateTopic)
                .isDisplayed()
                .hasText("topic")
    }

    @Test
    fun shouldShowChooseSideInfo() {
        startActivity()
        onText(R.string.debate_details_info_choose_side).isDisplayed()
    }

    @Test
    fun shouldShowRememberInfo() {
        startActivity()
        onText(R.string.debate_details_info_remember).perform(scrollTo()).isDisplayed()
    }

    @Test
    fun shouldShowCommentButton() {
        startActivity()
        onId(R.id.debateCommentButton)
                .hasText(R.string.debate_details_button_comment)
                .isDisplayed()
                .isEnabled()
    }

    @Test
    fun shouldShowInactiveImagesInButtons() {
        startActivity()
        onId(R.id.debatePositiveAnswerImage).hasImage(R.drawable.hand_positive_inactive)
        onId(R.id.debateNegativeAnswerImage).hasImage(R.drawable.hand_negative_inactive)
        onId(R.id.debateNeutralAnswerImage).hasImage(R.drawable.hand_neutral_inactive)
    }

    @Test
    fun shouldShowCorrectAnswersReturnedFromApi() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debatePositiveAnswerText)
                .isDisplayed()
                .hasText("answerPositive")
        onId(R.id.debateNegativeAnswerText)
                .isDisplayed()
                .hasText("answerNegative")
        onId(R.id.debateNeutralAnswerText)
                .isDisplayed()
                .hasText("answerNeutral")
    }

    @Test
    fun shouldHighlightPositiveAnswerWhenLastAnswerWasPositive() {
        startActivity()
        debateDetailsSubject.onSuccess(createDebateData(lastAnswerId = 1))
        onId(R.id.debatePositiveAnswerImage).hasImage(R.drawable.hand_positive)
        onId(R.id.debateNegativeAnswerImage).hasImage(R.drawable.hand_negative_inactive)
        onId(R.id.debateNeutralAnswerImage).hasImage(R.drawable.hand_neutral_inactive)
    }

    @Test
    fun shouldHighlightNegativeAnswerWhenLastAnswerWasNegative() {
        startActivity()
        debateDetailsSubject.onSuccess(createDebateData(lastAnswerId = 2))
        onId(R.id.debatePositiveAnswerImage).hasImage(R.drawable.hand_positive_inactive)
        onId(R.id.debateNegativeAnswerImage).hasImage(R.drawable.hand_negative)
        onId(R.id.debateNeutralAnswerImage).hasImage(R.drawable.hand_neutral_inactive)
    }

    @Test
    fun shouldHighlightNeutralAnswerWhenLastAnswerWasNeutral() {
        startActivity()
        debateDetailsSubject.onSuccess(createDebateData(lastAnswerId = 3))
        onId(R.id.debatePositiveAnswerImage).hasImage(R.drawable.hand_positive_inactive)
        onId(R.id.debateNegativeAnswerImage).hasImage(R.drawable.hand_negative_inactive)
        onId(R.id.debateNeutralAnswerImage).hasImage(R.drawable.hand_neutral)
    }

    @Test
    fun shouldNotHighlightAnswerWhenLastAnswerWasNull() {
        startActivity()
        debateDetailsSubject.onSuccess(createDebateData(lastAnswerId = null))
        onId(R.id.debatePositiveAnswerImage).hasImage(R.drawable.hand_positive_inactive)
        onId(R.id.debateNegativeAnswerImage).hasImage(R.drawable.hand_negative_inactive)
        onId(R.id.debateNeutralAnswerImage).hasImage(R.drawable.hand_neutral_inactive)
    }

    @Test
    fun shouldShowLoaderWhenCallingApi() {
        startActivity()
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun shouldNotShowLoaderWhenApiCallFinished() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.loader).doesNotExist()
    }

    @Ignore
    @Test
    fun shouldShowDebateDetailsErrorWhenApiCallFailed() {
        debateDetailsSubject.onError(RuntimeException())
        startActivity()
        onText(R.string.debate_details_error).isDisplayed()
    }

    @Test
    fun shouldShowVoteLoaderWhenClickedOnPositive() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debatePositiveAnswerButton).perform(scrollTo()).click()
        withParentId(R.id.debatePositiveAnswerLoader).isChildDisplayed(R.id.answerLoader)
    }

    @Test
    fun shouldShowVoteLoaderWhenClickedOnNegative() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNegativeAnswerButton).perform(scrollTo()).click()
        withParentId(R.id.debateNegativeAnswerLoader).isChildDisplayed(R.id.answerLoader)
    }

    @Test
    fun shouldShowVoteLoaderWhenClickedOnNeutral() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNeutralAnswerButton).perform(scrollTo()).click()
        withParentId(R.id.debateNeutralAnswerLoader).isChildDisplayed(R.id.answerLoader)
    }

    @Test
    fun shouldNotShowPositiveVoteLoaderWhenApiCallFinished() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debatePositiveAnswerButton).perform(scrollTo()).click()
        voteSuccessfully()
        withParentId(R.id.debatePositiveAnswerLoader).isChildNotDisplayed(R.id.answerLoader)
    }

    @Test
    fun shouldNotShowNegativeVoteLoaderWhenApiCallFinished() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNegativeAnswerButton).perform(scrollTo()).click()
        voteSuccessfully()
        withParentId(R.id.debateNegativeAnswerLoader).isChildNotDisplayed(R.id.answerLoader)
    }

    @Test
    fun shouldNotShowNeutralVoteLoaderWhenApiCallFinished() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNeutralAnswerButton).perform(scrollTo()).click()
        voteSuccessfully()
        withParentId(R.id.debateNeutralAnswerLoader).isChildNotDisplayed(R.id.answerLoader)
    }

    @Ignore
    @Test
    fun shouldShowVoteSuccessWhenClickOnAnswerAndApiCallFinishedSuccessfully() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNegativeAnswerButton).perform(scrollTo()).click()
        voteSuccessfully()
        onText(R.string.debate_details_vote_success).isDisplayed()
    }

    @Ignore
    @Test
    fun shouldShowVoteErrorWhenApiCallFails() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNeutralAnswerButton).perform(scrollTo()).click()
        sendVoteSubject.onError(RuntimeException())
        onText(R.string.debate_details_vote_error).isDisplayed()
    }

    @Test
    fun shouldNotShowVoteErrorWhenApiCallFinishedSuccessfully() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNeutralAnswerButton).perform(scrollTo()).click()
        voteSuccessfully()
        onText(R.string.debate_details_vote_error).doesNotExist()
    }

    @Test
    fun shouldUseTokenPassedWithIntent() {
        startActivity(token = "newToken")
        verify(apiMock).getDebateDetails("newToken")
    }

    @Test
    fun shouldUseTokenPassedWithIntentWhenSendingVote() {
        val token = "newToken"
        startActivity(token)
        debateDetailsSubject.onSuccess(createDebateData())
        onId(R.id.debatePositiveAnswerButton).perform(scrollTo()).click()
        verify(apiMock).vote(eq(token), any())
    }

    @Test
    fun shouldUseCorrectAnswerWhenSendingPositiveVote() {
        val debateData = createDebateData()
        startActivityAndSuccessfullyReturnDebateDetails(debateData)
        onId(R.id.debatePositiveAnswerButton).perform(scrollTo()).click()
        verify(apiMock).vote("token", debateData.answers.positive)
    }

    @Test
    fun shouldUseCorrectAnswerWhenSendingNegativeVote() {
        val debateData = createDebateData()
        startActivityAndSuccessfullyReturnDebateDetails(debateData)
        onId(R.id.debateNegativeAnswerButton).perform(scrollTo()).click()
        verify(apiMock).vote("token", debateData.answers.negative)
    }

    @Test
    fun shouldUseCorrectAnswerWhenSendingNeutralVote() {
        val debateData = createDebateData()
        startActivityAndSuccessfullyReturnDebateDetails(debateData)
        onId(R.id.debateNeutralAnswerButton).perform(scrollTo()).click()
        verify(apiMock).vote("token", debateData.answers.neutral)
    }

    @Test
    fun shouldOpenCommentScreenWhenCommentButtonClicked() {
        startActivity()
        stubAllIntents()
        onId(R.id.debateCommentButton).click()
        checkIntent(DebateCommentActivity::class.java)
    }

    @Test
    fun shouldOpenCommentScreenWithGivenToken() {
        val token = "someToken"
        startActivity(token)
        stubAllIntents()
        onId(R.id.debateCommentButton).click()
        Intents.intended(Matchers.allOf(
                IntentMatchers.hasExtra("debateAuthTokenKey", token),
                IntentMatchers.hasComponent(DebateCommentActivity::class.java.name)))
    }

    @Test
    fun shouldChangeImagesInButtonsWhenClickedOnPositive() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debatePositiveAnswerButton).perform(scrollTo()).click()
        voteSuccessfully()
        onId(R.id.debatePositiveAnswerImage).hasImage(R.drawable.hand_positive)
        onId(R.id.debateNegativeAnswerImage).hasImage(R.drawable.hand_negative_inactive)
        onId(R.id.debateNeutralAnswerImage).hasImage(R.drawable.hand_neutral_inactive)
    }

    @Test
    fun shouldChangeImagesInButtonsWhenClickedOnNegative() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNegativeAnswerButton).perform(scrollTo()).click()
        voteSuccessfully()
        onId(R.id.debatePositiveAnswerImage).hasImage(R.drawable.hand_positive_inactive)
        onId(R.id.debateNegativeAnswerImage).hasImage(R.drawable.hand_negative)
        onId(R.id.debateNeutralAnswerImage).hasImage(R.drawable.hand_neutral_inactive)
    }

    @Test
    fun shouldChangeImagesInButtonsWhenClickedOnNeutral() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNeutralAnswerButton).perform(scrollTo()).click()
        voteSuccessfully()
        onId(R.id.debatePositiveAnswerImage).hasImage(R.drawable.hand_positive_inactive)
        onId(R.id.debateNegativeAnswerImage).hasImage(R.drawable.hand_negative_inactive)
        onId(R.id.debateNeutralAnswerImage).hasImage(R.drawable.hand_neutral)
    }

    @Test
    fun shouldShowSlowDownInformationOn429ErrorCodeFromApi() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNegativeAnswerButton).click()
        sendVoteSubject.onError(createHttpException(429))
        onText(R.string.debate_vote_slow_down_title).isDisplayed()
        onText(R.string.debate_vote_slow_down_info).isDisplayed()
    }

    @Test
    fun shouldCloseSlowDownInformationOnButtonClick() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNegativeAnswerButton).click()
        sendVoteSubject.onError(createHttpException(429))
        onText(R.string.debate_vote_slow_down_OK_button).click()
        onText(R.string.debate_vote_slow_down_title).doesNotExist()
        onText(R.string.debate_vote_slow_down_info).doesNotExist()
    }

    @Test
    fun shouldHaveDisabledButtonsOnVoteCall() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNegativeAnswerButton).click()
        onId(R.id.debatePositiveAnswerButton).isDisabled()
        onId(R.id.debateNegativeAnswerButton).isDisabled()
        onId(R.id.debateNeutralAnswerButton).isDisabled()
    }

    @Test
    fun shouldHaveEnabledButtonsOnVoteCallEnd() {
        startActivityAndSuccessfullyReturnDebateDetails()
        voteSuccessfully()
        onId(R.id.debateNegativeAnswerButton).click()
        onId(R.id.debatePositiveAnswerButton).isEnabled()
        onId(R.id.debateNegativeAnswerButton).isEnabled()
        onId(R.id.debateNeutralAnswerButton).isEnabled()
    }

    private fun startActivity(token: String = "token") {
        val intent = intent(context = InstrumentationRegistry.getTargetContext(), debateToken = token)
        rule.launchActivity(intent)
    }

    private fun voteSuccessfully() {
        sendVoteSubject.onComplete()
    }

    private fun startActivityAndSuccessfullyReturnDebateDetails(debateData: DebateData = createDebateData()) {
        startActivity()
        debateDetailsSubject.onSuccess(debateData)
    }
}