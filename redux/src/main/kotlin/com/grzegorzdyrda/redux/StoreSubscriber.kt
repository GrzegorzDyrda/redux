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

    /**
     * Called each time a Command has been sent.
     *
     * Commands are a way of telling that some one-time side-effect should be performed.
     *
     * @param command recent Command sent by using [Store.sendCommand]
     */
    fun onCommandReceived(command: Any) {
        // Default implementation throws an exception
        throw NotImplementedError("A Command has been sent, but Target class doesn't implement onCommandReceived()! Please implement the missing method. Command = $command, Target = $this")
    }
}