package com.energymonitor.app.di

import com.energymonitor.app.data.remote.ApiService
import com.energymonitor.app.data.repository.EnergyRepositoryImpl
import com.energymonitor.app.domain.repository.EnergyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/") // Android Emulator için localhost
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideEnergyRepository(api: ApiService): EnergyRepository {
        return EnergyRepositoryImpl(api)
    }
}
