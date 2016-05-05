package com.stax.naptracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Info extends ActionBarActivity {
    EditText name_text, location_text, destination_text;
    Button find;
    String name, location, destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        // Setting the color of action bar
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D1004")));

        // Getting references to xml objects
        name_text = (EditText) findViewById(R.id.name);
        location_text = (EditText) findViewById(R.id.location);
        destination_text = (EditText) findViewById(R.id.destination);
        find = (Button) findViewById(R.id.find);

        // Listener for find button
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting text from fields
                name = name_text.getText().toString();
                location = location_text.getText().toString();
                destination = destination_text.getText().toString();

                // Check to see that none of the field is vacant
                if(name.equals("") || location.equals("") || destination.equals("")) {
                    Toast.makeText(getApplicationContext(), "Field is vacant", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // Checking network connectivity
                    ConnectivityManager connMgr = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        // Starting new activity and passing the text from fields to it
                        Intent intent = new Intent(getApplicationContext(), Map.class);
                        intent.putExtra("location", location);
                        intent.putExtra("destination", destination);
                        intent.putExtra("name", name);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "No network connection available", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}