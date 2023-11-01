package com.ultimate.music.downloader.downloader;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {
    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(this.mIndex, new Function<Integer, String>() {
        public String apply(Integer num) {
            StringBuilder sb = new StringBuilder();
            sb.append("Hello world from section: ");
            sb.append(num);
            return sb.toString();
        }
    });

    public void setIndex(int i) {
        this.mIndex.setValue(Integer.valueOf(i));
    }

    public LiveData<String> getText() {
        return this.mText;
    }
}
