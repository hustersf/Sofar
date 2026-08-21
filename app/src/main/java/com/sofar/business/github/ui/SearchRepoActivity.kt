package com.sofar.business.github.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.sofar.R
import com.sofar.business.github.api.GithubApiHolder
import com.sofar.business.github.db.RepoDatabase
import com.sofar.business.github.model.GithubRepository
import com.sofar.business.github.model.SearchRepoViewModel
import com.sofar.business.github.model.SearchRepoViewModelFactory
import com.sofar.core.ui.activity.BaseUIActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchRepoActivity : BaseUIActivity() {

  private val viewModel: SearchRepoViewModel by lazy {
    val database = RepoDatabase.getInstance(this)
    val service = GithubApiHolder.githubService
    ViewModelProvider(this, SearchRepoViewModelFactory(GithubRepository(service, database)))
      .get(SearchRepoViewModel::class.java)
  }

  private lateinit var adapter: RepoAdapter
  private lateinit var searchRepoEt: EditText
  private lateinit var list: RecyclerView
  private lateinit var emptyList: android.view.View

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.repo_search_activity)

    searchRepoEt = findViewById(R.id.search_repo)
    list = findViewById(R.id.list)
    emptyList = findViewById(R.id.empty_list)

    initAdapter()
    initSearch()
    collectPagingData()
  }

  private fun initAdapter() {
    adapter = RepoAdapter()
    list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    list.adapter = adapter
  }

  private fun initSearch() {
    val query = "Android"
    if (viewModel.query.value.isEmpty()) {
      viewModel.searchRepo(query)
    }
    searchRepoEt.setText(query)

    searchRepoEt.setOnEditorActionListener { _, actionId, _ ->
      if (actionId == EditorInfo.IME_ACTION_GO) {
        updateRepoListFromInput()
        true
      } else {
        false
      }
    }
    searchRepoEt.setOnKeyListener { _, keyCode, event ->
      if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
        updateRepoListFromInput()
        true
      } else {
        false
      }
    }
  }

  private fun updateRepoListFromInput() {
    searchRepoEt.text.trim().toString().let {
      if (it.isNotEmpty()) {
        list.scrollToPosition(0)
        viewModel.searchRepo(it)
      }
    }
  }

  private fun collectPagingData() {
    lifecycleScope.launch {
      viewModel.pagingDataFlow.collectLatest { pagingData ->
        adapter.submitData(pagingData)
      }
    }

    lifecycleScope.launch {
      adapter.loadStateFlow.collect { loadState ->
        val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
        list.visibility = if (isListEmpty) android.view.View.GONE else android.view.View.VISIBLE
        emptyList.visibility =
          if (isListEmpty) android.view.View.VISIBLE else android.view.View.GONE
      }
    }
  }
}
