package com.android.shopifyinternmobileproblem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String URL = "https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";
    TextView spending;
    TextView amount_item;
    EditText spending_et;
    EditText item_et;
    Button spending_btn;
    Button item_btn;
    RequestQueue rq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rq = Volley.newRequestQueue(this);
        spending = (TextView) findViewById(R.id.spending);
        amount_item = (TextView) findViewById(R.id.amount_items);
        spending_et = (EditText) findViewById(R.id.spending_et);
        item_et = (EditText) findViewById(R.id.item_et);
        spending_btn = (Button) findViewById(R.id.spending_btn);
        item_btn = (Button) findViewById(R.id.item_btn);
        spending_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestForSpending();
            }
        });
        item_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestForItem();
            }
        });

    }

    public void sendRequestForSpending() {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            double totalSpentbyNB = 0;
                            JSONArray orders = response.getJSONArray("orders");
                            for(int i = 0; i < orders.length(); i++) {
                                JSONObject order = orders.getJSONObject(i);
                                if (order.has("billing_address")) {
                                    JSONObject billing_address = order.getJSONObject("billing_address");
                                    String name = billing_address.getString("name");
                                    if (name.equals(spending_et.getText().toString())) {
                                        totalSpentbyNB += order.getDouble("total_price");
                                    }
                                }
                            }

                            spending.setText(totalSpentbyNB + " CAD");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        spending.setText("It didn't work");
                    }
                });
        rq.add(jsObjRequest);
    }

    public void sendRequestForItem() {
        final String input_item = item_et.getText().toString();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int quantity = 0;
                            JSONArray orders = response.getJSONArray("orders");
                            for(int i = 0; i < orders.length(); i++) {
                                JSONObject order = orders.getJSONObject(i);

                                JSONArray line_items = order.getJSONArray("line_items");
                                for(int j = 0; j < line_items.length(); j++) {
                                    JSONObject item = line_items.getJSONObject(j);
                                    String title = item.getString("title");
                                    if (title.equals(input_item)) {
                                        quantity += item.getInt("quantity");
                                    }
                                }
                            }

                            amount_item.setText( "" + quantity);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        spending.setText("It didn't work");
                    }
                });
        rq.add(jsObjRequest);
    }
}
