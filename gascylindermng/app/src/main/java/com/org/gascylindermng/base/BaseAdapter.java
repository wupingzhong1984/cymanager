package com.org.gascylindermng.base;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {
    protected Context mContext;
    protected List<T> mData;
    protected LayoutInflater mInflater;

    public BaseAdapter(Context context) {
        this.mContext = context;
        this.mData = new ArrayList<T>();
        this.mInflater = LayoutInflater.from(context);
    }

    final public void addData(List<T> list) {
        if (list.size() != 0) {
            this.mData.addAll(list);
            super.notifyDataSetChanged();
        }
    }

    final public void updateData(List<T> list) {
        if (list.size() != 0) {
            this.mData.clear();
            this.mData.addAll(list);
            super.notifyDataSetChanged();
        }
    }

    final public void deleteData(int position) {
        mData.remove(position);
        super.notifyDataSetChanged();
    }

    final public void deleteAllData() {
        this.mData.clear();
        super.notifyDataSetChanged();
    }

    final public List<T> getData() {
        return mData;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public abstract int getItemViewType(int position);

    public abstract int getItemLayoutId(int getItemViewType);

    public abstract void handleItem(int itemViewType, int position, T item, ViewHolder holder, boolean isRecycle);

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        int itemLayoutType = getItemViewType(position);
        ViewHolder viewHolder = null;
        boolean isRecycle = false;
        if (view == null) {
            view = mInflater.inflate(getItemLayoutId(itemLayoutType), null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            isRecycle = true;
        }

        handleItem(itemLayoutType, position, mData.get(position), viewHolder, isRecycle);

        return view;
    }

    public static class ViewHolder {
        View mRootView;
        SparseArray<View> mViews = new SparseArray<View>();

        public ViewHolder(View view) {
            this.mRootView = view;
        }

        public View getView() {
            return mRootView;
        }

        public <T extends View> T get(int viewId) {
            View childView = mViews.get(viewId);
            if (childView == null) {
                childView = mRootView.findViewById(viewId);
                mViews.put(viewId, childView);
            }
            return (T) childView;
        }

        public <T extends View> T get(int viewId, Class<T> viewClass) {
            View childView = mViews.get(viewId);
            if (childView == null) {
                childView = mRootView.findViewById(viewId);
                mViews.put(viewId, childView);
            }
            return (T) childView;
        }

    }
}
