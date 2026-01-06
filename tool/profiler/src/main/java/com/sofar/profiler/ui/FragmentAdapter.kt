package com.sofar.profiler.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

internal class FragmentAdapter : FragmentStateAdapter {
  private val fragments: MutableList<Fragment> = ArrayList()

  constructor(fragmentActivity: FragmentActivity) : super(fragmentActivity)

  constructor(fragment: Fragment) : super(fragment)

  constructor(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
  ) : super(fragmentManager, lifecycle)

  fun setFragments(list: List<Fragment>) {
    fragments.clear()
    fragments.addAll(list)
  }

  override fun createFragment(position: Int): Fragment {
    return fragments[position]
  }

  override fun getItemCount(): Int {
    return fragments.size
  }
}