package com.example.evote;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class split extends AppCompatActivity {
    
    private FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    private String user_id = getUser_id();
    int flag=0;
    public String getUser_id() {
        user_id= FirebaseAuth.getInstance().getUid();
        return user_id;
    }
    private CollectionReference aRef = firestoreDB.collection("Voters");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split);
    }

    public void votersWard(View view) {
        openClientsDialog();
    }

    public void openClientsDialog()
    {
        final AlertDialog.Builder clientAlert = new AlertDialog.Builder(split.this);
        View clientView=getLayoutInflater().inflate(R.layout.clientdialog,null);

         EditText voterName=clientView.findViewById(R.id.editTextVoterNameSplit);
         EditText votersCompanyId=clientView.findViewById(R.id.editTextVotersOrganisationIDSplit);
         EditText votersCompanyName=clientView.findViewById(R.id.editTextVoterOrganisationNameSplit);
         EditText voterSecretCode=clientView.findViewById(R.id.editTextVotersSecretCodeSplit);
         EditText voterEmployeeId=clientView.findViewById(R.id.editTextVotersEmployeeIdSplit);

        Button cancelBtn=clientView.findViewById(R.id.buttonCancelVoter);
        Button okBtn=clientView.findViewById(R.id.buttonOkVoter);

        clientAlert.setView(clientView);

        final AlertDialog alertDialog=clientAlert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        cancelBtn.setOnClickListener(v -> alertDialog.dismiss());
        okBtn.setOnClickListener(v -> {
                 flag=0;
            String voterNames=voterName.getText().toString();
            String votersCompanyIds=votersCompanyId.getText().toString();
            String votersCompanyNames=votersCompanyName.getText().toString();
            String voterSecretCodes=voterSecretCode.getText().toString();
            String voterEmployeeIds=voterEmployeeId.getText().toString();
            String voted="0";

            aRef.get().addOnSuccessListener(documentColl -> {
                for (QueryDocumentSnapshot documentSnapshotMain : documentColl) {
                    String vSecret = documentSnapshotMain.getString("voterSecretCode");
                    String myCompanyId = documentSnapshotMain.getString("votersCompanyId");
                    String eId = documentSnapshotMain.getString("voterEmployeeId");
                    String votedProof = documentSnapshotMain.getString("voted");

                    Toast.makeText(this, "running", Toast.LENGTH_SHORT).show();

                    if (vSecret.equals(voterSecretCodes) && eId.equals(voterEmployeeIds) && myCompanyId.equals(votersCompanyIds) && votedProof.equals("1")) {
                        Toast.makeText(this, "Please fill a different Secret Code. You have already voted once.", Toast.LENGTH_SHORT).show();
                        flag++;
                        break;
                    }
                }
                    Toast.makeText(this, "runned", Toast.LENGTH_SHORT).show();
           /* String clientsName = depoName.getText().toString();
            Map<String, String> userMap = new HashMap<>();
            userMap.put("Depositors Name", clientsName); */

             if(!voterNames.equals("") && !votersCompanyIds.equals("") && !votersCompanyNames.equals("") && !voterSecretCodes.equals("") && !voterEmployeeIds.equals("") ) {
                 //Check if the employee id and registered mail id has already voted for the entered secret code

                 if (flag!=0) {
                     Toast.makeText(this, "Please fill a different Secret Code. You have already voted once.", Toast.LENGTH_SHORT).show();
                     Intent i=new Intent(split.this,splash.class);
                     startActivity(i);
                 } else if(flag==0){
                     VotersNote voterNote = new VotersNote(voterNames, votersCompanyIds, votersCompanyNames, voterSecretCodes, voterEmployeeIds, voted);
                     firestoreDB.collection("Voters").document(user_id).set(voterNote).addOnCompleteListener(task -> {
                         if (task.isSuccessful()) {
                             Toast.makeText(split.this, "Firestore Linked Successfully", Toast.LENGTH_SHORT).show();
                             Intent i = new Intent(split.this, VotersPage.class);
                             startActivity(i);
                         } else {
                             Toast.makeText(split.this, "Firestore Linking Error" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                         }
                     });
                 }else{
                     Toast.makeText(this, "Please fill all.", Toast.LENGTH_SHORT).show();
                 }


                  }

            });

        });

        alertDialog.show();

    }

    public void pollCreatorWard(View view) {
        openAgentsDialog();
    }

    public void openAgentsDialog()
    {
        final AlertDialog.Builder agentAlert = new AlertDialog.Builder(split.this);
        View agentView=getLayoutInflater().inflate(R.layout.agentdialog,null);
        Button cancelBtn=agentView.findViewById(R.id.buttonCancelAgent);
        Button okBtn=agentView.findViewById(R.id.buttonOkAgent);
        EditText agentName=agentView.findViewById(R.id.editTextAgentNameSplit);
        EditText companyName=agentView.findViewById(R.id.editTextAgentOrganisationSplit);
        agentAlert.setView(agentView);

        final AlertDialog alertDialog=agentAlert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        cancelBtn.setOnClickListener(v -> alertDialog.dismiss());
        okBtn.setOnClickListener(v -> {

            String agentsName = agentName.getText().toString();
            String companysName = companyName.getText().toString();

            Map<String, String> userMap = new HashMap<>();
            userMap.put("User Name", agentsName);
            userMap.put("Company Name", companysName);
            firestoreDB.collection("PollCreators").document(user_id).set(userMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(split.this, "Firestore Linked Successfully", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(split.this,PollCreatorsPage.class);
                    startActivity(i);
                } else {
                    Toast.makeText(split.this, "Firestore Linking Error" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });

        alertDialog.show();

    }

}
