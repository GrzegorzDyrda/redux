package com.grzegorzdyrda.redux

import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Redux Store that holds the app's State.
 *
 * @constructor Creates the Redux Store.
 * @param initialState initial state of the Store
 * @param reducer a function that returns the next State, given the current State and the Action
 */
class Store<STATE : State<COMMAND>, ACTION, COMMAND>(
        initialState: STATE,
        private val reducer: (STATE, ACTION) -> STATE) {

    /**
     * Creates the Redux Store.
     *
     * @param initialState initial state of the Store
     * @param reducerProvider class that provides the Reducer function
     */
    constructor(initialState: STATE, reducerProvider: ReducerProvider<STATE, ACTION>) :
            this(initialState, reducerProvider::rootReducer)

    private val subscribers = mutableListOf<StoreSubscriber<STATE, COMMAND>>()

    private var currentState = initialState
    private var isDispatching = false

    /**
     * Dispatches an Action. It's the only way to trigger a State change.
     *
     * This method is thread-safe. Can be called from any thread.
     */
    @Synchronized
    fun dispatch(action: ACTION): ACTION {
        if (isDispatching)
            throw IllegalStateException("Reducers may not dispatch actions! They should be pure functions - no side effects at all.")

        // Compute the next State
        val newState = try {
            isDispatching = true
            reducer(currentState, action)
        } finally {
            isDispatching = false
        }

        // Obtain Command (if any) and remove it from the State
        val command = newState.CMD
        newState.CMD = null

        // Update current State and notify Subscribers
        if (newState != currentState) {
            currentState = newState
            subscribers.forEach { it.onNewState(newState) }
        }

        // Dispatch Command (if any)
        command?.let {
            subscribers.forEach { it.onCommandReceived(command) }
        }

        return action
    }

    /**
     * Convenient method to dispatch more complicated logic.
     *
     * @param block function to be called. It receives [dispatch] and [getState] functions as parameters
     */
    fun <R> dispatch(block: (dispatch: (ACTION) -> ACTION, getState: () -> STATE) -> R): R {
        return block(this::dispatch, this::getState)
    }

    /**
     * Convenient method to dispatch asynchronous logic by utilizing coroutines.
     *
     * The [block] parameter is a *suspending lambda*, thus allows calling any suspending
     * function inside.
     *
     * @param context coroutine context. If not specified [DefaultDispatcher] will be used
     * @param block suspending lambda - the coroutine code
     */
    fun <R> dispatchAsync(context: CoroutineContext = DefaultDispatcher,
                          block: suspend (dispatch: (ACTION) -> ACTION, getState: () -> STATE) -> R): Deferred<R> {
        return async(context) {
            block(this@Store::dispatch, this@Store::getState)
        }
    }

    /**
     * Returns the current State kept by the Store.
     */
    fun getState(): STATE {
        if (isDispatching)
            throw IllegalStateException("You may not call store.getState() while the Reducer is executing! The reducer has already received the State as an argument. Pass it down from the top Reducer instead of reading it from the store.")

        return currentState
    }

    /**
     * Subscribes the [subscriber] to this store's State changes.
     *
     * It also immediately notifies the [subscriber] about the current State.
     *
     * @param subscriber subscriber to be notified on every State change
     * @return the subscriber, which can be passed to [unsubscribe] to cancel subscription
     */
    @Synchronized
    fun subscribe(subscriber: StoreSubscriber<STATE, COMMAND>): StoreSubscriber<STATE, COMMAND> {
        if (isDispatching)
            throw IllegalStateException("You may not call store.subscribe() while the Reducer is executing! If you would like to be notified after the store has been updated, subscribe from a component and invoke store.getState() in the callback to access the latest state.")

        subscribers += subscriber
        // Immediately notify new subscriber about the current State.
        subscriber.onNewState(currentState)

        return subscriber
    }

    /**
     * Convenience method.
     * Allows to supply [onNewState] callback, but ignore all *onCommandReceived* events.
     *
     * See [subscribe(StoreSubscriber)][subscribe] for more info.
     *
     * @param onNewState callback to be called each time the State changes
     * @return [StoreSubscriber] which can be passed to [unsubscribe] to cancel the subscription
     */
    fun subscribe(onNewState: (STATE) -> Unit): StoreSubscriber<STATE, COMMAND> {
        val subscriber = object : StoreSubscriber<STATE, COMMAND> {
            override fun onNewState(state: STATE) = onNewState(state)
            override fun onCommandReceived(command: COMMAND) = Unit
        }

        return subscribe(subscriber)
    }

    /**
     * Unsubscribes the given [subscriber] from this store's State changes.
     */
    @Synchronized
    fun unsubscribe(subscriber: StoreSubscriber<STATE, COMMAND>) {
        subscribers -= subscriber
    }

}