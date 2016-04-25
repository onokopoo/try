package onokopoo.kanom;

/**
 * Created by onokopoo on 11/27/2015.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashActivity extends Activity {
    Handler handler;
    Runnable runnable;
    public static String user_id;
    public static String email;
    public static String user;

    @TargetApi(Build.VERSION_CODES.M)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splashscreen);

        String[] perms = {"android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"};
        int permsRequestCode = 200;
        requestPermissions(perms, permsRequestCode);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
            user_id = extras.getString("user_id");
            user = extras.getString("user");
        }

        handler = new Handler();

        runnable = new Runnable() {
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("user", user);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            }
        };
    }

    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
    }

    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }
}