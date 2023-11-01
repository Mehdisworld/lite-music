package com.ultimate.music.downloader.downloader;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {

  private String imageUrl;
  private String textCover;

  public Item(String mImageResource, String mTxtCover) {
    this.imageUrl = mImageResource;
    this.textCover = mTxtCover;
  }

  protected Item(Parcel in) {
    imageUrl = in.readString();
    textCover = in.readString();
  }

  public static final Creator<Item> CREATOR = new Creator<Item>() {
    @Override
    public Item createFromParcel(Parcel in) {
      return new Item(in);
    }

    @Override
    public Item[] newArray(int size) {
      return new Item[size];
    }
  };



  public String getImageUrl() {
    return imageUrl;
  }

  public String getTextCover() {
    return textCover;
  }


  @Override
  public String toString() {
    return "Item{" +
            "mImageResource=" + imageUrl +
            ", txtExercise='" + textCover + '\'' +
            '}';
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(imageUrl);
    dest.writeString(textCover);

  }
}
