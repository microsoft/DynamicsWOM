package com.ttpsc.dynamics365fieldService.views

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.ttpsc.dynamics365fieldService.AndroidApplication
import com.ttpsc.dynamics365fieldService.R
import com.ttpsc.dynamics365fieldService.bll.models.QrCodeScan
import com.ttpsc.dynamics365fieldService.viewmodels.LoginViewModel
import com.ttpsc.dynamics365fieldService.views.activities.MainActivity
import com.ttpsc.dynamics365fieldService.views.extensions.clicks
import com.ttpsc.dynamics365fieldService.views.extensions.viewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_login.*
import java.lang.Exception
import java.net.URI
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class LoginFragment : Fragment() {

    companion object {
        private const val BARCODE_REQUEST_CODE = 1984
        private const val SCAN_BARCODE = "com.realwear.barcodereader.intent.action.SCAN_BARCODE"

        private const val EXTRA_RESULT = "com.realwear.barcodereader.intent.extra.RESULT"
        private const val EXTRA_CODE_QR = "com.realwear.barcodereader.intent.extra.CODE_QR"
    }

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var disposeBag: CompositeDisposable
    private var qrCodeSubject: PublishSubject<QrCodeScan> = PublishSubject.create()

    private var navigationParameters: LoginFragmentArgs? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidApplication.companionAppComponent?.inject(this)

        loginViewModel = viewModel(viewModelFactory) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CookieSyncManager.createInstance(requireContext())
        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookie()

        try {
            navigationParameters = LoginFragmentArgs.fromBundle(requireArguments())
        }catch(ex: Exception){
            Log.w("LOGIN ARGUMENTS WARNING", ex.message)
        }
        disposeBag = CompositeDisposable()

        disposeBag += scanButton.clicks().subscribe() {
            launchQrScanner()
        }

        var scanResult: QrCodeScan? = null
        qrCodeSubject
            .throttleFirst(100, TimeUnit.MILLISECONDS)
            .flatMap { qrResult ->
                (activity as MainActivity).showLoadingIndicator()
            val validationResult = validateQrCode(qrResult.environmentUrl)
            if (validationResult.isNullOrEmpty()) {
                return@flatMap Observable.just(false)
            }

            scanResult = qrResult
            scanResult?.environmentUrl = validationResult
            loginViewModel.logInOperation.apply {
                activity = requireActivity()
                environmentUrl = scanResult?.environmentUrl
                userName = scanResult?.login
            }.execute()
        }.throttleFirst(100, TimeUnit.MILLISECONDS)
            .flatMap {
            (activity as MainActivity).hideLoadingIndicator()
            Observable.just(it)
        }.subscribe({
            if (it) {
                if (navigationParameters != null && navigationParameters?.bookableResourceBookingId != null)
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToGuideSummaryGraph(
                            navigationParameters!!.bookableResourceBookingId,
                            null,
                            null,
                            null,
                            null
                        )
                    )
                else
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainMenuFragment())

            } else {
                (activity as MainActivity).hideLoadingIndicator()
            }
        }) { exception ->
            (activity as MainActivity).showPopup(exception.localizedMessage, "")
            println(exception)
        }.addTo(disposeBag)
    }

    private fun validateQrCode(url: String): String? {
        return try {
            val uri = URI(url)
            val uriWithoutDomain = uri.toString().replace(uri.path, "")
            if (uriWithoutDomain.isEmpty()) {
                (activity as MainActivity).showPopup(
                    getString(R.string.incorrect_qr_code_error),
                    ""
                )
            }
            uriWithoutDomain
        } catch (e: Exception) {
            (activity as MainActivity).showPopup(getString(R.string.incorrect_qr_code_error), "")
            null
        }
    }

    private fun launchQrScanner() {
//        qrCodeSubject.onNext(
//            QrCodeScan(
//                "https://orgc7671703.crm.dynamics.com",
//                "earlenh@onemtc.net"
////                "https://fsdemott.crm4.dynamics.com",
////                "test1@kakubicz.onmicrosoft.com"
//            )
//        )
//        return

        val intent = Intent(SCAN_BARCODE)
        intent.putExtra(EXTRA_CODE_QR, true)
        startActivityForResult(intent, BARCODE_REQUEST_CODE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposeBag.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_OK || requestCode != BARCODE_REQUEST_CODE || data == null)
            return

        val result = data.getStringExtra(EXTRA_RESULT) ?: return

        try {
            val decoded = String(Base64.getDecoder().decode(result))
            val qrCode = Gson().fromJson(decoded, QrCodeScan::class.java)
            qrCodeSubject.onNext(qrCode)
        }
        catch (e : Exception) {
            AlertDialog.Builder(requireContext()).apply {
                setCustomTitle(
                    layoutInflater.inflate(R.layout.view_alert_dialog_title, null).apply {
                        findViewById<TextView>(R.id.title).text =
                            getString(R.string.incorrect_qr_code_title)
                    })
                setMessage(getString(R.string.incorrect_qr_code_error))
                setNeutralButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                setOnDismissListener {
                    (activity as MainActivity).hideLoadingIndicator()
                }
            }.create().show()
        }
    }
}
