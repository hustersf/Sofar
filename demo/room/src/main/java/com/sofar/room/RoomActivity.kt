package com.sofar.room

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sofar.room.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoomActivity : AppCompatActivity() {

  private lateinit var idEt: EditText
  private lateinit var nameEt: EditText
  private lateinit var ageEt: EditText
  private lateinit var addBtn: Button
  private lateinit var removeBtn: Button
  private lateinit var updateBtn: Button
  private lateinit var queryBtn: Button
  private lateinit var resultTv: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.room_activity)
    idEt = findViewById(R.id.id_et)
    nameEt = findViewById(R.id.name_et)
    ageEt = findViewById(R.id.age_et)

    addBtn = findViewById(R.id.add_btn)
    addBtn.setOnClickListener {
      lifecycleScope.launch {
        val id = withContext(Dispatchers.IO) {
          AppDatabase.get(this@RoomActivity).userDao().insert(buildUser())
        }
        resultTv.text = if (id > 0) "添加用户成功！" else "添加用户失败！"
      }
    }
    removeBtn = findViewById(R.id.remove_btn)
    removeBtn.setOnClickListener {
      lifecycleScope.launch {
        val rows = withContext(Dispatchers.IO) {
          AppDatabase.get(this@RoomActivity).userDao().delete(buildUser())
        }
        resultTv.text = if (rows > 0) "删除用户成功！" else "删除用户失败！"
      }
    }
    updateBtn = findViewById(R.id.update_btn)
    updateBtn.setOnClickListener {
      lifecycleScope.launch {
        val rows = withContext(Dispatchers.IO) {
          AppDatabase.get(this@RoomActivity).userDao().update(buildUser())
        }
        resultTv.text = if (rows > 0) "修改用户成功！" else "修改用户失败！"
      }
    }
    queryBtn = findViewById(R.id.query_btn)
    queryBtn.setOnClickListener {
      lifecycleScope.launch {
        val info = withContext(Dispatchers.IO) {
          val users = AppDatabase.get(this@RoomActivity).userDao().getAllUsers()
          val sb = StringBuilder()
          for (user in users) {
            sb.append(user.toString())
            sb.append("\n")
          }
          sb.toString()
        }
        resultTv.text = info
      }
    }

    resultTv = findViewById(R.id.result_tv)
  }

  private fun buildUser(): UserEntity {
    val name = nameEt.text.toString().trim()
    var age = 0
    try {
      age = ageEt.text.toString().trim().toInt()
    } catch (e: Exception) {
    }
    val id = idEt.text.toString().trim()
    if (id.isNullOrEmpty()) {
      return UserEntity(name = name, age = age)
    } else {
      return UserEntity(id = id.toInt(), name = name, age = age)
    }
  }
}