package com.sofar.business.github.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sofar.business.github.model.Repo;

import java.util.List;

@Dao
public interface RepoDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(List<Repo> repos);

  @Query("SELECT * FROM repos WHERE (name LIKE :queryString) OR (description LIKE " +
    ":queryString) ORDER BY stars DESC, name ASC")
  LiveData<List<Repo>> reposByName(String queryString);

}
