package com.example.passvault;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private UserSavedDataAdapter adapter;
    private ArrayList<UserSavedData> userList;
    private SessionManagement sessionManagement;
    private String userId;
    private TextView noRecordsTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noRecordsTextView = view.findViewById(R.id.norecordsfoundinsearch_textview); // Initialize TextView

        userList = new ArrayList<>();
        sessionManagement = new SessionManagement(getContext());
        userId = sessionManagement.getUserId();

        adapter = new UserSavedDataAdapter(getContext(), userList, this::onItemClick, this::onEditClick, this::onDeleteClick);
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    searchUserData(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    searchUserData(newText);
                } else {
                    userList.clear();
                    adapter.notifyDataSetChanged();
                    noRecordsTextView.setVisibility(View.GONE); // Hide TextView
                }
                return false;
            }
        });

        return view;
    }

    private void searchUserData(String query) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.searchData_url,
                response -> {
                    Log.d("SearchUserData", "Response: " + response);

                    response = response.substring(response.indexOf("["), response.lastIndexOf("]") + 1);

                    Log.d("SearchData", "Cleaned Response: " + response);

                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        userList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String username = jsonObject.getString("username");
                            String emailId = jsonObject.getString("email_id");
                            String password = jsonObject.getString("password");
                            String serviceType = jsonObject.getString("service_type");

                            userList.add(new UserSavedData(username, emailId, password, serviceType));
                        }
                        adapter.notifyDataSetChanged();

                        if (userList.isEmpty()) {
                            noRecordsTextView.setVisibility(View.VISIBLE); // Show TextView
                        } else {
                            noRecordsTextView.setVisibility(View.GONE); // Hide TextView
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("SearchUserData", "JSON Parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("SearchUserData", "Error: " + error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("search_query", query);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void onItemClick(UserSavedData userSavedData) {
        UserDetails userDetailsFragment = new UserDetails();
        Bundle args = new Bundle();
        args.putString("username", userSavedData.getUsername());
        args.putString("emailId", userSavedData.getEmailId());
        args.putString("password", userSavedData.getpassword());
        args.putString("serviceType", userSavedData.getServiceType());
        userDetailsFragment.setArguments(args);

        if (getActivity() != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
            transaction.replace(R.id.user_details_container, userDetailsFragment).addToBackStack(null).commit();

            recyclerView.setVisibility(View.GONE);
            getView().findViewById(R.id.user_details_container).setVisibility(View.VISIBLE);
        } else {
            Log.e("SearchFragment", "Activity is null. Cannot perform fragment transaction.");
        }
    }

    private void onEditClick(UserSavedData userSavedData) {
        Intent intent = new Intent(getContext(), UpdateData.class);
        intent.putExtra("username", userSavedData.getUsername());
        intent.putExtra("emailId", userSavedData.getEmailId());
        intent.putExtra("password", userSavedData.getpassword());
        intent.putExtra("serviceType", userSavedData.getServiceType());

        if (getActivity() != null) {
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            Log.e("SearchFragment", "Activity is null. Cannot start UpdateData activity.");
        }
    }

    private void onDeleteClick(UserSavedData userSavedData) {
        showConfirmDeleteDialog(userSavedData);
    }

    private void showConfirmDeleteDialog(final UserSavedData userSavedData) {
        Dialog confirmDeleteDialog = new Dialog(getContext());
        confirmDeleteDialog.setContentView(R.layout.custom_confirmdelete_dialog_box);

        MaterialButton cancelButton = confirmDeleteDialog.findViewById(R.id.cancelButton_DeleteData_dialogBox);
        MaterialButton deleteButton = confirmDeleteDialog.findViewById(R.id.DeleteButton_dialogBox);

        cancelButton.setOnClickListener(v -> confirmDeleteDialog.dismiss());

        deleteButton.setOnClickListener(v -> {
            confirmDeleteDialog.dismiss();
            String userId = sessionManagement.getUserId();

            if (userId != null) {
                retrievePasswordIdForDeletion(userId, userSavedData.getUsername(), userSavedData.getEmailId(), userSavedData.getpassword(), userSavedData.getServiceType());
            } else {
                String existingEmailid = sessionManagement.getEmailid();
                retrieveUserIdForDeletion(existingEmailid, userSavedData);
            }
        });

        confirmDeleteDialog.show();
    }

    private void retrievePasswordIdForDeletion(final String userId, final String username, final String emailId, final String password, final String serviceType) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.retrievePasswordId_url,
                response -> {
                    Log.d("RetrievePasswordId", "Response: " + response);
                    String passwordId = extractPasswordIdFromResponse(response);
                    Log.d("RetrievedPasswordid", "Password ID: " + passwordId);

                    if (passwordId != null) {
                        deleteData(userId, passwordId);
                    } else {
                        Toast.makeText(getContext(), "Failed to retrieve password ID", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            Log.e("RetrievePasswordId", "Error: " + error.toString());
            Toast.makeText(getContext(), "Error retrieving password ID", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("username", username);
                params.put("email_id", emailId);
                params.put("password", password);
                params.put("service_type", serviceType);
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

    private String extractPasswordIdFromResponse(String response) {
        String pattern = "Connection Successfull\\s*(\\d+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(response);

        if (m.find()) {
            return m.group(1);
        }

        Log.d("extractPasswordIdFromResponse", "Failed to extract password ID from response: " + response);
        return null;
    }

    private void retrieveUserIdForDeletion(final String email_id, final UserSavedData userSavedData) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.retrieveUserId_url, response -> {
            Log.d("retrieveUserId", "Response: " + response);
            String userId = extractUserIdFromResponse(response);

            if (userId != null) {
                sessionManagement.setUserId(userId);
                retrievePasswordIdForDeletion(userId, userSavedData.getUsername(), userSavedData.getEmailId(), userSavedData.getpassword(), userSavedData.getServiceType());
            } else {
                Toast.makeText(getContext(), "Failed to retrieve user ID", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Log.e("retrieveUserId", "Error: " + error.toString());
            Toast.makeText(getContext(), "Error retrieving user ID", Toast.LENGTH_SHORT).show();
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

    private void deleteData(final String userId, final String passwordId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.deleteData_url,
                response -> {
                    Log.d("DeleteData", "Response: " + response);
                    Toast.makeText(getContext(), "Data deleted successfully", Toast.LENGTH_SHORT).show();
                    refreshSearchResults();
                },
                error -> {
                    Log.e("DeleteData", "Error: " + error.toString());
                    Toast.makeText(getContext(), "Error deleting data", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("password_id", passwordId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void refreshSearchResults() {
        String currentQuery = searchView.getQuery().toString();
        if (!TextUtils.isEmpty(currentQuery)) {
            searchUserData(currentQuery);
        } else {
            userList.clear();
            adapter.notifyDataSetChanged();
        }
    }
}
