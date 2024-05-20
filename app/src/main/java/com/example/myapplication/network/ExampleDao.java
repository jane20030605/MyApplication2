package com.example.myapplication.network;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ExampleDao {
    @Insert
    void insert(ExampleEntity example);

    @Query("SELECT * FROM example_table")
    List<ExampleEntity> getAll();
}
