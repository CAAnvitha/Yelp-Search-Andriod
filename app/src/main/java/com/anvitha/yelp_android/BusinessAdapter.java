package com.anvitha.yelp_android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.anvitha.yelp_android.domain.Business;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.ViewHolder> {

    private List<Business> businessList;

    // Pass in the contact array into the constructor
    public BusinessAdapter(List<Business> businessList) {
        this.businessList = businessList;
    }

    public void setBusinessList(List<Business> businessList) {
        this.businessList = businessList;
    }

    @Override
    public BusinessAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View businessView = inflater.inflate(R.layout.business_row, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(businessView);

        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(BusinessAdapter.ViewHolder holder, int position) {

        // Get the data model based on position
        Business business = businessList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, BusinessDetails.class);
                intent.putExtra("id", businessList.get(holder.getAdapterPosition()).getId());
                context.startActivity(intent);
            }
        });

        holder.srNoView.setText(Integer.toString(business.getSrNo()));
        holder.nameTextView.setText(business.getName());
        try {
            Picasso.get().load(business.getImageUrl()).into(holder.imageView);
        } catch (Exception e) {
            Log.e("image_error", "failed to load image");
        }
        holder.ratingView.setText(Integer.toString(business.getRating()));
        holder.distanceView.setText(Integer.toString(business.getDistance()));


    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return businessList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView srNoView;
        public TextView nameTextView;
        public TextView ratingView;
        public TextView distanceView;
        public ImageView imageView;
        public View itemView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            this.itemView = itemView;

            srNoView = (TextView) itemView.findViewById(R.id.srNo);
            nameTextView = (TextView) itemView.findViewById(R.id.businessName);
            imageView = (ImageView) itemView.findViewById(R.id.businessImage);
            ratingView = (TextView) itemView.findViewById(R.id.rating);
            distanceView = (TextView) itemView.findViewById(R.id.distance);
        }
    }
}