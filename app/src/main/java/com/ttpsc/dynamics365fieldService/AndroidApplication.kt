package com.ttpsc.dynamics365fieldService

import android.app.Application
import com.ttpsc.dynamics365fieldService.core.di.ApplicationComponent
import com.ttpsc.dynamics365fieldService.core.di.ApplicationModule
import com.ttpsc.dynamics365fieldService.core.di.DaggerApplicationComponent

class AndroidApplication : Application() {

    companion object {
        var companionAppComponent: ApplicationComponent? = null
    }

    private val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        this.injectMembers()
        companionAppComponent = appComponent
    }

    private fun injectMembers() = appComponent.inject(this)
}
