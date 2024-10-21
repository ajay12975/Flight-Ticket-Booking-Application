package com.example.ticketbookingapp.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ticketbookingapp.Model.Flight;
import com.example.ticketbookingapp.R;
import com.example.ticketbookingapp.databinding.ActivityTicketDetailBinding;

public class TicketDetailActivity extends BaseActivity {

    Button mDialogButton;
    TextView okay_text, cancel_text;

    private ActivityTicketDetailBinding binding;
    private Flight flight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTicketDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDialogButton = findViewById(R.id.downloadTicketBtn);
        Dialog dialog = new Dialog(TicketDetailActivity.this);

        getIntentExtra();
        setVariable();

        // Set onClickListener for the download ticket button

        mDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.alert_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

                okay_text = dialog.findViewById(R.id.okay_text);
                cancel_text = dialog.findViewById(R.id.okay_text);

                okay_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Toast.makeText(TicketDetailActivity.this, "Downloaded successfully", Toast.LENGTH_SHORT).show();
                        Intent intent= new Intent(TicketDetailActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                });

                dialog.show();

            }
        });
    }

    private void setVariable() {
        binding.backBtnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.fromTxt.setText(flight.getFrom());
        binding.fromSmallTxt.setText(flight.getFrom());
        binding.toTxt.setText(flight.getTo());
        binding.toShortText.setText(flight.getToShort());
        binding.fromShortTxt.setText(flight.getFromShort());
        binding.toSmallTxt.setText(flight.getTo());
        binding.dateTxt.setText(flight.getDate());
        binding.timeTxt.setText(flight.getTime());
        binding.classTxt.setText(flight.getClassSeat());
        binding.priceTxt.setText("$" + flight.getPrice());
        binding.airlinesTxt.setText(flight.getAirlineName());
        binding.passengerTxt.setText(flight.getPassenger());
        binding.tv9.setText(flight.getArriveTime());

        Glide.with(TicketDetailActivity.this)
                .load(flight.getAirlineLogo())
                .into(binding.logo);
    }

    private void getIntentExtra() {
        flight = (Flight) getIntent().getSerializableExtra("flight");
    }
}
