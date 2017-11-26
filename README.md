# Redux

Redux is a predictable state container for [Kotlin](https://kotlinlang.org/) apps, inspired by the brilliant [Redux.js](https://redux.js.org/)!

Although based on redux.js, redux-kotlin differs in several ways:
- Written in Kotlin
- Thread safe (call `dispatch` from any thread)
- Out-of-the-box [coroutines](https://kotlinlang.org/docs/reference/coroutines.html) support
- Built-in support for [Commands](https://www.elm-tutorial.org/en/03-subs-cmds/02-commands.html) =
easy way to perform side-effects (eg. showing dialogs, navigation etc.)

## Download

Add the JitPack repository to your build file:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency:

```
dependencies {
    compile 'com.github.GrzegorzDyrda:redux:0.9-alpha'
}
```

## Getting Started

TODO

## License

MIT
