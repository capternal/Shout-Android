package com.shoutin.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.shoutin.CustomClasses.CustomSnackBarLayout;
import com.shoutin.R;
import com.shoutin.Utils.ConnectivityBroadcastReceiver;
import com.shoutin.Utils.Constants;
import com.shoutin.app.AppController;
import com.shoutin.main.Adapter.MyPreferencesAdapter;
import com.shoutin.main.Model.MyPreferencesModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PreferencesActivity extends Activity implements ConnectivityBroadcastReceiver.ConnectivityReceiverListener {

    private ListView objListViewPreferences;
    private TextView objTextViewBack;
    private TextView objTextViewSavePreferences;

    private ArrayList<MyPreferencesModel> arrMyPreferencesModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        // CALLES CONNETIVITY CHECK LISTENER
        new AppController().setConnectivityListener(this);
        initializeView();


        try {
            String tag_json_obj = "json_obj_req";
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();

            SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);


            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Constants.MY_PREFRENCES_API, new JSONObject().put("user_id", objSharedPreferences.getString(Constants.USER_ID, "")), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("API RESPONSE : " + response.toString());
                    pDialog.hide();
                    try {
                        JSONObject objJsonObject = new JSONObject(response.toString());

                        arrMyPreferencesModel = new ArrayList<MyPreferencesModel>();
                        if (objJsonObject.getString("result").equals("true")) {
                            JSONArray objJsonArray = new JSONArray(objJsonObject.getString("preferences"));
                            for (int index = 0; index < objJsonArray.length(); index++) {

                                if (objJsonArray.getJSONObject(index).getString("status").equals("A")) {
                                    MyPreferencesModel objMyPreferencesModel = new MyPreferencesModel(
                                            objJsonArray.getJSONObject(index).getString("id"),
                                            objJsonArray.getJSONObject(index).getString("preference_id"),
                                            objJsonArray.getJSONObject(index).getString("title"),
                                            objJsonArray.getJSONObject(index).getString("status"),
                                            true,
                                            objJsonArray.getJSONObject(index).getString("is_checked"),
                                            Constants.HTTP_URL + objJsonArray.getJSONObject(index).getString("image"),
                                            objJsonArray.getJSONObject(index).getString("message"));
                                    arrMyPreferencesModel.add(objMyPreferencesModel);
                                } else {
                                    MyPreferencesModel objMyPreferencesModel = new MyPreferencesModel(
                                            objJsonArray.getJSONObject(index).getString("id"),
                                            objJsonArray.getJSONObject(index).getString("preference_id"),
                                            objJsonArray.getJSONObject(index).getString("title"),
                                            objJsonArray.getJSONObject(index).getString("status"),
                                            false,
                                            objJsonArray.getJSONObject(index).getString("is_checked"),
                                            Constants.HTTP_URL + objJsonArray.getJSONObject(index).getString("image"),
                                            objJsonArray.getJSONObject(index).getString("message"));
                                    arrMyPreferencesModel.add(objMyPreferencesModel);
                                }
                            }
                            objListViewPreferences.setAdapter(new MyPreferencesAdapter(arrMyPreferencesModel, PreferencesActivity.this));
                        }
                    } catch (NullPointerException ne) {
                        ne.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("ERROR : " + error.toString());
                    pDialog.hide();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        // CALLES CONNETIVITY CHECK LISTENER
        new AppController().setConnectivityListener(this);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // CALLES CONNETIVITY CHECK LISTENER
        new AppController().setConnectivityListener(this);

    }

    private void showInternetView(boolean isConnected) {
        System.out.println("CONNECTIVITY LOGIN CHECK STATUS : " + isConnected);
        if (isConnected) {
            CustomSnackBarLayout obj = (CustomSnackBarLayout) findViewById(R.id.relative_internet_check);
            Constants.hideToBottom(obj);
        } else {
            CustomSnackBarLayout obj = (CustomSnackBarLayout) findViewById(R.id.relative_internet_check);
            obj.setVisibility(CustomSnackBarLayout.VISIBLE);
            Constants.show(obj);
        }
    }

    private void initializeView() {
        objListViewPreferences = (ListView) findViewById(R.id.listview_my_preferences);
        objTextViewBack = (TextView) findViewById(R.id.btn_back_my_preferences);
        objTextViewSavePreferences = (TextView) findViewById(R.id.btn_save_my_preferences);
        writeListener();
    }

    private void writeListener() {
        objTextViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent objIntent = new Intent(PreferencesActivity.this, ShoutDefaultActivity.class);
                startActivity(objIntent);
                finish();
                overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
            }
        });

        objTextViewSavePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONArray objJsonArray = new JSONArray();

                System.out.println("ARRAY SIZE" + arrMyPreferencesModel.size());

                for (int index = 0; index < arrMyPreferencesModel.size(); index++) {
                    MyPreferencesModel objMyPreferencesModel = arrMyPreferencesModel.get(index);
                    if (objMyPreferencesModel.getStatus().equals("A")) {
                        try {
                            JSONObject objNewJsonObject = new JSONObject();
                            objNewJsonObject.put("id", objMyPreferencesModel.getId());
                            objNewJsonObject.put("preference_id", objMyPreferencesModel.getPreference_id());
                            objJsonArray.put(objNewJsonObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    String tag_json_obj = "json_obj_req";
                    final ProgressDialog pDialog = new ProgressDialog(PreferencesActivity.this);
                    pDialog.setMessage("Loading...");
                    pDialog.show();

                    SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);

                    JSONObject objJsonObject = new JSONObject();
                    objJsonObject.put("user_id", objSharedPreferences.getString(Constants.USER_ID, ""));
                    objJsonObject.put("preferences", objJsonArray);

                    System.out.println("INPUT JSON : " + objJsonObject);

                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Constants.SAVE_PREFRENCES_API, objJsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("API RESPONSE : " + response.toString());
                            pDialog.hide();
                            try {
                                JSONObject objJsonObject = new JSONObject(response.toString());
                                if (objJsonObject.getString("result").equals("true")) {

                                    // TODO: Use your own attributes to track content views in your app
                                    SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
                                    Answers.getInstance().logCustom(new CustomEvent(Constants.EVENT_CHANGE_PREFERENCE)
                                            .putCustomAttribute("User Id", objSharedPreferences.getString(Constants.USER_ID, ""))
                                            .putCustomAttribute("Username", objSharedPreferences.getString(Constants.USER_NAME, ""))
                                            .putCustomAttribute("Facebook Id", objSharedPreferences.getString(Constants.USER_FACEBOOK_ID, ""))
                                            .putCustomAttribute("Email Id", objSharedPreferences.getString(Constants.PROFILE_EMAIL_ID, ""))
                                    );

                                    Toast.makeText(PreferencesActivity.this, objJsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (NullPointerException ne) {
                                ne.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("ERROR : " + error.toString());
                            pDialog.hide();
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/json");
                            headers.put("Accept", "application/json");
                            return headers;
                        }
                    };
                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent objIntent = new Intent(PreferencesActivity.this, ShoutDefaultActivity.class);
        startActivity(objIntent);
        finish();
        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showInternetView(isConnected);
    }
}
