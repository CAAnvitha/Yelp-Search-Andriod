package com.anvitha.yelp_android;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.anvitha.yelp_android.domain.Reservation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationDialog extends DialogFragment {

    private List<Reservation> reservationList;
    private EditText emailInput;
    private EditText dateInput;
    private EditText timeInput;
    private String businessName;
    private String businessId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reservation_dialog, container, false);
    }

    private void saveData() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();

        if (sharedPref.contains("reservations")) {
            String json = sharedPref.getString("reservations", "");
            Type type = new TypeToken<ArrayList<Reservation>>() {}.getType();
            reservationList = gson.fromJson(json, type);
            Log.i("reservation1", "here 2");
        } else {
            reservationList = new ArrayList<>();
            Log.i("reservation1", "here 3");
        }

        Reservation reservation = new Reservation();
        reservation.setBusinessId(businessId);
        reservation.setBusinessName(businessName);

        String email = emailInput.getText().toString();
        if (!isEmailValid(email.trim())) {
            Toast.makeText(getActivity(), "InValid Email Address.", Toast.LENGTH_SHORT).show();
            return;
        }

        String time = timeInput.getText().toString();
        List<Integer> data = Arrays.stream(time.split(":")).map(val -> Integer.parseInt(val)).collect(Collectors.toList());
        int hour = data.get(0);
        int min = data.get(1);
        if (hour < 10 || (hour > 17 || (hour == 17 && min > 0))) {
            Toast.makeText(getActivity(), "Time should be between 10AM and 5PM.", Toast.LENGTH_SHORT).show();
            return;
        }

        reservation.setEmail(email);
        reservation.setDate(dateInput.getText().toString());
        reservation.setTime(time);

        for (int i=0; i < reservationList.size(); i++) {
            if (reservationList.get(i).getBusinessId().equals(businessId)) {
                reservationList.remove(i);
                break;
            }
        }
        reservationList.add(reservation);

        SharedPreferences.Editor editor = sharedPref.edit();
        String updatedJson = gson.toJson(reservationList);
        editor.putString("reservations", updatedJson);
        editor.commit();
        Log.i("chekc", updatedJson);

        Toast.makeText(getActivity(), "Reservation Booked ", Toast.LENGTH_SHORT).show();

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.reservation_dialog, null);
        Bundle bundle = getArguments();

        businessName = bundle.getString("businessName","");
        businessId = bundle.getString("businessId","");

        TextView nameView = (TextView) v.findViewById(R.id.businessNameTitle);
        nameView.setText(businessName);

        emailInput = (EditText) v.findViewById(R.id.email);
        dateInput = (EditText) v.findViewById(R.id.date);
        timeInput = (EditText) v.findViewById(R.id.time);

        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                dateInput.setText((monthOfYear + 1) + "-" +  dayOfMonth  + "-" + year);

                            }
                        },
                        year, month, day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        timeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();

                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                timeInput.setText(hourOfDay + ":" + minute);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        builder.setView(v)
                // Add action buttons
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        saveData();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        LoginDialogFragment.this.getDialog().cancel();
                        Log.i("dialog", "cancelled");
                    }
                });
        return builder.create();
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}