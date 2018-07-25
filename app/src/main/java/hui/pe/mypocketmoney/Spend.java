package hui.pe.mypocketmoney;

import java.io.Serializable;
import java.util.Date;

public class Spend implements Serializable {
    /* 반듯이 입력해야하는지 설정 */
    public boolean isMust;
    /* 어떤 지출인지 ex:군것질, 쇼핑 */
    public int type;
    /* 지출방법 C:카드 W:출금 O:현금 */
    public String way;
    /* 승인여부 1:승인 또는 지급 0:승인취소 */
    public int ack;
    /* 지출내역 */
    public String contents;
    /* 가격 */
    public long price;
    /* 지출일자 */
    public Date spendAt;
    /* 전체 텍스트 */
    public String fullText;
}
