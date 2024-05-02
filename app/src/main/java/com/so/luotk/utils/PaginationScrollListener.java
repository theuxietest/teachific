package com.so.luotk.utils;

import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PaginationScrollListener extends RecyclerView.OnScrollListener {
    private static final String TAG = PaginationScrollListener.class.getSimpleName();
    private final LinearLayoutManager layoutManager;

    protected PaginationScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    /*
     Method gets callback when user scroll the search list
     */
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0) {
                Log.i(TAG, "Loading more items");
                loadMoreItems();
            }
        }

    }

    public void loadMoreItems() {

    }

    //optional
    public int getTotalPageCount() {
        return 0;
    }

    public boolean isLastPage() {
        return false;
    }

    public boolean isLoading() {
        return false;
    }
}
