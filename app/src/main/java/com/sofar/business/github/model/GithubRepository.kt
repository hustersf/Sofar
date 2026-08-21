package com.sofar.business.github.model

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sofar.business.github.api.GithubService
import com.sofar.business.github.db.RepoDatabase
import kotlinx.coroutines.flow.Flow

class GithubRepository(
  private val service: GithubService,
  private val database: RepoDatabase
) {

  fun getSearchResultStream(query: String): Flow<PagingData<Repo>> {
    val pagingSourceFactory = { database.reposDao().reposByQuery(query) }

    @OptIn(ExperimentalPagingApi::class)
    return Pager(
      config = PagingConfig(
        pageSize = NETWORK_PAGE_SIZE,
        initialLoadSize = NETWORK_PAGE_SIZE,
        prefetchDistance = 3,
        enablePlaceholders = false
      ),
      remoteMediator = GithubRemoteMediator(
        query,
        service,
        database
      ),
      pagingSourceFactory = pagingSourceFactory
    ).flow
  }

  companion object {
    private const val NETWORK_PAGE_SIZE = 30
  }
}
