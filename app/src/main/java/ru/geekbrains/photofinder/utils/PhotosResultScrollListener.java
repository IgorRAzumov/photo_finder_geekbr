package ru.geekbrains.photofinder.utils;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


public abstract class PhotosResultScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private boolean isGridLayoutNow;

    public PhotosResultScrollListener(LinearLayoutManager linearLayoutManager, GridLayoutManager gridLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
        this.gridLayoutManager = gridLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount;
        int totalItemCount;
        int firstVisibleItemPosition;

        if (recyclerView.getLayoutManager() == linearLayoutManager) {
            visibleItemCount = linearLayoutManager.getChildCount();
            totalItemCount = linearLayoutManager.getItemCount();
            firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        } else {
            visibleItemCount = gridLayoutManager.getChildCount();
            totalItemCount = gridLayoutManager.getItemCount();
            firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();

        }

        if (firstVisibleItemPosition != 0 && !isLoading() && !isDataEnd()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0) {
                loadMoreData();
            }
        }
    }

    protected abstract void loadMoreData();

    public abstract boolean isDataEnd();

    public abstract boolean isLoading();

}
