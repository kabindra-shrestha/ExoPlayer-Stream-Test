package com.kabindra.exoplayerstreamtestapp

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun TextInputEditText.isValidText(): Boolean {
    val textInputLayout = this.parent.parent as TextInputLayout
    textInputLayout.errorIconDrawable = null
    this.onChange { textInputLayout.error = null }

    return if (this.text.toString().trim().isEmpty()) {
        textInputLayout.error = "This field is required."
        false
    } else {
        true
    }
}

fun EditText.onChange(cb: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            cb(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}