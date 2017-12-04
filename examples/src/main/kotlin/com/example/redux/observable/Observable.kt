package com.example.redux.observable

/**
 * Example that shows integration with RxJava2.
 *
 * Created by Grzegorz Dyrda on 2017-12-04
 */

import com.grzegorzdyrda.redux.Store
import com.grzegorzdyrda.redux.rxjava2.toObservable
import io.reactivex.Observable

data class AppState(val count: Int)

sealed class Action
class IncrementAction : Action()
class DecrementAction : Action()

fun reducer(state: AppState, action: Action): AppState {
    return when (action) {
        is IncrementAction -> state.copy(count = state.count + 1)
        is DecrementAction -> state.copy(count = state.count - 1)
    }
}

val store = Store(AppState(1), ::reducer)

fun render(states: Observable<AppState>) {
    states
            .filter { it.count > 1 }
            .map { it.count }
            .subscribe {
                println("New value greater than one = $it")
            }
}

fun main(args: Array<String>) {
    render(store.toObservable())

    store.dispatch(IncrementAction())
    store.dispatch(IncrementAction())
    store.dispatch(DecrementAction())
}