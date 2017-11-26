package com.grzegorzdyrda.redux

import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlin.coroutines.experimental.CoroutineContext

class Store<out STATE, ACTION>(initialState: STATE,
                               private val reducer: (STATE, ACTION) -> STATE) {

    interface StoreSubscriber<in STATE> {
        fun onNewState(state: STATE)
        fun onCommandReceived(command: Any)
    }

    //v1 - listenery są funkcjami
    private val listeners = mutableListOf<(STATE) -> Unit>()
    //v2 - listenery są instancjami StoreSubscriber
    private val subscribers = mutableListOf<StoreSubscriber<STATE>>()

    private var currentState = initialState
    private var isDispatching = false

    init {
        // Notify listeners about the Initial State
        // * No need for it since we immediately notify newly subscribed listeners in subscribe()
        // * Also, at this point (in the constructor) there are certainly no listeners subscribed yet ;)
        //listeners.forEach { it(currentState) }
    }

    fun dispatch(action: ACTION): ACTION {
        if (isDispatching) {
            throw IllegalStateException("Reducers may not dispatch actions! They should be pure functions - no side effects at all.")
        }

        synchronized(this) {
            val newState = try {
                isDispatching = true
                reducer(currentState, action)
            } finally {
                isDispatching = false
            }
            // Notify listeners only when State actually changed
            if (newState != currentState) {
                currentState = newState
                listeners.forEach { it(currentState) }
                subscribers.forEach { it.onNewState(newState) }
            }
        }

        return action
    }

    fun dispatchCommand(command: Any): Any {
        subscribers.forEach { it.onCommandReceived(command) }

        return command
    }

    /**
     * Convenient method to dispatch more complicated logic.
     *
     * [block] function receives [dispatch] and [getState] functions as parameters
     */
    fun <R> dispatch(block: (dispatch: (ACTION) -> ACTION, getState: () -> STATE) -> R): R {
        return block(this::dispatch, this::getState)
    }

    /**
     * Convenient method to dispatch more complicated logic that involves coroutines.
     *
     * You may call
     *
     * @param context: CoroutineContext
     */
    fun <R> dispatchAsync(context: CoroutineContext = DefaultDispatcher,
                          block: suspend (dispatch: (ACTION) -> ACTION, getState: () -> STATE) -> R): Deferred<R> {
        return async(context) {
            block(this@Store::dispatch, this@Store::getState)
        }
    }

    fun getState(): STATE {
        if (isDispatching) {
            throw IllegalStateException("You may not call store.getState() while the reducer is executing! The reducer has already received the state as an argument. Pass it down from the top reducer instead of reading it from the store.")
        }

        return currentState
    }

    /**
     * Subscribes [listener] to the State changes.
     *
     * Also, immediately fires the [listener], giving him the current State.
     *
     * @return Function to remove this listener
     */
    fun subscribe(listener: (STATE) -> Unit): () -> Unit {
        if (isDispatching) {
            throw IllegalStateException("You may not call store.subscribe() while the reducer is executing! If you would like to be notified after the store has been updated, subscribe from a component and invoke store.getState() in the callback to access the latest state.")
        }

        listeners += listener
        // Immediately notify newly subscribed listener about the current State.
        listener(currentState)

        // Return a function that allows users to remove this listener
        return {
            if (isDispatching) {
                throw IllegalStateException("You may not unsubscribe from a store listener while the reducer is executing!")
            }
            listeners -= listener
        }
    }

    fun subscribe(subscriber: StoreSubscriber<STATE>) {
        if (isDispatching) {
            throw IllegalStateException("You may not call store.subscribe() while the reducer is executing! If you would like to be notified after the store has been updated, subscribe from a component and invoke store.getState() in the callback to access the latest state.")
        }

        subscribers += subscriber
        // Immediately notify new subscriber about the current State.
        subscriber.onNewState(currentState)
    }

    fun unsubscribe(subscriber: StoreSubscriber<STATE>) {
        subscribers -= subscriber
    }

}