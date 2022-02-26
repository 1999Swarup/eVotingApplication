package com.example.evote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class WinnerActivity extends AppCompatActivity {

    int c=0,d=0;
    private FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    private String user_id = getUser_id();
    private CollectionReference aRef = firestoreDB.collection(user_id);
    TextView WinnerTextview;
    EditText scET;
    EditText cnET;
    String sec;
    int totalCan;
    String data="Congratulating the Winner is: "+"\n";
    String data1="\n";
    public String getUser_id() {
        user_id= FirebaseAuth.getInstance().getUid();
        return user_id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);


        WinnerTextview = findViewById(R.id.textViewWinner);
        cnET = findViewById(R.id.secretEditTextTotalCandidate);
        scET = findViewById(R.id.secretEditTextCodeWinner);


        ImageButton search=findViewById(R.id.imageButton);
        ImageButton send=findViewById(R.id.imageButton2);

        search.setOnClickListener(this::searchWinner);
        send.setOnClickListener(this::sendWinnerName);


    }

    public void searchWinner(View view) {

        sec=scET.getText().toString();
        totalCan=Integer.parseInt(cnET.getText().toString());
        data="Congratulating the Winner is: "+"\n";
        data1="\n";
        //Toast.makeText(this, sec, Toast.LENGTH_SHORT).show();
      aRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
          int f=0,c=0,d=0;
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                String candidateSecretCode = documentSnapshot.getString("secretCode");

                //Toast.makeText(this, sec, Toast.LENGTH_SHORT).show();

                assert candidateSecretCode != null;
                if (candidateSecretCode.equals(sec)) {
                    int vc = Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString("voteCount")));
                    f++;
                    if(vc>c) {
                        String candidateName = documentSnapshot.getString("cName");
                        c=vc;
                        if(f==totalCan) {
                            data +=  candidateName + "\n" + "Votes : " + c+ "\n";
                        }
                    }else if(vc==c){
                        c=vc;
                        if(f==totalCan) {
                            d=1;
                            data1 +=  "TIED"+ "\n";
                        } 
                    }
                }

            }

            if(d==0){
                WinnerTextview.setText(data);
                data="";
            }else{
                WinnerTextview.setText(data1);
                data1="";
            }
        });
    }

    public void sendWinnerName(View view) {
       Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        if(d>=0){
            String text= WinnerTextview.getText().toString();
            intent.putExtra(Intent.EXTRA_TEXT,text);
            startActivity(Intent.createChooser(intent,"Share with All"));
        }else{
            Toast.makeText(this,"not allowed", Toast.LENGTH_SHORT).show();
        }   
    }
}
