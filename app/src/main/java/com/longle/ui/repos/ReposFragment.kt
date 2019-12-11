package com.longle.ui.repos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.longle.R
import com.longle.databinding.FragmentReposBinding
import com.longle.di.Injectable
import com.longle.di.ViewModelFactory
import com.longle.ui.repos.adapter.ReposAdapter
import com.longle.util.autoCleared
import javax.inject.Inject

class ReposFragment : Fragment(), ReposViewModel.MessageEvent, Injectable {

    @Inject
    lateinit var factory: ViewModelFactory<ReposViewModel>

    private val viewModel: ReposViewModel by viewModels { factory }
    private var binding by autoCleared<FragmentReposBinding>()
    private var adapter by autoCleared<ReposAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReposBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        subscribeUi()
    }

    private fun subscribeUi() {
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.viewModel = viewModel

        viewModel.uiEvent.setEventReceiver(viewLifecycleOwner, this)
        viewModel.repos.observe(viewLifecycleOwner) {
            if (it.size > 0) {
                adapter?.submitList(it)
            }
        }
        viewModel.networkState.observe(viewLifecycleOwner) {
            adapter?.setNetworkState(it)
        }
    }

    private fun setUpView() {
        adapter = ReposAdapter {
            viewModel.retry()
        }
        binding?.recyclerView?.adapter = adapter
        binding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var hasStartedScrolling = false
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager
                if (layoutManager is LinearLayoutManager && hasStartedScrolling) {
                    viewModel.handleItemPosition(layoutManager.findLastVisibleItemPosition())
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                hasStartedScrolling = true
            }
        })
    }

    override fun showReposCount(lastPosition: Int, publicRepos: Int) {
        binding?.tvLastPosition?.text = getString(
            R.string.repo_item_of_items,
            lastPosition.toString(),
            publicRepos.toString()
        )
        runAnimation()
    }

    private fun runAnimation() {
        if (binding?.tvLastPosition?.visibility == View.INVISIBLE) {
            binding?.tvLastPosition?.startAnimation(
                AnimationUtils.loadAnimation(context, R.anim.fade_in)
            )
            binding?.tvLastPosition?.visibility = View.VISIBLE
        }
        binding?.tvLastPosition?.removeCallbacks(runnable)
        binding?.tvLastPosition?.postDelayed(runnable, 1000)
    }

    private var runnable = Runnable {
        binding?.tvLastPosition?.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.fade_out)
        )
        binding?.tvLastPosition?.visibility = View.INVISIBLE
    }
}
