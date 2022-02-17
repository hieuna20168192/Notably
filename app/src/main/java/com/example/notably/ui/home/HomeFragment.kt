package com.example.notably.ui.home

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.notably.R
import com.example.notably.base.BaseFragment
import com.example.notably.databinding.FragmentHomeBinding
import com.example.notably.ui.home.note.NoteFragment
import com.example.notably.ui.home.todo.TodosFragment
import com.example.notably.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@RequiresApi(Build.VERSION_CODES.M)
@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    override val layoutId: Int = R.layout.fragment_home

    override val viewModel: HomeViewModel by viewModels()

    private val noteFragment = NoteFragment()

    private val todosFragment = TodosFragment()

    private var activeFragment: Fragment = noteFragment

    override fun initComponents() {
        childFragmentManager.beginTransaction()
            .add(R.id.fragment_container, todosFragment, TodosFragment::class.simpleName)
            .hide(todosFragment).commit()
        childFragmentManager.beginTransaction()
            .add(R.id.fragment_container, noteFragment, NoteFragment::class.simpleName).commit()
    }

    override fun initListeners() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.note_dest -> {
                    childFragmentManager.beginTransaction().hide(activeFragment).show(noteFragment)
                        .commit()
                    activeFragment = noteFragment
                    (requireActivity() as MainActivity).apply {
                        moreOptions.visibility = View.VISIBLE
                        toolbarTitle.text = getString(R.string.app_name)
                    }
                    true
                }
                R.id.todos_dest -> {
                    childFragmentManager.beginTransaction().hide(activeFragment).show(todosFragment)
                        .commit()
                    activeFragment = todosFragment
                    (requireActivity() as MainActivity).apply {
                        moreOptions.visibility = View.GONE
                        toolbarTitle.text = getString(R.string.all_todos)
                    }
                    true
                }
                else -> false
            }
        }
    }
}
