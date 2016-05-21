package onokopoo.kanom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Upload extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String filePath = null;
    long totalSize = 0;
    ProgressDialog dialog = null;
    String upLoadServerUri = null;
    String id[] = new String[3];
    String acc[]= new String[3];
    String name[]= new String[3];
    File file;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        file = new File(getIntent().getExtras().getString("url"));
        this.filePath = file.getAbsolutePath();

        upLoadServerUri = Config.FILE_UPLOAD_URL;

        dialog = ProgressDialog.show(Upload.this, "", getResources().getString(R.string.upload), true);
        new UploadFileToServer().execute();

    }

    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(upLoadServerUri);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity();

                File sourceFile = new File(filePath);

                entity.addPart("image", new FileBody(sourceFile));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }

        public String getJsonId(String i) throws JSONException {
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("kanom_id", i));
            params.add(new BasicNameValuePair("locale", getString(R.string.locale_config)));

            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(Config.URL_SELECT_KANOM, ServiceHandler.POST, params);

            JSONObject data = new JSONObject(jsonStr);

            JSONArray info = data.getJSONArray("info");
            JSONObject c = info.getJSONObject(0);
            String strNameTh = c.getString("name_th");
            String strNameEn = c.getString("name_en");
            if(getString(R.string.locale_config).equals("en")){
                return strNameEn;
            }
            return strNameTh;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "Response from server: " + result);

            String jsonStr = POST("http://10.35.23.78:8080", result);
            try {
                JSONObject answerobj = new JSONObject(jsonStr);
                String answerStr = answerobj.getString("answer");

                JSONObject resultobj = new JSONObject(answerStr);
                JSONArray resultArr = resultobj.getJSONArray("result");

                JSONObject json0 = resultArr.getJSONObject(0);
                id[0] = json0.getString("id");
                acc[0] = json0.getString("accuracy");
                //name[0]= "\t\t\t\t"+acc[0]+" : "+getJsonId(id[0]);
                name[0].format("%-8s %s", getJsonId(id[0]), acc[0]);

                JSONObject json1 = resultArr.getJSONObject(1);
                id[1] = json1.getString("id");
                acc[1] = json1.getString("accuracy");
                name[1]= acc[1]+"\t\t\t\t :"+getJsonId(id[1]);

                JSONObject json2 = resultArr.getJSONObject(2);
                id[2] = json2.getString("id");
                acc[2] = json2.getString("accuracy");
                name[2]= acc[2]+"\t\t\t\t :"+getJsonId(id[2]);

                Log.i(TAG, String.valueOf(resultArr));
                showAlert();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //showAlert(jsonStr);
            //Log.i(TAG,jsonStr);
            super.onPostExecute(result);
        }
    }

    /**
     * Method to show alert dialog
     * */
    private AlertDialog showAlert() {
        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO: do nothing

                    }
                });

        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        */
        //String[] test = new String[]{"red", "blue"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.response)
                .setItems(name, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, String.valueOf(which));
                        finish();

                        Intent newActivity = new Intent(Upload.this, DetailActivity.class);
                        newActivity.putExtra("kanom_id", id[which]);
                        startActivity(newActivity);
                    }
                });
        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        return builder.create();
    }

    public static String POST(String url, String jsonStr){
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            String json = "";
            JSONObject jsonObject = new JSONObject(jsonStr);
            json = jsonObject.toString();

            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}