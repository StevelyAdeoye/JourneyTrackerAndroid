package com.stax.naptracker;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

// Background task for fetching routes
public class DirectionsFetcher extends AsyncTask<GenericUrl, Integer, String> {
    static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport(); // For HTTP connection
    static final JsonFactory JSON_FACTORY = new JacksonFactory(); // Parsing factory
    HttpResponse httpResponse;
    String location, destination, temp1, temp2;
    // Lists for storing respective values
    private List<LatLng> latLngs = new ArrayList<LatLng>();
    private List<String> arrivals = new ArrayList<String>();
    private List<String> departures = new ArrayList<String>();
    private List<String> names = new ArrayList<String>();
    private Routes routes = new Routes();

    public DirectionsFetcher(String loc, String dest) // Getting location and destination
    {
        this.location = loc;
        this.destination = dest;
    }

    public Response delegate = null; // Interface for retrieval of values

    @Override
    protected String doInBackground(GenericUrl... urls) {
        try {
            HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer()
            {
                @Override
                public void initialize(HttpRequest request)
                {
                    request.setParser(new JsonObjectParser(JSON_FACTORY)); // Assigning parser to request
                }
            });

            // Generating url
            GenericUrl url = new GenericUrl("http://maps.googleapis.com/maps/api/directions/json");
            url.put("origin", location);
            url.put("destination", destination);
            url.put("mode", "transit");
            url.put("alternatives", "true");
            url.put("sensor",false);

            // Getting HTTP response
            HttpRequest request = requestFactory.buildGetRequest(url);
            httpResponse = request.execute();

            DirectionsResult directionsResult = httpResponse.parseAs(DirectionsResult.class); // Parsing the response using hierarchy

            // Parsing polyline
            String encodedPoints = directionsResult.routes.get(0).overviewPolyLine.points;
            latLngs = PolyUtil.decode(encodedPoints);

            // Parsing arrival and departure time and vehicle info
            for(int i = 0; i < directionsResult.routes.size(); i++) {
                List<String> buses = new ArrayList<>();
                List<String> subways = new ArrayList<>();
                List<String> trains = new ArrayList<>();
                List<String> trams = new ArrayList<>();
                List<String> rails = new ArrayList<>();
                String route = "";
                arrivals.add(directionsResult.routes.get(i).legs.get(0).arrival_time.text);
                departures.add(directionsResult.routes.get(i).legs.get(0).departure_time.text);
                for(int j = 0; j < directionsResult.routes.get(i).legs.get(0).steps.size(); j++) {
                    if(directionsResult.routes.get(i).legs.get(0).steps.get(j).travel_mode.equals("TRANSIT")) {
                        temp1 = directionsResult.routes.get(i).legs.get(0).steps.get(j).transit_details.line.vehicle.name;
                        temp2 = directionsResult.routes.get(i).legs.get(0).steps.get(j).transit_details.line.short_name;
                        if (temp1.equals("Bus")) {
                            buses.add(temp2);
                        }
                        if (temp1.equals("Subway")) {
                            subways.add(temp2);
                        }
                        if (temp1.equals("Train")) {
                            trains.add(temp2);
                        }
                        if (temp1.equals("Tram")) {
                            trams.add(temp2);
                        }
                        if (temp1.equals("Rail")) {
                            rails.add(temp2);
                        }
                    }
                }
                if(!buses.isEmpty())
                {
                    route = route + "\nBus:";
                    for(int k = 0; k < buses.size()-2; k++)
                    {
                        route = route + " " + buses.get(k) + ",";
                    }
                    route = route + " " + buses.get(buses.size() - 1);
                }
                if(!subways.isEmpty())
                {
                    route = route + "\nSubway:";
                    for(int k = 0; k < subways.size()-2; k++)
                    {
                        route = route + " " + subways.get(k) + ",";
                    }
                    route = route + " " + subways.get(subways.size()-1);
                }
                if(!trains.isEmpty())
                {
                    route = route + "\nTrain:";
                    for(int k = 0; k < trains.size()-2; k++)
                    {
                        route = route + " " + trains.get(k) + ",";
                    }
                    route = route + " " + trains.get(trains.size()-1);
                }
                if(!trams.isEmpty())
                {
                    route = route + "\nTram:";
                    for(int k = 0; k < trams.size()-2; k++)
                    {
                        route = route + " " + trams.get(k) + ",";
                    }
                    route = route + " " + trams.get(trams.size()-1);
                }
                if(!rails.isEmpty())
                {
                    route = route + "\nRail:";
                    for(int k = 0; k < rails.size()-2; k++)
                    {
                        route = route + " " + rails.get(k) + ",";
                    }
                    route = route + " " + rails.get(rails.size()-1);
                }
                names.add(route);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        routes.setPoly(latLngs);
        routes.setArrivals(arrivals);
        routes.setDepartures(departures);
        routes.setNames(names);
        delegate.postResponse(routes); // Exporting the results using routes object
    }

    // Class hierarchy used for parsing the directions API response
    public static class DirectionsResult {
        @Key("routes")
        public List<Route> routes;
    }

    public static class Route {
        @Key("legs")
        public List<Leg> legs;

        @Key("overview_polyline")
        public OverviewPolyLine overviewPolyLine;
    }

    public static class Leg {
        @Key("arrival_time")
        public ArrivalTime arrival_time;

        @Key("departure_time")
        public DepartureTime departure_time;

        @Key("steps")
        public List<Step> steps;
    }

    public static class OverviewPolyLine {
        @Key("points")
        public String points;
    }

    public static class ArrivalTime {
        @Key("text")
        public String text;
    }

    public static class DepartureTime {
        @Key("text")
        public String text;
    }

    public static class Step {
        @Key("transit_details")
        public TransitDetail transit_details;

        @Key("travel_mode")
        public String travel_mode;
    }

    public static class TransitDetail {
        @Key("line")
        public Line line;
    }

    public static class Line {
        @Key("short_name")
        public String short_name;

        @Key("vehicle")
        public Vehicle vehicle;
    }

    public static class Vehicle {
        @Key("name")
        public String name;
    }
}

