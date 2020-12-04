package com.example.cloudfilemanager;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context mContext;
    private ArrayList<UploadImage> mUploads;
    private onItemClickListener mListener;
    public ImageAdapter(Context context, ArrayList<UploadImage> uploads) {
        this.mContext = context;
        this.mUploads = uploads;
    }

    @NonNull
    @Override
    //image view holder for displaying
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        UploadImage uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getName());
       Glide.with(mContext).load(mUploads.get(position).getImageUrl()).into(holder.imageView);

       //setting up image size and resolution through picasso
        Picasso.with(mContext)
                .load(uploadCurrent.getImageUrl())
                .placeholder(R.mipmap.ic_launcher1)
                .fit()
                .centerInside()
                .into(holder.imageView);

    }

    //display the position of selected image in whole array
    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    //image options on RecyclerView
    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {


        public TextView textViewName;
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName=itemView.findViewById(R.id.name);
            imageView=itemView.findViewById(R.id.image_view_upload);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                int position=getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        }

        //position options
        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem doWhatever = menu.add(Menu.NONE, 1, 1, "Do whatever");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");
            MenuItem download = menu.add(Menu.NONE, 3, 3, "Download");
            MenuItem rotate = menu.add(Menu.NONE, 4, 4, "Rotate");
            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
            download.setOnMenuItemClickListener(this);
            rotate.setOnMenuItemClickListener(this);
        }

        //when user clicks on any option
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(mListener != null){
                int position=getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    switch (item.getItemId()) {
                        case 1:
                            mListener.onWhatEverClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                        case 3:
                            mListener.onDownloadClick(position);
                            return true;
                        case 4:
                            mListener.onRotateClick();
                            return true;
                    }
                }
            }
            return false;
        }
    }
    public interface onItemClickListener{
        void onItemClick(int position);

        void onWhatEverClick(int position);

        void onDeleteClick(int position);

        void onDownloadClick(int position);
        void onRotateClick();

    }

    public void setonItemClickListener(onItemClickListener listener){

        mListener=listener;

    }

}
