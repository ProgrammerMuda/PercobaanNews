package news.id.id.percobaannews;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends Function {

    @BindView(R.id.pgLoading)
    ProgressBar pgLoading;
    @BindView(R.id.edtUsername)
    EditText edtUsername;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.btnSignIn)
    Button btnSignIn;
    @BindView(R.id.btnSignUp)
    Button btnSignUp;

    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener listener;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (user.isEmailVerified()) {

                        toastService("Hallo " + user.getEmail() + ".");
                        intentService(HomeActivity.class);
                        finish();

                    } else {
                        toastService("Maaf! anda gagal login, silahkan coba lagi, coba cek email apakah sudah di verifikasi?");
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            }
        };
    }

    @OnClick({R.id.btnSignIn, R.id.btnSignUp})
    public void onViewClicked(View view) {
        final String userName = edtUsername.getText().toString();
        String passKey = edtPassword.getText().toString();
        switch (view.getId()) {
            case R.id.btnSignIn:
                if (TextUtils.isEmpty(userName)){
                    edtUsername.setError("Silah kan isi Email anda");
                    edtUsername.requestFocus();

                }else if (TextUtils.isEmpty(passKey)){
                    edtPassword.setError("Silah kan isi password anda Terlebih dahulu");
                    edtPassword.requestFocus();

                }else {
                    pgLoading.setVisibility(View.VISIBLE);
                    auth.signInWithEmailAndPassword(userName, passKey).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pgLoading.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                toastService("Maaf! anda gagal login silahkan cek " + task.getException() + ".");
                                intentService(LoginActivity.class);
                            } else {
                                intentService(HomeActivity.class);
                                finish();
                            }
                        }
                    });

                }
                break;
            case R.id.btnSignUp:
                intentService(RegisterActivity.class);
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener!=null){
            auth.removeAuthStateListener(listener);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
