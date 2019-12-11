package com.longle.ui.repos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.longle.R
import com.longle.data.model.Repo
import com.longle.data.repository.NetworkState
import com.longle.databinding.ListItemRepoBinding
import com.longle.ui.repos.ReposFragmentDirections

/**
 * Adapter for the [RecyclerView] in [ReposFragment].
 */
class ReposAdapter(private val retryCallback: () -> Unit) :
    PagedListAdapter<Repo, RecyclerView.ViewHolder>(
        RepoDiffCallback()
    ) {
    private var networkState: NetworkState? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.list_item_repo -> {
                val repo = getItem(position)
                repo?.let {
                    holder.apply {
                        (holder as RepoViewHolder).bind(createOnClickListener(repo.id), repo)
                        itemView.tag = repo
                    }
                }
            }
            R.layout.network_state_item -> (holder as NetworkStateViewHolder).bind(networkState)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.list_item_repo -> RepoViewHolder(
                ListItemRepoBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            R.layout.network_state_item -> NetworkStateViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_item
        } else {
            R.layout.list_item_repo
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    private fun createOnClickListener(id: Int) = View.OnClickListener { view ->
        val direction =
            ReposFragmentDirections.actionToRepoDetailFragment(
                id
            )
        view.findNavController().navigate(direction)
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED
}

private class RepoDiffCallback : DiffUtil.ItemCallback<Repo>() {

    override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
        return oldItem == newItem
    }
}
