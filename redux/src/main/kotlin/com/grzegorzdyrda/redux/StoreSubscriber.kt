package com.grzegorzdyrda.redux

/**
 * All Store subscribers must implement this interface.
 *
 * Contains the following methods:
 * - [onNewState] (required) - called each time the State has changed
 * - [onCommandReceived] (optional) - called each time a Command has been sent
 */
interface StoreSubscriber<in STATE, in COMMAND> {
    /**
     * Called each time the State has changed.
     *
     * @param state current State provided by the Store
     */
    fun onNewState(state: STATE)

    /**
     * Called each time a Command has been sent.
     *
     * Commands are a way of informing that some side-effect should be performed.
     *
     * @param command recent Command sent from the store's Reducer
     */
    fun onCommandReceived(command: COMMAND) {
        // Default implementation simply ignores all Commands
    }
}