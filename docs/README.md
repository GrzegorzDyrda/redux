# Table of Contents

* [Introduction](#introduction)
  * [What is Redux?](#what-is-redux)
  * [What is it for?](#what-is-it-for)
* [Hello World](#hello-world)
* [Basics](#basics)
  * [State](#state)
  * [Actions](#actions)
  * [Reducers](#reducers)
  * [Store](#store)
  * [Commands](#commands)
* [Async Flow](#async-flow)
  * [Async Actions](#async-actions)
  * [Coroutines](#coroutines)
* [Android](#android)
* [RxJava Integration](#rxjava-integration)

## Introduction

### What is Redux?

[Redux.kotlin](#) is an implementation of [Redux](https://redux.js.org/) library written in [Kotlin](https://kotlinlang.org/) and adapted to the specific needs of JVM & Android apps.

### What is it for?

The purpose of Redux is to **simplify application's state management**, especially in large and/or multi-threaded environment.

Redux serves as your **single source of truth**. The state of your whole application is stored in a single place.
Also, **the state is read-only**. The only way to change it is to **dispatch an action**.
 
<!--All state changes must go through a single pipeline.-->

This single pipeline makes your everyday life **much much easier**. Things like **keeping track of asynchronous state changes, logging, debugging, testing or even time-travel** - suddenly become easy.

<!-- You can log/record each single state change to easily spot possible problems. -->

On top of it, Redux is **extremely simple**. It roughly 15 minutes to learn the basics.

And when combined with Kotlin's [coroutines](https://kotlinlang.org/docs/reference/coroutines.html),
[ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel.html),
[Anvil](https://github.com/zserge/anvil) and similar libraries - it becomes a very powerful tool
that keeps your apps concise & predictable even at large scale.

## Hello World

Redux is really simple.

You describe the state of your app/screen as a simple POJO:

```kotlin
data class AppState(val count: Int)
```

Think of it as a "model" for you app/screen etc.

As you can see, the state is immutable (note the `val` keyword), thus nobody can change it directly. The only way to change the state is to dispatch "actions". Actions are also simple POJOs:

```kotlin
class IncrementAction : Action
class DecrementAction : Action
```

Now we define how those Actions will affect our State. We do it by writing a simple function called a "reducer". It's just a function that takes current State and an Action as arguments, and returns the next State:

```kotlin
fun counterReducer(state: AppState, action: Action): AppState {
    return when (action) {
        is IncrementAction -> state.copy(count = state.count + 1)
        is DecrementAction -> state.copy(count = state.count - 1)
        else -> state
    }
}
```

Because state is immutable, we take advantage of Kotlin's `copy` method, to return only slightly changed copy of a state received as an argument.

The thing that combines everything together is called a "store". Let's create one:

```kotlin
val store = Store(AppState(1), ::counterReducer)
```

The store takes two arguments: initial state and a reducer function.

In order to listen to state changes we simply "subscribe" to the store:

```kotlin
store.subscribe { state ->
    println("onNewState: $state")
}
```

Now we're ready to actually "dispatch" our actions and observe how the state changes in result:

```kotlin
// onNewState: AppState(count=1)       <-- initial state, received upon subscription
store.dispatch(IncrementAction())
// onNewState: AppState(count=2)
store.dispatch(IncrementAction())
// onNewState: AppState(count=3)
store.dispatch(DecrementAction())
// onNewState: AppState(count=2)
```

And that's it! Now each time the state changes, you'll be notified about it.

> The source code of this example can be found [here](../examples/src/main/kotlin/com/example/redux/basic/Basic.kt).


## Basics

### State

State is just a simple POJO that describes the whole state of your application/screen.

Imagine your application has a screen with a list of TODO items for the given day. It's state might look like this:

```kotlin
data class TodoState(val day: Date, val todos: Map<Long, Todo>)
```

State should be read-only (hence the `val` keyword), so that the only way to change it is to dispatch an [Action](#actions).

### Actions

Actions are also simple POJOs. We use them to describe that *something happened*, so that the state should change in some way. But they don't tell *how* to do it (that's what [Reducers](#reducers) are for).

Example actions in a TODO application might look like this:

```kotlin
sealed class Action
data class ADD_TODO(val text: String) : Action()
data class TOGGLE_TODO(val id: Long) : Action()
data class REMOVE_TODO(val id: Long) : Action()
```

The use `sealed` class helps us avoid subtle bugs like forgetting about some action.

### Reducers

Reducer is just a **pure function** that takes the current State and an Action, and returns next State. It's very important for Reducers to be pure. They **must not** perform any side-effects, call APIs etc.

Example of a reducer in a TODO app might look like this:

```kotlin
fun todoReducer(state: TodoState, action: Action) = when (action) {
    is ADD_TODO -> state.copy(
            todos = state.todos + Todo(action.text)
    )
    is REMOVE_TODO -> state.copy(
            todos = state.todos - action.id
    )
    ...
}
```

### Store

Store is the most important part of Redux. It combines all the previous elements together.

```kotlin
TODO
```

### Commands

Commands are simple objects of any type. They are a way of telling that a one-time side-effect should be performed (e.g. perform navigation, show toast etc.).
They are sent via `Store.sendCommand(command)` and received in `StoreSubscriber.onCommandReceived(command)`.

Imagine you want to show a temporary popup to the user (alert/toast/snackbar etc.).

You could dispatch an Action (e.g. `SHOW_TOAST("Success!")`) to set a flag in the state
(e.g. `toast = "Success!"`) and then react to this flag in your View.
But since this flag is part of a state, when your screen gets recreated, the popup would show again, right?
One way to cope with this would be to dispatch a separate Action (e.g. `CLEAR_TOAST`) that would clear
the flag (e.g. `toast = null`) right after the popup got shown. But this is just cumbersome.

Fortunately, we have a better way to deal with this. Please welcome Commands!

With Commands you can simply do something like this:

```kotlin
data class ShowToast(val msg: String)

store.sendCommand(ShowToast("Success!"))

// in the Subscriber:
override fun onCommandReceived(command: Any) {
    if (command is ShowToast) {
        Toast.makeText(this, command.msg, Toast.LENGTH_LONG).show()
    }
}
```

Since Commands are not stored in the state, they're ideal for one-time effects like this.

## Async Flow

### Async Actions

TODO

### Coroutines

TODO

## Android

TODO

## RxJava Integration

TODO

> You can see the whole source code [here](../examples/src/main/kotlin/com/example/redux/observable/Observable.kt).
