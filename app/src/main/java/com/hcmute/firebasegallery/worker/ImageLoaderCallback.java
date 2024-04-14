package com.hcmute.firebasegallery.worker;

import com.google.firebase.database.DataSnapshot;

public interface ImageLoaderCallback {
    void onPageLoadedSuccessfully(DataSnapshot snapshot);
    void onPageLoadFailed(String errorMessage);
}
