package com.grzegorzdyrda.redux

import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Redux Store that holds the app's State.
 *
 * @constructor Creates the Redux Store.
 * @param initialState initial state of the Store
 * @param reducer a function that returns the next State, given the current State and the Action
 */
class Store<STATE : Any, ACTION : Any>(
        initialState: STATE,
        private val reducer: Reducer<STATE, ACTION>) {

    /**
     * Creates the Redux Store.
     *
     * @param initialState initial state of the Store
     * @param reducerProvider class that provides the Reducer function
     */
    constructor(initialState: STATE, reducerProvider: ReducerProvider<STATE, ACTION>) :
            this(initialState, reducerProvider::rootReducer)

    private var subscribers = setOf<StoreSubscriber<STATE>>()
    private var currentState = initialState
    private var isDispatching = ConcurrentHashMap<Long, Boolean>(Runtime.getRuntime().availableProcessors())

    /**
     * Returns the current State kept by the Store.
     */
    val state: STATE
        get() {
            if (isDispatching[Thread.currentThread().id] == true)
                throw IllegalStateException("You may not call store.getState() while the Reducer is executing! The reducer has already received the State as an argument. Pass it down from the top Reducer instead of reading it from the store.")

            return currentState
        }

    /**
     * Dispatches an Action. It's the only way to trigger a State change.
     *
     * Dispatching an Action starts the following sequence:
     * - [ReducerProvider.rootReducer] gets called, and returns the new state
     * - Subscribers are notified about the state change via [StoreSubscriber.onNewState]
     *
     * This method is thread-safe. Can be called from any thread.
     *
     * @param action action to be dispatched
     * @return the dispatched action
     */
    fun dispatch(action: ACTION): ACTION {
        if (isDispatching[Thread.currentThread().id] == true)
            throw IllegalStateException("Reducers may not dispatch actions! They should be pure functions - no side effects at all.")

        var isChanged = false
        lateinit var newState: STATE

        synchronized(this) {
            // Compute the next State
            try {
                isDispatching[Thread.currentThread().id] = true
                newState = reducer(currentState, action)
            } finally {
                isDispatching[Thread.currentThread().id] = false
            }

            isChanged = (newState != currentState)

            // Update shared state
            currentState = newState
        }

        // Notify subscribers if needed
        // Note: The original Redux does ALWAYS notify, even if state didn't change. Should we do the same?
        if (isChanged) {
            subscribers.forEach { it.onNewState(newState) }
        }

        return action
    }

    /**
     * Convenient method to dispatch more complicated logic.
     *
     * @param actionCreator function to be called. It receives [Store] as parameter
     */
    fun <R> dispatch(actionCreator: (store: Store<STATE, ACTION>) -> R): R {
        return actionCreator(this)
    }

    /**
     * Convenient method to dispatch asynchronous logic by utilizing coroutines.
     *
     * The [actionCreator] parameter is a *suspending lambda*, thus allows calling any suspending
     * function inside.
     *
     * @param context coroutine context. If not specified [DefaultDispatcher] will be used
     * @param actionCreator suspending lambda - the coroutine code
     */
    fun <R> dispatchAsync(context: CoroutineContext = DefaultDispatcher,
                          actionCreator: suspend CoroutineScope.(store: Store<STATE, ACTION>) -> R): Deferred<R> {
        return async(context) {
            actionCreator(this@Store)
        }
    }

    /**
     * Sends a Command. It's a way of telling Subscribers to perform a one-time side-effect.
     *
     * Each time a Command is sent, [StoreSubscriber.onCommandReceived] gets called.
     *
     * Since Commands are NOT stored in the State, they're ideal for things like navigation,
     * showing toasts etc.
     *
     * @param command command to be sent to subscribers
     * @return the command
     */
    fun sendCommand(command: Any): Any {
        if (isDispatching[Thread.currentThread().id] == true)
            throw IllegalStateException("Reducers may not send commands! They should be pure functions - no side effects at all.")

        subscribers.forEach { it.onCommandReceived(command) }

        return command
    }

    /**
     * Subscribes the [subscriber] to this store's State changes.
     *
     * It also immediately notifies the [subscriber] about the current State.
     *
     * @param subscriber subscriber to be notified on every State change
     * @return the subscriber, which can be passed to [unsubscribe] to cancel subscription
     */
    fun subscribe(subscriber: StoreSubscriber<STATE>): StoreSubscriber<STATE> {
        if (isDispatching[Thread.currentThread().id] == true)
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
    fun subscribe(onNewState: (state: STATE) -> Unit): StoreSubscriber<STATE> {
        val subscriber = object : StoreSubscriber<STATE> {
            override fun onNewState(state: STATE) = onNewState(state)
        }

        return subscribe(subscriber)
    }

    /**
     * Unsubscribes the given [subscriber] from this store's State changes.
     */
    fun unsubscribe(subscriber: StoreSubscriber<STATE>) {
        subscribers -= subscriber
    }

}