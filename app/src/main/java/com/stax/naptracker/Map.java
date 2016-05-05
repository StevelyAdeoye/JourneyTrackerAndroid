package com.stax.naptracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class Map extends FragmentActivity implements Response // Interface for getting values from the async task
{
    private GoogleMap map;
    Button options;
    String name, location, destination;
    LatLng loc, dest;
    Routes routes;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        // Getting reference to button
        options = (Button) findViewById(R.id.options);

        // Getting reference to map fragment
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager
                .findFragmentById(R.id.map);
        map = mapFragment.getMap();

        map.getUiSettings().setZoomControlsEnabled(true); // Enabling zoom buttons on map

        // Getting fields from previous activity
        location = getIntent().getExtras().getString("location");
        destination = getIntent().getExtras().getString("destination");
        name = getIntent().getExtras().getString("name");

        // Running the async task in background
        DirectionsFetcher df = new DirectionsFetcher(location, destination);
        df.delegate = this;
        df.execute();

        // Listener for options button
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sending data to next activity
                Intent intent = new Intent(getApplicationContext(), RoutesList.class);
                intent.putExtra("location", location);
                intent.putExtra("destination", destination);
                intent.putExtra("name", name);
                intent.putStringArrayListExtra("departures", (ArrayList<String>) routes.getDepartures());
                intent.putStringArrayListExtra("arrivals", (ArrayList<String>) routes.getArrivals());
                intent.putStringArrayListExtra("names", (ArrayList<String>) routes.getNames());
                startActivity(intent);
            }
        });
    }

    // Overriding interface method
    @Override
    public void postResponse(Routes r) {
        routes = r; // Getting routes object from the async task
        if(!routes.getPoly().isEmpty()) {
            loc = routes.getPoly().get(0); // Getting the first point of polyline
            dest = routes.getPoly().get(routes.getPoly().size() - 1); // Getting the last point of polyline
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(loc, 14); // Zooming in on the first point
            map.animateCamera(update);
            drawRoute(routes.getPoly()); // Drawing polyline
            // Adding markers to first and last point
            map.addMarker(new MarkerOptions().position(loc).title(location));
            map.addMarker(new MarkerOptions().position(dest).title(destination));
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No transit routes are available", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), Info.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    // Function for drawing a polyline on the map according to the latitude and longitude points
    private void drawRoute(List<LatLng> route) {
        PolylineOptions line = new PolylineOptions();
        line.width(5);
        line.color(Color.RED);
        for (LatLng latLng : route) {
            line.add(latLng);
        }
        map.addPolyline(line);
    }

    // Overriding back pressed functionality
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, Info.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}