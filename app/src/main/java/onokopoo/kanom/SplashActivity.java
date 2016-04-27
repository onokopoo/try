package onokopoo.kanom;

/**
 * Created by onokopoo on 11/27/2015.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import java.util.ArrayList;

public class SplashActivity extends Activity {
    Handler handler;
    Runnable runnable;
    public static String user_id;
    public static String email;
    public static String user;
    SharedPreferences sharedpreferences;
    private ArrayList<String> permissionsToRequest;

    @TargetApi(Build.VERSION_CODES.M)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splashscreen);

        ArrayList<String> perms = new ArrayList<>();
        perms.add("android.permission.READ_EXTERNAL_STORAGE");
        perms.add("android.permission.WRITE_EXTERNAL_STORAGE");
        perms.add("android.permission.CAMERA");
        permissionsToRequest = findUnAskedPermissions(perms);

        if(permissionsToRequest.size()>0) {
            requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), 200);
            for (String perm : permissionsToRequest) {
                markAsAsked(perm);
            }
        }

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

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted){
        ArrayList result = new ArrayList<String>();
        for(String perm : wanted){

            if(!hasPermission(perm) && shouldWeAsk(perm)){
                result.add(perm);
            }
        }
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case 200:
                boolean readAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                boolean writeAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;
                boolean cameraAccepted = grantResults[2]==PackageManager.PERMISSION_GRANTED;

                break;
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasPermission(String permission){
        if(canMakeSmores()){
            return(checkSelfPermission(permission)== PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    private boolean canMakeSmores(){
        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private boolean shouldWeAsk(String permission){
        return (sharedpreferences.getBoolean(permission, true));
    }

    private void markAsAsked(String permission){
        sharedpreferences.edit().putBoolean(permission, false).apply();
    }
}