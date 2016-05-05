package com.stax.naptracker;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Routes {
    // Lists for storing values returned from Directions API
    List<LatLng> poly = new ArrayList<LatLng>();
    List<String> arrivals = new ArrayList<String>();
    List<String> departures = new ArrayList<String>();
    List<String> names = new ArrayList<String>();

    // Getter and setter functions
    public void setPoly(List<LatLng> list)
    {
        this.poly = list;
    }

    public List<LatLng> getPoly()
    {
        return poly;
    }

    public void setArrivals(List<String> list)
    {
        this.arrivals = list;
    }

    public List<String> getArrivals()
    {
        return arrivals;
    }

    public void setDepartures(List<String> list)
    {
        this.departures = list;
    }

    public List<String> getDepartures()
    {
        return departures;
    }

    public void setNames(List<String> list)
    {
        this.names = list;
    }

    public List<String> getNames()
    {
        return names;
    }
}
