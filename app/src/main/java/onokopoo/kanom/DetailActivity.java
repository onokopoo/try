package onokopoo.kanom;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class DetailActivity extends AppCompatActivity {
    MediaPlayer mp;
    public static String Type;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        /*FloatingActionButton myFab = (FloatingActionButton)findViewById(R.id.fav);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "llllllllllllllllllllll");
            }
        });
        */
        final CheckBox star = (CheckBox) findViewById(R.id.star);
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(star.isChecked()){
                    Type = "1";
                    Log.d(TAG, String.valueOf(Type));
                    addOrRemoveFavorite(Type);

                } else{
                    Type = "0";
                    Log.d(TAG, String.valueOf(Type));
                    addOrRemoveFavorite(Type);
                }
            }
        });

        // Permission StrictMode
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        checkFavorite();
        addView();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void checkFavorite() {
        Intent intent = getIntent();
        final String sKanom_id = intent.getStringExtra("kanom_id");

        Config globalVariable = ((Config) getApplicationContext());
        String userId = globalVariable.getUserId();

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("kanom_id", sKanom_id));
        params.add(new BasicNameValuePair("user_id", userId));

        try {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(Config.URL_FAVORITE, ServiceHandler.POST, params);

            JSONObject jsonObj = new JSONObject(jsonStr);
            Boolean check = jsonObj.getBoolean("check");

            CheckBox star = (CheckBox) findViewById(R.id.star);
            star.setChecked(check);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addOrRemoveFavorite(String type) {
        Intent intent = getIntent();
        final String sKanom_id = intent.getStringExtra("kanom_id");

        Config globalVariable = ((Config) getApplicationContext());
        String userId = globalVariable.getUserId();

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("kanom_id", sKanom_id));
        params.add(new BasicNameValuePair("user_id", userId));
        params.add(new BasicNameValuePair("type", type));

        try {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(Config.URL_FAVORITE, ServiceHandler.POST, params);

            JSONObject jsonObj = new JSONObject(jsonStr);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addView() {
        Intent intent = getIntent();
        final String sKanom_id = intent.getStringExtra("kanom_id");

        Config globalVariable = ((Config) getApplicationContext());
        String userId = globalVariable.getUserId();

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("kanom_id", sKanom_id));
        params.add(new BasicNameValuePair("user_id", userId));

        try {
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(Config.URL_VIEW, ServiceHandler.POST, params);

            JSONObject jsonObj = new JSONObject(jsonStr);
            Boolean error = jsonObj.getBoolean("error");
            if (!error) {
                showInfo();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showInfo() {
        Intent intent = getIntent();
        final String sKanom_id = intent.getStringExtra("kanom_id");
        final ImageView iImage = (ImageView) findViewById(R.id.image);
        //final TextView tNameOther = (TextView)findViewById(R.id.name_other);
        final TextView tType = (TextView) findViewById(R.id.type);
        final TextView tHistory = (TextView) findViewById(R.id.history);
        final TextView tIngredient = (TextView) findViewById(R.id.ingredient);
        final TextView tRecipe = (TextView) findViewById(R.id.recipe);

        String url = getString(R.string.url) + "/selectKanom.php";

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("kanom_id", sKanom_id));
        params.add(new BasicNameValuePair("locale", getString(R.string.locale_config)));

        String strKanomId = "";
        String strNameTh = "";
        String strNameEn = "";
        String strNameOther = "";
        String strType = "";
        String strImage = "";
        String strIngredient = "";
        String strRecipe = "";
        String strHistory = "";
        String strVoice = "";

        //JSONObject c;
        try {
            System.out.println("---------------------------------");
            System.out.println(getJSONUrl(url, params));
            System.out.println("---------------------------------");
            JSONObject data = new JSONObject(getJSONUrl(url, params));

            //JSONArray data = new JSONArray(getJSONUrl(url,params));
            JSONArray info = data.getJSONArray("info");
            JSONObject c = info.getJSONObject(0);
            strNameTh = c.getString("name_th");
            strNameEn = c.getString("name_en");
            strNameOther = c.getString("name_other");
            strType = c.getString("type");
            strVoice = c.getString("voice");
            strImage = c.getString("image");
            strHistory = c.getString("history");

            if (!strNameTh.equals("")) {
                //tNameOther.setText(strNameOther);
                tType.setText(strType);
                tHistory.setText(strHistory);
                //tIngredient.setText(strIngredient);
                //tRecipe.setText(strRecipe);
                iImage.getLayoutParams().height = 330;
                iImage.getLayoutParams().width = 800;
                iImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                try {
                    iImage.setImageBitmap(loadBitmap(getString(R.string.url) + strImage));
                } catch (Exception e) {
                    // When Error
                    iImage.setImageResource(android.R.drawable.ic_menu_report_image);
                }

            } else {
                //tNameOther.setText("-");
                tType.setText("-");
                //tIngredient.setText("-");
                //tRecipe.setText("-");
            }
            JSONArray ingredient = data.getJSONArray("ingredient");
            String textIngredient = "Ingredient : \n";
            for (int i = 0; i < ingredient.length(); i++) {
                String id = ingredient.getJSONObject(i).getString("ingredient_id");
                String ingred = ingredient.getJSONObject(i).getString("ingredient");
                textIngredient += "\t" + id + ". " + ingred + "\n";
            }
            tIngredient.setText(textIngredient);

            JSONArray recipe = data.getJSONArray("recipe");
            String textRecipe = "Recipe : \n";
            for (int i = 0; i < recipe.length(); i++) {
                String id = recipe.getJSONObject(i).getString("recipe_id");
                String recip = recipe.getJSONObject(i).getString("recipe");
                textRecipe += "\t" + id + ". " + recip + "\n";
            }
            tRecipe.setText(textRecipe);

            final ImageButton btnBack = (ImageButton) findViewById(R.id.sound);
            // Perform action on click
            final String finalStrVoice = strVoice;
            btnBack.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    managerOfSound(finalStrVoice);
                }
            });


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(strNameTh);
            toolbar.setSubtitle(strNameEn);
            setSupportActionBar(toolbar);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    protected void managerOfSound(String id) {
        mp = MediaPlayer.create(this, getResources().getIdentifier(id, "raw", "onokopoo.kanom"));

        if (mp.isPlaying()) {
            mp.stop();
            mp.reset();
            mp.release();
        } else {
            mp.start();
        }
    }

    public String getHttpPost(String url, List<NameValuePair> params) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Status OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public String getJSONUrl(String url, List<NameValuePair> params) {
        System.out.print("-------------------------------------------------------------------------------------------------------------");
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Download OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result.............");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    private static final String TAG = "ERROR";
    private static final int IO_BUFFER_SIZE = 4 * 1024;

    public static Bitmap loadBitmap(String url) {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;

        try {
            in = new BufferedInputStream(new URL(url).openStream(), IO_BUFFER_SIZE);

            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
            copy(in, out);
            out.flush();

            final byte[] data = dataStream.toByteArray();
            BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inSampleSize = 1;

            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } catch (IOException e) {
            Log.e(TAG, "Could not load Bitmap from: " + url);
        } finally {
            closeStream(in);
            closeStream(out);
        }

        return bitmap;
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close stream", e);
            }
        }
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Detail Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://onokopoo.kanom/http/host/path")
        );
        AppIndex.AppIndexApi.start(client2, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Detail Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://onokopoo.kanom/http/host/path")
        );
        AppIndex.AppIndexApi.end(client2, viewAction);
        client2.disconnect();
    }
}