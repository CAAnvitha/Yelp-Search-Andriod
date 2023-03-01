package com.anvitha.yelp_android;

import static com.anvitha.yelp_android.Constants.SERVER_URL;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.anvitha.yelp_android.domain.Business;
import com.anvitha.yelp_android.domain.Coordinates;
import com.anvitha.yelp_android.domain.Review;
import com.anvitha.yelp_android.ui.main.DetailsFragment;
import com.anvitha.yelp_android.ui.main.MapFragment;
import com.anvitha.yelp_android.ui.main.ReviewsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.StringRes;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import com.anvitha.yelp_android.ui.main.SectionsPagerAdapter;
import com.anvitha.yelp_android.databinding.ActivityBusinessDetailsBinding;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class BusinessDetails extends AppCompatActivity {

private ActivityBusinessDetailsBinding binding;

    @StringRes
    private static final int[] TAB_TITLES = new int[] {R.string.business_tab_1, R.string.business_tab_2, R.string.business_tab_3};

    private static final String FACEBOOK_URL = "https://www.facebook.com/sharer/sharer.php?u=";
    private static final String TWITTER_URL = "https://twitter.com/intent/tweet?text=";

    public Business businessData;
    private TextView titleView;
    private ImageButton facebookBtn;
    private ImageButton twitterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        String id = extras.getString("id");
        initializeView(id);

        binding = ActivityBusinessDetailsBinding.inflate(getLayoutInflater());
//        setSupportActionBar(binding.toolbar);
        setContentView(binding.getRoot());


        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        Bundle bundle =new Bundle();
        bundle.putSerializable("business",businessData);

        titleView = (TextView) findViewById(R.id.title);

        DetailsFragment detailsFragment = new DetailsFragment();
        MapFragment mapFragment = new MapFragment();
        ReviewsFragment reviewsFragment = new ReviewsFragment();

        sectionsPagerAdapter.addFragment(detailsFragment, TAB_TITLES[0]);
        sectionsPagerAdapter.addFragment(mapFragment, TAB_TITLES[1]);
        sectionsPagerAdapter.addFragment(reviewsFragment, TAB_TITLES[2]);

        ViewPager viewPager = binding.viewPager;
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

//        facebookBtn = (ImageButton) findViewById(R.id.share_facebook);
//        twitterBtn = (ImageButton) findViewById(R.id.share_twitter);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == R.id.share_facebook) {
            String quote = "Check " + businessData.getName() + " on Yelp.";
            String businessUrl = null;
            try { quote = URLEncoder.encode(quote, "UTF-8");
                businessUrl = URLEncoder.encode(businessData.getUrl(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String url = FACEBOOK_URL + businessUrl + "&quote=" + quote;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } else if (item.getItemId() == R.id.share_twitter) {
            String quote = "Check " + businessData.getName() + " on Yelp.";
            String businessUrl = null;
            try { quote = URLEncoder.encode(quote, "UTF-8");
                businessUrl = URLEncoder.encode(businessData.getUrl(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String url = TWITTER_URL + quote + "&url=" + businessUrl;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } else if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


        private void initializeView (String id) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, SERVER_URL.concat("/"+id), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Business business = new Business();
                            business.setId(response.getString("id"));
                            business.setName(response.getString("name"));
                            getSupportActionBar().setTitle(business.getName());

                            if (response.has("is_closed")) {
                                business.setClosed(response.getBoolean("is_closed"));
                            }

                            if (response.has("phone")) {
                                business.setPhone(response.getString("phone"));
                            }
                            if (response.has("price")) {
                                business.setPrice(response.getString("price"));
                            }

                            if (response.has("url")) {
                                business.setUrl(response.getString("url"));
                            }

                            if (response.has("photos")) {
                                JSONArray photosArray = response.getJSONArray("photos");
                                List<String> photos = new ArrayList<>();
                                for (int i = 0; i < photosArray.length(); i++) {
                                    photos.add(photosArray.getString(i));
                                }
                                business.setPhotos(photos);
                            }

                            Coordinates coordinates = new Coordinates();
                            coordinates.setLatitude(response.getJSONObject("coordinates").getDouble("latitude"));
                            coordinates.setLongitude(response.getJSONObject("coordinates").getDouble("longitude"));
                            business.setCoordinates(coordinates);

                            if (response.has("categories")) {
                                JSONArray categoriesArray = response.getJSONArray("categories");
                                List<String> categories = new ArrayList<>();
                                for (int i = 0; i < categoriesArray.length(); i++) {
                                    categories.add(categoriesArray.getString(i));
                                }
                                business.setCategories(categories);
                            }

                            if (response.has("address")) {
                                business.setAddress(response.getString("address"));
                            }

                            JSONArray reviewsArray = response.getJSONArray("reviews");
                            List<Review> reviews = new ArrayList<>();
                            for (int i = 0; i<reviewsArray.length(); i++) {
                                Review review = new Review();
                                review.setRating(reviewsArray.getJSONObject(i).getInt("rating"));
                                review.setText(reviewsArray.getJSONObject(i).getString("text"));
                                review.setName(reviewsArray.getJSONObject(i).getString("user_name"));
                                review.setDate(reviewsArray.getJSONObject(i).getString("time_created").split(" ")[0]);
                                reviews.add(review);
                            }
                            business.setReviews(reviews);
                            businessData = business;

                            pushEvent(business);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
    }

    private void pushEvent(Business business) {
        EventBus.getDefault().post(business);
    }

    private void openShareUrl(String url) {
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}