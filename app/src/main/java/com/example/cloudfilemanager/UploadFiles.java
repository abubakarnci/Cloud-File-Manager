package com.example.cloudfilemanager;

import com.google.firebase.database.Exclude;

public class UploadFiles {


    //variables using to store info of files
    private String mName;
    private String mFileUrl;
    private String mKey;

    public UploadFiles(){
        // needed
    }

    public UploadFiles(String mName, String mFileUrl ) {
        if(mName.trim().equals("")){
            mName="No Name";
        }
        this.mName = mName;
        this.mFileUrl = mFileUrl;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmFileUrl() {
        return mFileUrl;
    }

    public void setmFileUrl(String mFileUrl) {
        this.mFileUrl = mFileUrl;
    }
@Exclude
    public String getmKey() {
        return mKey;
    }
@Exclude
    public void setmKey(String mKey) {
        this.mKey = mKey;
    }
}
