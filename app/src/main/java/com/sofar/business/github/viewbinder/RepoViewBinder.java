package com.sofar.business.github.viewbinder;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.TextView;

import com.sofar.R;
import com.sofar.base.viewbinder.RecyclerViewBinder;
import com.sofar.business.github.model.Repo;

public class RepoViewBinder extends RecyclerViewBinder<Repo> {

  TextView repoName;
  TextView repoDes;
  TextView repoLanguage;
  TextView repoStars;
  TextView repoForks;

  @Override
  protected void onCreate() {
    super.onCreate();
    repoName = view.findViewById(R.id.repo_name);
    repoDes = view.findViewById(R.id.repo_description);
    repoLanguage = view.findViewById(R.id.repo_language);
    repoStars = view.findViewById(R.id.repo_stars);
    repoForks = view.findViewById(R.id.repo_forks);
  }

  @Override
  protected void onBind(Repo data) {
    super.onBind(data);
    repoName.setText(data.name);
    repoDes.setText(data.description);
    repoLanguage.setText(data.language);
    repoStars.setText(String.valueOf(data.stars));
    repoForks.setText(String.valueOf(data.forks));

    view.setOnClickListener(v -> {
      if (!TextUtils.isEmpty(data.url)) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.url));
        context.startActivity(intent);
      }
    });
  }
}
