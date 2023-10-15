package com.example.audiostreaming.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Music implements Serializable, Parcelable {
    String idMusic;
    String name;
    String image;
    String video;
    String time;
    String author;
    String lyrics;

    public Music() {
    }

    public Music(String idMusic, String name, String image, String video, String time, String author, String lyrics) {
        this.idMusic = idMusic;
        this.name = name;
        this.image = image;
        this.video = video;
        this.time = time;
        this.author = author;
        this.lyrics = lyrics;
    }

    protected Music(Parcel in) {
        idMusic = in.readString();
        name = in.readString();
        image = in.readString();
        video = in.readString();
        time = in.readString();
        author = in.readString();
        lyrics = in.readString();
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    public String getIdMusic() {
        return idMusic;
    }

    public void setIdMusic(String idMusic) {
        this.idMusic = idMusic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(idMusic);
        parcel.writeString(name);
        parcel.writeString(image);
        parcel.writeString(video);
        parcel.writeString(time);
        parcel.writeString(author);
        parcel.writeString(lyrics);
    }
}
