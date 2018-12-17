package news.id.id.percobaannews;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends Function {

    @BindView(R.id.pgLoading)
    ProgressBar pgLoading;
    @BindView(R.id.edtUsername)
    EditText edtUsername;
    @BindView(R.id.edtPassword)
    EditText edtPassword;


    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener listener;
    @BindView(R.id.btnSignup)
    Button btnSignup;
    @BindView(R.id.btnSignIn)
    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    toastService("Congratulation! cek email anda sekarang.");
                    verifikasiEmail();
                }
            }
        };

    }

    private void verifikasiEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseAuth.getInstance().signOut();
                    intentService(LoginActivity.class);
                    finish();
                } else {
                    overridePendingTransition(0, 0);
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null) {
            auth.removeAuthStateListener(listener);
        }
    }

    @OnClick({R.id.btnSignup, R.id.btnSignIn})
    public void onViewClicked(View view) {
        final String userName = edtUsername.getText().toString();
        String passKey = edtPassword.getText().toString();
        switch (view.getId()) {
            case R.id.btnSignup:
                if (TextUtils.isEmpty(userName)) {
                    edtUsername.setError("Isi dengan Email Anda");
                    edtUsername.requestFocus();
                } else if (TextUtils.isEmpty(passKey)) {
                    edtPassword.setError("Buat Password terlebih dahulu");
                    edtPassword.requestFocus();
                } else {
                    pgLoading.setVisibility(View.VISIBLE);
                    auth.createUserWithEmailAndPassword(userName, passKey).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pgLoading.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                toastService("Selamat! " + userName + "\n anda berhasil terdaftar.");
                            } else {
                                toastService("Maaf, gagal register! coba cek " + task.getException());
                            }
                        }
                    });
                }
                break;
            case R.id.btnSignIn:
                intentService(LoginActivity.class);
                break;
        }
    }
}
