package com.sofar.profiler.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.sofar.profiler.R
import com.sofar.profiler.ui.block.BlockListFragment
import com.sofar.profiler.ui.config.MonitorConfigFragment

class MonitorHostActivity : AppCompatActivity() {

  private lateinit var viewPager2: ViewPager2
  private lateinit var adapter: FragmentAdapter
  private lateinit var tabLayout: TabLayout
  private lateinit var mediator: TabLayoutMediator

  private val list = listOf("配置", "卡顿")
  private val canScroll = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    supportActionBar?.hide()
    setContentView(R.layout.monitor_host_activity)
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_view)) { v, insets ->
      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
      insets
    }
    initView()
    initData()
  }

  fun initView() {
    viewPager2 = findViewById(R.id.view_pager)
    tabLayout = findViewById(R.id.tab_layout)
  }

  fun initData() {
    adapter = FragmentAdapter(this)
    viewPager2.adapter = adapter
    viewPager2.isUserInputEnabled = canScroll
    adapter.setFragments(buildFragments())
    adapter.notifyDataSetChanged()
    selectItem(0)

    mediator = TabLayoutMediator(
      tabLayout, viewPager2, true, canScroll,
      TabConfigurationStrategy { tab: TabLayout.Tab, position: Int -> onConfigureTab(tab, position) })
    mediator.attach()
  }

  private fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
    tab.text = list[position]
  }

  private fun selectItem(index: Int) {
    viewPager2.setCurrentItem(index, false)
  }

  private fun buildFragments(): MutableList<Fragment> {
    val list = mutableListOf<Fragment>()
    list.add(MonitorConfigFragment())
    list.add(BlockListFragment())
    return list
  }

  override fun onDestroy() {
    super.onDestroy()
    mediator.detach()
  }

}