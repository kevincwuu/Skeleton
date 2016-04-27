package adambl4.instamotorsskeleton.logic

import adambl4.instamotorsskeleton.utils.extensions.andThen
import adambl4.instamotorsskeleton.utils.extensions.promiseThen
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.task
import rx.lang.kotlin.PublishSubject

/**
 * Created by Adambl4 on 25.04.2016.
 */

fun<R> actionAsyncWrapper(action: () -> R,
                          onSuccess: (R) -> Unit = {},
                          onFail: (Exception) -> Unit = {})
        : () -> Promise<R, Exception> = { task { action() } success(onSuccess) fail(onFail) }

fun <R, E> syncLocalWithRemote(loadData: () -> Promise<R, Exception>, mapToEntity: (R) -> E, save: (E) -> Unit): () -> Unit {
    return loadData promiseThen mapToEntity andThen save
}

object EventBus{
    private val bus = PublishSubject<Any>()

    fun stream() = bus.asObservable()

    fun push(event : Any){
        bus.onNext(event)
    }
}


