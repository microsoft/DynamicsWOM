package com.ttpsc.dynamics365fieldService.core.di

import com.ttpsc.dynamics365fieldService.AndroidApplication
import com.ttpsc.dynamics365fieldService.core.di.api.AuthenticationModule
import com.ttpsc.dynamics365fieldService.core.di.api.DynamicsApiModule
import com.ttpsc.dynamics365fieldService.core.di.viewmodel.ViewModelModule
import com.ttpsc.dynamics365fieldService.views.*
import com.ttpsc.dynamics365fieldService.views.activities.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, DynamicsApiModule::class, ViewModelModule::class, AuthenticationModule::class])
interface ApplicationComponent {
    fun inject(application: AndroidApplication)
    fun inject(loginFragment: LoginFragment)
    fun inject(guidesListFragment: GuidesListFragment)
    fun inject(guideSummaryFragment: GuideSummaryFragment)

    fun inject(guideDetailsFragment: GuideDetailsFragment)
    fun inject(startFragment: StartFragment)
    fun inject(mainActivity: MainActivity)
    fun inject(notesListFragment: NotesListFragment)
    fun inject(guideDetailsAttachmentsFragment: GuideDetailsAttachmentsFragment)
    fun inject(mainMenuFragment: MainMenuFragment)
    fun inject(noteDetailsFragment: NoteDetailsFragment)

}