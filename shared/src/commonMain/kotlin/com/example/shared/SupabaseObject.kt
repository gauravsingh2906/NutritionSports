package com.example.shared

import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient

import io.github.jan.supabase.storage.Storage
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object SupabaseObject {


    @OptIn(SupabaseInternal::class)
    val supabase = createSupabaseClient(
        supabaseUrl = "https://kivtzypuyiivcvlogqak.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImtpdnR6eXB1eWlpdmN2bG9ncWFrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTA3NDc0MDksImV4cCI6MjA2NjMyMzQwOX0.OzVC768078SXLVQJxCPAHm4oO-iVwHVhGf_nHY55V7E"
    ) {
        install(Storage)
        httpConfig {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys  = true })
            }
        }

    }

}