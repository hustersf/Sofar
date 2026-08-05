package com.sofar.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

//顶层函数类似于 java 中的静态方法, 实现了单例效果
val Context.dataStore by preferencesDataStore(name = "app_config")