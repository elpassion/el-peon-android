package pl.elpassion.elspace.debate.chat

import com.nhaarman.mockito_kotlin.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.dabate.chat.createComment
import pl.elpassion.elspace.dabate.details.createString
import pl.elpassion.elspace.debate.DebatesRepository

class DebateChatControllerTest {

    private val view = mock<DebateChat.View>()
    private val service = mock<DebateChat.Service>()
    private val debateRepo = mock<DebatesRepository>()
    private val initialsCommentsSubject = PublishSubject.create<Comment>()
    private val liveCommentsSubject = PublishSubject.create<Comment>()
    private val sendCommentSubject = CompletableSubject.create()
    private val controller = DebateChatController(view, debateRepo, service, SchedulersSupplier(Schedulers.trampoline(), Schedulers.trampoline()), maxMessageLength = 100)

    @Before
    fun setUp() {
        whenever(service.initialsCommentsObservable(any())).thenReturn(initialsCommentsSubject)
        whenever(service.liveCommentsObservable(any())).thenReturn(liveCommentsSubject)
        whenever(service.sendComment(any())).thenReturn(sendCommentSubject)
        whenever(debateRepo.getLatestDebateCode()).thenReturn("12345")
        whenever(debateRepo.areTokenCredentialsMissing(any())).thenReturn(false)
        whenever(debateRepo.getTokenCredentials(any())).thenReturn(createCredentials("firstName", "lastName"))
    }

    @Test
    fun shouldCallServiceInitialsCommentsWithGivenDataOnCreate() {
        onCreate()
        verify(service).initialsCommentsObservable("token")
    }

    @Test
    fun shouldCallServiceInitialsCommentsObservableWithReallyGivenTokenOnCreate() {
        val token = "someOtherToken"
        controller.onCreate(token)
        verify(service).initialsCommentsObservable(token)
    }

    @Test
    fun shouldShowCommentsReturnedFromServiceInitialsComments() {
        val comment = createComment()
        onCreate()
        initialsCommentsSubject.onNext(comment)
        verify(view).showComment(comment)
    }

    @Test
    fun shouldShowLoaderWhenServiceInitialsCommentsStarts() {
        controller.onCreate("token")
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderWhenServiceInitialsCommentsEnds() {
        controller.onCreate("token")
        initialsCommentsSubject.onComplete()
        verify(view).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToSubscribeOnWhenServiceInitialsComments() {
        val subscribeOn = TestScheduler()
        val controller = DebateChatController(view, debateRepo, service, SchedulersSupplier(subscribeOn, Schedulers.trampoline()), maxMessageLength = 100)
        controller.onCreate("token")
        initialsCommentsSubject.onError(RuntimeException())
        verify(view, never()).showInitialsCommentsError(any())
        subscribeOn.triggerActions()
        verify(view).showInitialsCommentsError(any())
    }

    @Test
    fun shouldUseGivenSchedulerToObserveOnWhenServiceInitialsComments() {
        val observeOn = TestScheduler()
        val controller = DebateChatController(view, debateRepo, service, SchedulersSupplier(Schedulers.trampoline(), observeOn), maxMessageLength = 100)
        controller.onCreate("token")
        initialsCommentsSubject.onError(RuntimeException())
        verify(view, never()).showInitialsCommentsError(any())
        observeOn.triggerActions()
        verify(view).showInitialsCommentsError(any())
    }

    @Test
    fun shouldShowInitialsCommentsErrorWhenServiceInitialsCommentsFails() {
        onCreate()
        val exception = RuntimeException()
        initialsCommentsSubject.onError(exception)
        verify(view).showInitialsCommentsError(exception)
    }

    @Test
    fun shouldCallServiceInitialsCommentsOnRefresh() {
        controller.onServiceRefresh("refreshToken")
        verify(service).initialsCommentsObservable("refreshToken")
    }

    @Test
    fun shouldCallServiceLiveCommentsWithReallyGivenDebateCodeOnCreate() {
        whenever(debateRepo.getLatestDebateCode()).thenReturn("67890")
        onCreate()
        initialsCommentsSubject.onComplete()
        verify(service).liveCommentsObservable("67890")
    }

    @Test
    fun shouldShowCommentsReturnedFromServiceLiveComments() {
        val comment = createComment()
        onCreate()
        initialsCommentsSubject.onComplete()
        liveCommentsSubject.onNext(comment)
        verify(view).showComment(comment)
    }

    @Test
    fun shouldShowLiveCommentsErrorOnServiceLiveCommentsError() {
        val exception = RuntimeException()
        controller.onCreate("token")
        initialsCommentsSubject.onComplete()
        liveCommentsSubject.onError(exception)
        verify(view).showLiveCommentsError(exception)
    }

    @Test
    fun shouldCallServiceSendCommentWithGivenDataOnSendComment() {
        sendComment("token", "message")
        verify(service).sendComment(CommentToSend("token", "message", "firstName", "lastName"))
    }

    @Test
    fun shouldReallyCallServiceSendCommentWithGivenDataWhenMessageIsValidOnSendComment() {
        val token = "someOtherToken"
        whenever(debateRepo.getTokenCredentials(token)).thenReturn(createCredentials("NewFirstName", "NewLastName"))
        sendComment(token = "someOtherToken", message = "someOtherMessage")
        verify(service).sendComment(CommentToSend("someOtherToken", "someOtherMessage", "NewFirstName", "NewLastName"))
    }

    @Test
    fun shouldNotCallServiceSendCommentWhenMessageIsEmptyOnSendComment() {
        sendComment(message = "")
        verify(service, never()).sendComment(any())
    }

    @Test
    fun shouldNotSendCommentIfCommentIsEmpty() {
        sendComment(message = "")
        verify(service, never()).sendComment(any())
    }

    @Test
    fun shouldNotCallServiceSendCommentWhenMessageIsBlankOnSendComment() {
        sendComment(message = " ")
        verify(service, never()).sendComment(any())
    }

    @Test
    fun shouldNotSendCommentIfCommentIsBlank() {
        sendComment(message = "")
        verify(service, never()).sendComment(any())
    }

    @Test
    fun shouldCallServiceSendCommentWhenMessageIsUnderLimitOnSendComment() {
        sendComment(message = createString(100))
        verify(service).sendComment(any())
    }

    @Test
    fun shouldNotCallServiceSendCommentWhenMessageIsOverLimitOnSendComment() {
        sendComment(message = createString(101))
        verify(service, never()).sendComment(any())
    }

    @Test
    fun shouldUseRealMaxMessageLengthWhenMessageIsUnderLimitOnSendComment() {
        val controller = DebateChatController(view, debateRepo, service, SchedulersSupplier(Schedulers.trampoline(), Schedulers.trampoline()), maxMessageLength = 30)
        controller.sendComment("token", createString(31))
        verify(service, never()).sendComment(any())
    }

    @Test
    fun shouldShowInputOverLimitErrorWhenMessageIsOverLimitOnSendComment() {
        sendComment(message = createString(101))
        verify(view).showInputOverLimitError()
    }

    @Test
    fun shouldShowLoaderOnSendComment() {
        sendComment()
        verify(view).showLoader()
    }

    @Test
    fun shouldNotHideLoaderIfSendCommentIsStillInProgress() {
        sendComment()
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenSendCommentSucceeded() {
        sendComment()
        sendCommentSubject.onComplete()
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenSendCommentFailed() {
        sendComment()
        sendCommentSubject.onError(RuntimeException())
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnDestroyIfSendCommentIsStillInProgress() {
        sendComment()
        controller.onDestroy()
        verify(view).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderOnDestroyIfSendCommentWasntCalled() {
        controller.onDestroy()
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToSubscribeOnWhenSendComment() {
        val subscribeOn = TestScheduler()
        val controller = DebateChatController(view, debateRepo, service, SchedulersSupplier(subscribeOn, Schedulers.trampoline()), maxMessageLength = 100)
        controller.sendComment(token = "token", message = "message")
        sendCommentSubject.onComplete()
        verify(view, never()).hideLoader()
        subscribeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToObserveOnWhenSendComment() {
        val observeOn = TestScheduler()
        val controller = DebateChatController(view, debateRepo, service, SchedulersSupplier(Schedulers.trampoline(), observeOn), maxMessageLength = 100)
        controller.sendComment(token = "token", message = "message")
        sendCommentSubject.onComplete()
        verify(view, never()).hideLoader()
        observeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldClearSendCommentInputWhenSendCommentSucceeded() {
        sendComment()
        sendCommentSubject.onComplete()
        verify(view).clearSendCommentInput()
    }

    @Test
    fun shouldShowErrorWhenSendCommentFailed() {
        sendComment()
        val exception = RuntimeException()
        sendCommentSubject.onError(exception)
        verify(view).showSendCommentError(exception)
    }

    @Test
    fun shouldShowCredentialDialogOnSendCommentIfCredentialsAreMissing() {
        whenever(debateRepo.areTokenCredentialsMissing("token")).thenReturn(true)
        controller.sendComment("token", "message")
        verify(view).showCredentialsDialog()
    }

    @Test
    fun shouldSaveCredentialsOnNewCredentials() {
        val token = "token"
        val credentials = createCredentials()
        controller.onNewCredentials(token, credentials)
        verify(debateRepo).saveTokenCredentials(token, credentials)
    }

    @Test
    fun shouldShowFirstNameErrorOnBlankFirstName() {
        val credentials = createCredentials(firstName = " ")
        controller.onNewCredentials("token", credentials)
        verify(view).showFirstNameError()
    }

    @Test
    fun shouldNotSaveTokenCredentialsOnBlankFirstName() {
        val credentials = createCredentials(firstName = " ")
        controller.onNewCredentials("token", credentials)
        verify(debateRepo, never()).saveTokenCredentials(any(), any())
    }

    @Test
    fun shouldShowLastNameErrorOnBlankLastName() {
        val credentials = createCredentials(lastName = " ")
        controller.onNewCredentials("token", credentials)
        verify(view).showLastNameError()
    }

    @Test
    fun shouldNotSaveTokenCredentialsOnBlankLastName() {
        val credentials = createCredentials(lastName = " ")
        controller.onNewCredentials("token", credentials)
        verify(debateRepo, never()).saveTokenCredentials(any(), any())
    }

    @Test
    fun shouldShowBothErrorsOnBlankCredentials() {
        val credentials = createCredentials(firstName = " ", lastName = " ")
        controller.onNewCredentials("token", credentials)
        verify(view).showLastNameError()
        verify(view).showFirstNameError()
    }

    @Test
    fun shouldCloseCredentialsDialogOnCorrectCredentials() {
        val credentials = createCredentials()
        controller.onNewCredentials("token", credentials)
        verify(view).closeCredentialsDialog()
    }

    @Test
    fun shouldNotCloseCredentialDialogOnIncorrectCredentials() {
        val credentials = createCredentials(firstName = " ", lastName = " ")
        controller.onNewCredentials("token", credentials)
        verify(view, never()).closeCredentialsDialog()
    }

    private fun onCreate(token: String = "token") = controller.onCreate(token)

    private fun createCredentials(firstName: String = "name", lastName: String = "lastName"): TokenCredentials = TokenCredentials(firstName, lastName)

    private fun sendComment(token: String = "token", message: String = "message") = controller.sendComment(token, message)
}