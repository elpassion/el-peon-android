package pl.elpassion.elspace.debate.details

import com.nhaarman.mockito_kotlin.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.SingleSubject
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.dabate.details.createAnswer
import pl.elpassion.elspace.dabate.details.createDebateData

class DebateDetailsControllerTest {

    private val api = mock<DebateDetails.Api>()
    private val view = mock<DebateDetails.View>()
    private val controller = DebateDetailsController(api, view, SchedulersSupplier(Schedulers.trampoline(), Schedulers.trampoline()))
    private val debateDetailsSubject = SingleSubject.create<DebateData>()
    private val sendVoteSubject = CompletableSubject.create()

    @Before
    fun setUp() {
        whenever(api.getDebateDetails(any())).thenReturn(debateDetailsSubject)
        whenever(api.vote(any(), any())).thenReturn(sendVoteSubject)
    }

    @Test
    fun shouldCallApiWithGivenTokenOnCreate() {
        controller.onCreate(token = "token")
        verify(api).getDebateDetails(token = "token")
    }

    @Test
    fun shouldReallyCallApiWithGivenTokenOnCreate() {
        controller.onCreate(token = "otherToken")
        verify(api).getDebateDetails(token = "otherToken")
    }

    @Test
    fun shouldShowDebateDetailsReturnedFromApiCallOnView() {
        controller.onCreate("token")
        val debateDetails = createDebateData()
        returnFromApi(debateDetails)
        verify(view).showDebateDetails(debateDetails)
    }

    @Test
    fun shouldShowLoaderOnApiCall() {
        controller.onCreate("token")
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderWhenApiCallFinishes() {
        controller.onCreate("token")
        returnFromApi(createDebateData())
        verify(view).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderWhenApiCallIsNotFinished() {
        controller.onCreate("token")
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToSubscribeOn() {
        val subscribeOn = TestScheduler()
        val controller = DebateDetailsController(api, view, SchedulersSupplier(subscribeOn, Schedulers.trampoline()))
        controller.onCreate("token")
        returnFromApi(createDebateData())
        verify(view, never()).hideLoader()
        subscribeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToObserveOn() {
        val observeOn = TestScheduler()
        val controller = DebateDetailsController(api, view, SchedulersSupplier(Schedulers.trampoline(), observeOn))
        controller.onCreate("token")
        returnFromApi(createDebateData())
        verify(view, never()).hideLoader()
        observeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldShowErrorWhenApiCallFails() {
        controller.onCreate("token")
        val exception = RuntimeException()
        debateDetailsSubject.onError(exception)
        verify(view).showDebateDetailsError(exception)
    }

    @Test
    fun shouldHideLoaderOnDestroyIfApiCallIsRunning() {
        controller.onCreate("token")
        controller.onDestroy()
        verify(view).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderOnDestroyIfApiCallWasntTriggered() {
        controller.onDestroy()
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldCallApiOnDebateDetailsRefresh() {
        controller.onDebateDetailsRefresh("newToken")
        verify(api).getDebateDetails("newToken")
    }

    @Test
    fun shouldCallApiWithSelectedAnswerOnVote() {
        controller.onVote("token", Answer(2, "answerNegative"))
        verify(api).vote("token", Answer(2, "answerNegative"))
    }

    @Test
    fun shouldReallyCallApiWithSelectedAnswerOnVote() {
        controller.onVote("differentToken", Answer(1, "answer"))
        verify(api).vote("differentToken", Answer(1, "answer"))
    }

    @Test
    fun shouldShowLoaderOnVote() {
        controller.onVote("token", createAnswer())
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderWhenVoteApiCallIsFinished() {
        controller.onVote("token", createAnswer())
        sendVoteSubject.onComplete()
        verify(view).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderIfVoteCallIsStillInProgress() {
        controller.onVote("token", createAnswer())
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToSubscribeOnWhenVote() {
        val subscribeOn = TestScheduler()
        val controller = DebateDetailsController(api, view, SchedulersSupplier(subscribeOn, Schedulers.trampoline()))
        controller.onVote("token", createAnswer())
        sendVoteSubject.onComplete()
        verify(view, never()).hideLoader()
        subscribeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToObserveOnWhenVote() {
        val observeOn = TestScheduler()
        val controller = DebateDetailsController(api, view, SchedulersSupplier(Schedulers.trampoline(), observeOn))
        controller.onVote("token", createAnswer())
        sendVoteSubject.onComplete()
        verify(view, never()).hideLoader()
        observeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnDestroyIfVoteCallIsStillInProgress() {
        controller.onVote("", createAnswer())
        controller.onDestroy()
        verify(view).hideLoader()
    }

    @Test
    fun shouldShowSuccessWhenVoteSuccessfully() {
        controller.onVote("", createAnswer())
        sendVoteSubject.onComplete()
        verify(view).showVoteSuccess()
    }

    @Test
    fun shouldShowErrorWhenVoteFailed() {
        controller.onVote("", createAnswer())
        val exception = RuntimeException()
        sendVoteSubject.onError(exception)
        verify(view).showVoteError(exception)
    }

    private fun returnFromApi(debateData: DebateData) {
        debateDetailsSubject.onSuccess(debateData)
    }
}