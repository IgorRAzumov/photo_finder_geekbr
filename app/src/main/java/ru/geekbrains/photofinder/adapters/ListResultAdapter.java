package ru.geekbrains.photofinder.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKPhotoArray;

import ru.geekbrains.photofinder.R;


public class ListResultAdapter extends RecyclerView.Adapter<ListResultAdapter.PhotoCardViewHolder> {
    public static final int GRID_TYPE = 1;
    public static final int LINEAR_TYPE = 2;

    public class PhotoCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView songCardView;
        ImageView photoImageView;
        TextView uploadDateTextView;

        public PhotoCardViewHolder(View itemView, int viewType) {
            super(itemView);
            switch (viewType) {
                case GRID_TYPE:
                    photoImageView = itemView.findViewById(R.id.iv_horizontal_card_photo);
                    break;
                default: //LINEAR_TYPE
                    songCardView = itemView.findViewById(R.id.cv_vertical_photo_card);
                    photoImageView = itemView.findViewById(R.id.iv_vertical_card_photo);
                    uploadDateTextView = itemView.findViewById(R.id.tv_vertical_card_date);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recycleViewOnItemClickListener.onItemRecyclerClick(v, getAdapterPosition());
        }
    }

    public interface RecycleViewOnItemClickListener {
        void onItemRecyclerClick(View v, int position);
    }

    public static VKPhotoArray photosArray;//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private int viewType;
    private RecycleViewOnItemClickListener recycleViewOnItemClickListener;


    public ListResultAdapter(RecycleViewOnItemClickListener
                                     recycleViewOnItemClickListener) {
        this.recycleViewOnItemClickListener = recycleViewOnItemClickListener;
        photosArray = new VKPhotoArray();
    }

    @Override
    public PhotoCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case GRID_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_photo_card,
                        parent, false);
                break;
            default: {//LINEAR_TYPE
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vertical_photo_card,
                        parent, false);
            }
        }
        return new PhotoCardViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(PhotoCardViewHolder holder, int position) {
        VKApiPhoto photo = photosArray.get(position);
        String photoUrl = photo.photo_130;

        switch (viewType) {
            case GRID_TYPE: {
                bindImageView(holder.photoImageView, photoUrl, holder.itemView.getContext());
                break;
            }
            default: { //LINEAR_TYPE
                bindImageView(holder.photoImageView, photoUrl, holder.itemView.getContext());
                holder.uploadDateTextView.setText(String.valueOf(photo.date));
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
                loadImage(imageView, url, context, 100, 120);
                break;
            }
            default: {
                loadImage(imageView, url, context, 260, 260);
            }
        }
    }

    private void loadImage(ImageView imageView, String url, Context context, int targetWidth,
                           int targetHeight) {
        Picasso
                .with(context)
                .load(url)
               // .resize(targetWidth, targetHeight)
                .into(imageView);
    }

    public void setData(VKPhotoArray data) {
        this.photosArray = data;
    }

    public void setViewType(int type) {
        this.viewType = type;
    }
}
