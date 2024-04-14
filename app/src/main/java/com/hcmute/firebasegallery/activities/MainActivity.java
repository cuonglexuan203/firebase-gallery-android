package com.hcmute.firebasegallery.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmute.firebasegallery.model.DataClass;
import com.hcmute.firebasegallery.R;
import com.hcmute.firebasegallery.adapter.FirebaseAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    private RecyclerView recyclerView;
    private ArrayList<DataClass> dataList;
    private FirebaseAdapter adapter;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Images");

    //
    private static final int PAGE_SIZE = 5;
    private boolean isLoading = false;
    private String lastKey;
    private boolean isReachEndOfData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recyclerView);
        //
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<>();
        adapter = new FirebaseAdapter(this, dataList);
        recyclerView.setAdapter(adapter);
        //
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // Check if the end of the list is reached and not currently loading
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    adapter.setLoading(true);
                    if (!isReachEndOfData) {
                    // Load the next page of data
                        isLoading = true;
                        loadNextPage();
                    }

                }
            }
        });

        loadImages();

        //
        fab.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this, UploadActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void updateUI(DataSnapshot snapshot) {
        Iterator<DataSnapshot> iter = snapshot.getChildren().iterator();
        DataSnapshot dataSnapshot = null;
        while (iter.hasNext()) {
            dataSnapshot = iter.next();
            DataClass dataClass = dataSnapshot.getValue(DataClass.class);
            dataList.add(dataClass);
            lastKey = dataSnapshot.getKey();
        }
        if (dataSnapshot == null) {
            isReachEndOfData = true;
        }
        adapter.notifyDataSetChanged();
        isLoading = false;
    }

    private void loadImages() {
        isLoading = true;
        databaseReference.limitToFirst(PAGE_SIZE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                updateUI(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isLoading = false;
            }
        });
    }


    public void loadNextPage() {
        databaseReference.orderByKey().startAfter(lastKey).limitToFirst(PAGE_SIZE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        updateUI(snapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        isLoading = false;
                    }
                });
    }

}