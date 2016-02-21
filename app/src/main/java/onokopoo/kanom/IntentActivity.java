package onokopoo.kanom;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IntentActivity extends Activity {
    public static final int REQUEST_CAMERA = 2;
    ImageView imageView;
    Uri uri;
    String imgPath;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intent_second);

        imageView = (ImageView)findViewById(R.id.imageView);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";
        File f = new File(Environment.getExternalStorageDirectory()
                , "DCIM/Camera/kanom/" + imageFileName);
        this.imgPath = f.getAbsolutePath();
        uri = Uri.fromFile(f);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(Intent.createChooser(intent
                , "Take a picture with"), REQUEST_CAMERA);


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            getContentResolver().notifyChange(uri, null);
            ContentResolver cr = getContentResolver();
//            Uri testUri = data.getData();
            try {
                imageView.setImageBitmap(decodeSampledBitmapFromFile(uri.getPath(), 300, 300));
                //Toast.makeText(getApplicationContext(), uri.getPath(), Toast.LENGTH_SHORT).show();
 //               Toast.makeText(getApplicationContext(), testUri.getPath(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Upload.class);
                intent.putExtra("url", uri.getPath());
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }
}