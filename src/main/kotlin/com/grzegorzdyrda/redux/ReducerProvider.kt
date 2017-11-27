package com.grzegorzdyrda.redux

interface ReducerProvider<STATE, in ACTION> {
    /**
     * Called by the Store each time an Action is dispatched.
     */
    fun rootReducer(state: STATE, action: ACTION): STATE
}