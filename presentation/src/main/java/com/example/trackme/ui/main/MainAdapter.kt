package com.example.trackme.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.example.trackme.R
import com.example.trackme.base.BaseRecyclerAdapter
import com.example.trackme.base.ModelItem
import com.example.trackme.databinding.ItemWorkoutRecordBinding
import com.example.trackme.model.WorkoutRecordItem

class MainAdapter(private val onClickRepoListener: ((WorkoutRecordItem) -> Unit)?) :
    BaseRecyclerAdapter<ModelItem>(DIFF_CALLBACK) {

    override fun createBinding(parent: ViewGroup, viewType: Int?): ViewDataBinding {
        if (viewType != null && viewType != -1) {
            return DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                viewType,
                parent,
                false
            ) as ItemWorkoutRecordBinding
        } else {
            throw Throwable("Not found this view type $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int = R.layout.item_workout_record

    override fun bind(binding: ViewDataBinding, modelItem: ModelItem, position: Int) {
        if (binding is ItemWorkoutRecordBinding) {
            if (modelItem is WorkoutRecordItem) {
                binding.item = modelItem
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ModelItem>() {
            override fun areItemsTheSame(oldItem: ModelItem, newItem: ModelItem): Boolean {
                return if (oldItem is WorkoutRecordItem && newItem is WorkoutRecordItem) {
                    oldItem.workoutID == newItem.workoutID
                } else {
                    false
                }
            }

            override fun areContentsTheSame(oldItem: ModelItem, newItem: ModelItem): Boolean {
                return oldItem.equals(newItem)
            }
        }
    }
}
