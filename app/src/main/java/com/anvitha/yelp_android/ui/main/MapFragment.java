package com.anvitha.yelp_android.ui.main;

import static com.anvitha.yelp_android.Constants.ARG_SECTION_NUMBER;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anvitha.yelp_android.R;
import com.anvitha.yelp_android.domain.Business;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    private MapView mapView;
    private Business business;
    private GoogleMap googleMapG;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(int index) {
        MapFragment fragment = new MapFragment();
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
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Add a marker in Sydney, Australia,
                // and move the map's camera to the same location.
                MapsInitializer.initialize(getActivity());
                googleMapG = googleMap;
                if (business != null) {
                    LatLng latLng = new LatLng(business.getCoordinates().getLatitude(), business.getCoordinates().getLongitude());
                    Log.i("latlng", business.getCoordinates().toString());

                    googleMapG.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Marker business"));
                    googleMapG.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                    Log.i("mapsydney1", "updating map");
                }
            }
        });
        return view;
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

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    private void initializeView() {
        if (this.business != null && googleMapG != null) {
            Log.i("latlngmap1", "updating map");
            Log.i("latlngmap2", business.getCoordinates().getLatitude() + ", " + business.getCoordinates().getLongitude());
            LatLng latLng = new LatLng(business.getCoordinates().getLatitude(), business.getCoordinates().getLongitude());

            googleMapG.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Marker business"));
            googleMapG.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        }
    }

}