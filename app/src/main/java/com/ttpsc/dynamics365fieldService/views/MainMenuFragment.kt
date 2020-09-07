package com.ttpsc.dynamics365fieldService.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ttpsc.dynamics365fieldService.AndroidApplication
import com.ttpsc.dynamics365fieldService.BuildConfig
import com.ttpsc.dynamics365fieldService.R
import com.ttpsc.dynamics365fieldService.helpers.extensions.plusAssign
import com.ttpsc.dynamics365fieldService.viewmodels.MainViewModel
import com.ttpsc.dynamics365fieldService.views.activities.MainActivity
import com.ttpsc.dynamics365fieldService.views.extensions.clicks
import com.ttpsc.dynamics365fieldService.views.extensions.viewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main_menu.*
import javax.inject.Inject


class MainMenuFragment : Fragment() {
    private lateinit var disposeBag: CompositeDisposable
    private lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidApplication.companionAppComponent?.inject(this)

        mainViewModel = viewModel(viewModelFactory) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disposeBag = CompositeDisposable()

        if (mainViewModel.hasUserInfo() == false) {
            (activity as MainActivity).showLoadingIndicator()
            mainViewModel.setUserInfo()
                .subscribe({userInfo ->
                    userDataTextView.text = mainViewModel.getUserFullName()
                    (activity as MainActivity).hideLoadingIndicator()
                },
                    { ex ->
                        (activity as MainActivity).hideLoadingIndicator()
                        Log.e("USER INFO ERROR", ex.message)
                    })
        } else {
            userDataTextView.text = mainViewModel.getUserFullName()
        }

        version.text = getString(R.string.version, BuildConfig.VERSION_NAME)

        disposeBag += bookingsButton.clicks().subscribe {
            findNavController().navigate(R.id.action_mainMenuFragment_to_guidesListFragment)
        }

        disposeBag += openIconicsButton.clicks().subscribe {
            // TODO This should work and would be better but currently we have no proper ICONICS app which recognizes these deep links
//            Intent(Intent.ACTION_VIEW).apply {
//                data = Uri.parse("iconicsapp://iconics/") // iconicsapp://iconics/${this.props.lastUrl}
//                startActivity(this)
//            }
            try {
                startActivity(requireActivity().packageManager.getLaunchIntentForPackage("com.iconics.AndroidAppHub"))
            } catch (e: Exception) {
                Log.e("CONTACT EXPERT", "Failed to open ICONICS!", e)
            }
        }

        disposeBag += openTeamsButton.clicks().subscribe {
            // TODO This should work and would be better but currently Teams app run this way crashes
//            Intent(Intent.ACTION_VIEW).apply {
//                data = Uri.parse("msteams://teams.microsoft.com/l/") // msteams://teams.microsoft.com/l/chat/0/0?users=username@email.com
//                startActivity(this)
//            }
            try {
                startActivity(requireActivity().packageManager.getLaunchIntentForPackage("com.microsoft.teams"))
            } catch (e: Exception) {
                Log.e("CONTACT EXPERT", "Failed to open MS Teams!", e)
            }
        }

        disposeBag += logoutButton.clicks()
            .observeOn(Schedulers.io())
            .subscribe {
                mainViewModel.authorizationManager.signOut()
                CookieSyncManager.createInstance(requireContext())
                val cookieManager: CookieManager = CookieManager.getInstance()
                cookieManager.removeAllCookie()
                findNavController().navigate(R.id.action_mainMenuFragment_to_loginFragment)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposeBag.dispose()
    }
}
