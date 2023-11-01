package com.ultimate.music.downloader.downloader;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.ultimate.music.downloader.R;

public class AutoLoadListView extends ListView implements OnScrollListener {
    private static final String TAG = AutoLoadListView.class.getSimpleName();
    private boolean mEnableLoad = true;
    private int mFirstVisibleItem = 0;
    private boolean mIsLoading = false;
    private OnLoadListener mListener;
    private View vFooter;

    public interface OnLoadListener {
        void onLoad();
    }

    public void onScrollStateChanged(AbsListView absListView, int i) {
    }

    public AutoLoadListView(Context context) {
        super(context);
        init();
    }

    public AutoLoadListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public AutoLoadListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        this.vFooter = LayoutInflater.from(getContext()).inflate(R.layout.footer_list_auto, null);
        addFooterView(this.vFooter, null, false);
        setOnScrollListener(this);
        onLoadComplete();
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.mListener = onLoadListener;
    }

    public void onLoadComplete() {
        Log.d(TAG, "onLoadComplete");
        this.mIsLoading = false;
        removeFooterView(this.vFooter);
    }

    public void setEnable(boolean z) {
        this.mEnableLoad = z;
    }

    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        boolean z = i > this.mFirstVisibleItem;
        if (this.mEnableLoad && !this.mIsLoading && z && i2 + i >= i3 - 1) {
            onLoad();
        }
        this.mFirstVisibleItem = i;
    }

    private void onLoad() {
        Log.d(TAG, "onLoad");
        this.mIsLoading = true;
        addFooterView(this.vFooter, null, false);
        OnLoadListener onLoadListener = this.mListener;
        if (onLoadListener != null) {
            onLoadListener.onLoad();
        }
    }
}
