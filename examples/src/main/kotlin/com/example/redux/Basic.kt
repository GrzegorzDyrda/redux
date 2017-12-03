package com.example.redux

/**
 * Basic Redux example.
 *
 * It demonstrates the basic setup, which is:
 * - Define state POJO
 * - Define some actions (also POJOs)
 * - Write a reducer (function that describes how actions are affecting the state)
 * - Subscribe to state changes
 *
 * Created by Grzegorz Dyrda on 2017-12-02
 */

import com.grzegorzdyrda.redux.Store

data class AppState(val count: Int)

sealed class Action
class IncrementAction : Action()
class DecrementAction : Action()

fun reducer(state: AppState, action: Action): AppState {
    return when (action) {
        is IncrementAction -> state.copy(count = state.count + 1)
        is DecrementAction -> state.copy(count = state.count - 1)
        else -> state
    }
}

val store = Store(AppState(1), ::reducer)

fun main(args: Array<String>) {

    store.subscribe { state ->
        println("onNewState: $state")
    }

    store.dispatch(IncrementAction())
    store.dispatch(IncrementAction())
    store.dispatch(DecrementAction())
}