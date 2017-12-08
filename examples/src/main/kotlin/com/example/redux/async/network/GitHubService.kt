package com.example.redux.async.network

import com.example.redux.async.model.Contributor
import com.example.redux.async.model.Repo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by Grzegorz Dyrda on 2017-12-08
 */
interface GitHubService {

    @GET("users/{user}/repos")
    fun repos(@Path("user") user: String): Call<List<Repo>>

    @GET("/repos/{owner}/{repo}/contributors")
    fun contributors(
            @Path("owner") owner: String,
            @Path("repo") repo: String): Call<List<Contributor>>
}