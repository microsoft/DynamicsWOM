package com.ttpsc.dynamics365fieldService.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ttpsc.dynamics365fieldService.AndroidApplication
import com.ttpsc.dynamics365fieldService.R
import com.ttpsc.dynamics365fieldService.viewmodels.StartViewModel
import com.ttpsc.dynamics365fieldService.views.extensions.viewModel
import kotlinx.android.synthetic.main.fragment_start.*
import javax.inject.Inject

class StartFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var _viewModel: StartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidApplication.companionAppComponent?.inject(this)

        val workOrder = activity?.intent?.data?.lastPathSegment

        _viewModel = viewModel(viewModelFactory) {
            val isUserLogged = this.checkIfUserIsLoggedIn()
            if (isUserLogged) {
                loadEnvironmentUrl()
                performNavigation(true, workOrder)
            } else {
                performNavigation(false, workOrder)
            }
        }
    }

    private fun performNavigation(isUserLogged: Boolean, workOrder: String?) {
        if (!isUserLogged) {
            findNavController().navigate(
                StartFragmentDirections.actionStartFragmentToLoginFragment(workOrder)
            )
        } else {
            findNavController().navigate(
                if (workOrder != null)
                    StartFragmentDirections.actionStartFragmentToGuideSummaryGraph(
                        workOrder,
                        null,
                        null,
                        null,
                    null
                    )
                else
                    StartFragmentDirections.actionStartFragmentToMainMenuFragment()
            )
        }

    }
}
