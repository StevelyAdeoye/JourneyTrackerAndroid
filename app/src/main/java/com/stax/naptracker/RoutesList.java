package com.stax.naptracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RoutesList extends ActionBarActivity {
    private ListView lv;
    private List<String> arrivals = new ArrayList<String>();
    private List<String> departures = new ArrayList<String>();
    private List<String> names = new ArrayList<String>();
    String name, location, destination, arrival, departure, transport, options;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routes);

        // Setting the color of action bar
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D1004")));

        // Getting values from previous activity
        location = getIntent().getExtras().getString("location");
        destination = getIntent().getExtras().getString("destination");
        name = getIntent().getExtras().getString("name");

        arrivals = getIntent().getStringArrayListExtra("arrivals");
        departures = getIntent().getStringArrayListExtra("departures");
        names = getIntent().getStringArrayListExtra("names");

        options = "";
        for(int i = 0; i < arrivals.size(); i++)
        {
            options = options + "\n\nDeparture Time: " + departures.get(i) + "\nArrival Time: " + arrivals.get(i) + "\nTransport:-" + names.get(i);
        }

        lv = (ListView) findViewById(R.id.routes_list);
        lv.setAdapter(new EfficientAdapter(this)); // Assigning adapter to listview

        // List view on click listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                // Getting the values on selected list position
                arrival = arrivals.get(position);
                departure = departures.get(position);
                transport = names.get(position);

                // Sending mail with relevant info in a new thread
                new Thread(new Runnable() {

                    public void run() {
                        Mail m = new Mail("naptrackerapp@gmail.com", "easyman1");

                        String[] toArr = {"naptrackerapp@gmail.com"};
                        m.setTo(toArr);
                        m.setFrom("naptrackerapp@gmail.com");
                        m.setSubject("User Selection");
                        m.setBody("Name: " + name +
                                "\nLocation: " + location +
                                "\nDestination: " + destination +
                                "\n\nTransport Options:-" + options +
                                "\n\nOption Selected:-" +
                                "\n\nDeparture Time: " + departure +
                                "\nArrival Time: " + arrival +
                                "\nTransport:-" + transport);
                        try {
                            m.send();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                Toast.makeText(getApplicationContext(), "Route Selected", Toast.LENGTH_LONG).show();
                Intent intent = new Intent (getApplicationContext(), Info.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent); // Going back to start screen for new session
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // List adapter to display the values in the list
    private class EfficientAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public EfficientAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return departures.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item, null);
                holder = new ViewHolder();
                holder.depart = (TextView) convertView.findViewById(R.id.depart);
                holder.arrive = (TextView) convertView.findViewById(R.id.arrive);
                holder.vehicle = (TextView) convertView.findViewById(R.id.vehicle);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.depart.setText(departures.get(position));
            holder.arrive.setText(arrivals.get(position));
            holder.vehicle.setText(names.get(position));

            return convertView;
        }

        class ViewHolder {
            TextView depart;
            TextView arrive;
            TextView vehicle;
        }
    }
}
