package hui.pe.mypocketmoney.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hui.pe.mypocketmoney.SmsActivity;
import hui.pe.mypocketmoney.Spend;

public class DBHelper extends SQLiteOpenHelper {

    private static SimpleDateFormat FORMAT_DB_DATETIME = new SimpleDateFormat("yyyyMMddHHmmss");
    public static SimpleDateFormat FORMAT_TIME = new SimpleDateFormat("HH:mm");

    /*
        way
        0.식료품구입
        1.외식 & 군것질
        2.점심
        3.담배
        4.유흥
        5.자동차유지비
        6.쇼핑
        7.교통비
        8.공과금
        9.잡비
     */
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

     @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE SPENDS (_id INTEGER PRIMARY KEY AUTOINCREMENT, ack INTEGER, type INTEGER, way TEXT, contents TEXT, price NUMBER, spend_at TEXT, full_text TEXT)");

//        db.execSQL("INSERT INTO SPENDS VALUES(null, '1', '1', 'C', '치킨마루 목감점', '6000', '20180720101013', 'abbb')");
//        db.execSQL("INSERT INTO SPENDS VALUES(null, '1', '2', 'C', '착한통닭 안양점', '5000', '20180722101113', 'bccc')");
//        db.execSQL("INSERT INTO SPENDS VALUES(null, '1', '4', 'C', '롯떼마트', '1000', '20180722101213', 'bccc')");
//        db.execSQL("INSERT INTO SPENDS VALUES(null, '0', '2', 'C', '착한통닭 안양점', '5000', '20180722101313', 'bccc')");
//        db.execSQL("INSERT INTO SPENDS VALUES(null, '1', '3', 'C', 'GS25', '4500', '20180722101413', 'bccc')");
//        db.execSQL("INSERT INTO SPENDS VALUES(null, '1', '3', 'C', 'GS25', '4500', '20180722101513', 'bccc')");
//         db.execSQL("INSERT INTO SPENDS VALUES(null, '1', '1', 'C', '치킨마루 목감점', '6000', '20180720101013', 'abbb')");
//         db.execSQL("INSERT INTO SPENDS VALUES(null, '1', '2', 'C', '착한통닭 안양점', '5000', '20180722101113', 'bccc')");
//         db.execSQL("INSERT INTO SPENDS VALUES(null, '1', '4', 'C', '롯떼마트', '1000', '20180722101213', 'bccc')");
//         db.execSQL("INSERT INTO SPENDS VALUES(null, '0', '2', 'C', '착한통닭 안양점', '5000', '20180722101313', 'bccc')");
//         db.execSQL("INSERT INTO SPENDS VALUES(null, '1', '3', 'C', 'GS25', '4500', '20180722101413', 'bccc')");
//         db.execSQL("INSERT INTO SPENDS VALUES(null, '1', '3', 'C', 'GS25', '4500', '20180722101513', 'bccc')");
//
//         db.execSQL("INSERT INTO SPENDS VALUES(null, '1', '4', 'C', '롯떼마트y', '1000', '20180721101213', 'bccc')");
//         db.execSQL("INSERT INTO SPENDS VALUES(null, '0', '2', 'C', '착한통닭y 안양점', '5000', '20180721101313', 'bccc')");
//         db.execSQL("INSERT INTO SPENDS VALUES(null, '1', '3', 'C', 'GS25y', '4500', '20180721101413', 'bccc')");
//         db.execSQL("INSERT INTO SPENDS VALUES(null, '1', '3', 'C', 'GS25y', '4500', '20180720115959', 'bccc')");
//

     }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 가격 정보 수정
//        db.execSQL("INSERT INTO SPENDS VALUES(null, '2', 'C', '치킨마루 목감점', '6000', '"+ new Date().parse("Jul 20, 2018 10:10:13") +"', 'abbb')");
//        db.execSQL("INSERT INTO SPENDS VALUES(null, '3', 'C', '착한통닭 안양점', '5000', '"+ new Date().parse("Jul 20, 2018 11:10:14") +"', 'bccc')");
//        db.execSQL("INSERT INTO SPENDS VALUES(null, '5', 'C', '롯떼마트', '1000', '"+ new Date().parse("Jul 20, 2018 12:10:15") +"', 'bccc')");
//        db.execSQL("INSERT INTO SPENDS VALUES(null, '4', 'C', 'GS25', '4500', '"+ new Date().parse("Jul 21, 2018 09:10:13") +"', 'bccc')");
//        db.execSQL("INSERT INTO SPENDS VALUES(null, '4', 'C', 'GS25', '4500', '"+ new Date().parse("Jul 21, 2018 10:12:14") +"', 'bccc')");
    }

    public void save(Spend spend) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO SPENDS VALUES(null, ?, ?, ?, ?, ?, ?, ?)", new Object[] {spend.ack, spend.type, spend.way,  spend.contents, spend.price, FORMAT_DB_DATETIME.format(spend.spendAt), spend.fullText});
    }

    public Map<Integer, Long> getTotalByType() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT type, SUM(price) FROM SPENDS WHERE ack = '1' GROUP BY type", null);

        HashMap<Integer, Long> result = new HashMap<>();

        while (cursor.moveToNext()) {
            result.put(cursor.getInt(0), cursor.getLong(1));
        }

        cursor = db.rawQuery("SELECT type, SUM(price) FROM SPENDS WHERE ack = '0' GROUP BY type", null);
        while (cursor.moveToNext()) {
            long money = result.get(cursor.getInt(0));
            result.put(cursor.getInt(0), money - cursor.getLong(1));
        }

        return result;
    }

    public HashMap<Integer, Object> getTodaySpends() {
        SQLiteDatabase db = getReadableDatabase();

        HashMap<Integer, Object> result = new HashMap<>();

        long totalMoney = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String yesterday = format.format(new Date(new Date().getTime() + (1000*60*60*24*-1)));

        Log.d("aaaabbb", yesterday);
        Cursor cursor = db.rawQuery("SELECT ack, type, way, contents, price, spend_at FROM SPENDS WHERE spend_at > ?", new String[] {yesterday+"240000"});
        String[] raw = new String[cursor.getCount()];
        for (int i=0; cursor.moveToNext(); i++) {
            int ack = cursor.getInt(0);
            int type = cursor.getInt(1);
            String way = cursor.getString(2);
            String contents = cursor.getString(3);
            long price = cursor.getLong(4);
            String spendAt = cursor.getString(5);

            String ackStr = null;
            if (ack == 1) {
                ackStr = "승인";
                totalMoney = totalMoney + price;
            } else if (ack == 0) {
                totalMoney = totalMoney - price;
                ackStr = "취소";
            }
            try {
                raw[i] = String.format("%s, %s, %s, %d원, %s", FORMAT_TIME.format(FORMAT_DB_DATETIME.parse(spendAt)), way, ackStr, price, contents);
            } catch (Exception e){}
        }
        result.put(0, raw);
        result.put(1, totalMoney);
        return result;
    }

    public long getYesterDayTotal() {
        SQLiteDatabase db = getReadableDatabase();

        long totalMoney = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String theDayOfYesterday = format.format(new Date(new Date().getTime() + (1000*60*60*24*-2)));
        String today = format.format(new Date());
        Log.d("aaaabbb", theDayOfYesterday);
        Log.d("aaaabbb", today);
        Cursor cursor = db.rawQuery("SELECT ack, price FROM SPENDS WHERE spend_at > ? AND spend_at < ?", new String[] {theDayOfYesterday+"240000", today+"000000"});
        String[] raw = new String[cursor.getCount()];
        for (int i=0; cursor.moveToNext(); i++) {
            int ack = cursor.getInt(0);
            long price = cursor.getLong(1);

            if (ack == 1) {
                totalMoney = totalMoney + price;
            } else if (ack == 0) {
                totalMoney = totalMoney - price;
            }
        }
        return totalMoney;
    }

    public long getTotalByThisMonth() {
        SQLiteDatabase db = getReadableDatabase();

        long result = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(price) FROM SPENDS WHERE ack = '1'", null);

        while (cursor.moveToNext()) {
            result = result + cursor.getLong(0);
        }
        cursor = db.rawQuery("SELECT SUM(price) FROM SPENDS WHERE ack = '0'", null);
        while (cursor.moveToNext()) {
            result = result - cursor.getLong(0);
        }
        return result;
    }

    public long getAverage() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(price) FROM (SELECT substr(spend_at, 0, 9) AS ara, price FROM SPENDS) GROUP BY ara ", null);

        long result = 0;
        while (cursor.moveToNext()) {
            result = result + cursor.getLong(0);
        }
        result = result / cursor.getCount();

        return result;
    }
}
