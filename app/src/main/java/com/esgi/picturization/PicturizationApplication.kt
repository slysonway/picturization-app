package com.esgi.picturization

import android.app.Application
import com.esgi.picturization.data.db.AppDatabase
import com.esgi.picturization.data.network.AuthApi
import com.esgi.picturization.data.network.AuthenticationInterceptorRefreshToken
import com.esgi.picturization.data.network.ImageApi
import com.esgi.picturization.data.network.NetworkConnectionInterceptor
import com.esgi.picturization.data.repositories.ImageRepository
import com.esgi.picturization.data.repositories.UserRepository
import com.esgi.picturization.ui.auth.AuthViewModelFactory
import com.esgi.picturization.ui.home.profile.ProfileViewModelFactory
import com.esgi.picturization.ui.home.start.StartViewModelFactory
import com.esgi.picturization.ui.home.transform.TransformPictureViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class PicturizationApplication: Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@PicturizationApplication))

        //INTERCEPTOR AREA
        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { AuthenticationInterceptorRefreshToken(instance()) }

        //API AREA
        bind() from singleton { AuthApi(instance()) }
        bind() from singleton { ImageApi(instance(), instance()) }

        //REPOSITORY AREA
        bind() from singleton { AppDatabase(instance()) }
        bind() from singleton { UserRepository(instance(), instance()) }
        bind() from singleton { ImageRepository(instance()) }

        //VIEW MODEL AREA
        bind() from provider { AuthViewModelFactory(instance()) }
        bind() from provider { ProfileViewModelFactory(instance(), instance()) }
        bind() from provider { StartViewModelFactory() }
        bind() from provider { TransformPictureViewModelFactory() }
    }
}