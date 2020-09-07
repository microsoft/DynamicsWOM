package com.ttpsc.dynamics365fieldService.core.di

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat.startActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.GsonBuilder
import com.microsoft.identity.client.*
import com.ttpsc.dynamics365fieldService.AndroidApplication
import com.ttpsc.dynamics365fieldService.AppConfiguration
import com.ttpsc.dynamics365fieldService.BuildConfig
import com.ttpsc.dynamics365fieldService.R
import com.ttpsc.dynamics365fieldService.core.abstraction.AuthorizationManager
import com.ttpsc.dynamics365fieldService.core.abstraction.LanguageManager
import com.ttpsc.dynamics365fieldService.core.managers.ApiLogger
import com.ttpsc.dynamics365fieldService.core.managers.SystemLanguageManager
import dagger.Module
import dagger.Provides
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.internal.immutableListOf
import okhttp3.logging.HttpLoggingInterceptor
import org.reactivestreams.Subscriber
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton


@Module
class ApplicationModule(private val application: AndroidApplication) {

    @Provides
    @Singleton
    fun provideApplicationContext(): Context = application

    @Provides
    @Singleton
    fun providesLanguageManager(): LanguageManager = SystemLanguageManager()

    @Inject
    @Provides
    @Singleton
    fun provideRetrofit(
        @Named("BASE_ENDPOINT") baseEndpoint: String,
        authorizationManager: AuthorizationManager
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseEndpoint)
            .client(createClient(authorizationManager))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(createGsonConverter()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePublicClientApplication(context: Context): ISingleAccountPublicClientApplication {
        return HackySingleAccountPublicClientApplication(
            PublicClientApplicationConfigurationFactory.initializeConfiguration(
                context.applicationContext,
                R.raw.auth_config_maksym
            )
        )
    }

    class HackySingleAccountPublicClientApplication(c: PublicClientApplicationConfiguration) :
        SingleAccountPublicClientApplication(c)

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences
            .create(
                "AppPreferences",
                masterKeyAlias,
                application,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
    }

    class EndpointInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val desiredEndpoint = AppConfiguration.endpoint + AppConfiguration.ENDPOINT_SUFFIX
            val tag = chain.request().tag(String::class.java)

            if (tag.isNullOrEmpty() || tag != AppConfiguration.RequestTags.leaveUrl) {
                val currentUrl = chain.request().url.toString()
                if (currentUrl.startsWith(desiredEndpoint))
                    return chain.proceed(chain.request())

                val urlPath = currentUrl.substring(
                    currentUrl.indexOf(AppConfiguration.ENDPOINT_SUFFIX)
                            + AppConfiguration.ENDPOINT_SUFFIX.length
                )
                val newRequest = chain.request().newBuilder()
                    .url(desiredEndpoint + urlPath)
                    .build()
                return chain.proceed(newRequest)
            } else {
                return chain.proceed(chain.request())
            }
        }
    }

    private fun createClient(authorizationManager: AuthorizationManager): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val loggingInterceptor =
                HttpLoggingInterceptor(ApiLogger()).setLevel(HttpLoggingInterceptor.Level.BODY)
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }

        okHttpClientBuilder.addInterceptor(EndpointInterceptor())
        okHttpClientBuilder.addInterceptor(authorizationManager)
        okHttpClientBuilder.authenticator(authorizationManager)
        return okHttpClientBuilder.build()
    }

    private fun createGsonConverter() =
        GsonBuilder()
            .create()
}