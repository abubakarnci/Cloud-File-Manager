package com.example.cloudfilemanager;

import com.google.firebase.database.Exclude;

public class UploadSong {
    private String name;
    private String songUrl;
    private String songDuration;
    private String Key;

   public UploadSong(){
        // needed
    }

    public UploadSong(String name, String songDuration, String songUrl){
        if(name.trim().equals("")){
            name="No Name";
        }
        this.name=name;
        this.songUrl=songUrl;
        this.songDuration=songDuration;



    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    @Exclude
    public String getKey() {
        return Key;
    }

    @Exclude
    public void setKey(String Key) {
        this.Key = Key;
    }
}

