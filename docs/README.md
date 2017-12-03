# Table of Contents

* [Introduction](#introduction)
* [Basics](#basics)
* [Examples](#examples)
  * [Hello World](#hello-world)
  * [Counter](#counter)
  * [Async Actions](#async-actions)

## Introduction

Redux.kotlin is a **predictable state container** for Kotlin JVM & Android apps. It's inspired by [Redux.js](https://redux.js.org/) but adapted to the specific needs of JVM & Android apps.

The purpuse of Redux is to **help you manage your app's state**, especially in a multi-threaded environment.

Additionally it serves as your **single source of truth** - all your app's state is stored in a single place. Thus you can always see what's going on.

It also **makes debugging easy**, because all state changes must go through the single pipeline. You can log/record each single state change to easily spot possible problems.

## Basics

Redux is actually really simple.

You describe the state of your app/window/activity as a simple POJO:

```kotlin
data class AppState(val count: Int) : State
```

Think of it as a "model" for you app/view etc.

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

And that's it!

The basics of Redux are simple. But when combined with Kotlin's coroutines, ViewModel, Anvil and similar libraries - it becomes a very powerful tool that keeps your app's behaviour predictable even at large scale.


## Examples

### Hello World

TODO

You can see the whole source code [here](#).

### Counter

TODO

You can see the whole source code [here](#).

### Async Actions

TODO

You can see the whole source code [here](#).
