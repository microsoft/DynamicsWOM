package com.ttpsc.dynamics365fieldService.views.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.ttpsc.dynamics365fieldService.AndroidApplication
import com.ttpsc.dynamics365fieldService.R
import com.ttpsc.dynamics365fieldService.core.abstraction.AuthorizationManager
import com.ttpsc.dynamics365fieldService.core.abstraction.LanguageManager
import com.ttpsc.dynamics365fieldService.core.abstraction.LifecycleProvidingActivity
import com.ttpsc.dynamics365fieldService.views.LoginFragment
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), LifecycleProvidingActivity {

    @Inject
    lateinit var languageManager: LanguageManager

    @Inject
    lateinit var authorizationManager: AuthorizationManager

    private val onCreateSubject = PublishSubject.create<Unit>()
    private val onStartSubject = PublishSubject.create<Unit>()
    private val onResumeSubject = PublishSubject.create<Unit>()
    private val onPauseSubject = PublishSubject.create<Unit>()
    private val onStopSubject = PublishSubject.create<Unit>()
    private val onDestroySubject = PublishSubject.create<Unit>()

    override val onCreate: Observable<Unit>
        get() = onCreateSubject
    override val onStart: Observable<Unit>
        get() = onStartSubject
    override val onResume: Observable<Unit>
        get() = onResumeSubject
    override val onPause: Observable<Unit>
        get() = onPauseSubject
    override val onStop: Observable<Unit>
        get() = onStopSubject
    override val onDestroy: Observable<Unit>
        get() = onDestroySubject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidApplication.companionAppComponent?.inject(this)
        languageManager.registerForLanguageChange(this)
        val window: Window = window

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.statusbar_color)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        AppCenter.start(
            application, "75943365-242e-4696-b6f3-c865deda9b64",
            Analytics::class.java, Crashes::class.java
        )

        Crashes.hasCrashedInLastSession()
            .thenAccept { hasError ->
                if (hasError) {
                    Crashes.getLastSessionCrashReport().thenAccept { error ->
                        Analytics.trackEvent(error.stackTrace)
                    }
                }
            }

        setContentView(R.layout.activity_main)

        authorizationManager.userNeedsLogin.subscribe {
            val navHostFragment: NavHostFragment? =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val currentFragment = navHostFragment!!.childFragmentManager.fragments[0]
            val navHost = currentFragment.nav_host_fragment as Fragment
            if (!(navHost is LoginFragment)) {
                clearBackStack()
            }
        }

    }

    override fun onBackPressed() {
        hideLoadingIndicator()
        // TODO In case backing from fragments does not work think of below line
//        Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment).popBackStack()
        super.onBackPressed()
    }

    fun showLoadingIndicator() {
        loadingIndicatorLayout.visibility = View.VISIBLE
    }

    fun hideLoadingIndicator() {
        loadingIndicatorLayout.visibility = View.GONE
    }

    fun showPopup(popupMessage: String, popupTitle: String) {
        val alertDialog: AlertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setTitle(popupTitle)
        alertDialog.setMessage(popupMessage)
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "OK"
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }

    fun clearBackStack() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        this.finish()
    }

    override fun onStop() {
        super.onStop()
        onStopSubject.onNext(Unit)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        onCreateSubject.onNext(Unit)
    }

    override fun onStart() {
        super.onStart()
        onStartSubject.onNext(Unit)
    }

    override fun onResume() {
        super.onResume()
        onResumeSubject.onNext(Unit)
    }

    override fun onPause() {
        super.onPause()
        onPauseSubject.onNext(Unit)
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestroySubject.onNext(Unit)
    }
}

