package com.ttpsc.dynamics365fieldService.bll.operations.dynamics

import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.DownloadFileOperation
import com.ttpsc.dynamics365fieldService.core.extensions.executeOnBackground
import com.ttpsc.dynamics365fieldService.dal.repository.DynamicsApiRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class DynamicsDownloadFile @Inject constructor(dynamicsRepository: DynamicsApiRepository) :
    DownloadFileOperation {
    private val _dynamicsRepository = dynamicsRepository
    override lateinit var fileUrl: String

    override fun execute(): Observable<ByteArray> {
        return _dynamicsRepository
            .downloadFile(fileUrl)
            .executeOnBackground()
            .map { it?.bytes() }
    }
}