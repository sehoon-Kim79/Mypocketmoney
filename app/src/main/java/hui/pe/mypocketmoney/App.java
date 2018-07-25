package hui.pe.mypocketmoney;

import android.app.Application;

import hui.pe.mypocketmoney.data.DBHelper;

public class App extends Application {

    static DBHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DBHelper(this, "pocketMoney.db", null, 1);
    }
}
