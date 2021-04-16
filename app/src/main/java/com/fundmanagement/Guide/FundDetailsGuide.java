package com.fundmanagement.Guide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fundmanagement.FundDetails;
import com.fundmanagement.HOD.HOD_Prior;
import com.fundmanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class FundDetailsGuide extends AppCompatActivity {
    String collectionId;
    TextView arr_no,prior_id,category,date,email,name,paid_amount,roll_number,status;
    Button nitc_id,bill;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    String image1_url,image2_url;
    ImageView fund_image;
    FirebaseStorage firebaseStorage;
    Button accept,reject;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_details_guide);
        collectionId = getIntent().getStringExtra("collectionId");
        arr_no = findViewById(R.id.arr_no_fund);
        prior_id = findViewById(R.id.prior_no_fund);
        category = findViewById(R.id.category_fund);
        date = findViewById(R.id.date_fund);
        email = findViewById(R.id.email_fund);
        name = findViewById(R.id.name_fund);
        paid_amount = findViewById(R.id.paid_amount_fund);
        roll_number = findViewById(R.id.roll_number_fund);
        status = findViewById(R.id.status_fund);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        nitc_id = findViewById(R.id.view_nitc_fund);
        bill = findViewById(R.id.view_bill_fund);
        firebaseStorage = FirebaseStorage.getInstance();
        accept = findViewById(R.id.accept);
        reject  = findViewById(R.id.reject);
        accept.setVisibility(View.VISIBLE);
        reject.setVisibility(View.VISIBLE);


        accept.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                DocumentReference ref = firestore.collection("fundRequest").document(collectionId);
                Context context = FundDetailsGuide.this;
                builder = new AlertDialog.Builder(context,R.style.CustomDialog);
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText titleBox = new EditText(context);
                titleBox.setHint("Provide ARR number");
                titleBox.setTextColor(R.color.black);
                layout.addView(titleBox);
                builder.setView(layout);
                builder.setTitle("ARR Number");
                builder.setMessage("Please fill it now");

                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        WriteBatch batch = firestore.batch();
                        batch.update(ref,"ARR_no",titleBox.getText().toString().trim());
                        batch.update(ref,"status","Approved by Guide");
                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                        dialogInterface.cancel();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();;
                    }
                });
                builder.show();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                DocumentReference ref = firestore.collection("fundRequest").document(collectionId);
                Context context = FundDetailsGuide.this;
                builder = new AlertDialog.Builder(context,R.style.CustomDialog);
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText titleBox = new EditText(context);
                titleBox.setHint("Enter the message");
                titleBox.setTextColor(R.color.black);
                layout.addView(titleBox);
                builder.setView(layout);
                builder.setTitle("Rejection Message");
                builder.setMessage("Please leave a reason for rejection");

                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        WriteBatch batch = firestore.batch();
                        batch.update(ref,"message",titleBox.getText().toString().trim());
                        batch.update(ref,"status","Rejected by Guide");
                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                        dialogInterface.cancel();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();;
                    }
                });
                builder.show();
            }
        });

        DocumentReference reference  = firestore.collection("fundRequest").document(collectionId);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Log.e("fetch_data", "onEvent: Error in fetching fund data",error );
                    return;
                }
                arr_no.setText(value.getString("ARR_no").toString());
                prior_id.setText(value.getString("prior_id").toString());
                category.setText(value.getString("category").toString());
                date.setText(value.getString("date1").toString());
                email.setText(value.getString("email").toString());
                name.setText(value.getString("name").toString());
                paid_amount.setText(value.getString("paid_amount").toString());
                roll_number.setText(value.getString("roll_no").toString());
                status.setText(value.getString("status").toString());
                image1_url = value.getString("nitc_id").toString();
                image2_url = value.getString("bill_image").toString();
            }
        });
        nitc_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                builder =new AlertDialog.Builder(FundDetailsGuide.this,R.style.CustomDialog);
                Context context = FundDetailsGuide.this;
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                final ImageView fund_image = new ImageView(context);
                fund_image.setMaxHeight(300);
                layout.addView(fund_image);

                StorageReference storageReference = firebaseStorage.getReference().child("images/"+image1_url);
                try {
                    final File localfile  = File.createTempFile(image2_url,"jpg");
                    storageReference.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(FundDetailsGuide.this, "Image Retrieved", Toast.LENGTH_SHORT).show();
                            Bitmap bitmap  = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                            fund_image.setImageBitmap(bitmap);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
                builder.setView(layout);
                builder.setTitle("image");
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });
        bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                builder =new AlertDialog.Builder(FundDetailsGuide.this,R.style.CustomDialog);
                Context context = FundDetailsGuide.this;
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                final ImageView fund_image = new ImageView(context);
                fund_image.setMaxHeight(300);
                layout.addView(fund_image);


                StorageReference storageReference = firebaseStorage.getReference().child("images/"+image2_url);
                try {
                    final File localfile  = File.createTempFile(image2_url,"jpg");
                    storageReference.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(FundDetailsGuide.this, "Image Retrieved", Toast.LENGTH_SHORT).show();
                            Bitmap bitmap  = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                            fund_image.setImageBitmap(bitmap);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
                builder.setView(layout);
                builder.setTitle("image");
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });
    }
}