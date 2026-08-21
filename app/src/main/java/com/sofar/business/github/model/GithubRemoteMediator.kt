package com.sofar.business.github.model

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.sofar.business.github.api.GithubService
import com.sofar.business.github.db.RepoDatabase
import com.sofar.business.github.db.RepoRemoteKeys
import retrofit2.HttpException
import java.io.IOException

private const val GITHUB_STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class GithubRemoteMediator(
  private val query: String,
  private val service: GithubService,
  private val repoDatabase: RepoDatabase
) : RemoteMediator<Int, Repo>() {

  override suspend fun load(loadType: LoadType, state: PagingState<Int, Repo>): MediatorResult {
    val page = when (loadType) {
      LoadType.REFRESH -> GITHUB_STARTING_PAGE_INDEX
      LoadType.PREPEND -> {
        val remoteKeys = getRemoteKeyForFirstItem(state)
        val prevKey = remoteKeys?.prevKey
          ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
        prevKey
      }

      LoadType.APPEND -> {
        val remoteKeys = getRemoteKeyForLastItem(state)
        val nextKey = remoteKeys?.nextKey
          ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
        nextKey
      }
    }

    try {
      // 初始加载使用 initialLoadSize 避免触发第二次请求
      val loadSize =
        if (loadType == LoadType.REFRESH) state.config.initialLoadSize else state.config.pageSize
      val apiResponse = service.searchRepos(query, page, loadSize)

      val repos = apiResponse.items
      val endOfPaginationReached = repos.isEmpty()

      repoDatabase.withTransaction {
        if (loadType == LoadType.REFRESH) {
          repoDatabase.remoteKeysDao().clearRemoteKeys(query)
          repoDatabase.reposDao().clearReposByQuery(query)
        }

        val prevKey = if (page == GITHUB_STARTING_PAGE_INDEX) null else page - 1
        val nextKey =
          if (endOfPaginationReached) null else page + (loadSize / state.config.pageSize)

        val startPosition = (page - 1) * state.config.pageSize
        val keys = repos.mapIndexed { index, repo ->
          RepoRemoteKeys(
            query = query,
            repoId = repo.id,
            prevKey = prevKey,
            nextKey = nextKey,
            position = startPosition + index
          )
        }
        repoDatabase.remoteKeysDao().insertAll(keys)
        repoDatabase.reposDao().insertAll(repos)
      }
      return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
    } catch (exception: IOException) {
      return MediatorResult.Error(exception)
    } catch (exception: HttpException) {
      return MediatorResult.Error(exception)
    }
  }

  private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Repo>): RepoRemoteKeys? {
    return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
      ?.let { repo ->
        repoDatabase.remoteKeysDao().remoteKeys(query, repo.id)
      }
  }

  private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Repo>): RepoRemoteKeys? {
    return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
      ?.let { repo ->
        repoDatabase.remoteKeysDao().remoteKeys(query, repo.id)
      }
  }
}
