package com.example.cinesuggest.di

import android.content.Context
import androidx.room.Room
import com.example.cinesuggest.data.local.AppDatabase
import com.example.cinesuggest.data.local.dao.FavouriteMovieDao
import com.example.cinesuggest.data.remote.ApiService
import com.example.cinesuggest.data.repository.MovieRepositoryImpl
import com.example.cinesuggest.domain.repository.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "http://10.0.2.2:8000"

    @Provides
    @Singleton
    fun provideHttpClient() : OkHttpClient{
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit (okHttpClient: OkHttpClient) : Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json { ignoreUnknownKeys = true }

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }


    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit) : ApiService {
        return retrofit.create(ApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context : Context) : AppDatabase {
        return Room.databaseBuilder(
            context = context,
            AppDatabase::class.java,
            "cinesuggest_db"
        ).build()
    }


    @Provides
    @Singleton
    fun provideMovieDao(database : AppDatabase) : FavouriteMovieDao {
        return database.movieDao()
    }

    @Provides
    @Singleton
    fun provideMovieRepository(
        apiService: ApiService,
        movieDao: FavouriteMovieDao
    ): MovieRepository {
        return MovieRepositoryImpl(apiService =  apiService , movieDao =  movieDao)
    }
}