package ru.geekbrains.photofinder.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKPhotoArray;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.utils.DateTimeUtils;
import ru.geekbrains.photofinder.utils.NetworkUtils;


public class PhotoResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int GRID_TYPE = 1;
    public static final int LINEAR_TYPE = 2;
    public static final int PROGRESS_TYPE = 3;

    private RecycleViewOnItemClickListener recycleViewClickListener;
    private VKPhotoArray photosList;
    private int viewType;
    private boolean isLoadingAdded;

    private int penultimateViewType;


    public PhotoResultAdapter(RecycleViewOnItemClickListener
                                      recycleViewClickListener, VKPhotoArray vkPhotoArray) {
        this.recycleViewClickListener = recycleViewClickListener;
        photosList = vkPhotoArray;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case PROGRESS_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_photos_more_progress,
                        parent, false);
                return new LoadMoreButtonViewHolder(view);
            }
            case GRID_TYPE: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_photo_card,
                        parent, false);
                return new PhotoCardGridViewHolder(view);
            }
            default: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vertical_photo_card,
                        parent, false);
                return new PhotoCardVerticalViewHolder(view);
            }
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VKApiPhoto photo = photosList.get(position);
        String photoUrl;

        switch (viewType) {
            case PROGRESS_TYPE: {
                break;
            }
            case GRID_TYPE: {
                photoUrl = photo.photo_604;
                PhotoCardGridViewHolder gridHolder = (PhotoCardGridViewHolder) holder;
                bindImageView(gridHolder.photoImageView, photoUrl, holder.itemView.getContext());
                break;
            }
            default: {
                photoUrl = photo.photo_604;
                System.out.println(holder.getClass());
                PhotoCardVerticalViewHolder verticalHolder = (PhotoCardVerticalViewHolder) holder;

                bindImageView(verticalHolder.photoImageView, photoUrl, holder.itemView.getContext());
                long date = photo.date;
                if (date != 0) {
                    verticalHolder.uploadDateTextView.setText(DateTimeUtils
                            .convertUnixDate(holder.itemView.getContext(), date));
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @Override
    public int getItemCount() {
        return photosList == null ? 0 : photosList.size();
    }

    public VKApiPhoto getItem(int position) {
        VKApiPhoto vkApiPhoto;
        if (position < photosList.size()) {
            vkApiPhoto = photosList.get(position);
        } else {
            vkApiPhoto = null;
        }
        return vkApiPhoto;
    }

    private void bindImageView(ImageView imageView, String url, Context context) {//, int targetWidth, int targetHeight) {
        switch (viewType) {
            case GRID_TYPE: {
                NetworkUtils.loadImage(imageView, url, context, 100, 120);
                break;
            }
            default: {
                NetworkUtils.loadImage(imageView, url, context, 260, 260);
            }
        }
    }


    public void setData(VKPhotoArray data) {
        photosList = data;
    }

    public void setItemViewType(int type) {
        penultimateViewType = viewType;
        viewType = type;
    }


    public void add(VKApiPhoto photo) {
        photosList.add(photo);
        notifyItemInserted(photosList.size() - 1);
    }

    public void addAll(VKPhotoArray vkPhotoArray) {
        for (VKApiPhoto photo : vkPhotoArray) {
            add(photo);
        }
    }

    public void remove(VKApiPhoto photo) {
        int position = photosList.indexOf(photo);
        if (position > -1) {
            photosList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new VKApiPhoto());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = photosList.size() - 1;
        VKApiPhoto photo = getItem(position);

        if (photo != null) {
            photosList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public int getPenultimateViewType() {
        return penultimateViewType;
    }


    public interface RecycleViewOnItemClickListener {
        void onItemRecyclerClick(int position);

        void onOpenProfileButtonClickListener(int adapterPosition);

    }

    class PhotoCardGridViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        ImageView photoImageView;

        PhotoCardGridViewHolder(View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.iv_horizontal_card_photo);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cv_horizontal_photo_card: {
                    recycleViewClickListener.onItemRecyclerClick(getAdapterPosition());
                    break;
                }
            }
        }
    }

    public class PhotoCardVerticalViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        ImageView photoImageView;
        TextView uploadDateTextView;
        ImageButton openProfileButton;

        public PhotoCardVerticalViewHolder(View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.iv_vertical_card_photo);
            uploadDateTextView = itemView.findViewById(R.id.tv_vertical_card_upload_date);
            openProfileButton = itemView.findViewById(R.id.imbt_vertical_photo_open_profile);

            openProfileButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cv_vertical_photo_card: {
                    recycleViewClickListener.onItemRecyclerClick(getAdapterPosition());
                    break;
                }
                case R.id.imbt_vertical_photo_open_profile: {
                    recycleViewClickListener.onOpenProfileButtonClickListener(getAdapterPosition());
                    break;
                }
            }
        }
    }

    public class LoadMoreButtonViewHolder extends RecyclerView.ViewHolder {
        public LoadMoreButtonViewHolder(View itemView) {
            super(itemView);
        }
    }
}