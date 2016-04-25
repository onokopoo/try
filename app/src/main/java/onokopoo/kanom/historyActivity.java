package onokopoo.kanom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.HashMap;

@SuppressWarnings("deprecation")
public class historyActivity extends Activity {

    ArrayList<HashMap<String, String>> MyArrList;
    String type;
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        type = getIntent().getExtras().getString("type");
        ShowData(type);
    }

    public void ShowData(String type) {

        final ListView lisView1 = (ListView) findViewById(R.id.listView1);

        Config globalVariable = ((Config) getApplicationContext());
        String userId = globalVariable.getUserId();

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("type", type));
        params.add(new BasicNameValuePair("user_id", userId));

        try {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(Config.URL_HISTORY, ServiceHandler.POST, params);

            JSONArray data = new JSONArray(jsonStr);

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

                    Intent newActivity = new Intent(historyActivity.this, DetailActivity.class);
                    newActivity.putExtra("kanom_id", sKanom_id);
                    startActivity(newActivity);
                }
            });

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public class ImageAdapter extends BaseAdapter
    {
        private Context context;
        private ArrayList<HashMap<String, String>> MyArr = new ArrayList<HashMap<String, String>>();

        public ImageAdapter(Context c, ArrayList<HashMap<String, String>> list)
        {
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_column, null);
            }

            // image
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
            imageView.getLayoutParams().height = 250;
            imageView.getLayoutParams().width = 800;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            try
            {
                imageView.setImageBitmap(loadBitmap(getString(R.string.url)+MyArr.get(position).get("image")));
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

            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);
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
                android.util.Log.e(TAG, "Could not close stream", e);
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
