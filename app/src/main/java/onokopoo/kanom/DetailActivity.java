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
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class DetailActivity extends AppCompatActivity {
    MediaPlayer mp;
    public static String Type;
    public static Boolean Check = false;
    public String finalStrVoice;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private GoogleApiClient client2;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        checkFavorite();
        addView();
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), getResources().getString(R.string.detail));
        adapter.addFragment(new TwoFragment(), getResources().getString(R.string.detail_ingredient));
        adapter.addFragment(new ThreeFragment(), getResources().getString(R.string.detail_recipe));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
            Check = jsonObj.getBoolean("check");

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

        try {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(Config.URL_SELECT_KANOM, ServiceHandler.POST, params);

            JSONObject data = new JSONObject(jsonStr);

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
            finalStrVoice = strVoice;

            // TODO :subtitle
            Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
            if(getString(R.string.locale_config).equals("en")){
                toolbar.setTitle(strNameEn);
            } else if(getString(R.string.locale_config).equals("th")){
                toolbar.setTitle(strNameTh);
            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem check_fav = menu.findItem(R.id.action_star);
        check_fav.setChecked(Check);

        if(check_fav.isChecked()){
            check_fav.setIcon(R.drawable.ic_star2);
        } else {
            check_fav.setIcon(R.drawable.ic_star);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_star:
                if (item.isChecked()){
                    item.setChecked(false);
                    item.setIcon(R.drawable.ic_star);
                    Type = "0";
                } else {
                    item.setChecked(true);
                    item.setIcon(R.drawable.ic_star2);
                    Type = "1";
                }
                addOrRemoveFavorite(Type);
                return true;
            case R.id.action_mp:
                managerOfSound(finalStrVoice);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
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

    private class OneFragment extends Fragment {
    }

    private class TwoFragment extends Fragment {
    }

    private class ThreeFragment extends Fragment {
    }
}