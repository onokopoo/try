package onokopoo.kanom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity {
    String sNameTh;
    String sNameEn;
    String sNameOther;
    String sType;
    String sIndi;
    String sHistory;
    String typrDrop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Spinner dropdown = (Spinner)findViewById(R.id.type);
        String[] items = new String[]{"ประเภทนึ่ง", "ประเภทกวน", "ประเภทต้ม", "ประเภททอด", "ประเภทปิ้ง/ย่าง"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        typrDrop = dropdown.getSelectedItem().toString();

        Button add = (Button) findViewById(R.id.btn_login);
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText tNameTh = (EditText) findViewById(R.id.tNameTh);
                sNameTh = tNameTh.getText().toString();

                EditText tNameEn = (EditText) findViewById(R.id.tNameEn);
                sNameEn = tNameEn.getText().toString();

                EditText tNameOther = (EditText) findViewById(R.id.tNameOther);
                sNameOther = tNameOther.getText().toString();

                EditText tIndi = (EditText) findViewById(R.id.tIndi);
                sIndi = tIndi.getText().toString();

                EditText tHistory = (EditText) findViewById(R.id.tHistory);
                sHistory = tHistory.getText().toString();

                switch (typrDrop){
                    case "ประเภทนึ่ง":
                        sType = "1";
                        break;
                    case "ประเภทกวน":
                        sType = "2";
                        break;
                    case "ประเภทต้ม":
                        sType = "3";
                        break;
                    case "ประเภททอด":
                        sType = "4";
                        break;
                    case "ประเภทปิ้ง/ย่าง":
                        sType = "5";
                        break;
                }

                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("NameTh", sNameTh));
                params.add(new BasicNameValuePair("NameEn", sNameEn));
                params.add(new BasicNameValuePair("NameOt", sNameOther));
                params.add(new BasicNameValuePair("type", sType));
                params.add(new BasicNameValuePair("In", sIndi));
                params.add(new BasicNameValuePair("History", sHistory));

                ServiceHandler sh = new ServiceHandler();
                String sd = sh.makeServiceCall(Config.URL_ADD, ServiceHandler.POST, params);

            }
        });

    }
}
