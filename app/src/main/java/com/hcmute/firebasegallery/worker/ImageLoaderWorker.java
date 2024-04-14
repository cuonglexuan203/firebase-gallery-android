package com.hcmute.firebasegallery.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class ImageLoaderWorker extends Worker {
    private static ImageLoaderCallback callback;
    public ImageLoaderWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Images");
        String lastKey = getInputData().getString("lastKey");
        final int PAGE_SIZE = getInputData().getInt("PAGE_SIZE", 1);
        loadNextPage(databaseReference, lastKey, PAGE_SIZE);
        return Result.success();
    }

    public void loadNextPage(DatabaseReference databaseReference, String lastKey, final int PAGE_SIZE) {
        final DataSnapshot result;

        databaseReference.orderByKey().startAfter(lastKey).limitToFirst(PAGE_SIZE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        callback.onPageLoadedSuccessfully(snapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onPageLoadFailed(error.getMessage());
                    }
                });
    }
    public static void setCallback(ImageLoaderCallback callback){
        ImageLoaderWorker.callback = callback;
    }
}
