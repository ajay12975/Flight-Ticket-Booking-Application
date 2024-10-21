package com.example.ticketbookingapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbookingapp.Adapter.SeatAdapter;
import com.example.ticketbookingapp.Model.Flight;
import com.example.ticketbookingapp.Model.Seat;
import com.example.ticketbookingapp.R;
import com.example.ticketbookingapp.databinding.ActivitySeatBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatActivity extends BaseActivity {

    TextView tvseat;

    private ActivitySeatBinding binding;
    private Flight flight;
    private Double price = 0.0;
    private int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tvseat= findViewById(R.id.tvselectedSeat);

        getIntentExtra();
        initSeatList();
        setVariable();
    }

    private void setVariable() {
        binding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve seat information when the button is clicked
                String seat = tvseat.getText().toString().trim();

                // Assuming 'num' and 'MainActivity.seat' are integers
                if (TextUtils.isEmpty(seat)) {
                    // Seat is empty
                    Toast.makeText(SeatActivity.this, "Please select your seat", Toast.LENGTH_SHORT).show();
                    return;
                } else if (num == MainActivity.seat) {
                    // Seat matches the required number of seats
                    // Make sure 'flight' is initialized properly
                    flight.setPassenger(binding.tvseatName.getText().toString());
                    flight.setPrice(price);  // Assuming 'price' is already defined
                    Toast.makeText(SeatActivity.this, "Please select the required number of seats", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Pass the flight object to TicketDetailActivity
                Intent intent = new Intent(SeatActivity.this, TicketDetailActivity.class);
                intent.putExtra("flight", flight);
                startActivity(intent);
            }
        });

    }

    private void initSeatList() {
        // Create grid layout manager with 7 columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 7);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // Return 1 for all positions
                return 1;
            }
        });

        binding.seatRecyclerview.setLayoutManager(gridLayoutManager);

        List<Seat> seatList = new ArrayList<>();
        int row = 0;
        int numberSeat = flight.getNumberSeat() + (flight.getNumberSeat() / 7) + 1;

        // Define seat alphabet mappings
        Map<Integer, String> seatAlphabetMap = new HashMap<>();
        seatAlphabetMap.put(0, "A");
        seatAlphabetMap.put(1, "B");
        seatAlphabetMap.put(2, "C");
        seatAlphabetMap.put(3, " ");
        seatAlphabetMap.put(4, "D");
        seatAlphabetMap.put(5, "E");
        seatAlphabetMap.put(6, "F");

        for (int i = 0; i < numberSeat; i++) {
            if (i % 7 == 0) {
                row++;
            }

            if (i % 7 == 3) {
                // Empty middle column for aisles
                seatList.add(new Seat(Seat.SeatStatus.EMPTY, String.valueOf(row)));
            } else {
                String seatName = seatAlphabetMap.get(i % 7) + row;
                Seat.SeatStatus seatStatus = flight.getReservedSeats().contains(seatName)
                        ? Seat.SeatStatus.UNAVAILABLE
                        : Seat.SeatStatus.AVAILABLE;
                seatList.add(new Seat(seatStatus, seatName));
            }
        }

        // Set up adapter and update UI
        SeatAdapter seatAdapter = new SeatAdapter(seatList, this, new SeatAdapter.SelectedSeat() {
            @Override
            public void Return(String selectedName, int num) {


                binding.tvselectedSeat.setText(num + " Seat Selected");
                binding.tvseatName.setText(selectedName);
                DecimalFormat df = new DecimalFormat("#.##");
                price = Double.valueOf(df.format(num * flight.getPrice()));
                SeatActivity.this.num = num; // Correct scope
                binding.priceTxt.setText("$" + price);
            }
        });

        binding.seatRecyclerview.setAdapter(seatAdapter);
        binding.seatRecyclerview.setNestedScrollingEnabled(false);
    }

    private void getIntentExtra() {
        flight = (Flight) getIntent().getSerializableExtra("flight");
    }
}
