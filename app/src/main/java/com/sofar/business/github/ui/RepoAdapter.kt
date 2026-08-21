package com.sofar.business.github.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sofar.R
import com.sofar.business.github.model.Repo

class RepoAdapter : PagingDataAdapter<Repo, RepoAdapter.RepoViewHolder>(REPO_COMPARATOR) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.repo_item, parent, false)
    return RepoViewHolder(view)
  }

  override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
    val repo = getItem(position)
    if (repo != null) {
      holder.bind(repo)
    }
  }

  class RepoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val name: TextView = view.findViewById(R.id.repo_name)
    private val description: TextView = view.findViewById(R.id.repo_description)
    private val stars: TextView = view.findViewById(R.id.repo_stars)
    private val forks: TextView = view.findViewById(R.id.repo_forks)
    private val language: TextView = view.findViewById(R.id.repo_language)

    fun bind(repo: Repo) {
      name.text = repo.name
      description.text = repo.description
      stars.text = repo.stars.toString()
      forks.text = repo.forks.toString()
      language.text = repo.language

      itemView.setOnClickListener {
        repo.url?.let { url ->
          val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
          itemView.context.startActivity(intent)
        }
      }
    }
  }

  companion object {
    private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Repo>() {
      override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean =
        oldItem.id == newItem.id

      override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean =
        oldItem == newItem
    }
  }
}
