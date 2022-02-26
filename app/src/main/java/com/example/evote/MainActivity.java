package com.example.evote;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Button NextButton;
    private int f;
    private String userid,userid_at_database,userid_at_database_depo;
    private FirebaseFirestore FirestoreDB = FirebaseFirestore.getInstance();
    private CollectionReference dRef = FirestoreDB.collection("PollCreators");
    private CollectionReference aRef = FirestoreDB.collection("Voters");

    /*  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  FirebaseUI Authentications @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SignIN=1999;
    private String mUsername;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mauthstatelistener;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SignIN){
            if(resultCode==RESULT_OK){
                //Intent intent=new Intent(MainActivity.this,Account_Settings.class);
                //startActivity(intent);
                Toast.makeText(this, "Signed In!!!", Toast.LENGTH_SHORT).show();
                //finish();
            } else if(resultCode==RESULT_CANCELED){
                Toast.makeText(this, "Signed In Canceled...Sorry", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /*  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        NextButton=findViewById(R.id.buttonNext);
        ProgressBar bar =findViewById(R.id.progressBar);
        bar.setVisibility(View.INVISIBLE);
        
        NextButton.setOnClickListener(v -> {

            f=0;

            aRef.get().addOnSuccessListener(queryDocumentSnapshots -> {

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    userid_at_database = documentSnapshot.getId();

                    bar.setVisibility(View.VISIBLE);
                    bar.setProgress(20);
                    if(userid.equalsIgnoreCase(userid_at_database))
                    {
                        f++;
                        String votedProof = documentSnapshot.getString("voted");
                        int vcount= Integer.parseInt(votedProof);
                        if(vcount==0) {
                            bar.setVisibility(View.INVISIBLE);
                            Intent in = new Intent(this, VotersPage.class);
                            startActivity(in);
                        }else if(vcount==1){
                            bar.setVisibility(View.INVISIBLE);
                            Intent in=new Intent(this,split.class);
                            startActivity(in);
                        }
                    }
                }
                dRef.get().addOnSuccessListener(queryDocumentDepoSnapshots -> {
                    for (QueryDocumentSnapshot documentDepoSnapshot : queryDocumentDepoSnapshots) {

                        userid_at_database_depo = documentDepoSnapshot.getId();

                        if(userid.equalsIgnoreCase(userid_at_database_depo))
                        {
                            f++;
                            bar.setVisibility(View.INVISIBLE);
                            Intent in=new Intent(this,PollCreatorsPage.class);
                            startActivity(in);
                        }
                    }
                });

                if(f==0)
                {
                    bar.setVisibility(View.INVISIBLE);
                    Intent in=new Intent(this,split.class);
                    startActivity(in);
                }

            });

           /* Intent in=new Intent(this,split.class);
            startActivity(in); */

        });

        /*  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  FirebaseUI Authentications @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
        mUsername = ANONYMOUS;

        mAuth=FirebaseAuth.getInstance();

        final List<AuthUI.IdpConfig> providers= Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build());

        //the state listener below understands if our user is signed in or signed out
        mauthstatelistener= firebaseAuth -> {
            //here firebaseAuth has the exact information regarding the state of the user i.e. signedIn or signedOut
            FirebaseUser user=firebaseAuth.getCurrentUser();
            if(user!=null){
                //user signed in
                Toast.makeText(MainActivity.this, "Helping You,Monitor Your Clients To Make Schedules Easily!", Toast.LENGTH_SHORT).show();
                onSignedInInitialize(user.getDisplayName());
            }else{
                onSignedOut();
                //user signed out
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(providers)
                                .setLogo(R.drawable.wood)
                                .setTheme(R.style.Theme_AppCompat_DayNight)
                                .build(),
                        RC_SignIN );
            }
        };
        /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
    }
    

    /*  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  FirebaseUI Authentications @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mauthstatelistener);
    }
    protected void onPause() {
        super.onPause();
        if (mauthstatelistener!=null) {
            mAuth.removeAuthStateListener(mauthstatelistener);
        }
    }

    private void onSignedOut() {
        mUsername=ANONYMOUS;
    }

    private void onSignedInInitialize(String displayName) {
        mUsername=displayName;
        userid=mAuth.getCurrentUser().getUid();
        //Here we will be able to set our data only if the user is signed in
    }

    public void logout(View view) {
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Intent intent=new Intent(this,splash.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(this, "Log Out Error...", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void helping(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=sk-9KVTfdOU"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.google.android.youtube");
        startActivity(intent);
    }

  /*  public void crashMe(View view) {
        // Creates a button that mimics a crash when clicked
        Button crashButton = new Button(this);
        crashButton.setOnClickListener(view1 -> {
            throw new RuntimeException("Test Crash"); // Force a crash
        });

        addContentView(crashButton, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

    } */
    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
}

