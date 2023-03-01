package com.anvitha.yelp_android;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.anvitha.yelp_android.domain.Reservation;

import java.util.List;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ViewHolder> {

    private List<Reservation> reservations;

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView srNoView;
        public TextView nameTextView;
        public TextView dateView;
        public TextView timeView;
        public TextView emailView;
        public View itemView;

        public ViewHolder(View view) {
            super(view);
            this.itemView = view;

            srNoView = (TextView) view.findViewById(R.id.rsSrNo);
            nameTextView = (TextView) view.findViewById(R.id.rsBusinessName);
            dateView = (TextView) view.findViewById(R.id.rsDate);
            timeView = (TextView) view.findViewById(R.id.rsTime);
            emailView = (TextView) view.findViewById(R.id.rsEmail);
        }

    }

    public ReservationsAdapter(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.reservation_row, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Reservation reservation = reservations.get(position);
        viewHolder.srNoView.setText(String.valueOf(position+1));
        viewHolder.nameTextView.setText(reservation.getBusinessName());
        viewHolder.dateView.setText(reservation.getDate());
        viewHolder.timeView.setText(reservation.getTime());
        viewHolder.emailView.setText(reservation.getEmail());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public void removeItem(int position) {
        reservations.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Reservation item, int position) {
        reservations.add(position, item);
        notifyItemInserted(position);
    }
}
