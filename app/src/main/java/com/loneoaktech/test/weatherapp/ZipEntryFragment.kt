package com.loneoaktech.test.weatherapp

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.loneoaktech.test.weatherapp.model.ForecastLocation
import com.loneoaktech.test.weatherapp.model.ZipCode
import com.loneoaktech.test.weatherapp.viewmodel.LocationViewModel
import kotlinx.android.synthetic.main.fragment_zip_entry.*
import kotlinx.android.synthetic.main.fragment_zip_entry.view.*
import timber.log.Timber

/**
 *
 * Created by BillH on 9/16/2017.
 */
class ZipEntryFragment : Fragment(){
    private var _locationModel: LocationViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_zip_entry, container, false).apply {
            this.zipCodeText.setOnEditorActionListener(_zipActionListener)
            this.zipCodeText.onFocusChangeListener = _zipFocusChangeListener
            this.zipCodeText.setOnClickListener(_zipClickListener)
            this.zipCodeText.isCursorVisible=false
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        _locationModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)?.apply {
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
                if (it != null) {
                    selectLocation(it)
                    // *** End dialog
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
            if (!hasFocus)
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
