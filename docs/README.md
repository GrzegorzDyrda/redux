# Table of Contents

* [Introduction](#introduction)
  * [What is Redux?](#what-is-redux)
  * [What is it for?](#what-is-it-for)
* [Basics](#basics)
* [Examples](#examples)
  * [Hello World](#hello-world)
  * [Counter](#counter)
  * [Async Actions](#async-actions)

## Introduction

### What is Redux?

[Redux](https://redux.js.org/) is a predictable state container for JavaScript apps.

[Redux.kotlin](#) is a reimplementation of this library written in [Kotlin](https://kotlinlang.org/). It's based on the same principles but adapted to the specific needs of JVM & Android apps.

### What is it for?

The purpose of Redux is to **simplify application's state management**, especially in large and/or multi-threaded environment.

Redux serves as your **single source of truth**. The state of your whole application is stored in a single place.
Also, **the state is read-only**. The only way to change it is to **dispatch an action**.
 
<!--All state changes must go through a single pipeline.-->

This single pipeline makes your everyday life **much much easier**. Things like **keeping track of
the state, logging, debugging, or even time-travel** - suddenly become easy.

<!-- You can log/record each single state change to easily spot possible problems. -->

On top of it, Redux is **extremely simple**. It only takes 10-15 minutes to learn the basics.

And when combined with Kotlin's [coroutines](https://kotlinlang.org/docs/reference/coroutines.html),
[ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel.html),
[Anvil](https://github.com/zserge/anvil) and similar libraries - it becomes a very powerful tool
that keeps your apps concise & predictable even at large scale.

## Basics

Redux is really simple.

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

And that's it! Now each time the state changes, you'll be notified about it.

> The source code of this example can be found [here](../examples/src/main/kotlin/com/example/redux/Basic.kt).


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
