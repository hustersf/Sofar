package com.sofar.profiler.ui.block

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.sofar.profiler.R
import com.sofar.profiler.block.model.BlockInfo
import com.sofar.profiler.getMetricsHeight

class BlockDetailDialogFragment : DialogFragment() {

  private lateinit var titleTv: TextView
  private lateinit var detailTv: TextView

  private var blockInfo: BlockInfo? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.block_detail_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initView(view)
    initData()
  }

  fun setData(blockInfo: BlockInfo) {
    this.blockInfo = blockInfo
  }

  private fun initView(view: View) {
    titleTv = view.findViewById(R.id.block_title_tv)
    detailTv = view.findViewById(R.id.block_detail_tv)
  }

  private fun initData() {
    blockInfo?.let {
      if (it.keyClass.isEmpty()) {
        titleTv.text = BlockListFragment.DEFAULT_CLASS
      } else {
        titleTv.text = it.keyClass
      }
      var source = it.toString()
      var spannableString = SpannableString(it.toString())
      var start = source.indexOf(it.keyClass)
      var end = start + it.keyClass.length
      spannableString.setSpan(ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
      spannableString.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
      detailTv.text = spannableString
    }
  }

  override fun onStart() {
    super.onStart()
    dialog?.window?.let {
      val width = ViewGroup.LayoutParams.MATCH_PARENT
      val height = 1.0f * getMetricsHeight(requireActivity()) / 4 * 3
      it.setLayout(width, height.toInt())
    }
  }
}