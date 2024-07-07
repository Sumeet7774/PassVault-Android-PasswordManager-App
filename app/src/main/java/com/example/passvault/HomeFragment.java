package com.example.passvault;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.material.button.MaterialButton;
import android.view.View;

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
        userSavedDataAdapter = new UserSavedDataAdapter(getContext(), arrayList, this::showUserDetails, this::editUserDetails,this::deleteUserDetails);
        recyclerView.setAdapter(userSavedDataAdapter);

        sessionManagement = new SessionManagement(getContext());
        String existingEmailid = sessionManagement.getEmailid();
        retrieveUserId(existingEmailid);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String userId = sessionManagement.getUserId();

        if (userId != null) {
            loadallData(userId);
        } else {
            String existingEmailid = sessionManagement.getEmailid();
            retrieveUserId(existingEmailid);
        }
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
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
            transaction.replace(R.id.user_details_container, userDetailsFragment).addToBackStack(null).commit();

            recyclerView.setVisibility(View.GONE);
            getView().findViewById(R.id.user_details_container).setVisibility(View.VISIBLE);
        } else {
            Log.e("HomeFragment", "Activity is null. Cannot perform fragment transaction.");
        }
    }

    private void editUserDetails(final UserSavedData userSavedData) {
        Intent intent = new Intent(getContext(), UpdateData.class);
        intent.putExtra("username", userSavedData.getUsername());
        intent.putExtra("emailId", userSavedData.getEmailId());
        intent.putExtra("password", userSavedData.getpassword());
        intent.putExtra("serviceType", userSavedData.getServiceType());

        if (getActivity() != null) {
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            Log.e("HomeFragment", "Activity is null. Cannot start UpdateData activity.");
        }
    }

    private void deleteUserDetails(final UserSavedData userSavedData)
    {
        showConfirmDeleteDialog(userSavedData);
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

    private void retrievePasswordIdForDeletion(final String userId, final String username, final String emailId, final String password, final String serviceType) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.retrievePasswordId_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RetrievePasswordId", "Response: " + response);
                        String passwordId = extractPasswordIdFromResponse(response);
                        Log.d("RetrievedPasswordid","Password ID:- " + passwordId);

                        if (passwordId != null) {
                            deleteData(userId, passwordId);
                        } else {
                            Toast.makeText(getContext(), "Failed to retrieve password ID", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RetrievePasswordId", "Error: " + error.toString());
                Toast.makeText(getContext(), "Error retrieving password ID", Toast.LENGTH_SHORT).show();
            }
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

    private void retrieveUserIdForDeletion(final String email_id, final UserSavedData userSavedData) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.retrieveUserId_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("retrieveUserId", "Response: " + response);
                String userId = extractUserIdFromResponse(response);

                if (userId != null) {
                    sessionManagement.setUserId(userId);
                    retrievePasswordIdForDeletion(userId, userSavedData.getUsername(), userSavedData.getEmailId(), userSavedData.getpassword(), userSavedData.getServiceType());
                } else {
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

    private void deleteData(final String user_id, final String password_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.deleteData_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("DeleteData", "Response: " + response);

                String successMessage = extractSuccessMessage(response);

                Log.d("DeleteSuccessMessage","Delete Success Message: " + successMessage);

                if (successMessage != null) {
                    showDataDeletedSuccessDialog();
                    loadallData(user_id);
                } else {
                    Toast.makeText(getContext(), "Failed to delete record", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DeleteData", "Error: " + error.toString());
                Toast.makeText(getContext(), "Error deleting data", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("password_id", password_id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private String extractSuccessMessage(String response) {
        String successMessage = null;
        String pattern = "Record deleted successfully";
        Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = r.matcher(response);

        if (m.find()) {
            successMessage = m.group();
        }

        return successMessage;
    }

    private void showConfirmDeleteDialog(final UserSavedData userSavedData) {
        Dialog confirmDeleteDialog = new Dialog(getContext());
        confirmDeleteDialog.setContentView(R.layout.custom_confirmdelete_dialog_box);

        MaterialButton cancelButton = confirmDeleteDialog.findViewById(R.id.cancelButton_DeleteData_dialogBox);
        MaterialButton deleteButton = confirmDeleteDialog.findViewById(R.id.DeleteButton_dialogBox);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteDialog.dismiss();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteDialog.dismiss();

                String userId = sessionManagement.getUserId();

                if (userId != null) {
                    retrievePasswordIdForDeletion(userId, userSavedData.getUsername(), userSavedData.getEmailId(), userSavedData.getpassword(), userSavedData.getServiceType());
                } else {
                    String existingEmailid = sessionManagement.getEmailid();
                    retrieveUserIdForDeletion(existingEmailid, userSavedData);
                }
            }
        });

        confirmDeleteDialog.show();
    }

    private void showDataDeletedSuccessDialog() {
        Dialog successfulDataDeletedDialogBox = new Dialog(getContext());
        successfulDataDeletedDialogBox.setContentView(R.layout.custom_deletedata_successfully_dialog_box);
        Button dialogBoxOkButton = successfulDataDeletedDialogBox.findViewById(R.id.okButtonDataDeletedSuccesfull_dialogBox);
        dialogBoxOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successfulDataDeletedDialogBox.dismiss();
                if (getActivity() != null) {
                    Intent intent = new Intent(getContext(), HomePage.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    getActivity().finish();
                }
            }
        });
        successfulDataDeletedDialogBox.show();
    }

    public void onDestroyView() {
        super.onDestroyView();
        // Clear animations when view is destroyed to prevent leaks
        recyclerView.setItemAnimator(null);
    }
}