package com.longle.ui.repos.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.longle.data.model.Repo
import com.longle.databinding.ListItemRepoBinding

class RepoViewHolder(private val binding: ListItemRepoBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(listener: View.OnClickListener, item: Repo) {
        binding.apply {
            repo = item
            itemView.setOnClickListener(listener)
            executePendingBindings()
        }
    }
}
