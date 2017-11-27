package com.grzegorzdyrda.redux

interface StoreSubscriber<in STATE> {
    /**
     * Called each time the State has changed.
     * @param [state] current State
     */
    fun onNewState(state: STATE)

    /**
     * Called each time a Command has been sent.
     *
     * Commands are a way of telling that some side-efect should be performed.
     */
    fun onCommandReceived(command: Any)
}