package com.example.akshay.dustaway;

import android.content.Intent;
import android.graphics.ColorSpace;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QRScanActivity extends AppCompatActivity {
    //View Objects
    private TextView nameTitle;
    private TextView textViewName;
    private final static String ID="-L8JOHhLatbhnt1lj63R";
    private ChildEventListener mChildEventListener;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mNotifyDatabaseReference;

    //qr code scanner object
    private IntentIntegrator qrScan;

    ModelUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

        //View objects
        textViewName = (TextView) findViewById(R.id.textViewName);
        nameTitle = (TextView) findViewById(R.id.nameTitle);

        textViewName.setVisibility(View.GONE);
        nameTitle.setVisibility(View.GONE);
        //intializing scan object
        qrScan = new IntentIntegrator(this);

        mFirebaseDatabase= FirebaseDatabase.getInstance();
        mNotifyDatabaseReference=mFirebaseDatabase.getReference().child("cleaners");
        if(mChildEventListener==null) {
            mChildEventListener = new ChildEventListener() {


                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            mNotifyDatabaseReference.addChildEventListener(mChildEventListener);

        }
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                   int i = Integer.valueOf(result.getContents());
                    textViewName.setVisibility(View.VISIBLE);
                    textViewName.setText("Congratulations you earned" + String.valueOf(i) + "Points");
                DatabaseReference taskref = mNotifyDatabaseReference.child(ID);
/*
                taskref.child("message").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        user = (ModelUser) snapshot.getValue();  //prints "Do you have data? You'll love Firebase."
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
*/
                Map<String,Object> taskMap = new HashMap();
                taskMap.put("cash", "25000");
                taskref.updateChildren(taskMap); //should I use setValue()...?
                Intent i1 =new Intent(QRScanActivity.this, PointGainActivity.class);

                startActivity(i1);
                //setting values to textviews
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();

    }


    private void detachDatabaseReadListener() {
        if (mChildEventListener!=null){
            mNotifyDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener=null;

        }
    }



    public void ScanQR(View view) {
        qrScan.initiateScan();
    }
}
