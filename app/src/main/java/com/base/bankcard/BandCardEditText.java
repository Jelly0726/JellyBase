package com.base.bankcard;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.widget.EditText;

import com.base.httpmvp.presenter.GetBankPresenter;
import com.base.httpmvp.view.IGetBankView;

import java.util.Map;
import java.util.TreeMap;

/**
 * 欢迎关注微信公众号：aikaifa
 */
public class BandCardEditText extends EditText implements IGetBankView{

    private boolean shouldStopChange = false;
    private final String space = " ";

    private BankCardListener listener;
    private GetBankPresenter getBankPresenter;

    public BandCardEditText(Context context) {
        this(context, null);
    }

    public BandCardEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BandCardEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getBankPresenter=new GetBankPresenter(this);
        init();
    }

    private void init() {
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(23)});
        //setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);//限制输入类型
        setKeyListener(DigitsKeyListener.getInstance("0123456789\\u0000"));//限制输入固定的某些字符
        format(getText());
        shouldStopChange = false;
        setFocusable(true);
        setEnabled(true);
        setFocusableInTouchMode(true);
        addTextChangedListener(new BandCardWatcher());
    }

    /**
     * 获取未格式化的卡号
     * @return
     */
    public String getCardNo() {
        return getText().toString().trim().replaceAll(space, "");
    }

    /**
     * 获取格式化后的卡号
     * @return
     */
    @Override
    public Editable getText() {
        return super.getText();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void closeProgress() {

    }

    @Override
    public Object getBankParam() {
        String bankNo=getText().toString().trim().replaceAll(space, "");
        Map<String,String> map=new TreeMap<>();
        map.put("cardNo",bankNo);
        return map;
    }

    @Override
    public void getBankSuccess( Object mCallBackVo) {
        BankCardInfo bankCardInfo= (BankCardInfo) mCallBackVo;
        String name="";
        String type="";
        if (bankCardInfo!=null){
            if (!TextUtils.isEmpty(bankCardInfo.getBankName())){
                name=bankCardInfo.getBankName();
                type=bankCardInfo.getType();
            }
        }
        if (listener != null) {
            if (name.trim().length()>0) {
                listener.success(name,type);
            } else {
                listener.failure();
            }
        }
    }

    @Override
    public void getBankFailed(String message) {
        if (listener != null) {
            listener.failure();
        }
    }

    class BandCardWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            format(editable);
        }
    }

    private void format(Editable editable) {
        if (shouldStopChange) {
            shouldStopChange = false;
            return;
        }

        shouldStopChange = true;

        String str = editable.toString().trim().replaceAll(space, "");
        int len = str.length();
        int courPos;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            builder.append(str.charAt(i));
            if (i == 3 || i == 7 || i == 11 || i == 15) {
                if (i != len - 1)
                    builder.append(space);
            }
        }
        courPos = builder.length();
        setText(builder.toString());
        setSelection(courPos);
        if(courPos==19||courPos==22||courPos==23){
            getBankPresenter.getBank(null);
        }else {
            if (listener != null) {
                listener.failure();
            }
        }
//        if (listener != null) {
////            if (isBankCard()) {
////                char[] ss = getBankCardText().toCharArray();
////                listener.success(BankCardInfo.getNameOfBank(ss, 0));
////            } else {
////                listener.failure();
////            }
//            if(courPos<19)return;
//            String bankname=BankUtil.getNameOfBank(getBankCardText());
//            if (bankname.trim().length()>0) {
//                listener.success(bankname);
//            } else {
//                listener.failure();
//            }
//        }
    }

    public String getBankCardText() {
        return getText().toString().trim().replaceAll(" ", "");
    }

    public boolean isBankCard() {
        return checkBankCard(getBankCardText());
    }

    /**
     * 校验银行卡卡号
     * @param cardId
     * @return
     */
    public boolean checkBankCard(String cardId) {
        if(TextUtils.isEmpty(cardId)) {
            return  false;
        }
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }


    /**
     *
     * @param nonCheckCodeCardId
     * @return
     */
    public char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (TextUtils.isEmpty(nonCheckCodeCardId)
                || !nonCheckCodeCardId.matches("\\d+")
                || nonCheckCodeCardId.length() < 16
                || nonCheckCodeCardId.length() > 19) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int sum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            sum += k;
        }
        return (sum % 10 == 0) ? '0' : (char) ((10 - sum % 10) + '0');
    }

    public void setBankCardListener(BankCardListener listener) {
        this.listener = listener;
    }

    public interface BankCardListener {
        void success(String name,String type);

        void failure();
    }
}
