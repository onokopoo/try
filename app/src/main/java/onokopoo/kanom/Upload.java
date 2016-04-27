package onokopoo.kanom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

public class Upload extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String filePath = null;
    long totalSize = 0;
    ProgressDialog dialog = null;
    String upLoadServerUri = null;

    /**********  File Path *************/
    File file;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        file = new File(getIntent().getExtras().getString("url"));
        this.filePath = file.getAbsolutePath();

        upLoadServerUri = Config.FILE_UPLOAD_URL;

        dialog = ProgressDialog.show(Upload.this, "", "Uploading file...", true);
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

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                //entity.addPart("website",
                //        new StringBody("www.androidhive.info"));
                //entity.addPart("email", new StringBody("abc@gmail.com"));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
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

        @Override
        protected void onPostExecute(String result) {
            Log.e( TAG, "Response from server: " + result);

            // showing the server response in an alert dialog
            showAlert(result);

            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
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
        } });
        AlertDialog alert = builder.create();
        alert.show();
    }
}