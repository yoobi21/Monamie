package com.android.monamie.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.android.monamie.R;
import com.android.monamie.adapters.HistoryAdapter;
import com.android.monamie.models.HistoryItem;
import com.android.monamie.utils.ProductManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private HistoryAdapter adapter;
    private List<HistoryItem> historyList;
    private DatabaseReference ordersRef;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        
        rvHistory = view.findViewById(R.id.rvHistory);
        
        currentUserId = FirebaseAuth.getInstance().getUid();
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        setupRecyclerView();
        fetchHistoryFromFirebase();
        
        return view;
    }

    private void setupRecyclerView() {
        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(historyList);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistory.setAdapter(adapter);
    }

    private void fetchHistoryFromFirebase() {
        if (currentUserId == null) return;

        ordersRef.orderByChild("userId").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        historyList.clear();
                        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("id", "ID"));

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                String orderId = ds.child("orderId").getValue(String.class);
                                String date    = ds.child("createdAt").getValue(String.class);
                                
                                // Ambil daftar item untuk ditampilkan menggantikan status
                                StringBuilder itemSummary = new StringBuilder();
                                DataSnapshot itemsSnapshot = ds.child("items");
                                for (DataSnapshot itemDs : itemsSnapshot.getChildren()) {
                                    String itemName = itemDs.child("name").getValue(String.class);
                                    if (itemName != null) {
                                        if (itemSummary.length() > 0) itemSummary.append(", ");
                                        itemSummary.append(itemName);
                                    }
                                }
                                String displayMenu = itemSummary.toString();
                                if (displayMenu.isEmpty()) displayMenu = "Pesanan Mon Amie";
                                
                                // Ambil imageRes: Prioritaskan dari Firebase (untuk Promo), fallback ke ProductManager (untuk item reguler)
                                int firstItemImage = R.drawable.img_cookie_velvet;
                                DataSnapshot firstItem = itemsSnapshot.getChildren().iterator().hasNext() ? 
                                        itemsSnapshot.getChildren().iterator().next() : null;
                                
                                if (firstItem != null) {
                                    Long fbImageRes = firstItem.child("imageRes").getValue(Long.class);
                                    if (fbImageRes != null && fbImageRes != 0) {
                                        firstItemImage = fbImageRes.intValue();
                                    } else {
                                        String firstItemName = firstItem.child("name").getValue(String.class);
                                        if (firstItemName != null) {
                                            firstItemImage = ProductManager.getInstance().getImageByName(firstItemName);
                                        }
                                    }
                                }
                                
                                // Firebase menyimpan angka sebagai Long
                                Object totalObj = ds.child("total").getValue();
                                long totalVal = 0;
                                if (totalObj instanceof Long) {
                                    totalVal = (Long) totalObj;
                                } else if (totalObj instanceof Integer) {
                                    totalVal = ((Integer) totalObj).longValue();
                                }
                                
                                String totalStr = "Rp " + fmt.format(totalVal);

                                historyList.add(new HistoryItem(
                                        orderId != null ? "Order #" + orderId.substring(Math.max(0, orderId.length() - 8)) : "Order", 
                                        date, 
                                        totalStr, 
                                        displayMenu,
                                        firstItemImage
                                ));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        // Balik urutan agar pesanan terbaru di paling atas
                        Collections.reverse(historyList);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }
}