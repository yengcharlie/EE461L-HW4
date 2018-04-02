package com.example.charlieyeng.geocode;

import android.app.DownloadManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    int zoomLevel = 15;
    private GoogleMap mMap;
    double currentLat;
    double currentLng;
    String currentzip;
    String currentcountry;

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
        LatLng latLng = new LatLng(lat,lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng,zoom);
        mMap.moveCamera(update);
    }

    public void geoLocate(View v) throws IOException{
        keyboard(v);
        try {
            EditText editText = (EditText) findViewById(R.id.editText1);
            String location = editText.getText().toString();

            Geocoder geocoder = new Geocoder(this);
            List<Address> list = geocoder.getFromLocationName(location, 10);
            if (list.size() == 0) {
                getLocationFromAddress(location, 0); //this is function to find place by landmark/location name id addressList returns nothing
            }
            else {
                if (list.size() > 1) {
                    for (Address ad : list) {
                        String local = ad.getLocality();
                        Toast.makeText(this, local, Toast.LENGTH_LONG).show();//adds the additional addresses if there are any
                    }
                }
                Address add = list.get(0);
                currentzip = add.getLocality();
                currentcountry = add.getCountryCode();

                String locality = add.getLocality();
                Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

                double lat = add.getLatitude();
                double lng = add.getLongitude();
                currentLat = lat;
                currentLng = lng;
                gotoLocation(lat, lng, 10);
            }
        }
        catch(Exception e){
            Toast.makeText(this, "No valid location entered", Toast.LENGTH_LONG).show();
        }
    }

    private void keyboard(View v){
        InputMethodManager input = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        input.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void calculateDistance(View v) throws IOException{
        try {
            keyboard(v);
            EditText editText1 = (EditText) findViewById(R.id.editText1);
            EditText editText2 = (EditText) findViewById(R.id.editText2);

            String location1 = editText1.getText().toString();
            String location2 = editText2.getText().toString();

            Geocoder geocoder = new Geocoder(this);
            List<Address> list1 = geocoder.getFromLocationName(location1, 10);
            List<Address> list2 = geocoder.getFromLocationName(location2, 10);

            Address add1 = list1.get(0);
            Address add2 = list2.get(0);
            String locality1 = add1.getLocality();
            String locality2 = add2.getLocality();
            Toast.makeText(this, locality1, Toast.LENGTH_LONG).show();
            Toast.makeText(this, locality2, Toast.LENGTH_LONG).show();


            double lat1 = currentLat;
            double lng1 = currentLng;

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
        double radius = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2-lat1);  // convert to radians
        double dLon = deg2rad(lon2-lon1);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = radius * c; // Distance in km
        return d;
    }

    public double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }

    public void zoomIn(View v){
        //keyboard(v);
        try {
            double lat = currentLat;
            double lng = currentLng;
            zoomLevel=zoomLevel+1;
            gotoLocation(lat, lng, zoomLevel);
        }
        catch(Exception e){
            Toast.makeText(this, "No valid location entered", Toast.LENGTH_LONG).show();
        }

    }
    public void zoomOut(View v){
        //keyboard(v);
        try {

            double lat = currentLat;
            double lng = currentLng;
            zoomLevel = zoomLevel-1;
            gotoLocation(lat, lng, zoomLevel);
        }
        catch(Exception e){
            Toast.makeText(this, "No valid location entered", Toast.LENGTH_LONG).show();
        }

    }
    public void getLocationFromAddress(String address, final int x) {


        String url1 = "https://maps.googleapis.com/maps/api/geocode/json?address=%22whataburger%22&components=locality:"+currentzip+"|country:"+currentcountry+"&sensor=false";

        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + Uri.encode(address) + "&sensor=false";
        if(x==1) {
            url = url1;
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject location;
                try {
                    // Get JSON Array called "results" and then get the 0th
                    // complete object as JSON
                    if (x == 1) {
                        for (int i = 0; i < response.getJSONArray("results").length(); i++) {
                            location = response.getJSONArray("results").getJSONObject(i).getJSONObject("geometry").getJSONObject("location");

                            // Get the value of the attribute whose name is
                            // "formatted_string"
                            if (location.getDouble("lat") != 0 && location.getDouble("lng") != 0) {
                                LatLng latLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                                currentLat = latLng.latitude;

                                currentLng = latLng.longitude;

                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13);
                                mMap.addMarker(new MarkerOptions().position(latLng).title("Whataburger"));
                                mMap.moveCamera(cameraUpdate);
                            }

                        }
                    } else {
                        location = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");

                        // Get the value of the attribute whose name is
                        // "formatted_string"
                        if (location.getDouble("lat") != 0 && location.getDouble("lng") != 0) {
                            LatLng latLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                            currentLat = latLng.latitude;

                            currentLng = latLng.longitude;

                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13);
                            mMap.moveCamera(cameraUpdate);

                            //Do what you want
                        }
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Not found", Toast.LENGTH_SHORT).show();

                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", error.toString());
            }
        });
        requestQueue.add(req);
    }

    public void onClickWhataburger (View view) {

        getLocationFromAddress("Whataburger", 1);

    }




}
