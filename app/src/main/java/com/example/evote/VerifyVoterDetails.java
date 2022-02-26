package com.example.evote;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.w3c.dom.Text;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class VerifyVoterDetails extends AppCompatActivity {
    private FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    //private String user_id = getUser_id();
    private String data="";
    int f=0;
    /*public String getUser_id() {
        user_id= FirebaseAuth.getInstance().getUid();
        return user_id;
    }*/
    private static final int STORAGE_CODE = 1999;
    private EditText secretCodeText;
    private TextView detailsText;
    //private CollectionReference aRef = firestoreDB.collection("Voters");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_voter_details);


        secretCodeText=findViewById(R.id.secretEditTextCodeDownload);
        detailsText=findViewById(R.id.textViewVoterDetails);
    }

    public void searchVotersDetails(View view) {
        data="All Voters Details ";
        f=0;
        firestoreDB.collection("Voters").get().addOnSuccessListener(documentColl -> {
            for (QueryDocumentSnapshot documentSnapshotMain : documentColl) {
                String vSecret = documentSnapshotMain.getString("voterSecretCode");
                //String myCompanyId = documentSnapshotMain.getString("votersCompanyId");
                String eId = documentSnapshotMain.getString("voterEmployeeId");
                String votedProof = documentSnapshotMain.getString("voted");
                String voterName = documentSnapshotMain.getString("voterName");
                String voterCompanyName = documentSnapshotMain.getString("votersCompanyName");
                String voterUserId = documentSnapshotMain.getId();


                Toast.makeText(this, "running", Toast.LENGTH_SHORT).show();

                if (vSecret.equals(secretCodeText.getText().toString()) && votedProof.equals("1")) {
                    f++;
                    Toast.makeText(this, "Vote Count : "+f, Toast.LENGTH_SHORT).show();
                    data+="\n\n"+f+")\n"+"Name : "+voterName+"\n"+"Company Name : "+voterCompanyName+"\n"+"Employee Id : "+eId+"\n"+"Mail Id : "+"***\n"
                            +"-------------";
                }
            }
            Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
            detailsText.setText(data);
            detailsText.setMovementMethod(new ScrollingMovementMethod());
        });

    }

    public void downloadVotersDetails(View view) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions,STORAGE_CODE);
            }else{
                savePdf();
            }
        }else{
            savePdf();
        }
    }
    private void savePdf() {
        Document document=new Document(PageSize.A4);
        String filename=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        String mFilepath=Environment.getExternalStorageDirectory() + "/"+ filename + ".pdf";
       /* File docsFolder = new File(Environment.getExternalStorageDirectory() + "/"+ filename + ".pdf");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Created a new directory for PDF");
        }   */
        try {
            PdfWriter.getInstance(document, new FileOutputStream(mFilepath));

            document.open();
            String text=data;

            document.addTitle("DOP-eSchedule");
            document.add(new Paragraph(text));
            document.addAuthor("Swarup Mishra");
            document.close();

            Toast.makeText(this, filename+".pdf \n is saved to \n"+mFilepath, Toast.LENGTH_SHORT).show();


        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    savePdf();
                } else {
                    Toast.makeText(this, "Permission denied......", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void shareCode(View view) {
        Intent intent=new Intent(Intent.ACTION_SEND);
        int c=1;
        intent.setType("text/plain");
        if(c>0){
            String head="Details : ";
            String text=data;
            intent.putExtra(Intent.EXTRA_TEXT,text);
            intent.putExtra(Intent.EXTRA_SUBJECT,head);
            startActivity(Intent.createChooser(intent,"You can verify details"));
        }else{
            Toast.makeText(this, "Secret Message cannot be shared...", Toast.LENGTH_SHORT).show();
        }
    }
}
