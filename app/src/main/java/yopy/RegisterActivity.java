package yopy;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import yopy.modutalk.LoginActivity;
import yopy.modutalk.MainActivity;
import yopy.modutalk.R;
import yopy.modutalk.common.Util9;
import yopy.modutalk.model.UserModel;

public class RegisterActivity extends AppCompatActivity {
    private EditText user_id;
    private EditText user_pw;
    SharedPreferences sharedPreferences;
    private static final int MY_PERMISSION_STORAGE = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user_id = findViewById(R.id.user_id);
        user_pw = findViewById(R.id.user_pw);
        Button signupBtn = findViewById(R.id.signupBtn);

        signupBtn.setOnClickListener(signupClick);

        sharedPreferences = getSharedPreferences("gujc", Activity.MODE_PRIVATE);
        String id = sharedPreferences.getString("user_id", "");


        if (!"".equals(id)) {
            user_id.setText(id);

            int permssionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permssionCheck!= PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this,"권한 승인이 필요합니다",Toast.LENGTH_LONG).show();

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this,"권한이 필요합니다.",Toast.LENGTH_LONG).show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSION_STORAGE);
                    Toast.makeText(this,"권한이 필요합니다.",Toast.LENGTH_LONG).show();

                }
            }
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this,"승인이 허가되어 있습니다.",Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this,"아직 승인받지 않았습니다.", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }


    Button.OnClickListener signupClick = new View.OnClickListener() {
        public void onClick(View view) {
            if (!validateForm()) return;
            final String id = user_id.getText().toString();

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(id, user_pw.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        sharedPreferences.edit().putString("user_id", id).commit();
                        final String uid = FirebaseAuth.getInstance().getUid();

                        UserModel userModel = new UserModel();
                        userModel.setUid(uid);
                        userModel.setUserid(id);
                        userModel.setUsernm(extractIDFromEmail(id));
                        userModel.setUsermsg("...");

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users").document(uid)
                                .set(userModel)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent  intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        Log.d(String.valueOf(R.string.app_name), "DocumentSnapshot added with ID: " + uid);
                                    }
                                });
                    } else {
                        Util9.showMessage(RegisterActivity.this, "이메일,비밀번호를 입력해주세요.");
                    }
                }
            });
        }
    };

    String extractIDFromEmail(String email){
        String[] parts = email.split("@");
        return parts[0];
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = user_id.getText().toString();
        if (TextUtils.isEmpty(email)) {
            user_id.setError("Required.");
            valid = false;
        } else {
            user_id.setError(null);
        }

        String password = user_pw.getText().toString();
        if (TextUtils.isEmpty(password)) {
            user_pw.setError("Required.");
            valid = false;
        } else {
            user_pw.setError(null);
        }

        return valid;
    }
}