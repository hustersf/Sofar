package com.sofar.business.github.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest

class SearchRepoViewModel(private val repository: GithubRepository) : ViewModel() {

  private val _query = MutableStateFlow("")
  val query: StateFlow<String> = _query.asStateFlow()

  @OptIn(ExperimentalCoroutinesApi::class)
  val pagingDataFlow: Flow<PagingData<Repo>> = _query
    .filter { it.isNotEmpty() }
    .flatMapLatest { queryString ->
      repository.getSearchResultStream(queryString)
    }
    .cachedIn(viewModelScope)

  fun searchRepo(queryString: String) {
    _query.value = queryString
  }
}
