package com.example.redux.async.model

/**
 * Retrofit models.
 *
 * Created by Grzegorz Dyrda on 2017-12-08
 */

data class Repo(val name: String)

data class Contributor(val login: String, val contributions: Int)