package com.sofar.widget.recycler;

import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.sofar.R;
import com.sofar.base.recycler.RecyclerAdapter;
import com.sofar.base.viewbinder.RecyclerViewBinder;
import com.sofar.utility.ToastUtil;

public class StackCardAdapter extends RecyclerAdapter {

  private int loopCount = 200;

  public StackCardAdapter(int loopCount) {
    this.loopCount = loopCount;
  }

  @Override
  protected int getItemLayoutId(int viewType) {
    return R.layout.stack_card_item;
  }

  @NonNull
  @Override
  protected RecyclerViewBinder onCreateViewBinder(int viewType) {
    return new OverLayViewBinder(getList().size());
  }

  @Override
  public int getItemCount() {
    return super.getItemCount() * loopCount;
  }

  @Nullable
  @Override
  public Object getItem(int position) {
    position = position % super.getItemCount();
    return super.getItem(position);
  }

  private static class OverLayViewBinder extends RecyclerViewBinder {

    ImageView mImageView;
    TextView mTextView;

    int bannerSize;

    public OverLayViewBinder(int size) {
      this.bannerSize = size;
    }

    @Override
    protected void onCreate() {
      super.onCreate();
      mImageView = view.findViewById(R.id.image);
      mTextView = view.findViewById(R.id.text);
    }

    @Override
    protected void onBind(Object data) {
      super.onBind(data);
      if (viewAdapterPosition % 2 == 0) {
        mImageView
          .setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.themeColor));
      } else {
        mImageView
          .setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorAccent));
      }
      int position = viewAdapterPosition % bannerSize;
      mTextView.setText(position + "号");

      view.setOnClickListener(v -> {
        ToastUtil.startShort(context, "我是" + position + "号");
      });
    }
  }

}
