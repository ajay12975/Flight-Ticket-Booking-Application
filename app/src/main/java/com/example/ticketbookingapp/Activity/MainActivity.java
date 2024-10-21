package com.example.ticketbookingapp.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.ticketbookingapp.Model.Location;
import com.example.ticketbookingapp.R;
import com.example.ticketbookingapp.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private int adultPassenger= 1, childPassenger= 1;
    private SimpleDateFormat dateFormat= new SimpleDateFormat("d MMM, yyyy", Locale.ENGLISH);
    private Calendar calendar= Calendar.getInstance();

    public  static  int seat=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initLocation();
        initPassengers();
        initClassSeat();
        initDatePickup();
        setVariable();

    }

    private void setVariable() {
        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,SearchActivity.class);
                intent.putExtra("from",((Location)binding.fromSp.getSelectedItem()).getName());
                intent.putExtra("to",((Location)binding.toSp.getSelectedItem()).getName());
                intent.putExtra("date",binding.departurerdateTxt.getText().toString());
                intent.putExtra("numPassenger",adultPassenger+childPassenger);
                startActivity(intent);
            }
        });
    }

    private void initDatePickup() {
        Calendar calendarToday= Calendar.getInstance();
        String currentDate= dateFormat.format(calendarToday.getTime());
        binding.departurerdateTxt.setText(currentDate);

        Calendar calendarTomorrow= Calendar.getInstance();
        calendarTomorrow.add(Calendar.DAY_OF_YEAR,1);
        String tomorrowDate= dateFormat.format(calendarTomorrow.getTime());
        binding.returnDateTxt.setText(tomorrowDate);

        binding.departurerdateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(binding.departurerdateTxt);
            }
        });

        binding.returnDateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(binding.returnDateTxt);
            }
        });
    }

    private void initClassSeat() {

        binding.progressClass.setVisibility(View.VISIBLE);
        ArrayList<String> list= new ArrayList<>();
        list.add("Business Class");
        list.add("First Class");
        list.add("Economy Class");

        ArrayAdapter<String> adapter= new ArrayAdapter<>(MainActivity.this, R.layout.sp_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.classSp.setAdapter(adapter);
        binding.progressClass.setVisibility(View.GONE);
    }

    private void initPassengers() {
        binding.plusAdultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adultPassenger++;
                seat=adultPassenger;
                binding.adultTxt.setText(adultPassenger+" Adult");
            }
        });

        binding.minusAdultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adultPassenger>1){
                    adultPassenger--;
                    seat=adultPassenger;
                    binding.adultTxt.setText(adultPassenger+" Adult");
                }
            }
        });

        binding.plusChildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childPassenger++;
                binding.childTxt.setText(childPassenger+" Child");
            }
        });

        binding.minusChildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(childPassenger>0){
                    childPassenger--;
                    binding.childTxt.setText(childPassenger+" Child");
                }
            }
        });
    }

    private void initLocation() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        DatabaseReference myRef= database.getReference("Locations");
        ArrayList<Location> list= new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot issue:snapshot.getChildren()){
                        list.add(issue.getValue(Location.class));
                    }
                    ArrayAdapter<Location> adapter= new ArrayAdapter<>(MainActivity.this,R.layout.sp_item,list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.fromSp.setAdapter(adapter);
                    binding.toSp.setAdapter(adapter);
                    binding.fromSp.setSelection(1);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.progressBar2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDatePickerDialog(TextView textView) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            calendar.set(selectedYear, selectedMonth, selectedDay);
            String formattedDate = dateFormat.format(calendar.getTime());
            textView.setText(formattedDate);

        },year,month,day);
        datePickerDialog.show();
    }
}