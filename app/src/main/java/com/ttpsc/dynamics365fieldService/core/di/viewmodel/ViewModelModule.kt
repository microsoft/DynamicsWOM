package com.ttpsc.dynamics365fieldService.core.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ttpsc.dynamics365fieldService.viewmodels.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindsLoginViewModel(viewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StartViewModel::class)
    abstract fun bindsStartViewModel(viewModel: StartViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProcedureDetailsViewModel::class)
    abstract fun bindsProcedureDetailsViewModel(viewModel: ProcedureDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProcedureStepViewModel::class)
    abstract fun bindsProcedureStepViewModel(viewModel: ProcedureStepViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProcedureStepsViewModel::class)
    abstract fun bindsProcedureStepsViewModel(viewModel: ProcedureStepsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GalleryViewModel::class)
    abstract fun bindsGalleryViewModel(viewModel: GalleryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindsSettingsViewModel(viewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProcedureListViewModel::class)
    abstract fun bindsProcedureListViewModel(viewModel: ProcedureListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindsMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NoteDetailsViewModel::class)
    abstract fun bindsNoteDetailsViewModel(viewModel: NoteDetailsViewModel): ViewModel
}