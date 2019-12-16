package com.sofar.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sofar.R;
import com.sofar.utility.ToastUtil;
import com.sofar.utility.ViewUtil;

import java.util.List;

public class MainListAdapter extends RecyclerView.Adapter {

  List<PageData> dataList;

  public MainListAdapter() {
    dataList = PageData.buildPageDatas();
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = ViewUtil.inflate(parent, R.layout.main_list_item);
    return new ItemViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
    final PageData pageData = dataList.get(position);
    if (holder instanceof ItemViewHolder) {
      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          jump(holder.itemView.getContext(), pageData);
        }
      });
      ((ItemViewHolder) holder).nameTv.setText(pageData.name);
    }
  }

  @Override
  public int getItemCount() {
    return dataList.size();
  }

  class ItemViewHolder extends RecyclerView.ViewHolder {

    TextView nameTv;

    public ItemViewHolder(@NonNull View itemView) {
      super(itemView);
      nameTv = itemView.findViewById(R.id.name);
    }
  }

  private void jump(Context context, PageData pageData) {
    try {
      Intent intent = new Intent();
      intent.setData(Uri.parse(pageData.uri));
      context.startActivity(intent);
    } catch (Exception e) {
      ToastUtil.startShort(context, e.toString());
    }
  }
}
