package com.example.notably.binding

import android.view.View
import android.widget.TextView
import androidx.core.view.isGone
import androidx.databinding.BindingAdapter

@BindingAdapter("textOrGone")
fun textOrGone(textView: TextView, value: String?) {
    textView.isGone = value.isNullOrEmpty()
    textView.text = value
}

@BindingAdapter("isGone")
fun isGone(view: View, isGone: Boolean?) {
    view.isGone = isGone ?: true
}
