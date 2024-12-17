package com.submission.storyapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.submission.storyapp.data.remote.response.ListStoryItem
import com.submission.storyapp.databinding.ItemsStoriesBinding

class StoriesAdapter(private val onItemClick: (ListStoryItem) -> Unit) : ListAdapter<ListStoryItem, StoriesAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemsStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val stories = getItem(position)
        holder.bind(stories, onItemClick)
    }

    class MyViewHolder(private val binding: ItemsStoriesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(storyItem: ListStoryItem, onItemClick: (ListStoryItem) -> Unit) {
            Glide.with(binding.ivItemPhoto.context)
                .load(storyItem.photoUrl)
                .into(binding.ivItemPhoto)

            binding.tvItemName.text = storyItem.name
            binding.tvItemDescription.text = storyItem.description

            binding.root.setOnClickListener {
                onItemClick(storyItem)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ListStoryItem> =
            object : DiffUtil.ItemCallback<ListStoryItem>() {
                override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                    return oldItem == newItem
                }
            }
    }
}