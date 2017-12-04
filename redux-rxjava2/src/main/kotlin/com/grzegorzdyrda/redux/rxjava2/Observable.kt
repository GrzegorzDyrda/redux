package com.grzegorzdyrda.redux.rxjava2

import com.grzegorzdyrda.redux.Store
import io.reactivex.Observable

/**
 * Returns a stream of state changes.
 *
 * Created by Grzegorz Dyrda on 2017-12-04
 */
fun <STATE : Any, ACTION : Any> Store<STATE, ACTION>.toObservable(): Observable<STATE> {
    return Observable.create<STATE> { emitter ->
        subscribe { state ->
            emitter.onNext(state)
        }
    }
}