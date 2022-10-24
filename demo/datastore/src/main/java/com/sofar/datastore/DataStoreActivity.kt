package com.sofar.datastore

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DataStoreActivity : AppCompatActivity() {

  private val countKey = intPreferencesKey("counter_test")
  private lateinit var textView: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.datastore_activity)
    textView = findViewById(R.id.datastore_text)

    lifecycleScope.launch {
      try {
        read()
      } catch (e: Exception) {
        //解决协程中的崩溃堆栈出不来
        e.printStackTrace()
      }
    }
    lifecycleScope.launch {
      write()
    }
  }

  private suspend fun read() {
    dataStore.data.map { preferences ->
      preferences[countKey] ?: 0
    }.collect {
      textView.text = "第 $it 次进入"
    }
  }

  private suspend fun write() {
    dataStore.edit { preferences ->
      val count = preferences[countKey] ?: 0
      preferences[countKey] = count + 1
    }
  }

}