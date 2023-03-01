package com.anvitha.yelp_android.ui.main;

import static com.anvitha.yelp_android.Constants.ARG_SECTION_NUMBER;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anvitha.yelp_android.BusinessDetails;
import com.anvitha.yelp_android.R;
import com.anvitha.yelp_android.ReservationDialog;
import com.anvitha.yelp_android.domain.Business;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {

    private Business business;

    public DetailsFragment() {
        // Required empty public constructor
    }
    public static DetailsFragment newInstance(int index) {
        DetailsFragment fragment = new DetailsFragment();
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
        return inflater.inflate(R.layout.fragment_details, container, false);
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
        if (this.business != null) {

            LinearLayout detailsLayout = (LinearLayout) getActivity().findViewById(R.id.details_layout);
            detailsLayout.setVisibility(View.VISIBLE);

            TextView addressView = (TextView) getActivity().findViewById(R.id.address);
            addressView.setText(business.getAddress());
            TextView priceView = (TextView) getActivity().findViewById(R.id.price);
            priceView.setText(business.getPrice());
            TextView phoneView = (TextView) getActivity().findViewById(R.id.phone);
            phoneView.setText(business.getPhone());
            TextView statusView = (TextView) getActivity().findViewById(R.id.status);
            if (business.isClosed()) {
                statusView.setText("Closed");
                statusView.setTextColor(Color.RED);
            } else {
                statusView.setText("Open Now");
                statusView.setTextColor(Color.GREEN);
            }

            TextView categoryView = (TextView) getActivity().findViewById(R.id.categories);
            categoryView.setText(business.getCategories().stream().collect(Collectors.joining(" | ")));
            TextView urlView = (TextView) getActivity().findViewById(R.id.url);
            urlView.setMovementMethod(LinkMovementMethod.getInstance());
            String url = "<a href='"+ business.getUrl() + "'>Business Link</a>";
            urlView.setText(Html.fromHtml(url, Html.FROM_HTML_MODE_COMPACT));

            LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.image_row);
            layout.setPadding(50, 50, 50, 50);
            if (this.business.getPhotos() != null) {
                for (int i = 0; i < this.business.getPhotos().size(); i++) {
                    ImageView imageView = new ImageView(getActivity());
                    imageView.setId(i);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setPadding(50, 50, 50, 50);
                    Picasso.get().load(this.business.getPhotos().get(i)).into(imageView);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    layout.addView(imageView);
                }
            }

            Button reserveButton = (Button) getActivity().findViewById(R.id.reserveNow);
            reserveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ReservationDialog reservationDialog = new ReservationDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("businessName", business.getName());
                    bundle.putString("businessId", business.getId());
                    reservationDialog.setArguments(bundle);

                    reservationDialog.show(getChildFragmentManager(), "ReservationDialogFragment");
                }
            });
        }
    }
}