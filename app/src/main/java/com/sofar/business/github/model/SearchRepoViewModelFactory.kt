package com.sofar.business.github.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SearchRepoViewModelFactory(private val repository: GithubRepository) :
  ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(SearchRepoViewModel::class.java)) {
      return SearchRepoViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
