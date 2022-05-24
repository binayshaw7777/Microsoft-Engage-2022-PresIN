package com.geekym.face_recognition_engage.HomeFragments.Settings;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.geekym.face_recognition_engage.Authentication.SignIn_Activity;
import com.geekym.face_recognition_engage.R;
import com.google.firebase.auth.FirebaseAuth;

public class settings_Fragment extends Fragment {

    ImageButton Logout;
    TextView Name;
    CardView PrivacyPolicy, Profile, AccountSettings;
    ImageView accountIncompleteIndicator;

    @SuppressLint({"SimpleDateFormat", "SetTextI18n", "UseCompatLoadingForDrawables", "ObsoleteSdkInt"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Initialization(view); //Function to initialize the variables

        Profile.setOnClickListener(v -> startActivity(new Intent(getContext(), Profile.class)));

        AccountSettings.setOnClickListener(v -> startActivity(new Intent(getContext(), AccountSettings.class)));

        PrivacyPolicy.setOnClickListener(v -> {
            String url = "https://pages.flycricket.io/presin/privacy.html";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        //Calling User Data from SharedPreference
        SharedPreferences userDataSP = requireContext().getSharedPreferences("userData", 0);
        String SPname = userDataSP.getString("name", "0");
        String SPPhone = userDataSP.getString("phone", "0");
        String SPYear = userDataSP.getString("year", "0");

        if (SPPhone.equals("0") && SPYear.equals("0")) {
            accountIncompleteIndicator.setVisibility(View.VISIBLE);
        } else {
            accountIncompleteIndicator.setVisibility(View.GONE);
        }

        //If the user data was saved in SharedPreference -> which is always true, still checking to make sure it does not creates issue
        if (!SPname.equals("0")) {
            Name.setText("Hi, " + SPname);
        }

        Logout.setOnClickListener(view1 -> {
            //Pop up a Dialog when the user clicks on logout button, ask them whether they really want to logout or not.

            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.custom_dialog);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_dialog_background));
            }
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false); //Optional
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation; //Setting the animations to dialog

            Button Proceed = dialog.findViewById(R.id.proceed);
            Button Cancel = dialog.findViewById(R.id.cancel);
            TextView title = dialog.findViewById(R.id.dialog_title);
            TextView description = dialog.findViewById(R.id.dialog_description);

            Proceed.setText("Logout");
            Proceed.setBackground(getResources().getDrawable(R.drawable.negative));
            title.setText("Confirm Logout");
            description.setText("Do you really want to logout?");

            Proceed.setOnClickListener(v -> { //On logout -> Logout the current logged in user
                dialog.dismiss();
                LogoutFun();
            });

            Cancel.setOnClickListener(v -> dialog.dismiss()); //On Cancel
            dialog.show();
        });

        return view;
    }

    private void LogoutFun() {
        SharedPreferences userDataSP = requireContext().getSharedPreferences("userData", 0);
        SharedPreferences.Editor editor = userDataSP.edit();
        editor.clear();
        editor.apply();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), SignIn_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    //Function to initialize the variables
    private void Initialization(View view) {
        Logout = view.findViewById(R.id.logout_button);
        Name = view.findViewById(R.id.usernameDisplay);
        PrivacyPolicy = view.findViewById(R.id.privacyPolicy_Card);
        AccountSettings = view.findViewById(R.id.account_Card);
        Profile = view.findViewById(R.id.profile_Card);
        accountIncompleteIndicator = view.findViewById(R.id.accountIncompleteIndicator);
    }

}