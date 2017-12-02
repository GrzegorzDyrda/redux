package com.grzegorzdyrda.redux

typealias Reducer<STATE, ACTION> = (STATE, ACTION) -> STATE