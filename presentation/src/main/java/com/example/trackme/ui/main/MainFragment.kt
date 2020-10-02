package com.example.trackme.ui.main

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackme.R
import com.example.trackme.base.BaseFragment
import com.example.trackme.databinding.FragmentMainBinding
import com.example.trackme.model.WorkoutRecordItem
import com.example.trackme.util.autoCleared
import timber.log.Timber

class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>() {

    override val layoutId = R.layout.fragment_main

    override val viewModel: MainViewModel by viewModels { viewModelFactory }

    private var mainAdapter by autoCleared<MainAdapter>()


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finishAfterTransition()
                }
            })
        subscribeUI()
        viewModel.init()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startShimmer()

        mainAdapter = MainAdapter { workoutItem -> onClickWorkoutItem(workoutItem) }
        with(viewDataBinding) {
            recycler.adapter = mainAdapter
            recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == mainAdapter.itemCount - REMAINING_ITEM_REFRESH) {
                        viewModel?.loadMore()
                    }
                }
            })

            swipeRefresh.setOnRefreshListener {
                viewModel?.refresh()
                startShimmer()
            }
        }
    }

    private fun onClickWorkoutItem(workoutItem: WorkoutRecordItem) {
        // TODO: for future tasks
    }

    private fun subscribeUI() = with(viewModel) {
        record.observe(viewLifecycleOwner) {
            findNavController().navigate(MainFragmentDirections.navigateToTrackLocation())
        }
        workouts.observe(viewLifecycleOwner) {
            Timber.d(it.toString())
            stopShimmer()
            mainAdapter.submitList(it)
        }
    }

    private fun startShimmer() {
        viewDataBinding.apply {
            shimmerLayout.startShimmer()
            shimmerLayout.visibility = View.VISIBLE
        }
    }

    private fun stopShimmer() {
        viewDataBinding.apply {
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
        }
    }

    companion object {
        private const val REMAINING_ITEM_REFRESH = 1
    }
}
