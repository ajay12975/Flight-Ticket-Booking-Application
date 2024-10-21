package com.example.ticketbookingapp.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ticketbookingapp.Adapter.FlightAdapter;
import com.example.ticketbookingapp.Model.Flight;
import com.example.ticketbookingapp.databinding.ActivitySearchBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends BaseActivity {

    private ActivitySearchBinding binding;
    private String from,to , date;
    private int numPassenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        initList();
        setVariable();

    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initList() {

        DatabaseReference myRef= database.getReference("Flights");
        ArrayList<Flight> list= new ArrayList<>();
        Query query= myRef.orderByChild("from").equalTo(from);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot issue: snapshot.getChildren()){
                        Flight flight= issue.getValue(Flight.class);
                        if(flight.getTo().equals(to)){
                            list.add(flight);
                        }


                        //To filter with date, incomment below lines and comment top line
                        /*if(flight.getTo().equals(to) && flight.getDate().equals(date)){
                            list.add(flight);
                        }*/


                        if(!list.isEmpty()){
                            binding.recyclerSearch.setLayoutManager(new LinearLayoutManager(SearchActivity.this,LinearLayoutManager.VERTICAL,false));
                            binding.recyclerSearch.setAdapter(new FlightAdapter(list));

                        }
                        binding.pbSearch.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getIntentExtra() {
        from= getIntent().getStringExtra("from");
        to= getIntent().getStringExtra("to");
        date= getIntent().getStringExtra("date");
    }
}