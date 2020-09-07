package com.ttpsc.dynamics365fieldService.bll.operations.dynamics

import com.google.gson.Gson
import com.ttpsc.dynamics365fieldService.bll.abstraction.operations.CreateNoteOperation
import com.ttpsc.dynamics365fieldService.core.extensions.executeOnBackground
import com.ttpsc.dynamics365fieldService.dal.models.dynamics.DalChangeStatusErrorBody
import com.ttpsc.dynamics365fieldService.dal.models.dynamics.DalCreateNoteRequestBody
import com.ttpsc.dynamics365fieldService.dal.repository.DynamicsApiRepository
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response
import java.util.*
import javax.inject.Inject

class DynamicsCreateNoteOperation  @Inject constructor(
    repository: DynamicsApiRepository
) :
    CreateNoteOperation {
    override var filename: String? = null
    override var subject: String? = null
    override var isDocument: Boolean = false
    override lateinit var objecttypecode: String
    override var workOrderId: String? = null
    override var documentBody: Array<Byte>? = null

    private val _repository = repository

    override fun execute(): Observable<Pair<Boolean, String?>> {

        val note = DalCreateNoteRequestBody()

        if (filename != null) {
            note.filename = this.filename!!
        }

        if (subject != null) {
            note.subject = this.subject!!
        }

        note.isdocument = this.isDocument

        if (workOrderId != null) {
            note.objecttypecode = "msdyn_workorder"
            note.workOrderBindPath = "/msdyn_workorders($workOrderId)"
        }
        if(documentBody != null) {
            note.base64documentbody = Base64.getEncoder().encodeToString(documentBody!!.toByteArray())
        }

        return _repository.createNote(note)
            .executeOnBackground()
            .map {response ->
                if (response.isSuccessful == false) {
                    val error = Gson().fromJson(
                        response.errorBody()?.string(),
                        DalChangeStatusErrorBody::class.java
                    )
                    return@map Pair<Boolean, String?>(true, error.error?.message)
                }
                return@map Pair<Boolean, String?>(false, null)
            }
    }
}
