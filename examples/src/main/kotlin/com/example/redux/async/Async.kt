package com.example.redux.async

/**
 * This example shows how to perform asynchronous logic using coroutines.
 *
 * It performs two dependent network requests sequentially.
 *
 * Created by Grzegorz Dyrda on 2017-12-08
 */

import com.example.redux.async.model.Contributor
import com.example.redux.async.network.NetworkManager
import com.grzegorzdyrda.redux.Store
import kotlinx.coroutines.experimental.async

data class AppState(val contributors: List<Contributor> = emptyList(),
                    val error: Exception? = null,
                    val isFetching: Boolean = false)

sealed class Action
object FETCH_CONTRIBUTORS_REQUEST : Action()
data class FETCH_CONTRIBUTORS_RESPONSE(val contributors: List<Contributor>) : Action()
data class FETCH_CONTRIBUTORS_ERROR(val exception: Exception) : Action()

fun reducer(state: AppState, action: Action): AppState {
    return when (action) {
        is FETCH_CONTRIBUTORS_REQUEST -> state.copy(
                isFetching = true
        )
        is FETCH_CONTRIBUTORS_RESPONSE -> state.copy(
                contributors = action.contributors,
                isFetching = false
        )
        is FETCH_CONTRIBUTORS_ERROR -> state.copy(
                error = action.exception,
                isFetching = true
        )
    }
}

val store = Store(AppState(), ::reducer)
val github = NetworkManager.github
val userName = "grzegorzdyrda"

fun main(args: Array<String>) {
    store.subscribe { state ->
        println("onNewState: $state")
    }

    store.dispatchAsync {
        try {
            store.dispatch(FETCH_CONTRIBUTORS_REQUEST)

            // first request
            val repos = async { github.repos(userName).execute().body()!! }
            val firstRepo = repos.await()[0]

            // second request
            val contributors = async { github.contributors(userName, firstRepo.name).execute().body()!! }
            store.dispatch(FETCH_CONTRIBUTORS_RESPONSE(contributors.await()))

        } catch (e: Exception) {
            store.dispatch(FETCH_CONTRIBUTORS_ERROR(e))
        }
    }

    Thread.sleep(5000) // wait until our asynchronous code is finished
}