package com.anvitha.yelp_android;

import static com.anvitha.yelp_android.Constants.AUTOCOMPLETE_URL;
import static com.anvitha.yelp_android.Constants.GEO_CODE_URL;
import static com.anvitha.yelp_android.Constants.IP_INFO_URL;
import static com.anvitha.yelp_android.Constants.SERVER_URL;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anvitha.yelp_android.domain.Business;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BusinessSearchFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusinessSearchFormFragment extends Fragment {

    private AutoCompleteTextView keyword;
    private EditText distance;
    private Spinner category;
    private EditText location;
    private CheckBox autoDetectLocation;
    private Button submit;
    private Button clear;
    private RecyclerView businessListView;
    private TextView notFoundView;

    private ArrayAdapter<String> autocompleteAdapter;
    private BusinessAdapter businessAdapter;

    private static Map<String, String> categories;

    static {
        categories = new HashMap<>();
        categories.put("Default", "all");
        categories.put("Arts & Entertainment", "artsAndEntertainment");
        categories.put("Health & Medical", "healthAndMedical");
        categories.put("Hotels & Travel", "hotelsAndTravel");
        categories.put("Food", "food");
        categories.put("Professional Services", "professionalServices");
    }

    public BusinessSearchFormFragment() {
        // Required empty public constructor
    }

    public static BusinessSearchFormFragment newInstance(String param1, String param2) {
        BusinessSearchFormFragment fragment = new BusinessSearchFormFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Creating the instance of ArrayAdapter containing list of fruit names
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_business_search_form, container, false);

        keyword = (AutoCompleteTextView) v.findViewById(R.id.keyword);
        distance = (EditText) v.findViewById(R.id.distance);
        location = (EditText) v.findViewById(R.id.location);
        category = (Spinner) v.findViewById(R.id.category);
        autoDetectLocation = (CheckBox) v.findViewById(R.id.autoDetectLocation);
        submit = (Button) v.findViewById(R.id.submitSearchForm);
        businessListView = (RecyclerView) v.findViewById(R.id.businessList);
        clear = (Button) v.findViewById(R.id.clearSearchForm);
        notFoundView = (TextView) v.findViewById(R.id.no_result_message);

        autocompleteAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.select_dialog_item, new ArrayList<>());
        keyword = (AutoCompleteTextView) v.findViewById(R.id.keyword);
        keyword.setThreshold(1);
        keyword.setAdapter(autocompleteAdapter);

        keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                autoCompleteKeyword(s.toString());
            }
        });

        this.businessAdapter = new BusinessAdapter(new ArrayList<Business>());
        // Attach the adapter to the recyclerview to populate items
        businessListView.setAdapter(this.businessAdapter);
        // Set layout manager to position the items
        businessListView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        businessListView.setHasFixedSize(false);
//        businessListView.setNestedScrollingEnabled(false);

        autoDetectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (autoDetectLocation.isChecked()) {
                    location.setEnabled(false);
                    location.setVisibility(View.INVISIBLE);
                } else {
                    location.setEnabled(true);
                    location.setVisibility(View.VISIBLE);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (keyword.getText().toString().trim().length() == 0) {
                    keyword.setError("This field is required");
                    return;
                }
                if (distance.getText().toString().trim().length() == 0) {
                    distance.setError("This field is required");
                    return;
                }
                Boolean autoDetectLocationValue = autoDetectLocation.isChecked();
                if(!autoDetectLocationValue) {
                    if (location.getText().toString().trim().length() == 0) {
                        distance.setError("This field is required");
                        return;
                    }
                }
                searchBusinesses(view);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyword.setText("");
                distance.setText("");
                location.setText("");
                location.setEnabled(true);
                location.setVisibility(View.VISIBLE);
                autoDetectLocation.setChecked(false);
                notFoundView.setVisibility(View.INVISIBLE);
                businessAdapter.setBusinessList(new ArrayList<>());
                businessAdapter.notifyDataSetChanged();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        category.setAdapter(adapter);

        return v;
    }

    private void autoCompleteKeyword(String text) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, AUTOCOMPLETE_URL.concat(text), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<String> suggestions = new ArrayList<>();
                        try {
                            JSONArray terms = response.getJSONArray("terms");
                            for (int i = 0; i < terms.length(); i++) {
                                JSONObject data = terms.getJSONObject(i);
                                suggestions.add(data.getString("text"));
                            }
                            JSONArray categories = response.getJSONArray("categories");
                            for (int i = 0; i < categories.length(); i++) {
                                JSONObject data = categories.getJSONObject(i);
                                suggestions.add(data.getString("title"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        autocompleteAdapter = new ArrayAdapter<String>
                                (getActivity(), android.R.layout.select_dialog_item, suggestions);
                        keyword.setThreshold(1);
                        keyword.setAdapter(autocompleteAdapter);
                        autocompleteAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
            }
        });
        Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);
    }

    private void searchBusinesses(View view) {
        Boolean autoDetectLocationValue = autoDetectLocation.isChecked();
        if(autoDetectLocationValue) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, IP_INFO_URL, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String[] coordinates = response.getString("loc").split(",");
                                searchBusinesses(coordinates[0], coordinates[1]);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", error.toString());
                }
            });
            Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);
        } else {
            String address = location.getText().toString();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GEO_CODE_URL.concat(address), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String latitude = response.getString("latitude");
                                String longitude = response.getString("longitude");
                                searchBusinesses(latitude, longitude);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", error.toString());
                }
            });
            Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);
        }
    }

    private void searchBusinesses(String latitude, String longitude) {
        String keywordValue = keyword.getText().toString();
        Float radius = Float.parseFloat(distance.getText().toString());
        String categoryValue = category.getSelectedItem().toString();
        String url = SERVER_URL.concat("/search?keyword=").concat(keywordValue)
                .concat("&latitude=").concat(latitude)
                .concat("&longitude=").concat(longitude)
                .concat("&category=").concat(categories.get(categoryValue))
                .concat("&radius=").concat(String.valueOf(Math.round(radius)));
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Business> businessList = new ArrayList<>();
                        for (int i=0; i<response.length(); i++) {
                            try {
                                JSONObject businessJson = response.getJSONObject(i);
                                Business business = new Business();
                                business.setSrNo(i+1);
                                business.setId(businessJson.getString("id"));
                                business.setName(businessJson.getString("name"));
                                business.setDistance((int) Math.round(businessJson.getInt("distance")* 0.00062));
                                business.setRating(businessJson.getInt("rating"));
                                business.setImageUrl(businessJson.getString("image_url"));
                                businessList.add(business);
                                Log.i("response", String.valueOf(i));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        businessAdapter.setBusinessList(businessList);
                        businessAdapter.notifyDataSetChanged();
                        if (businessList.isEmpty()) {
                            notFoundView.setVisibility(View.VISIBLE);
                        } else {
                            notFoundView.setVisibility(View.INVISIBLE);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
            }
        });
        Volley.newRequestQueue(getActivity()).add(jsonArrayRequest);
    }
}