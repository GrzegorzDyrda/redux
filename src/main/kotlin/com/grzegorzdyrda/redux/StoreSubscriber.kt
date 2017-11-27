package com.grzegorzdyrda.redux

interface StoreSubscriber<in STATE> {
    /**
     * Fired each time the State has changed.
     * @param [state] current State
     */
    fun onNewState(state: STATE)

    /**
     * Fired each time a Command has been sent.
     *
     * Commands are a way of telling that some side-efect should be performed.
     */
    fun onCommandReceived(command: Any)
}