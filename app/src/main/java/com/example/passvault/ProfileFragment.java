package com.example.passvault;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProfileFragment extends Fragment {

    private SessionManagement sessionManagement;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        CardView card_AboutUs = view.findViewById(R.id.cardAboutUs);
        CardView card_Help = view.findViewById(R.id.cardHelp);
        MaterialButton logout_button = view.findViewById(R.id.logout_btn);

        sessionManagement = new SessionManagement(requireContext());

        card_AboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aboutUsIntent = new Intent(requireContext(), AboutUsPage.class);
                startActivity(aboutUsIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        card_Help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent helpIntent = new Intent(requireContext(), AboutUsPage.class);
                startActivity(helpIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmLogoutDialogBox(); // Show confirmation dialog for logout
            }
        });

        return view;
    }

    private void showConfirmLogoutDialogBox() {

        Dialog confirmLogoutDialog = new Dialog(requireContext());
        confirmLogoutDialog.setContentView(R.layout.custom_confirmlogout_dialog_box);

        MaterialButton logoutButton = confirmLogoutDialog.findViewById(R.id.logoutButton_dialogBox);
        MaterialButton cancelButton = confirmLogoutDialog.findViewById(R.id.cancelButton_dialogBox);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManagement.logout();
                Intent intent = new Intent(requireContext(), LogoutSplashScreen.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                requireActivity().finish();
                confirmLogoutDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmLogoutDialog.dismiss(); // Dismiss the dialog
            }
        });

        confirmLogoutDialog.show();
    }
}
