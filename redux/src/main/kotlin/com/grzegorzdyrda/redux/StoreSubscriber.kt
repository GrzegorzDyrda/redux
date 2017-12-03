package com.grzegorzdyrda.redux

/**
 * All Store subscribers must implement this interface.
 *
 * Contains the following methods:
 * - [onNewState] (required) - called each time the State has changed
 * - [onCommandReceived] (optional) - called each time a Command has been sent
 */
interface StoreSubscriber<in STATE> {
    /**
     * Called each time the State has changed.
     *
     * @param state current State provided by the Store
     */
    fun onNewState(state: STATE)
}