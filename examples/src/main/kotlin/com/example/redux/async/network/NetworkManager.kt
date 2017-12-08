package com.example.redux.async.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Network manager singleton.
 *
 * Created by Grzegorz Dyrda on 2017-12-08
 */
object NetworkManager {

    val github: GitHubService by lazy {
        retrofit.create(GitHubService::class.java)
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

}