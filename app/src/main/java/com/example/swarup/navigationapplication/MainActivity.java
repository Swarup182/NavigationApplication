package com.example.swarup.navigationapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Spinner spinner;
    String URL = "http://express-it.optusnet.com.au/sample.json";
    ArrayList<String> name;
    ArrayList<String> carlist;
    ArrayList<String> trainList;
    ArrayList<String> latitudeList;
    ArrayList<String> longitudeList;
    ArrayList<JSONObject> locationList;

    ArrayList<JSONObject> fromCentralList;

    TextView carTextView;
    TextView trainTextView;
    Button navigateButton;

    double selectedLatitude;
    double selectedLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = new ArrayList<>();
        carlist = new ArrayList<>();
        trainList = new ArrayList<>();
        latitudeList = new ArrayList<>();
        longitudeList = new ArrayList<>();
        fromCentralList = new ArrayList<>();
        locationList = new ArrayList<>();
        spinner = (Spinner) findViewById(R.id.country_Name);
        loadSpinnerData(URL);

        carTextView = findViewById(R.id.car_text_view);
        trainTextView = findViewById(R.id.train_text_view);
        navigateButton = findViewById(R.id.navigate_button);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String country = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
                JSONObject jsonObject = fromCentralList.get(spinner.getSelectedItemPosition());
                try {
                    carTextView.setText("Car :" + jsonObject.getString("car"));

                    String train = jsonObject.getString("train");
                    if (train != null && !train.isEmpty()) {
                        trainTextView.setText("Train :" + jsonObject.getString("train"));
                    } else {
                        trainTextView.setText("Train : NA");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject jsonObjectLocation = locationList.get(spinner.getSelectedItemPosition());
                try {
                    selectedLatitude = jsonObjectLocation.getDouble("latitude");
                    selectedLongitude = jsonObjectLocation.getDouble("longitude");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Toast.makeText(getApplicationContext(), country, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });

        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("latitude", selectedLatitude );
                intent.putExtra("longitude", selectedLongitude );
                startActivity(intent);
            }
        });

    }

    private void loadSpinnerData(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        MainActivity.this.name.add(name);
                        if (!jsonObject.isNull("fromcentral")) {
                            JSONObject fromCentral = jsonObject.getJSONObject("fromcentral");
                            fromCentralList.add(fromCentral);
                        }
                        if (!jsonObject.isNull("location")) {
                            JSONObject location = jsonObject.getJSONObject("location");
                            locationList.add(location);
                        }
                    }

                    spinner.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, name));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }


}
