package edu.umd.cschroe2.event_garden;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Christine Schroeder on 4/8/2016.
 */
public class Map_Fragment extends Fragment{

    MapView mapView;
    GoogleMap map;
    Geocoder coder;
    ArrayList<EventMarker> eventMarkers = new ArrayList<EventMarker>();

    @Override
    public void onCreate(Bundle saved){
        super.onCreate(saved);
        setRetainInstance(true);
        // Setup coder, which is used to get Lat/Long for addresses.
        coder = new Geocoder(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Note: This method is called every time the map tab is switched to.

        View v = inflater.inflate(R.layout.map, container, false);



        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        //map.getUiSettings().setMyLocationButtonEnabled(false);
        //map.setMyLocationEnabled(true);
        for (EventMarker eventMarker : eventMarkers){
            map.addMarker(eventMarker.markerOptions);
        }

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }


        return v;
    }

    public void addMarker(Event event){
        // http://stackoverflow.com/questions/17835426/get-latitude-longitude-from-address-in-android
        try {
            // TODO Prompt user to select correct location if multiple locations are found.

            List<Address> possibleAddresses = coder.getFromLocationName(event.location,1); // TODO throw this onto another thread, it seems like it can sometimes be work intensive.

            if (!possibleAddresses.isEmpty()) {
                double latitude = possibleAddresses.get(0).getLatitude();
                double longitude = possibleAddresses.get(0).getLongitude();

                // Create event marker and add it to the list of markers, as well as add it to the map.
                EventMarker newMarker = new EventMarker(event,new MarkerOptions().position(new LatLng(latitude, longitude)).title(event.event_name));
                eventMarkers.add(newMarker);
                map.addMarker(newMarker.markerOptions);

                // Zoom in on the created event.
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10);
                map.animateCamera(cameraUpdate);
            } else {
                Log.d(this.getClass().getName().toString(), "Could not find address");
                Toast.makeText(this.getContext(), "Could not find address", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            // TODO Handle case where we do not have internet
            Toast.makeText(this.getContext(),"Could not lookup address. Does this app have internet access?",Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * TODO Replace TabHost? We just need the fragment to call "show()/hide()" instead of "replace()"
     * This class is a work around for a problem with how the mapview is being handled by the tabhost.
     * as of 4/18, the tab host calls "destroy" on the map fragment when it swaps away, meaning that all markers
     * on the map view are deleted. Thus, every time a user swaps to the map view, it is cleared of all markers.
     *
     * This class stores an event and a MarkerOptions so that they can be restored when the map tab is opened again.
     */
    private class EventMarker {
        Event event;
        MarkerOptions markerOptions;
        public EventMarker(Event event, MarkerOptions markerOptions) {
            this.event = event;
            this.markerOptions = markerOptions;
        }
    }
}
