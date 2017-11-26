# Redux
Redux is a predictable state container for [Kotlin](https://kotlinlang.org/) apps, inspired by the brilliant [Redux.js](https://redux.js.org/)!

Although based on redux.js, redux-kotlin differs from it in several of ways:
- Written in Kotlin
- Thread safe (call `dispatch` from any thread)
- Out-of-the-box [coroutines](https://kotlinlang.org/docs/reference/coroutines.html) support
- Built-in support for [Commands](https://www.elm-tutorial.org/en/03-subs-cmds/02-commands.html) =
easy way to perform side-effects (eg. showing dialogs, navigation etc.)
