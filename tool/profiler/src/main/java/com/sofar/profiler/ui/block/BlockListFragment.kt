package com.sofar.profiler.ui.block

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sofar.profiler.MonitorCallback
import com.sofar.profiler.MonitorManager
import com.sofar.profiler.R
import com.sofar.profiler.block.model.BlockInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BlockListFragment : Fragment() {

  private lateinit var countTv: TextView
  private lateinit var recyclerView: RecyclerView
  private lateinit var adapter: BlockListAdapter
  private val sdkConfig = MonitorManager.config()

  companion object {
    const val DEFAULT_CLASS = "Unknown Class"
  }

  private var callback: MonitorCallback = object : MonitorCallback {
    override fun onBlock(info: BlockInfo) {
      super.onBlock(info)
      parseKeyClass(info)
      adapter.insert(info)
      udpateBlockCountUI()
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.block_list_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    MonitorManager.register(callback)
    initView(view)
    initData()
  }

  private fun initView(view: View) {
    countTv = view.findViewById(R.id.block_count_tv)

    recyclerView = view.findViewById(R.id.block_list)
    adapter = BlockListAdapter(this)
    recyclerView.adapter = adapter
    recyclerView.layoutManager = LinearLayoutManager(requireActivity())
    var padding = resources.getDimensionPixelSize(R.dimen.page_padding)
    recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
      override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
      ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = padding
        outRect.right = padding
        outRect.bottom = padding
      }
    })
  }

  private fun initData() {
    lifecycleScope.launch {
      var list = MonitorManager.blockList()
      withContext(Dispatchers.Default) {
        for (item in list) {
          if (item.keyClass.isEmpty()) {
            parseKeyClass(item)
          }
        }
      }
      adapter.setDatas(list)
      adapter.notifyDataSetChanged()
    }
    udpateBlockCountUI()
  }

  private fun udpateBlockCountUI() {
    countTv.text = "${MonitorManager.blockList().size} Blocks"
  }

  private fun parseKeyClass(blockInfo: BlockInfo) {
    var key = ""
    var lines = blockInfo.toString().split("\n")
    for (line in lines) {
      if (line.startsWith(sdkConfig.blockPackage)) {
        var start = line.indexOf('(')
        key = line.substring(start + 1, line.length - 2)
      }
    }
    blockInfo.keyClass = key
  }

  override fun onDestroyView() {
    super.onDestroyView()
    MonitorManager.unregister(callback)
  }
}