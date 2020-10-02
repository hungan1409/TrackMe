package com.example.trackme.base

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.Size
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.trackme.BR
import com.example.trackme.R
import com.example.trackme.extension.showDialog
import com.example.trackme.util.autoCleared
import dagger.android.support.DaggerFragment
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import javax.inject.Inject

abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel> : DaggerFragment(),
    EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    abstract val viewModel: V

    @get:LayoutRes
    abstract val layoutId: Int

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var errorMessageDialog: AlertDialog? = null

    var viewDataBinding by autoCleared<T>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.apply {
            setVariable(BR.viewModel, viewModel)
            executePendingBindings()
            lifecycleOwner = this@BaseFragment
        }
        subscriberException()
    }

    override fun onStop() {
        super.onStop()
        errorMessageDialog?.dismiss()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            Timber.i("Returned from app settings screen")
            onReturnedFromAppSettings()
        }
    }

    internal fun hasPermission(@Size(min = 1) vararg permissions: String): Boolean {
        permissions.forEach {
            when (ContextCompat.checkSelfPermission(
                requireActivity(),
                it
            ) != PackageManager.PERMISSION_GRANTED) {
                true -> return false
            }
        }
        return true
    }

    internal fun requestPermission(
        rationale: String,
        requestCode: Int = PERMISSION_REQUEST_CODE,
        @Size(min = 1) vararg permissions: String
    ) {
        EasyPermissions.requestPermissions(this, rationale, requestCode, *permissions)
    }

    @AfterPermissionGranted(PERMISSION_REQUEST_CODE)
    open fun permissionAccepted() {
    }

    open fun onReturnedFromAppSettings() = Unit

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) = Unit

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) = Unit

    override fun onRationaleAccepted(requestCode: Int) = Unit

    override fun onRationaleDenied(requestCode: Int) = Unit

    protected open fun subscriberException() {
        viewModel.run {
            httpUnauthorized.observe(viewLifecycleOwner, Observer {
                // TODO: Handle HTTP is unauthorized
            })
            unexpectedError.observe(viewLifecycleOwner, Observer {
                handleErrorMessage(message = getString(R.string.unexpected_error))
            })
            httpUnavailableError.observe(viewLifecycleOwner, Observer {
                handleErrorMessage(message = getString(R.string.http_unavailable_error))
            })
            rxMapperError.observe(viewLifecycleOwner, Observer {
                handleErrorMessage(message = getString(R.string.rx_mapper_error))
            })
            httpForbiddenError.observe(viewLifecycleOwner, Observer {
                handleErrorMessage(message = getString(R.string.http_forbidden_error))
            })
            httpGatewayTimeoutError.observe(viewLifecycleOwner, Observer {
                handleErrorMessage(message = getString(R.string.no_internet_error))
            })
            errorMessage.observe(viewLifecycleOwner, Observer {
                handleErrorMessage(message = it)
            })
        }
    }

    private fun handleErrorMessage(message: String) {
        if (errorMessageDialog?.isShowing != true) {
            errorMessageDialog =
                context?.showDialog(message = message, positiveMessage = getString(R.string.ok))
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = Activity.RESULT_FIRST_USER + 1
    }
}
