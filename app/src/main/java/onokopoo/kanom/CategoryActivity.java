package onokopoo.kanom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class CategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button t1 = (Button) findViewById(R.id.t1);
        Button t2 = (Button) findViewById(R.id.t2);
        Button t3 = (Button) findViewById(R.id.t3);
        Button t4 = (Button) findViewById(R.id.t4);
        Button t5 = (Button) findViewById(R.id.t5);

        t1.setOnClickListener(myOnlyhandler);
        t2.setOnClickListener(myOnlyhandler);
        t3.setOnClickListener(myOnlyhandler);
        t4.setOnClickListener(myOnlyhandler);
        t5.setOnClickListener(myOnlyhandler);
    }
    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            switch(v.getId()) {
                case R.id.t1:
                    intent.putExtra("type", "1");
                    break;
                case R.id.t2:
                    intent.putExtra("type", "2");
                    break;
                case R.id.t3:
                    intent.putExtra("type", "3");
                    break;
                case R.id.t4:
                    intent.putExtra("type", "4");
                    break;
                case R.id.t5:
                    intent.putExtra("type", "5");
                    break;
            }
            startActivity(intent);
        }
    };
}
