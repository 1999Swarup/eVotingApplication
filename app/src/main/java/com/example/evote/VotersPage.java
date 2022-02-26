package com.example.evote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.concurrent.atomic.AtomicInteger;

public class VotersPage extends AppCompatActivity {

    private FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    private String user_id = getUser_id();
    private CollectionReference aRef = firestoreDB.collection("Voters");
    //private DocumentReference votersRef = firestoreDB.document(user_id);

    String data="Candidate Names : "+"\n";
    public String getUser_id() {
        user_id= FirebaseAuth.getInstance().getUid();
        return user_id;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voters_page);

        TextView TextCandidateName = findViewById(R.id.candidates_names);

        aRef.get().addOnSuccessListener(documentColl -> {
            for (QueryDocumentSnapshot documentSnapshotMain : documentColl) {

                if(documentSnapshotMain.getId().equals(user_id)){
            String myCompanyId = documentSnapshotMain.getString("votersCompanyId");
            String mySecretCode = documentSnapshotMain.getString("voterSecretCode");

                    //Toast.makeText(this, mySecretCode, Toast.LENGTH_SHORT).show();

                    firestoreDB.collection(myCompanyId).get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String candidateSecretCode = documentSnapshot.getString("secretCode");

                    //Toast.makeText(this, candidateSecretCode, Toast.LENGTH_SHORT).show();

                    assert candidateSecretCode != null;
                    if (candidateSecretCode.equals(mySecretCode)) {
                        String candidateName = documentSnapshot.getString("cName");

                        data += candidateName+"\n";
                    }

                }
                        TextCandidateName.setText(data);
            });

        }
    }
        });
    }

    public void voted(View view) {
        EditText textVotedName=findViewById(R.id.votedCandidateName);
                       String name=textVotedName.getText().toString();

        aRef.get().addOnSuccessListener(documentColl -> {
            for (QueryDocumentSnapshot documentSnapshotMain : documentColl) {

                if(documentSnapshotMain.getId().equals(user_id)){
            String myCompanyId = documentSnapshotMain.getString("votersCompanyId");
            String mySecretCode = documentSnapshotMain.getString("voterSecretCode");
            
            firestoreDB.collection(myCompanyId).get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String candidateSecretCode = documentSnapshot.getString("secretCode");

                    if (candidateSecretCode.equals(mySecretCode)) {

                        String candidateName = documentSnapshot.getString("cName");
                        String candidateVoteCount = documentSnapshot.getString("voteCount");
                        if (name.trim().equalsIgnoreCase(candidateName)) {
                   
                            int vc = Integer.parseInt(candidateVoteCount);
                            vc++;
                            candidateVoteCount = String.valueOf(vc);
                            firestoreDB.collection(myCompanyId).document(documentSnapshot.getId()).update("voteCount", candidateVoteCount);
                            Toast.makeText(this, "Vote Successfully Saved. Thank You.", Toast.LENGTH_SHORT).show();
                            //String votedProof = documentSnapshotMain.getString("voted");
                            //int vcount= Integer.parseInt(votedProof);
                            aRef.document(user_id).update("voted","1");
                            logout();
                            break;
                        }
                    }
                }
            });
        }
    }
        });

    }

    public void logout() {
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
}
