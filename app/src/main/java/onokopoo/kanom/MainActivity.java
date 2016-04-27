package onokopoo.kanom;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<HashMap<String, String>> MyArrList;
    Configuration config = new Configuration();
    private GoogleApiClient client2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String[] intents = {getString(R.string.intent1), getString(R.string.intent2)};

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.camera);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.intent_popup_title));
                builder.setItems(intents, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent i = new Intent(getApplicationContext(), IntentActivity.class);
                            startActivity(i);
                        } else {
                            Intent i = new Intent(getApplicationContext(), IntentGalleryActivity.class);
                            startActivity(i);
                        }
                    }
                }).setNegativeButton(getString(R.string.cancel), null);
                builder.create();
                builder.show();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        ShowData();

        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        Button history = (Button) findViewById(R.id.history);
        history.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), historyActivity.class);
                i.putExtra("type", "view");
                startActivity(i);
            }
        });
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.getChildAt(0);

        Intent intent = getIntent();
        if(intent.hasExtra("email")){
            Bundle extras = getIntent().getExtras();
            if(!extras.getString("email").equals(null)){
                String email = extras.getString("email");
                String user = extras.getString("user");
                String user_id = extras.getString("user_id");

                Config globalVariable = ((Config)getApplicationContext());

                globalVariable.setUsername(user);
                globalVariable.setEmail(email);
                globalVariable.setUserId(user_id);

                TextView navName = (TextView)headerLayout.findViewById(R.id.nav_user);
                TextView navEmail = (TextView)headerLayout.findViewById(R.id.nav_email);
                navName.setText(user);
                navEmail.setText(email);
            }
        }

        int id = item.getItemId();

        if (id == R.id.nav_main) {

        } else if (id == R.id.nav_heart) {
            //TODO:view
            Intent i = new Intent(getApplicationContext(), historyActivity.class);
            i.putExtra("type", "favorite");
            startActivity(i);
        } else if (id == R.id.nav_stack) {
            Intent i = new Intent(getApplicationContext(), CategoryActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_language) {
            if (getString(R.string.locale_config).equals("en")) {
                config.locale = new Locale("th");
            } else {
                config.locale = Locale.ENGLISH;
            }
            getResources().updateConfiguration(config, null);
            restartActivity();
        } else if (id == R.id.nav_about) {
            SharedPreferences sharedpreferences = getSharedPreferences(SignupActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
        } else if (id == R.id.nav_buy) {
            Config globalVariable = ((Config)getApplicationContext());

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"onokopoo@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "แจ้งรายละเอียดการซื้อแอปพลิเคชั่น Kanom");
            String body = "ถึง ผู้พัฒนาแอปพลิเคชั่น\n\n";
            body += "\t\tUser\t\t: "+globalVariable.getUsernme()+"\n";
            body += "\t\tEmail\t\t: "+globalVariable.getEmail()+"\n";
            body += "\nต้องการซื้อแอปพลิเคชั่นตัวเต็ม";
            i.putExtra(Intent.EXTRA_TEXT, body);
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void ShowData() {
        // listView1
        final ListView lisView1 = (ListView) findViewById(R.id.listView1);

        // keySearch
        //EditText strKeySearch = (EditText)findViewById(R.id.txtKeySearch);
        // Disbled Keyboard auto focus
        //InputMethodManager imm = (InputMethodManager)getSystemService(
        //       Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(strKeySearch.getWindowToken(), 0);

        String url = getString(R.string.url) + "/pop.php";

        // Paste Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("locale", getString(R.string.locale_config)));

        try {
            JSONArray data = new JSONArray(getJSONUrl(url, params));

            MyArrList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;

            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);

                map = new HashMap<String, String>();
                map.put("image", c.getString("image"));
                map.put("kanom_id", c.getString("kanom_id"));
                map.put("name_th", c.getString("name_th"));
                map.put("name_en", c.getString("name_en"));
                map.put("type", c.getString("type"));
                map.put("voice", c.getString("voice"));
                MyArrList.add(map);
            }

            lisView1.setAdapter(new ImageAdapter(this, MyArrList));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) lisView1.getLayoutParams();
            lp.height = 450 * data.length();
            lisView1.setLayoutParams(lp);

            // OnClick Item
            lisView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> myAdapter, View myView,
                                        int position, long mylng) {

                    String sImage = MyArrList.get(position).get("image").toString();
                    String sKanom_id = MyArrList.get(position).get("kanom_id").toString();
                    String sNameTH = MyArrList.get(position).get("name_th").toString();
                    String sNameEN = MyArrList.get(position).get("name_en").toString();
                    String sType = MyArrList.get(position).get("type").toString();
                    String sVoice = MyArrList.get(position).get("name_en").toString();

                    Intent newActivity = new Intent(MainActivity.this, DetailActivity.class);
                    newActivity.putExtra("kanom_id", sKanom_id);
                    startActivity(newActivity);

                }
            });

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
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
                "Main Page", // TODO: Define a title for the content shown.
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

    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<HashMap<String, String>> MyArr = new ArrayList<HashMap<String, String>>();

        public ImageAdapter(Context c, ArrayList<HashMap<String, String>> list) {
            // TODO Auto-generated method stub
            context = c;
            MyArr = list;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return MyArr.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_column, null);
            }

            // image
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
            imageView.getLayoutParams().height = 330;
            imageView.getLayoutParams().width = 800;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            try {
                imageView.setImageBitmap(loadBitmap(getString(R.string.url) + MyArr.get(position).get("image")));
            } catch (Exception e) {
                // When Error
                imageView.setImageResource(android.R.drawable.ic_menu_report_image);

            }

            // name th
            TextView txtNameTh = (TextView) convertView.findViewById(R.id.name_th);
            txtNameTh.setPadding(10, 0, 0, 0);
            txtNameTh.setText(MyArrList.get(position).get("name_th"));

            // name en
            TextView txtNameEn = (TextView) convertView.findViewById(R.id.name_en);
            txtNameEn.setPadding(5, 0, 0, 0);
            txtNameEn.setText(MyArrList.get(position).get("name_en"));

            // type
            TextView txtType = (TextView) convertView.findViewById(R.id.type);
            txtType.setPadding(5, 0, 0, 0);
            txtType.setText(MyArrList.get(position).get("type"));

            return convertView;
        }
    }

    @SuppressWarnings("deprecation")
    public String getJSONUrl(String url, List<NameValuePair> params) {
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

}
