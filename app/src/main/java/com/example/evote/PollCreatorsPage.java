package com.example.evote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class PollCreatorsPage extends AppCompatActivity {

    LinearLayout layoutList;
    Button buttonAdd;
    Button buttonSubmitList;
    String secretCode = null;
    String postName = null;
    int c=0;
    EditText postText;

    private FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    private String user_id = getUser_id();
    private CollectionReference myRef = firestoreDB.collection(user_id);

    public String getUser_id() {
        user_id= FirebaseAuth.getInstance().getUid();
        return user_id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_creators_page);

        layoutList = findViewById(R.id.layout_list);
        buttonAdd = findViewById(R.id.button_add);
        buttonSubmitList = findViewById(R.id.button_submit_list);

        buttonAdd.setOnClickListener(this::addCandidates);
        buttonSubmitList.setOnClickListener(this::addAll);
    }
    @Override
    public void onBackPressed () {

    }
    public void addCandidates(View view) {
        addCandidateView();
    }

    private void addCandidateView() {
        final View candidateViews=getLayoutInflater().inflate(R.layout.candidate_row,null);
    
        EditText editTextName = (EditText)candidateViews.findViewById(R.id.edit_candidate_name);
        EditText editTextVote = (EditText)candidateViews.findViewById(R.id.edit_vote_count);
        ImageView imageClose = (ImageView)candidateViews.findViewById(R.id.image_remove);

        imageClose.setOnClickListener(v -> removeView(candidateViews));

        layoutList.addView(candidateViews);
    }

    // function to generate a random string of length n
    public static String getAlphaNumericString(int n)
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    private void removeView(View candidateViews) {
        layoutList.removeView(candidateViews);
    }

    public void addAll(View view) {

        TextView secretCodeText = (TextView) findViewById(R.id.secretTextCode);
        c=0;
        int f=0;
        secretCode = null;
        postText = findViewById(R.id.postEdit);
        postName = postText.getText().toString();

        for(int i=0;i<layoutList.getChildCount();i++) {
            View candidateViews = layoutList.getChildAt(i);

            EditText editTextName = (EditText) candidateViews.findViewById(R.id.edit_candidate_name);
            EditText editTextVote = (EditText) candidateViews.findViewById(R.id.edit_vote_count);
            if(!editTextName.getText().toString().equals("") && editTextVote.getText().toString().equals("0")){
                  f++;
            }
        }
        if(f==layoutList.getChildCount()) {
            for (int i = 0; i < layoutList.getChildCount(); i++) {
                View candidateViews = layoutList.getChildAt(i);

                EditText editTextName = (EditText) candidateViews.findViewById(R.id.edit_candidate_name);
                EditText editTextVote = (EditText) candidateViews.findViewById(R.id.edit_vote_count);
                //TextView secretText = (TextView)candidateViews.findViewById(R.id.secretTextCode);

                if (!editTextName.getText().toString().equals("") && editTextVote.getText().toString().equals("0")) {
                    String name = editTextName.getText().toString();
                    String voteCount = editTextVote.getText().toString();

                    if (i == 0) {
                        secretCode = getAlphaNumericString(7);
                    }

                    CandidateNote note = new CandidateNote(secretCode, name, voteCount, postName);
                    c++;

                 /*Map<String, String> userMap = new HashMap<>();
                userMap.put("Secret Code", secretCode);
                userMap.put("Candidates Name", name);
                userMap.put("Vote Count", voteCount);*/

                    myRef.add(note).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(PollCreatorsPage.this, "Firestore Linked Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PollCreatorsPage.this, "Firestore Linking Error" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(this, "Enter the candidate name and zero for vote count.", Toast.LENGTH_SHORT).show();
                    break;
                }

            }
        }else{                                                                               
            Toast.makeText(this, "Please enter the vote count for every candidate 0", Toast.LENGTH_SHORT).show();
        }
        if(c>0){
            secretCodeText.setText(secretCode);
            Toast.makeText(this, "Secret Message should be noted.", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareCode(View view) {
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        if(c>0){
            String head="Please keep them safe and use it to login for voting : ";
           String text="Please keep them safe and use it to login for voting : "+
                   "The secret code for "+ postName +" position is "+secretCode+". The voting code for our company is "+user_id+".";
           intent.putExtra(Intent.EXTRA_TEXT,text);
           intent.putExtra(Intent.EXTRA_SUBJECT,head);
           startActivity(Intent.createChooser(intent,"Share with Voters"));
        }else{
            Toast.makeText(this, "Secret Message cannot be shared...", Toast.LENGTH_SHORT).show();
        }
    }

    public void winner(View view) {

        Intent i=new Intent(PollCreatorsPage.this,WinnerActivity.class);
        startActivity(i);

    }

    public void backMe(View view) {
        Intent i=new Intent(PollCreatorsPage.this,MainActivity.class);
        startActivity(i);
    }

    public void checkDetails(View view) {
        Intent i=new Intent(PollCreatorsPage.this,VerifyVoterDetails.class);
        startActivity(i);
    }
}
