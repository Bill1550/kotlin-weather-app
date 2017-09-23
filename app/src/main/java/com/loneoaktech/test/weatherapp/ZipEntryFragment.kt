package com.loneoaktech.test.weatherapp

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.loneoaktech.test.weatherapp.di.Injectable
import com.loneoaktech.test.weatherapp.model.ForecastLocation
import com.loneoaktech.test.weatherapp.model.ZipCode
import com.loneoaktech.test.weatherapp.viewmodel.LocationViewModel
import com.loneoaktech.test.weatherapp.viewmodel.LocationViewModelFactory
import kotlinx.android.synthetic.main.fragment_zip_entry.*
import kotlinx.android.synthetic.main.fragment_zip_entry.view.*
import timber.log.Timber
import javax.inject.Inject

/**
 *
 * Created by BillH on 9/16/2017.
 */
class ZipEntryFragment : DialogFragment(), Injectable {
    private var _locationModel: LocationViewModel? = null

    @Inject
    lateinit var _locationViewModelFactory : LocationViewModelFactory


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_Dialog)
        dialog.window.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP)
        dialog.window.attributes = dialog.window.attributes.apply{
            y = resources.getDimensionPixelOffset(R.dimen.action_bar_height)
        }
        dialog.setCanceledOnTouchOutside(false)


        return inflater.inflate(R.layout.fragment_zip_entry, container, false).also { rv ->
            rv.zipCodeText.setOnEditorActionListener(_zipActionListener)
            rv.zipCodeText.onFocusChangeListener = _zipFocusChangeListener
            rv.zipCodeText.setOnClickListener(_zipClickListener)
            rv.zipCodeText.isCursorVisible=false
            rv.cancelButton.setOnClickListener { dismiss() }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        _locationModel = ViewModelProviders.of(this, _locationViewModelFactory)
                .get(LocationViewModel::class.java)?.apply {
            // Subscribe to the model
            clearValidation()

            selectedLocation.observe(this@ZipEntryFragment, Observer<ForecastLocation> {
                locationNameText.text = it?.title ?: "-----"
                zipCodeText.setText(it?.zipCode.toString())
            })

            errorMessage.observe(this@ZipEntryFragment, Observer<String> {
                errorText.text = it
                if (!it.isNullOrEmpty())
                    zipCodeText.selectAll()
            })

            validatedLocation.observe(this@ZipEntryFragment, Observer<ForecastLocation>{
                // Pass validated location back to the model, makes sure selection only occurs
                // if fragment is still active.
                Timber.i("valid location changed: %s", it)
                if (it != null ) {
                    selectLocation(it)
                    this@ZipEntryFragment.dismiss()
                }

            })
        }
    }


    // Handles end-of-editing events from the zip entry
    private val _zipActionListener = TextView.OnEditorActionListener { v, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event != null && event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
            if (event == null || !event.isShiftPressed) {
                // the user is done typing.
                validateZipCodeInput(v.text)
                v.isCursorVisible = false
                return@OnEditorActionListener false
            }
        }
        false // pass on to other listeners.
    }

    private val _zipFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        (v as EditText).run {
            if (hasFocus) {
                dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                selectAll()
                isCursorVisible = true
            }
            else
                validateZipCodeInput(text)
        }
    }

    private val _zipClickListener = View.OnClickListener { v ->

        (v as? EditText)?.run {
            if (!isCursorVisible)
                selectAll()
            isCursorVisible = true
        }
        locationNameText.text = ""
        errorText.text = ""

    }

    private fun validateZipCodeInput(text: CharSequence){
        if (ZipCode.isValidZipCodeString(text.toString())) {
            _locationModel?.validateZipCode(ZipCode.fromString(text.toString()))
        } else
            Timber.e("Invalid zip string: %s", text)
    }

}
