package com.example.notably.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheet<V : ViewModel, B : ViewDataBinding>
    : BottomSheetDialogFragment() {

    @get: LayoutRes
    protected abstract val layoutId: Int

    protected abstract val viewModel: V
    protected lateinit var binding: B

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
        initListeners()
    }

    protected abstract fun initComponents()
    protected abstract fun initListeners()

    protected fun showKeyBoard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            0
        )
    }

    protected fun hideKeyBoard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)
        (inputMethodManager as InputMethodManager).toggleSoftInput(
            InputMethodManager.HIDE_IMPLICIT_ONLY,
            0
        )
    }
}
