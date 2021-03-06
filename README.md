# Redux.kotlin

[![](https://jitpack.io/v/grzegorzdyrda/redux.svg)](https://jitpack.io/#grzegorzdyrda/redux)

Redux.kotlin is a predictable state container for [Kotlin](https://kotlinlang.org/) JVM & Android apps, inspired by the brilliant [Redux.js](https://redux.js.org/)!

For more information please see [The Documentation](/docs/README.md).

<!--
Although based on redux.js, redux.kotlin differs in several ways:
- Written in Kotlin
- Thread safe (call `dispatch` from any thread)
- Out-of-the-box [coroutines](https://kotlinlang.org/docs/reference/coroutines.html) support
- Built-in support for [Commands](https://www.elm-tutorial.org/en/03-subs-cmds/02-commands.html) =
easy way to perform side-effects (eg. showing dialogs, navigation etc.)
- Subscribers are notified about the current State as soon as they subscribe
-->

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
    compile 'com.github.grzegorzdyrda.redux:redux:0.9.3-alpha'
}
```

## License

MIT
