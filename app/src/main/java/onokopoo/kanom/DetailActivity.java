package onokopoo.kanom;

/**
 * Created by onokopoo on 11/14/2015.
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class DetailActivity extends Activity {

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        showInfo();

        // btnBack
        final Button btnBack = (Button) findViewById(R.id.btnBack);
        // Perform action on click
        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent newActivity = new Intent(DetailActivity.this,MainActivity.class);
                startActivity(newActivity);
            }
        });
    }

    public void showInfo()
    {
        final ImageView image   = (ImageView)findViewById(R.id.image);
        final TextView tNameTh  = (TextView)findViewById(R.id.name_th);
        final TextView tNameEn  = (TextView)findViewById(R.id.name_en);
        //final TextView tNameOther = (TextView)findViewById(R.id.name_other);
        final TextView tType    = (TextView)findViewById(R.id.type);
        //final TextView tIngredient = (TextView)findViewById(R.id.txtEmail);
        //final TextView tRecipe = (TextView)findViewById(R.id.txtTel);

        //String url = "http://10.35.23.50/selectKanom.php";
        String url = "http://192.168.1.12/selectKanom.php";

        Intent intent= getIntent();
        final String sKanom_id = intent.getStringExtra("kanom_id");

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("kanom_id", sKanom_id));

        String strKanomId = "";
        String strNameTh = "";
        String strNameEn = "";
        String strNameOther = "";
        String strType = "";
        String strimage = "";
        String strIngredient = "";
        String strRecipe = "";

        //JSONObject c;
        try {
            JSONArray data = new JSONArray(getJSONUrl(url,params));
            JSONObject c = data.getJSONObject(0);
            strNameTh       = c.getString("name_th");
            strNameEn       = c.getString("name_en");
            //strNameOther    = c.getString("name_other");
            strType         = c.getString("type");
            //strIngredient   = c.getString("voice");
            //strRecipe       = c.getString("image");

            if(!strNameTh.equals(""))
            {
                tNameTh.setText(strNameTh);
                tNameEn.setText(strNameEn);
                //tNameOther.setText(strNameOther);
                tType.setText(strType);
                //tIngredient.setText(strIngredient);
                //tRecipe.setText(strRecipe);
            }
            else
            {
                tNameTh.setText("-");
                tNameEn.setText("-");
                //tNameOther.setText("-");
                tType.setText("-");
                //tIngredient.setText("-");
                //tRecipe.setText("-");
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String getHttpPost(String url,List<NameValuePair> params) {
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

    public String getJSONUrl(String url,List<NameValuePair> params) {
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
}