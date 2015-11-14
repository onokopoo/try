package onokopoo.kanom;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by onokopoo on 11/4/2015.
 */
public class IntentGalleryActivity extends Activity {
    public static final int REQUEST_GALLERY = 1;
    Bitmap bitmap;
    ImageView imageView;
    Uri uri;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intent_second);

        imageView = (ImageView)findViewById(R.id.imageView);

        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        //startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);

        //Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //startActivityForResult(i, RESULT_LOAD_IMAGE);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY);

        final Button btnBack = (Button) findViewById(R.id.btnBack);
        // Perform action on click
        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent newActivity = new Intent(IntentGalleryActivity.this, MainActivity.class);
                startActivity(newActivity);
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            uri = data.getData();
            String[] filePathColumn = {android.provider.MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri,filePathColumn,null,null,null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            try {
                Toast.makeText(getApplicationContext(), filePath, Toast.LENGTH_SHORT).show();
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                imageView.setImageBitmap(decodeSampledBitmapFromFile(filePath, 300, 300));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);

            if (cursor == null || cursor.getCount() < 1) {
                return; // no cursor or no record. DO YOUR ERROR HANDLING
            }

            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            if(columnIndex < 0) // no column index
                return; // DO YOUR ERROR HANDLING

            String picturePath = cursor.getString(columnIndex);

            cursor.close(); // close cursor

            photo = decodeFilePath(picturePath.toString());

            List<Bitmap> bitmap = new ArrayList<Bitmap>();
            bitmap.add(photo);
            ImageAdapter imageAdapter = new ImageAdapter(
                    AddIncidentScreen.this, bitmap);
            imageAdapter.notifyDataSetChanged();
            newTagImage.setAdapter(imageAdapter);
        }*/
    }
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }
}