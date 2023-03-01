package com.anvitha.yelp_android.ui.main;

import static com.anvitha.yelp_android.Constants.ARG_SECTION_NUMBER;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anvitha.yelp_android.R;
import com.anvitha.yelp_android.domain.Business;
import com.anvitha.yelp_android.domain.Review;
import com.google.android.material.divider.MaterialDivider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewsFragment extends Fragment {

    private Business business;
    
    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView nameView1;
    TextView nameView2;
    TextView nameView3;
    TextView dateView1;
    TextView dateView2;
    TextView dateView3;
    TextView ratingView1;
    TextView ratingView2;
    TextView ratingView3;

    private List<TextView> textViews;
    private List<TextView> nameViews;
    private List<TextView> dateViews;
    private List<TextView> ratingViews;

    public static ReviewsFragment newInstance(int index) {
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reviews, container, false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doThis(Business business) {
        if (business != null) {
            this.business = business;
            initializeView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initializeView() {
        textView1 = (TextView) getActivity().findViewById(R.id.text1);
        textView2 = (TextView) getActivity().findViewById(R.id.text2);
        textView3 = (TextView) getActivity().findViewById(R.id.text3);
        nameView1 = (TextView) getActivity().findViewById(R.id.name1);
        nameView2 = (TextView) getActivity().findViewById(R.id.name2);
        nameView3 = (TextView) getActivity().findViewById(R.id.name3);
        dateView1 = (TextView) getActivity().findViewById(R.id.date1);
        dateView2 = (TextView) getActivity().findViewById(R.id.date2);
        dateView3 = (TextView) getActivity().findViewById(R.id.date3);
        ratingView1 = (TextView) getActivity().findViewById(R.id.rating1);
        ratingView2 = (TextView) getActivity().findViewById(R.id.rating2);
        ratingView3 = (TextView) getActivity().findViewById(R.id.rating3);

        textViews = Arrays.asList(textView1, textView2, textView3);
        ratingViews = Arrays.asList(ratingView1, ratingView2, ratingView3);
        dateViews = Arrays.asList(dateView1, dateView2, dateView3);
        nameViews = Arrays.asList(nameView1, nameView2, nameView3);

        if (this.business != null) {
            if (this.business.getReviews() != null && this.business.getReviews().size() > 0) {
                List<Review> reviews = this.business.getReviews();
                for (int i=0; i<reviews.size(); i++) {
                    Review review = reviews.get(i);
                    textViews.get(i).setText(review.getText());
                    dateViews.get(i).setText(review.getDate());
                    nameViews.get(i).setText(review.getName());
                    ratingViews.get(i).setText("Rating: " + String.valueOf(review.getRating()) + "/5");
                }
                if (reviews.size() < 2) {
                    LinearLayout layout2=(LinearLayout)getActivity().findViewById(R.id.review2);
                    layout2.setVisibility(View.GONE);
                    LinearLayout layout3=(LinearLayout)getActivity().findViewById(R.id.review3);
                    layout3.setVisibility(View.GONE);
                    MaterialDivider divider1 = (MaterialDivider) getActivity().findViewById(R.id.divider1);
                    divider1.setVisibility(View.GONE);
                    MaterialDivider divider2 = (MaterialDivider) getActivity().findViewById(R.id.divider2);
                    divider2.setVisibility(View.GONE);
                } else if (reviews.size() < 3) {
                    LinearLayout layout3=(LinearLayout)getActivity().findViewById(R.id.review3);
                    layout3.setVisibility(View.GONE);
                    MaterialDivider divider2 = (MaterialDivider) getActivity().findViewById(R.id.divider2);
                    divider2.setVisibility(View.GONE);
                }
            } else {
                LinearLayout layout1=(LinearLayout)getActivity().findViewById(R.id.review1);
                layout1.setVisibility(View.GONE);
                LinearLayout layout2=(LinearLayout)getActivity().findViewById(R.id.review2);
                layout2.setVisibility(View.GONE);
                LinearLayout layout3=(LinearLayout)getActivity().findViewById(R.id.review3);
                layout3.setVisibility(View.GONE);
                MaterialDivider divider1 = (MaterialDivider) getActivity().findViewById(R.id.divider1);
                divider1.setVisibility(View.GONE);
                MaterialDivider divider2 = (MaterialDivider) getActivity().findViewById(R.id.divider2);
                divider2.setVisibility(View.GONE);
            }
        }
    }
}