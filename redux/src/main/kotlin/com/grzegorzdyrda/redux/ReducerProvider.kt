package com.grzegorzdyrda.redux

/**
 * All Reducer providers must implement this interface.
 *
 * Its sole [rootReducer] method is called by the Store each time an Action has been dispatched.
 */
interface ReducerProvider<STATE, in ACTION> {
    /**
     * Called by the Store each time an Action has been dispatched.
     */
    fun rootReducer(state: STATE, action: ACTION): STATE
}