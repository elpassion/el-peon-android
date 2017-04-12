package pl.elpassion.elspace.commons

import io.reactivex.Observable
import org.mockito.stubbing.OngoingStubbing

fun <T> OngoingStubbing<Observable<T>>.thenNever(): OngoingStubbing<Observable<T>> = thenReturn(Observable.never())

fun <T> OngoingStubbing<Observable<T>>.thenJust(value: T): OngoingStubbing<Observable<T>> = thenReturn(Observable.just(value))

fun <T> OngoingStubbing<Observable<List<T>>>.thenJust(vararg values: T): OngoingStubbing<Observable<List<T>>> = thenReturn(Observable.just(values.toList()))

fun <T> OngoingStubbing<Observable<T>>.thenError(exception: Exception): OngoingStubbing<Observable<T>> = thenReturn(Observable.error(exception))

