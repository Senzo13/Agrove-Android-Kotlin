package fr.devid.plantR.dagger

import fr.devid.plantR.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import fr.devid.plantR.MainActivityBle

@Module
interface MainActivityModule {

    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    fun contributeMainActivity(): MainActivity
    fun contributeMainActivityBle(): MainActivityBle

}
