package com.example.passvault;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HomeFragment extends Fragment {

    SessionManagement sessionManagement;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView noRecordsFound;
    UserSavedDataAdapter userSavedDataAdapter;
    ArrayList<UserSavedData> arrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recycleview);
        progressBar = view.findViewById(R.id.progressBarLoading);
        noRecordsFound = view.findViewById(R.id.norecordsfound_textview);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //userSavedDataAdapter = new UserSavedDataAdapter(HomeFragment.this,arrayList);
        userSavedDataAdapter = new UserSavedDataAdapter(getContext(), arrayList, this::showUserDetails);
        recyclerView.setAdapter(userSavedDataAdapter);

        sessionManagement = new SessionManagement(getContext());
        String existingEmailid = sessionManagement.getEmailid();
        retrieveUserId(existingEmailid);

        return view;
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void showNoRecordsFoundMessage() {
        noRecordsFound.setVisibility(View.VISIBLE);
    }

    private void hideNoRecordsFoundMessage() {
        noRecordsFound.setVisibility(View.GONE);
    }

    private void retrieveUserId(final String email_id) {

        showProgressBar();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.retrieveUserId_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressBar();

                Log.d("retrieveUserId", "Response: " + response);

                String userId = extractUserIdFromResponse(response);

                if (userId != null)
                {
                    sessionManagement.setUserId(userId);
                    String userid = sessionManagement.getUserId();
                    Log.d("HomeFragmentPage", "User Id: "+ userid);
                    loadallData(userId);
                }
                else
                {
                    hideProgressBar();
                    Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error retrieving user_id", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email_id", email_id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private String extractUserIdFromResponse(String response) {
        String pattern = "Connection Successfull\\s*(\\d+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(response);

        if (m.find())
        {
            return m.group(1);
        }

        Log.d("extractUserIdFromResponse", "Failed to extract user ID from response: " + response);
        return null;
    }

    public void loadallData(final String user_id) {
        StringRequest request = new StringRequest(Request.Method.POST, ApiEndpoints.displaySavedData_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressBar();

                Log.d("loadallData", "Full Response: " + response);

                response = response.substring(response.indexOf("["), response.lastIndexOf("]") + 1);

                Log.d("loadallData", "Cleaned Response: " + response);

                try
                {
                    JSONArray array = new JSONArray(response);

                    arrayList.clear();

                    for (int i = 0; i < array.length(); i++)
                    {
                        try
                        {
                            JSONObject object = array.getJSONObject(i);
                            String username = object.getString("username").trim();
                            String emailid = object.getString("email_id").trim();
                            String password = object.getString("password").trim();
                            String serviceType = object.getString("service_type").trim();

                            UserSavedData userSavedData = new UserSavedData(username, emailid, password, serviceType);
                            userSavedData.setUsername(username);
                            userSavedData.setEmailId(emailid);
                            userSavedData.setpassword(password);
                            userSavedData.setServiceType(serviceType);
                            arrayList.add(userSavedData);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    if (arrayList.isEmpty())
                    {
                        showNoRecordsFoundMessage();
                    }
                    else
                    {
                        hideNoRecordsFoundMessage();
                        userSavedDataAdapter.notifyDataSetChanged();
                    }
                    //userSavedDataAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressBar();
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }

    private void showUserDetails(UserSavedData userSavedData) {
        UserDetails userDetailsFragment = new UserDetails();
        Bundle args = new Bundle();

        args.putString("username", userSavedData.getUsername());
        args.putString("emailId", userSavedData.getEmailId());
        args.putString("password", userSavedData.getpassword());
        args.putString("serviceType", userSavedData.getServiceType());
        userDetailsFragment.setArguments(args);

        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.user_details_container, userDetailsFragment)
                    .addToBackStack(null)
                    .commit();

            // Hide the RecyclerView and show the user details container
            recyclerView.setVisibility(View.GONE);
            getView().findViewById(R.id.user_details_container).setVisibility(View.VISIBLE);
        } else {
            Log.e("HomeFragment", "Activity is null. Cannot perform fragment transaction.");
        }
    }


    public void onDestroyView() {
        super.onDestroyView();
        // Clear animations when view is destroyed to prevent leaks
        recyclerView.setItemAnimator(null);
    }
}