/*
 * Copyright © Yan Zhenjie. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.base.album;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jelly.jellybase.R;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.impl.OnItemClickListener;
import com.yanzhenjie.album.util.AlbumUtils;

import java.util.List;

/**
 * <p>Image adapter.</p>
 * Created by Yan Zhenjie on 2016/10/30.
 */
public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mInflater;
    private int itemSize;
    private int maxItem=6;//设置最大item数
    private OnItemClickListener mItemClickListener;

    private List<AlbumFile> mAlbumFiles;

    public AlbumAdapter(Context context, int itemSize, OnItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.itemSize = itemSize;
        this.mItemClickListener = itemClickListener;
    }

    public void notifyDataSetChanged(List<AlbumFile> imagePathList) {
        this.mAlbumFiles = imagePathList;
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(mAlbumFiles==null)
            return AlbumFile.TYPE_IMAGE;
        if (position>=mAlbumFiles.size()-1)
            return AlbumFile.TYPE_IMAGE;
        AlbumFile albumFile = mAlbumFiles.get(position);
        if (albumFile.getMediaType() == AlbumFile.TYPE_IMAGE) {
            return AlbumFile.TYPE_IMAGE;
        } else {
            return AlbumFile.TYPE_VIDEO;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case AlbumFile.TYPE_IMAGE: {
                return new ImageViewHolder(mInflater.inflate(R.layout.album_item_image, parent, false), itemSize, mItemClickListener);
            }
            case AlbumFile.TYPE_VIDEO:
            default: {
                return new VideoViewHolder(mInflater.inflate(R.layout.album_item_video, parent, false), itemSize, mItemClickListener);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case AlbumFile.TYPE_IMAGE: {
                if(mAlbumFiles==null){
                    ((ImageViewHolder) holder).setData(R.drawable.ic_camera);
                    break;
                }
                if(position<=mAlbumFiles.size()-1){
                    ((ImageViewHolder) holder).setData(mAlbumFiles.get(position));
                }else {
                    if(position<=maxItem-1){
                        ((ImageViewHolder) holder).setData(R.drawable.ic_camera);
                    }
                }
                break;
            }
            case AlbumFile.TYPE_VIDEO: {
                if(mAlbumFiles==null){
                    ((VideoViewHolder) holder).setData(R.drawable.ic_camera);
                    break;
                }
                if(position<=mAlbumFiles.size()-1){
                    ((VideoViewHolder) holder).setData(mAlbumFiles.get(position));
                }else {
                    if(position<=maxItem-1){
                        ((VideoViewHolder) holder).setData(R.drawable.ic_camera);
                    }
                }
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if( mAlbumFiles == null){
            return 1;
        }else if(mAlbumFiles.size()==maxItem){
            return mAlbumFiles.size();
        }
        return mAlbumFiles.size()+1;
    }

    public int getMaxItem() {
        return maxItem;
    }

    public void setMaxItem(int maxItem) {
        this.maxItem = maxItem;
    }

    private static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final int itemSize;
        private final OnItemClickListener mItemClickListener;

        private ImageView mIvImage;

        ImageViewHolder(View itemView, int itemSize, OnItemClickListener itemClickListener) {
            super(itemView);
            itemView.getLayoutParams().height = itemSize;

            this.itemSize = itemSize;
            this.mItemClickListener = itemClickListener;

            mIvImage = (ImageView) itemView.findViewById(R.id.iv_album_content_image);

            itemView.setOnClickListener(this);
        }

        public void setData(AlbumFile albumFile) {
            mIvImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Album.getAlbumConfig().
                    getAlbumLoader().
                    load(mIvImage, albumFile);

        }
        public void setData(int id) {
            mIvImage.setScaleType(ImageView.ScaleType.CENTER);
            mIvImage.setImageResource(id);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    private static class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final int itemSize;
        private final OnItemClickListener mItemClickListener;

        private ImageView mIvImage;
        private TextView mTvDuration;

        VideoViewHolder(View itemView, int itemSize, OnItemClickListener itemClickListener) {
            super(itemView);
            itemView.getLayoutParams().height = itemSize;

            this.itemSize = itemSize;
            this.mItemClickListener = itemClickListener;

            mIvImage = (ImageView) itemView.findViewById(com.yanzhenjie.album.R.id.iv_album_content_image);
            mTvDuration = (TextView) itemView.findViewById(com.yanzhenjie.album.R.id.tv_duration);

            itemView.setOnClickListener(this);
        }

        void setData(AlbumFile albumFile) {
            mIvImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Album.getAlbumConfig().
                    getAlbumLoader().
                    load(mIvImage, albumFile);
            mTvDuration.setText(AlbumUtils.convertDuration(albumFile.getDuration()));
        }
        public void setData(int id) {
            mIvImage.setScaleType(ImageView.ScaleType.CENTER);
            mIvImage.setImageResource(id);
        }
        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

}
