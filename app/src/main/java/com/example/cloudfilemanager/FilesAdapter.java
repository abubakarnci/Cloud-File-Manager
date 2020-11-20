package com.example.cloudfilemanager;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FilesAdapterViewHolder>{

    private FilesAdapter.onItemClickListener mListener;

    Context context;
    ArrayList<UploadFiles> arrayListFiles;
    public FilesAdapter(Context context, ArrayList<UploadFiles> arrayListFiles) {
        this.context= context;
        this.arrayListFiles=arrayListFiles;
    }

    @NonNull
    @Override
    public FilesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
        return new FilesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilesAdapterViewHolder holder, int i) {

        UploadFiles uploadFile =arrayListFiles.get(i);
        holder.titleTxt.setText(uploadFile.getmName());

    }
    @Override
    public int getItemCount()
    {
        return arrayListFiles.size();
    }


    public class FilesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener
    {

        TextView titleTxt;
        public FilesAdapterViewHolder(@NonNull View itemView) {

            super(itemView);
            titleTxt=(TextView)itemView.findViewById(R.id.file_title);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener((View.OnCreateContextMenuListener) this);
        }

        @Override
        public void onClick(View view) {

            if(mListener != null){
                int position=getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem doWhatever = menu.add(Menu.NONE, 1, 1, "Do whatever");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");
            MenuItem download = menu.add(Menu.NONE, 3, 3, "Download");
            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
            download.setOnMenuItemClickListener(this);
        }

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
    }

    public void setonItemClickListener(FilesAdapter.onItemClickListener listener){

        mListener=listener;

    }

}
