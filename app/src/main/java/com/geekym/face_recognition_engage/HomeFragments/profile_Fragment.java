package com.geekym.face_recognition_engage.HomeFragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.geekym.face_recognition_engage.Authentication.SignIn_Activity;
import com.geekym.face_recognition_engage.R;
import com.geekym.face_recognition_engage.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class profile_Fragment extends Fragment {

    ImageButton Logout;
    Button Delete;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    TextView Name, Email, Uid, OrgName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_, container, false);

        Initialization(view);

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), SignIn_Activity.class));
                getActivity().finish();
            }
        });

        Delete.setOnClickListener(view1 -> deleteAccount()); //To delete account

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() { //To display user's data in card view
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userprofile = snapshot.getValue(Users.class);
                if (userprofile != null) {
                    String name = userprofile.name;
                    String email = userprofile.email;
                    String uid = userprofile.id;
                    String org = userprofile.organization;

                    Name.setText("Name: " + name);
                    Email.setText("Email: " + email);
                    Uid.setText("ID: " + uid);
                    OrgName.setText("Organization: " + org);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void Initialization(View view) {
        Logout = view.findViewById(R.id.logout_button);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        Name = view.findViewById(R.id.name);
        Email = view.findViewById(R.id.email);
        Uid = view.findViewById(R.id.uid);
        OrgName = view.findViewById(R.id.orgname);
        Delete = view.findViewById(R.id.delete_button);
    }

    public void deleteAccount() { //function to delete account
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Account Deleted Successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(), SignIn_Activity.class));
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


//    private void Fetch() {
//        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Users userprofile = snapshot.getValue(Users.class);
//                Intent i = new Intent(getContext(), HomeScreen.class);
//                if (userprofile != null){
//                    String name = userprofile.name;
//                    String email = userprofile.email;
//                    String uid = userprofile.id;
//                    String org = userprofile.organization;
//
//                    Name.setText(name);
//                    Email.setText(email);
//                    Uid.setText(uid);
//                    OrgName.setText(org);
//
//                    Toast.makeText(getContext(), "Welcome "+name, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}