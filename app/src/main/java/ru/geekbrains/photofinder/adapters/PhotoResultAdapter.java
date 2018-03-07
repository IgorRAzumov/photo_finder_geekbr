package ru.geekbrains.photofinder.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKPhotoArray;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.utils.DateTimeUtils;
import ru.geekbrains.photofinder.utils.NetworkUtils;


public class PhotoResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int GRID_TYPE = 1;
    public static final int LINEAR_TYPE = 2;
    private static final int BUTTON_TYPE = 2;


    private VKPhotoArray photosArray;
    private int viewType;
    private RecycleViewOnItemClickListener recycleViewClickListener;

    public PhotoResultAdapter(RecycleViewOnItemClickListener
                                      recycleViewClickListener, VKPhotoArray vkPhotoArray) {
        this.recycleViewClickListener = recycleViewClickListener;
        photosArray = vkPhotoArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case GRID_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_photo_card,
                        parent, false);
                return new PhotoCardGridViewHolder(view);

            default: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vertical_photo_card,
                        parent, false);
                return new PhotoCardVerticalViewHolder(view);
            }
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VKApiPhoto photo = photosArray.get(position);
        String photoUrl;

        switch (viewType) {
            case GRID_TYPE: {
                photoUrl = photo.photo_130;
                PhotoCardGridViewHolder gridHolder = (PhotoCardGridViewHolder) holder;
                bindImageView(gridHolder.photoImageView, photoUrl, holder.itemView.getContext());
                break;
            }
            default: {
                photoUrl = photo.photo_604;
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
        return photosArray.size();
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
        photosArray = data;
    }

    public void setViewType(int type) {
        this.viewType = type;
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

    public class DownloadMoreButtonViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        Button downloadMoreButton;

        public DownloadMoreButtonViewHolder(View itemView) {
            super(itemView);
            downloadMoreButton = itemView.findViewById(R.id.bt_download_more);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_download_more: {
                    recycleViewClickListener.onItemRecyclerClick(getAdapterPosition());
                    break;
                }

            }
        }
    }
}