package com.example.charlieyeng.geocode;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    int zoomLevel = 15;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void gotoLocation(double lat, double lng, float zoom){
        LatLng ll = new LatLng(lat,lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,zoom);
        mMap.moveCamera(update);
    }

    public void geoLocate(View v) throws IOException{
        hideSoftKeyboard(v);
        try {
            EditText et = (EditText) findViewById(R.id.editText1);
            String location = et.getText().toString();

            Geocoder gc = new Geocoder(this);
            List<Address> list = gc.getFromLocationName(location, 10);
            if (list.size() > 1) {
                for (Address ad : list) {
                    String local = ad.getLocality();
                    Toast.makeText(this, local, Toast.LENGTH_LONG).show();
                }
            }
            Address add = list.get(0);
            String locality = add.getLocality();
            Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

            double lat = add.getLatitude();
            double lng = add.getLongitude();

            gotoLocation(lat, lng, 10);
        }
        catch(Exception e){
            Toast.makeText(this, "No valid location entered", Toast.LENGTH_LONG).show();
        }
    }

    private void hideSoftKeyboard(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void calculateDistance(View v) throws IOException{
        try {
            hideSoftKeyboard(v);
            EditText et1 = (EditText) findViewById(R.id.editText1);
            EditText et2 = (EditText) findViewById(R.id.editText2);

            String location1 = et1.getText().toString();
            String location2 = et2.getText().toString();

            Geocoder gc = new Geocoder(this);
            List<Address> list1 = gc.getFromLocationName(location1, 10);
            List<Address> list2 = gc.getFromLocationName(location2, 10);

            Address add1 = list1.get(0);
            Address add2 = list2.get(0);
            String locality1 = add1.getLocality();
            String locality2 = add2.getLocality();
            Toast.makeText(this, locality1, Toast.LENGTH_LONG).show();
            Toast.makeText(this, locality2, Toast.LENGTH_LONG).show();


            double lat1 = add1.getLatitude();
            double lng1 = add1.getLongitude();

            double lat2 = add2.getLatitude();
            double lng2 = add2.getLongitude();

            double distance = getDistanceFrom(lat1, lng1, lat2, lng2);

            String distanceString = Double.toString(distance);
            Toast.makeText(this, "The distance from " + locality1 + " to " + locality2 + " is " + distanceString + "km", Toast.LENGTH_LONG).show();
            gotoLocation(lat1, lng1, 10);
        }
        catch(Exception e){
            Toast.makeText(this, "Missing an input", Toast.LENGTH_LONG).show();
        }

    }

    public double getDistanceFrom( double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2-lat1);  // deg2rad below
        double dLon = deg2rad(lon2-lon1);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in km
        return d;
    }

    public double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }

    public void zoomIn(View v){
        hideSoftKeyboard(v);
        try {
            EditText et = (EditText) findViewById(R.id.editText1);
            String location = et.getText().toString();

            Geocoder gc = new Geocoder(this);
            List<Address> list = gc.getFromLocationName(location, 10);
            if (list.size() > 1) {
                for (Address ad : list) {
                    String local = ad.getLocality();
                    Toast.makeText(this, local, Toast.LENGTH_LONG).show();
                }
            }
            Address add = list.get(0);
            String locality = add.getLocality();
            Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

            double lat = add.getLatitude();
            double lng = add.getLongitude();
            zoomLevel=zoomLevel+1;
            gotoLocation(lat, lng, zoomLevel);
        }
        catch(Exception e){
            Toast.makeText(this, "No valid location entered", Toast.LENGTH_LONG).show();
        }

    }
    public void zoomOut(View v){
        hideSoftKeyboard(v);
        try {
            EditText et = (EditText) findViewById(R.id.editText1);
            String location = et.getText().toString();

            Geocoder gc = new Geocoder(this);
            List<Address> list = gc.getFromLocationName(location, 10);
            if (list.size() > 1) {
                for (Address ad : list) {
                    String local = ad.getLocality();
                    Toast.makeText(this, local, Toast.LENGTH_LONG).show();
                }
            }
            Address add = list.get(0);
            String locality = add.getLocality();
            Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

            double lat = add.getLatitude();
            double lng = add.getLongitude();
            zoomLevel = zoomLevel-1;
            gotoLocation(lat, lng, zoomLevel);
        }
        catch(Exception e){
            Toast.makeText(this, "No valid location entered", Toast.LENGTH_LONG).show();
        }

    }
}
