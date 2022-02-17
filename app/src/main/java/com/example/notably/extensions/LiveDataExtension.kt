package com.example.notably.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

fun ViewModel.runIO(func: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch(Dispatchers.IO) { func() }
}

@ExperimentalCoroutinesApi
fun EditText.onTextChange(): Flow<String> =
    callbackFlow<String> {
        val listener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(text: Editable) {
                trySend(text.toString())
            }
        }
        addTextChangedListener(listener)
        awaitClose {
            removeTextChangedListener(listener)
        }
    }

fun <T, V, R> LiveData<T>.combineNonNull(
    liveData: LiveData<V>,
    block: (T, V) -> LiveData<R>
): LiveData<R> {
    val result = MediatorLiveData<R>()
    var mSource: LiveData<R>? = null
    result.addSource(this) { t ->
        val newLiveData = block(t, liveData.value ?: return@addSource)
        if (mSource == newLiveData) {
            return@addSource
        }
        mSource?.let { r ->
            result.removeSource(r)
        }
        mSource = newLiveData
        mSource?.apply {
            result.addSource(this) { r ->
                result.value = r
            }
        }
    }

    result.addSource(liveData) { v ->
        val newLiveData = block(this.value ?: return@addSource, v)
        if (mSource == newLiveData) {
            return@addSource
        }
        mSource?.let { r ->
            result.removeSource(r)
        }
        mSource = newLiveData
        mSource?.apply {
            result.addSource(this) { r ->
                result.value = r
            }
        }
    }

    return result
}
