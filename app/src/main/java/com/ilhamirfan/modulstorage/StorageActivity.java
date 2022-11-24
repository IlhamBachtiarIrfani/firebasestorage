package com.ilhamirfan.modulstorage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class StorageActivity extends AppCompatActivity {

    public static final int PICKFILE_RESULT_CODE = 1;
    private Button chooseBtn, uploadBtn;
    private TextView filePathViewer, listFileViewer;

    private Uri fileUri;
    private String filePath;

    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();

        chooseBtn = findViewById(R.id.ChooseBtn);
        uploadBtn = findViewById(R.id.UploadBtn);
        filePathViewer = findViewById(R.id.FilePathViewer);
        listFileViewer = findViewById(R.id.listFile);

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });

        readAllFile();
    }

    private void readAllFile() {
        StorageReference storageRef = mStorage.getReference().child(mAuth.getUid());
        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                listFileViewer.setText("");
                for (StorageReference item : listResult.getItems()) {
                    String currentText = listFileViewer.getText().toString();
                    currentText += "\n" + item.getName();

                    listFileViewer.setText(currentText);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StorageActivity.this, "Tidak bisa membuka data : " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadFile() {
        if (filePath == null) {
            Toast.makeText(StorageActivity.this, "Pilih file terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageRef = mStorage.getReference();
        StorageReference mountainsRef = storageRef.child(mAuth.getUid()).child(fileUri.getLastPathSegment());

        UploadTask uploadTask = mountainsRef.putFile(fileUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(StorageActivity.this, "GAGAL! : " + exception.toString(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(StorageActivity.this, "GAMBAR BERHASIL DI UPLOAD", Toast.LENGTH_SHORT).show();
                readAllFile();
            }
        });
    }


    private void chooseFile() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("image/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == -1) {
                    fileUri = data.getData();
                    filePath = fileUri.getPath();
                    filePathViewer.setText(filePath);
                }
                break;
        }
    }
}