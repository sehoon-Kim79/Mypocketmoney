package hui.pe.mypocketmoney;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SmsReceiver";

    /* 카드 사용 */
    private static final String WAY_CARD = "15447200";
    /* 현금인출 사용 */
    private static final String WAY_WITHDRAW = "15778000";

    @Override
    public void onReceive(Context context, Intent intent) {
        // SMS_RECEIVED에 대한 액션일때 실행
        if (intent.getAction().equals(SMS_RECEIVED)) {
            Log.d(TAG, "onReceiver() 호출");

            // Bundle을 이용해서 메세지 내용을 가져옴
            Bundle bundle = intent.getExtras();
            SmsMessage[] messages = parseSmsMessage(bundle);
            // 메세지가 있을 경우 내용을 로그로 출력해 봄
            if (messages.length > 0) {
                // 메세지의 내용을 가져옴
                String sender = messages[0].getOriginatingAddress();

                // 로그를 찍어보는 과정이므로 생략해도 됨
                Log.d(TAG, "Sender :" + sender);


                if (WAY_CARD.equals(sender)
                    || WAY_WITHDRAW.equals(sender)) {
                    String contents = messages[0].getMessageBody().toString();
                    Date receivedDate = new Date(messages[0].getTimestampMillis());

                    Log.d(TAG, "contents : " + contents);
                    Log.d(TAG, "receivedDate : " + receivedDate);

                    Spend spend = new Spend();

                    if (WAY_CARD.equals(sender)) {
                        spend.way = "C";
                    } else if (WAY_WITHDRAW.equals(sender)) {
                        spend.way = "W";
                    }
                    String[] splitContents = contents.split(" ");
                    spend.contents = splitContents[splitContents.length - 1];

                    Pattern moneyPattern = Pattern.compile("(\\d+)원");
                    Matcher matcher = moneyPattern.matcher(contents.replace(",", ""));
                    Log.i("aa","matche:" +matcher.matches());
                    String aa = "";
                    while(matcher.find()){
                        aa += matcher.group(0);
                    }
                    try {
                        spend.price = Long.valueOf(aa.replaceAll("[^\\d]", ""));
                    } catch (Exception e){}

                    if (contents.contains(")승인 ") || contents.contains("지급 ")) {
                        spend.ack = 1;
                    } else if (contents.contains(")취소 ")) {
                        spend.ack = 0;
                    } else {
                        spend.ack = -1;
                    }
                    spend.spendAt = receivedDate;
                    spend.fullText = contents;
                    spend.isMust = true;

                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
                    r.play();

                    SmsActivity.startActivity(context, spend);
                }
            }
        }
    }

    private SmsMessage[] parseSmsMessage(Bundle bundle) {
        Object[] objs = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[objs.length];

        for (int i = 0; i < objs.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String format = bundle.getString("format");
                messages[i] = SmsMessage.createFromPdu((byte[]) objs[i], format);
            } else {
                messages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);
            }
        }

        return messages;
    }


}
//    출처: http://hongku.tistory.com/209 [IT easy]}
