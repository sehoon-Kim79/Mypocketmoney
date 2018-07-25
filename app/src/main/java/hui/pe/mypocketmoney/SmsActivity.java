package hui.pe.mypocketmoney;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsActivity extends Activity {

    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd일 HH시 mm분");

    /*
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
    public final String[] items = new String[]{"식료품구입", "외식&군것질", "점심", "담배", "유흥", "자동차유지비", "쇼핑", "교통비", "공과금", "잡비"};

    private Spend spend;

    ListView spendsToday;
    TextView spendInfo;
    TextView spendInfo2;
    TextView ackText;
    EditText fullText;
    TextView receivedDateText;
    TextView contentsText;
    EditText moneyUI;
    Button button;
    Spinner waySelectBox;

    // 액티비티로 메세지의 내용을 전달해줌
    public static void startActivity(Context context, Spend spend) {
        Intent intent = new Intent(context, SmsActivity.class);

        // Flag 설정
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
//        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        // 메세지의 내용을 Extra에 넣어줌
        intent.putExtra("spend", spend);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        spendsToday = (ListView) findViewById(R.id.spendsToday);
        spendInfo = (TextView) findViewById(R.id.spendInfo);
        spendInfo2 = (TextView) findViewById(R.id.spendInfo2);

        fullText = (EditText) findViewById(R.id.fullText);
        waySelectBox = (Spinner) findViewById(R.id.way);
        ArrayAdapter sAdapter = ArrayAdapter.createFromResource(this, R.array.way, android.R.layout.simple_spinner_dropdown_item);
        waySelectBox.setAdapter(sAdapter);

        ackText = (TextView) findViewById(R.id.ack);
        receivedDateText = (TextView) findViewById(R.id.receivedDateText);
        contentsText = (TextView) findViewById(R.id.contents);
        moneyUI = (EditText) findViewById(R.id.money);

        moneyUI.addTextChangedListener(new TextWatcher() {
            String result;

            public void afterTextChanged(Editable s) { }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && !s.toString().equals(result)) {
                    result = Common.FORMAT_MONEY.format(Double.parseDouble(s.toString().replaceAll(",", "")));
                    moneyUI.setText(result);
                    moneyUI.setSelection(result.length());
                }
            }
        });

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long money = -1;
                try {
                    money = Long.valueOf(moneyUI.getText().toString().replaceAll(",", ""));
                } catch (Exception e) { }
                if (money < 1) {
                    Toast.makeText(SmsActivity.this, "돈 넣어라~", Toast.LENGTH_SHORT).show();
                    return;
                }
                spend.price = money;

                final int[] selectedIndex = {0};
                AlertDialog.Builder dialog = new AlertDialog.Builder(SmsActivity.this);
                dialog.setTitle("어떤 지출 입니까?")
                .setSingleChoiceItems(items,
                        -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                selectedIndex[0] = i;
                            }
                        })
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index) {
                        spend.type = selectedIndex[0];

                        App.dbHelper.save(spend);

                        SmsActivity.this.finishAndRemoveTask();

                        try {
                            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                            List<ActivityManager.AppTask> tasks = activityManager.getAppTasks();
                            for (ActivityManager.AppTask task : tasks) {
                                if (".SmsActivity".equals(task.getTaskInfo().topActivity.getShortClassName())) {
                                    task.moveToFront();
                                    Log.d("aaaaaㅇ", "task.moveToFront()");
                                    break;
                                }
                            }
                            for (ActivityManager.AppTask task : tasks) {
                                if (".MainActivity".equals(task.getTaskInfo().topActivity.getShortClassName())) {
                                    task.moveToFront();
                                    Log.d("aaaaaㅇ", "task.moveToFront()");
                                    break;
                                }
                            }
                        } catch (Exception e) {}
                    }
                }).create().show();
            }
        });
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i("aa","onNewIntent");
        super.onNewIntent(intent);
        spend = (Spend) intent.getSerializableExtra("spend");

        if (spend.price > 0) {
            moneyUI.setText(spend.price + "");
        }
        ackText.setText(spend.ack == 0? "승인취소" : "승인");
        receivedDateText.setText(format.format(spend.spendAt));
        fullText.setText(spend.fullText);
        contentsText.setText(spend.contents);

//        Log.d("aaaspend.way", spend.way);
        if ("C".equals(spend.way)) {
            waySelectBox.setSelection(0);
            waySelectBox.setEnabled(false);
        } else if ("W".equals(spend.way)){
            waySelectBox.setSelection(1);
            waySelectBox.setEnabled(false);
        } else {
            waySelectBox.setSelection(2);
        }

        long yesterDayTotal = App.dbHelper.getYesterDayTotal();

        HashMap<Integer, Object> spendsResult = App.dbHelper.getTodaySpends();

        long average = App.dbHelper.getAverage();

        String[] spends = (String[]) spendsResult.get(0);
        long todayTotal = (long) spendsResult.get(1);

        spendInfo.setText("오늘 "+Common.FORMAT_MONEY.format(todayTotal)+"원 사용");
        spendInfo2.setText("평균 "+Common.FORMAT_MONEY.format(average)+"원 사용");

        TextView spendInfo3 = (TextView) findViewById(R.id.spendInfo3);
        spendInfo3.setText("어제 "+Common.FORMAT_MONEY.format(yesterDayTotal)+"원 사용");

        if (yesterDayTotal < todayTotal) {
            spendInfo.setTextColor(Color.RED);
        } else {
            spendInfo.setTextColor(Color.BLUE);
        }
        if (average < yesterDayTotal) {
            spendInfo3.setTextColor(Color.RED);
        } else {
            spendInfo3.setTextColor(Color.BLUE);
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, spends);
        spendsToday.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (spend.isMust) {
            Toast.makeText(this, "강제 입력 모드입니다. 입력해주세요", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
}
