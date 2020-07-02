package com.sofar.business.github.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sofar.R;
import com.sofar.business.github.model.GithubRepository;
import com.sofar.business.github.model.SearchRepoViewModel;
import com.sofar.business.github.model.SearchRepoViewModelFactory;
import com.sofar.utility.CollectionUtil;
import com.sofar.utility.ToastUtil;

import java.util.Collections;

public class SearchRepoActivity extends AppCompatActivity {

  private SearchRepoViewModel repoViewModel;
  private static final String DEFAULT_QUERY = "Android";

  EditText searchRepoEt;
  TextView emptyList;

  RecyclerView list;
  RepoAdapter adapter;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.repo_search_activity);
    searchRepoEt = findViewById(R.id.search_repo);
    list = findViewById(R.id.list);
    emptyList = findViewById(R.id.empty_list);

    GithubRepository repository = new GithubRepository(this);
    repoViewModel = new ViewModelProvider(this, new SearchRepoViewModelFactory(repository)).get(SearchRepoViewModel.class);

    initSearch();
    initAdapter();
    setupScrollListener();
  }

  private void initAdapter() {
    DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
    list.addItemDecoration(itemDecoration);
    adapter = new RepoAdapter();
    list.setLayoutManager(new LinearLayoutManager(this));
    list.setAdapter(adapter);

    repoViewModel.repos.observe(this, repos -> {
      if (CollectionUtil.isEmpty(repos)) {
        list.setVisibility(View.GONE);
        emptyList.setVisibility(View.VISIBLE);
      } else {
        list.setVisibility(View.VISIBLE);
        emptyList.setVisibility(View.GONE);
        adapter.setList(repos);
        adapter.notifyDataSetChanged();
      }
    });

    repoViewModel.networkErrors.observe(this, s -> {
      ToastUtil.startShort(this, s);
    });
  }

  private void initSearch() {
    searchRepoEt.setText(DEFAULT_QUERY);
    repoViewModel.searchRepo(DEFAULT_QUERY);

    searchRepoEt.setOnEditorActionListener((v, actionId, event) -> {
      if (actionId == EditorInfo.IME_ACTION_GO) {
        updateRepoListFromInput();
        return true;
      } else {
        return false;
      }
    });

    searchRepoEt.setOnKeyListener((v, keyCode, event) -> {
      if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
        updateRepoListFromInput();
        return true;
      } else {
        return false;
      }
    });
  }

  private void updateRepoListFromInput() {
    String query = searchRepoEt.getText().toString().trim();
    if (!TextUtils.isEmpty(query)) {
      repoViewModel.searchRepo(query);
      list.scrollToPosition(0);
      adapter.setList(Collections.EMPTY_LIST);
      adapter.notifyDataSetChanged();
    }
  }

  private void setupScrollListener() {
    if (list.getLayoutManager() instanceof LinearLayoutManager) {
      LinearLayoutManager layoutManager = (LinearLayoutManager) list.getLayoutManager();
      list.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);

          int totalItemCount = layoutManager.getItemCount();
          int visibleItemCount = layoutManager.getChildCount();
          int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

          repoViewModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount);
        }
      });
    }
  }
}
