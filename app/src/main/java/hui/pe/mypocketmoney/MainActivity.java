package hui.pe.mypocketmoney;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.Map;

public class MainActivity extends Activity {

    private static final String TAG = "TAG";
    private static final int MY_PERMISSIONS_READ_SMS = 1;
    private static final int MY_PERMISSIONS_RECEIVE_SMS = 2;

    TextView viewMonthlyTotal;
    Button bt_insert;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewMonthlyTotal = (TextView) findViewById(R.id.viewMonthlyTotal);
        bt_insert = (Button) findViewById(R.id.bt_insert);

        bt_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spend spend = new Spend();
                spend.spendAt = new Date();
                spend.way = "O";
                spend.ack = 1;

                SmsActivity.startActivity(MainActivity.this, spend);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED |
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                    MY_PERMISSIONS_READ_SMS);
        }

        onNewIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        long monthlyTotal = App.dbHelper.getTotalByThisMonth();
        viewMonthlyTotal.setText(Common.FORMAT_MONEY.format(monthlyTotal));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String message = null;
        switch (requestCode) {
            case MY_PERMISSIONS_READ_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    message = "READ_SMS permission is granted";
                    Log.d("", message);
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    message = "READ_SMS permission is NOT granted";
                    Log.d("", message);
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                }
            } case MY_PERMISSIONS_RECEIVE_SMS : {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    message = "RECEIVE_SMS permission is granted";
                    Log.d("", message);
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    message = "RECEIVE_SMS permission is NOT granted";
                    Log.d("", message);
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                }

            }

        }
    }
}
