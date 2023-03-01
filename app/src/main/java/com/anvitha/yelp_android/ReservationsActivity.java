package com.anvitha.yelp_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.anvitha.yelp_android.domain.Business;
import com.anvitha.yelp_android.domain.Reservation;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReservationsActivity extends AppCompatActivity {

    RecyclerView reservationListView;
    ReservationsAdapter reservationsAdapter;
    TextView noBookingView;

    ConstraintLayout constraintLayout;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        noBookingView = (TextView) findViewById(R.id.no_bookings_message);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(
                this);
        Gson gson = new Gson();
        String json = sharedPref.getString("reservations", null);

        Type type = new TypeToken<ArrayList<Reservation>>() {}.getType();

        List<Reservation> reservationList = gson.fromJson(json, type);

        if (reservationList == null) {
            reservationList = new ArrayList<>();
        }

        if (!reservationList.isEmpty()) {
            noBookingView.setVisibility(View.GONE);
        } else {
            noBookingView.setVisibility(View.VISIBLE);
        }

        reservationListView = (RecyclerView) findViewById(R.id.reservationsList);

        this.reservationsAdapter = new ReservationsAdapter(reservationList);
        // Attach the adapter to the recyclerview to populate items
        reservationListView.setAdapter(this.reservationsAdapter);
        // Set layout manager to position the items
        reservationListView.setLayoutManager(new LinearLayoutManager(this));

        constraintLayout = (ConstraintLayout) findViewById(R.id.reservation_activity);
        enableSwipeToDeleteAndUndo();

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteReservationCallback swipeToDeleteCallback = new SwipeToDeleteReservationCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final Reservation item = reservationsAdapter.getReservations().get(position);

//                reservationsAdapter.removeItem(position);
                removeItemFromSharedPreferences(position);


                Snackbar snackbar = Snackbar
                        .make(constraintLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        reservationsAdapter.restoreItem(item, position);
                        reservationListView.scrollToPosition(position);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(reservationListView);
    }

    private void removeItemFromSharedPreferences(int position) {
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = sharedPref.getString("reservations", null);

        Type type = new TypeToken<ArrayList<Reservation>>() {}.getType();

        List<Reservation> reservationList = gson.fromJson(json, type);

        reservationList.remove(position);
        if (!reservationList.isEmpty()) {
            noBookingView.setVisibility(View.GONE);
        } else {
            noBookingView.setVisibility(View.VISIBLE);
        }
        String updatedJson = gson.toJson(reservationList);
        editor.putString("reservations", updatedJson);
        editor.commit();
        reservationsAdapter.setReservations(reservationList);
        reservationsAdapter.notifyDataSetChanged();
    }

}