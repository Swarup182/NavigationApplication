package com.example.swarup.navigationapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Spinner spinner;
    String URL="http://express-it.optusnet.com.au/sample.json";
    ArrayList<String> name;
    ArrayList<String> carlist;
    ArrayList<String> trainList;
    ArrayList<String> latitudeList;
    ArrayList<String> longitudeList;

    ArrayList<JSONObject> fromCentralList;

    TextView carTextView;
    TextView trainTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name =new ArrayList<>();
        carlist=new ArrayList<>();
        trainList=new ArrayList<>();
        latitudeList=new ArrayList<>();
        longitudeList=new ArrayList<>();
        fromCentralList = new ArrayList<>();
        spinner=(Spinner)findViewById(R.id.country_Name);
        loadSpinnerData(URL);

        carTextView = findViewById(R.id.car_text_view);
        trainTextView = findViewById(R.id.train_text_view);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String country=   spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
                JSONObject jsonObject = fromCentralList.get(spinner.getSelectedItemPosition());
                try {
                    carTextView.setText("Car :" + jsonObject.getString("car"));

                    String train = jsonObject.getString("train");
                    if(train != null && !train.isEmpty()){
                        trainTextView.setText("Train :" + jsonObject.getString("train"));
                    }
                    else{
                        trainTextView.setText("Train : NA" );
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //carTextView.setText("Car :" + carlist.get(spinner.getSelectedItemPosition()));
                /*trainTextView.setText("Train :" + trainList.get(spinner.getSelectedItemPosition()));*/
                Toast.makeText(getApplicationContext(),country,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });

    }

    private void loadSpinnerData(String url) {
        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    /*JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getInt("success")==1){
                        JSONArray jsonArray=jsonObject.getJSONArray("Name");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            String country=jsonObject1.getString("Country");
                            name.add(country);
                        }
                    }*/

                    JSONArray jsonArray = new JSONArray(response);
                    for(int i =0; i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        MainActivity.this.name.add(name);

                        if(!jsonObject.isNull("fromcentral")){
                            /*String car = jsonObject.getJSONObject("fromcentral").getString("car");

                            if(car != null && !car.isEmpty()){
                                carlist.add(car);
                            }*/
                            JSONObject fromCentral =jsonObject.getJSONObject("fromcentral");
                            fromCentralList.add(fromCentral);

                            /*String train = jsonObject.getJSONObject("fromcentral").getString("train");

                            if(train != null && !train.isEmpty()){
                                trainList.add(train);
                            }*/


                        }


                    }

                    spinner.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, name));
                }catch (JSONException e){e.printStackTrace();}
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
