package fr.devid.plantR.dagger

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import fr.devid.plantR.App
import fr.devid.plantR.BuildConfig
import fr.devid.plantR.Constants
import fr.devid.plantR.api.AppService
import fr.devid.plantR.moshi.ThreeTenAdapters
import fr.devid.plantR.room.AppDatabase
import fr.devid.plantR.services.AuthenticationTokenInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module(includes = [ViewModelModule::class])
object AppModule {

    @Singleton
    @Provides
    @JvmStatic
    fun provideOkHttpClient(authenticationTokenInterceptor: AuthenticationTokenInterceptor): OkHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(authenticationTokenInterceptor)
        if (BuildConfig.DEBUG) {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
    }.build()

    @Singleton
    @Provides
    @JvmStatic
    fun provideAppService(okHttpClient: OkHttpClient): AppService = Retrofit.Builder().apply {
        baseUrl(Constants.BASE_URL)
        addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder().apply {
                    add(KotlinJsonAdapterFactory())
                    add(ThreeTenAdapters())
                }.build()
            )
        )
        client(okHttpClient)
    }.build().create(AppService::class.java)

//    @Singleton
//    @Provides
//    @JvmStatic
//    fun provideAppServiceLiveObjectsCommandsPost(okHttpClient: OkHttpClient, dev_eui : String, appService : AppService): LiveObjectsCommandsRaw = Retrofit.Builder().apply {
//        baseUrl("${Constants.BASE_URL_LIVE_OBJECTS}/$dev_eui/commands")
//        addConverterFactory(
//            MoshiConverterFactory.create(
//                Moshi.Builder().apply {
//                    add(KotlinJsonAdapterFactory())
//                    add(ThreeTenAdapters())
//                }.build()
//            )
//        )
//        client(okHttpClient)
//    }.build().create(appService.commands(LiveObjectsCommandsRaw("", 2, true)))

    @Provides
    @JvmStatic
    fun provideContext(app: App): Context
        = app

    @Singleton
    @Provides
    @JvmStatic
    fun provideAppDatabase(applicationContext: Context): AppDatabase
        = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app-db").build()

}
