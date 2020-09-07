package com.ttpsc.dynamics365fieldService.core.di.api

import android.content.SharedPreferences
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.ttpsc.dynamics365fieldService.AppConfiguration
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.*
import com.ttpsc.dynamics365fieldService.bll.operations.dynamics.*
import com.ttpsc.dynamics365fieldService.core.abstraction.AuthorizationManager
import com.ttpsc.dynamics365fieldService.core.abstraction.LanguageManager
import com.ttpsc.dynamics365fieldService.core.managers.DynamicsAuthorizationManager
import com.ttpsc.dynamics365fieldService.dal.repository.DynamicsApiRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class DynamicsApiModule {
    @Provides
    @Named("BASE_ENDPOINT")
    fun getBaseEndpoint(): String {
        return AppConfiguration.endpoint + AppConfiguration.ENDPOINT_SUFFIX
    }

    @Provides
    @Singleton
    fun provideAuthorizationManager(
        sharedPreferences: SharedPreferences,
        publicClientApplication: ISingleAccountPublicClientApplication
    ): AuthorizationManager =
        DynamicsAuthorizationManager(sharedPreferences, publicClientApplication)

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit) =
        DynamicsApiRepository(retrofit)

    @Provides
    fun providesGetUserInfoOperation(
        repository: DynamicsApiRepository
    ): GetUserInfoOperation = DynamicsGetUserInfo(repository)

    @Provides
    fun provideGetProcedureDetailsOperation(
        repository: DynamicsApiRepository
    ): GetBookableResourceBookingDetailsOperation =
        DynamicsGetBookableResourceBookingDetails(repository)

    @Provides
    fun provideGetProceduresOperation(
        repository: DynamicsApiRepository,
        languageManager: LanguageManager
    ): GetBookableResourcesBookingsOperation =
        DynamicsGetBookableResourceBookingsOperation(repository)

    @Provides
    fun provideGetBookableResourceBookingsOperationCount(
        repository: DynamicsApiRepository
    ): GetBookableResourceBookingsCountOperation =
        DynamicsGetBookableResourceBookingsCount(repository)

    @Provides
    fun provideGetAlertsOperation(
        repository: DynamicsApiRepository
    ): GetAlertsOperation = DynamicsGetAlerts(repository)

    @Provides
    fun providesGetBookingStatusesOperation(
        repository: DynamicsApiRepository
    ): GetBookingStatusesOperation = DynamicsGetBookingStatusesOperation(repository)

    @Provides
    fun provideWorkOrdersOperation(repository: DynamicsApiRepository): GetWorkOrdersOperation =
        DynamicsGetWorkOrdersOperation(repository)

    @Provides
    fun provideGetBookableResourcesOperation(
        repository: DynamicsApiRepository,
        authorizationManager: AuthorizationManager
    ): GetBookableResourcesOperation =
        DynamicsGetBookableResource(repository, authorizationManager)

    @Provides
    fun providesChangeBookableResourceBookingStatus(
        repository: DynamicsApiRepository
    ): ChangeBookableResourceBookingStatusOperation =
        DynamicsChangeBookableResourceBookingStatus(repository)

    @Provides
    fun provideGetNotesOperation(
        repository: DynamicsApiRepository
    ): GetNotesOperation = DynamicsGetNotes(repository)

    @Provides
    fun provideCreateNoteOperation(
        repository: DynamicsApiRepository
    ): CreateNoteOperation = DynamicsCreateNoteOperation(repository)

    @Provides
    fun providesDownloadFileOperation(repository: DynamicsApiRepository): DownloadFileOperation =
        DynamicsDownloadFile(repository)

    @Provides
    fun providesGetFormOperation(repository: DynamicsApiRepository): GetFormOperation =
        DynamicsGetForm(repository)

    @Provides
    fun providesGetRawWorkOrdersOperation(repository: DynamicsApiRepository): GetRawWorkOrdersOperation =
        DynamicsGetRawWorkOrder(repository)

    @Provides
    fun provideGetCategoriesOperation(repository: DynamicsApiRepository): GetCategoriesOperation {
        throw NotImplementedError()
    }

    @Provides
    fun provideGetGuideDetailsOperation(repository: DynamicsApiRepository): GetProcedureOperation {
        throw NotImplementedError()
    }


    @Provides
    fun providesCreateProcedureOperation(repository: DynamicsApiRepository): CreateProcedureOperation {
        throw NotImplementedError()
    }

    @Provides
    fun providesStepProcedureOperation(repository: DynamicsApiRepository): CreateStepOperation {
        throw NotImplementedError()
    }
}