package com.grzegorzdyrda.redux

/**
 * All State POJOs must implement this interface.
 *
 * Its sole [CMD] property does nothing by default. But once overridden, it serves as a means of
 * sending *Commands* (objects that describe one-time (fire-and-forget) side-effects).
 */
interface State<COMMAND> {
    var CMD: COMMAND?
        get() = null
        set(value) {}
}

// Old version
//interface StateWithCommand<COMMAND> {
//    var CMD: COMMAND?
//}
//
//interface State : StateWithCommand<Unit> {
//    override var CMD: Unit?
//        get() = null
//        set(value) {}
//}