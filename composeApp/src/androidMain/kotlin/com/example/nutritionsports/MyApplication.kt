package com.example.nutritionsports

import android.app.Application

import com.example.di.initializeKoin

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import io.github.jan.supabase.annotations.SupabaseInternal
import org.koin.android.ext.koin.androidContext


class MyApplication : Application() {
    @OptIn(SupabaseInternal::class)
    override fun onCreate() {
        super.onCreate()
        initializeKoin(
            config = {
                androidContext(this@MyApplication)
            }
        )
        Firebase.initialize(context = this)

       

    }
}