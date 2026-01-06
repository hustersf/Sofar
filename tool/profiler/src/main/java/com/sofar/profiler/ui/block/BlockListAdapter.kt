package com.sofar.profiler.ui.block

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.sofar.profiler.R
import com.sofar.profiler.block.model.BlockInfo

class BlockListAdapter(var fragment: Fragment) : RecyclerView.Adapter<BlockViewHolder>() {

  private var datas = mutableListOf<BlockInfo>()

  fun setDatas(list: List<BlockInfo>) {
    datas.clear()
    datas.addAll(list)
  }

  fun insert(item: BlockInfo) {
    datas.add(0, item)
    notifyItemInserted(0)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
    var view = LayoutInflater.from(parent.context).inflate(R.layout.block_list_item_layout, parent, false)
    val viewHolder = BlockViewHolder(view)
    view.setOnClickListener {
      val pos = viewHolder.bindingAdapterPosition
      showDetailDialog(datas[pos])
    }
    return viewHolder
  }

  override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
    var item = datas[position]
    if (item.keyClass.isEmpty()) {
      holder.classTv.text = BlockListFragment.DEFAULT_CLASS
    } else {
      holder.classTv.text = item.keyClass
    }
    holder.costTv.text = "blocked ${item.timeCost} ms"
    holder.timeTv.text = "${item.timeEnd}"
  }

  override fun getItemCount(): Int {
    return datas.size
  }

  private fun showDetailDialog(blockInfo: BlockInfo) {
    var dialogFragment = BlockDetailDialogFragment()
    dialogFragment.setData(blockInfo)
    dialogFragment.show(fragment.childFragmentManager, "block_detail")
  }

}

class BlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  var classTv: TextView
  var costTv: TextView
  var timeTv: TextView

  init {
    classTv = itemView.findViewById(R.id.class_tv)
    costTv = itemView.findViewById(R.id.cost_tv)
    timeTv = itemView.findViewById(R.id.time_tv)
  }
}