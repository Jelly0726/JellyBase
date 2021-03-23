package com.base.bankcard;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.base.appManager.FixMemLeak;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 欢迎关注微信公众号：aikaifa
 */
public class BandCardEditText extends EditText {
    private boolean shouldStopChange = false;
    private final String space = " ";

    private BankCardListener listener;
    private AsyncTask<String, Integer, String> task;
    private final String bankName = "{\n" +
            "    \"SRCB\":\"深圳农村商业银行\",\n" +
            "    \"BGB\":\"广西北部湾银行\",\n" +
            "    \"SHRCB\":\"上海农村商业银行\",\n" +
            "    \"BJBANK\":\"北京银行\",\n" +
            "    \"WHCCB\":\"威海市商业银行\",\n" +
            "    \"BOZK\":\"周口银行\",\n" +
            "    \"KORLABANK\":\"库尔勒市商业银行\",\n" +
            "    \"SPABANK\":\"平安银行\",\n" +
            "    \"SDEB\":\"顺德农商银行\",\n" +
            "    \"HURCB\":\"湖北省农村信用社\",\n" +
            "    \"WRCB\":\"无锡农村商业银行\",\n" +
            "    \"BOCY\":\"朝阳银行\",\n" +
            "    \"CZBANK\":\"浙商银行\",\n" +
            "    \"HDBANK\":\"邯郸银行\",\n" +
            "    \"BOC\":\"中国银行\",\n" +
            "    \"BOD\":\"东莞银行\",\n" +
            "    \"CCB\":\"中国建设银行\",\n" +
            "    \"ZYCBANK\":\"遵义市商业银行\",\n" +
            "    \"SXCB\":\"绍兴银行\",\n" +
            "    \"GZRCU\":\"贵州省农村信用社\",\n" +
            "    \"ZJKCCB\":\"张家口市商业银行\",\n" +
            "    \"BOJZ\":\"锦州银行\",\n" +
            "    \"BOP\":\"平顶山银行\",\n" +
            "    \"HKB\":\"汉口银行\",\n" +
            "    \"SPDB\":\"上海浦东发展银行\",\n" +
            "    \"NXRCU\":\"宁夏黄河农村商业银行\",\n" +
            "    \"NYNB\":\"广东南粤银行\",\n" +
            "    \"GRCB\":\"广州农商银行\",\n" +
            "    \"BOSZ\":\"苏州银行\",\n" +
            "    \"HZCB\":\"杭州银行\",\n" +
            "    \"HSBK\":\"衡水银行\",\n" +
            "    \"HBC\":\"湖北银行\",\n" +
            "    \"JXBANK\":\"嘉兴银行\",\n" +
            "    \"HRXJB\":\"华融湘江银行\",\n" +
            "    \"BODD\":\"丹东银行\",\n" +
            "    \"AYCB\":\"安阳银行\",\n" +
            "    \"EGBANK\":\"恒丰银行\",\n" +
            "    \"CDB\":\"国家开发银行\",\n" +
            "    \"TCRCB\":\"江苏太仓农村商业银行\",\n" +
            "    \"NJCB\":\"南京银行\",\n" +
            "    \"ZZBANK\":\"郑州银行\",\n" +
            "    \"DYCB\":\"德阳商业银行\",\n" +
            "    \"YBCCB\":\"宜宾市商业银行\",\n" +
            "    \"SCRCU\":\"四川省农村信用\",\n" +
            "    \"KLB\":\"昆仑银行\",\n" +
            "    \"LSBANK\":\"莱商银行\",\n" +
            "    \"YDRCB\":\"尧都农商行\",\n" +
            "    \"CCQTGB\":\"重庆三峡银行\",\n" +
            "    \"FDB\":\"富滇银行\",\n" +
            "    \"JSRCU\":\"江苏省农村信用联合社\",\n" +
            "    \"JNBANK\":\"济宁银行\",\n" +
            "    \"CMB\":\"招商银行\",\n" +
            "    \"JINCHB\":\"晋城银行JCBANK\",\n" +
            "    \"FXCB\":\"阜新银行\",\n" +
            "    \"WHRCB\":\"武汉农村商业银行\",\n" +
            "    \"HBYCBANK\":\"湖北银行宜昌分行\",\n" +
            "    \"TZCB\":\"台州银行\",\n" +
            "    \"TACCB\":\"泰安市商业银行\",\n" +
            "    \"XCYH\":\"许昌银行\",\n" +
            "    \"CEB\":\"中国光大银行\",\n" +
            "    \"NXBANK\":\"宁夏银行\",\n" +
            "    \"HSBANK\":\"徽商银行\",\n" +
            "    \"JJBANK\":\"九江银行\",\n" +
            "    \"NHQS\":\"农信银清算中心\",\n" +
            "    \"MTBANK\":\"浙江民泰商业银行\",\n" +
            "    \"LANGFB\":\"廊坊银行\",\n" +
            "    \"ASCB\":\"鞍山银行\",\n" +
            "    \"KSRB\":\"昆山农村商业银行\",\n" +
            "    \"YXCCB\":\"玉溪市商业银行\",\n" +
            "    \"DLB\":\"大连银行\",\n" +
            "    \"DRCBCL\":\"东莞农村商业银行\",\n" +
            "    \"GCB\":\"广州银行\",\n" +
            "    \"NBBANK\":\"宁波银行\",\n" +
            "    \"BOYK\":\"营口银行\",\n" +
            "    \"SXRCCU\":\"陕西信合\",\n" +
            "    \"GLBANK\":\"桂林银行\",\n" +
            "    \"BOQH\":\"青海银行\",\n" +
            "    \"CDRCB\":\"成都农商银行\",\n" +
            "    \"QDCCB\":\"青岛银行\",\n" +
            "    \"HKBEA\":\"东亚银行\",\n" +
            "    \"HBHSBANK\":\"湖北银行黄石分行\",\n" +
            "    \"WZCB\":\"温州银行\",\n" +
            "    \"TRCB\":\"天津农商银行\",\n" +
            "    \"QLBANK\":\"齐鲁银行\",\n" +
            "    \"GDRCC\":\"广东省农村信用社联合社\",\n" +
            "    \"ZJTLCB\":\"浙江泰隆商业银行\",\n" +
            "    \"GZB\":\"赣州银行\",\n" +
            "    \"GYCB\":\"贵阳市商业银行\",\n" +
            "    \"CQBANK\":\"重庆银行\",\n" +
            "    \"DAQINGB\":\"龙江银行\",\n" +
            "    \"CGNB\":\"南充市商业银行\",\n" +
            "    \"SCCB\":\"三门峡银行\",\n" +
            "    \"CSRCB\":\"常熟农村商业银行\",\n" +
            "    \"SHBANK\":\"上海银行\",\n" +
            "    \"JLBANK\":\"吉林银行\",\n" +
            "    \"CZRCB\":\"常州农村信用联社\",\n" +
            "    \"BANKWF\":\"潍坊银行\",\n" +
            "    \"ZRCBANK\":\"张家港农村商业银行\",\n" +
            "    \"FJHXBC\":\"福建海峡银行\",\n" +
            "    \"ZJNX\":\"浙江省农村信用社联合社\",\n" +
            "    \"LZYH\":\"兰州银行\",\n" +
            "    \"JSB\":\"晋商银行\",\n" +
            "    \"BOHAIB\":\"渤海银行\",\n" +
            "    \"CZCB\":\"浙江稠州商业银行\",\n" +
            "    \"YQCCB\":\"阳泉银行\",\n" +
            "    \"SJBANK\":\"盛京银行\",\n" +
            "    \"XABANK\":\"西安银行\",\n" +
            "    \"BSB\":\"包商银行\",\n" +
            "    \"JSBANK\":\"江苏银行\",\n" +
            "    \"FSCB\":\"抚顺银行\",\n" +
            "    \"HNRCU\":\"河南省农村信用\",\n" +
            "    \"COMM\":\"交通银行\",\n" +
            "    \"XTB\":\"邢台银行\",\n" +
            "    \"CITIC\":\"中信银行\",\n" +
            "    \"HXBANK\":\"华夏银行\",\n" +
            "    \"HNRCC\":\"湖南省农村信用社\",\n" +
            "    \"DYCCB\":\"东营市商业银行\",\n" +
            "    \"ORBANK\":\"鄂尔多斯银行\",\n" +
            "    \"BJRCB\":\"北京农村商业银行\",\n" +
            "    \"XYBANK\":\"信阳银行\",\n" +
            "    \"ZGCCB\":\"自贡市商业银行\",\n" +
            "    \"CDCB\":\"成都银行\",\n" +
            "    \"HANABANK\":\"韩亚银行\",\n" +
            "    \"CMBC\":\"中国民生银行\",\n" +
            "    \"LYBANK\":\"洛阳银行\",\n" +
            "    \"GDB\":\"广东发展银行\",\n" +
            "    \"ZBCB\":\"齐商银行\",\n" +
            "    \"CBKF\":\"开封市商业银行\",\n" +
            "    \"H3CB\":\"内蒙古银行\",\n" +
            "    \"CIB\":\"兴业银行\",\n" +
            "    \"CRCBANK\":\"重庆农村商业银行\",\n" +
            "    \"SZSBK\":\"石嘴山银行\",\n" +
            "    \"DZBANK\":\"德州银行\",\n" +
            "    \"SRBANK\":\"上饶银行\",\n" +
            "    \"LSCCB\":\"乐山市商业银行\",\n" +
            "    \"JXRCU\":\"江西省农村信用\",\n" +
            "    \"ICBC\":\"中国工商银行\",\n" +
            "    \"JZBANK\":\"晋中市商业银行\",\n" +
            "    \"HZCCB\":\"湖州市商业银行\",\n" +
            "    \"NHB\":\"南海农村信用联社\",\n" +
            "    \"XXBANK\":\"新乡银行\",\n" +
            "    \"JRCB\":\"江苏江阴农村商业银行\",\n" +
            "    \"YNRCC\":\"云南省农村信用社\",\n" +
            "    \"ABC\":\"中国农业银行\",\n" +
            "    \"GXRCU\":\"广西省农村信用\",\n" +
            "    \"PSBC\":\"中国邮政储蓄银行\",\n" +
            "    \"BZMD\":\"驻马店银行\",\n" +
            "    \"ARCU\":\"安徽省农村信用社\",\n" +
            "    \"GSRCU\":\"甘肃省农村信用\",\n" +
            "    \"LYCB\":\"辽阳市商业银行\",\n" +
            "    \"JLRCU\":\"吉林农信\",\n" +
            "    \"URMQCCB\":\"乌鲁木齐市商业银行\",\n" +
            "    \"XLBANK\":\"中山小榄村镇银行\",\n" +
            "    \"CSCB\":\"长沙银行\",\n" +
            "    \"JHBANK\":\"金华银行\",\n" +
            "    \"BHB\":\"河北银行\",\n" +
            "    \"NBYZ\":\"鄞州银行\",\n" +
            "    \"LSBC\":\"临商银行\",\n" +
            "    \"BOCD\":\"承德银行\",\n" +
            "    \"SDRCU\":\"山东农信\",\n" +
            "    \"NCB\":\"南昌银行\",\n" +
            "    \"TCCB\":\"天津银行\",\n" +
            "    \"WJRCB\":\"吴江农商银行\",\n" +
            "    \"CBBQS\":\"城市商业银行资金清算中心\",\n" +
            "    \"HBRCU\":\"河北省农村信用社\"\n" +
            "}";

    public BandCardEditText(Context context) {
        this(context, null);
    }

    public BandCardEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BandCardEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    @Override
    protected void onDetachedFromWindow() {
        listener = null;
        FixMemLeak.fixLeak(getContext());
        if (task!=null) {
            task.cancel(true);
            task = null;
        }
        super.onDetachedFromWindow();
    }

    /**
     * 获取未格式化的卡号
     *
     * @return
     */
    public String getCardNo() {
        return getText().toString().trim().replaceAll(space, "");
    }

    /**
     * 获取格式化后的卡号
     *
     * @return
     */
    @Override
    public Editable getText() {
        return super.getText();
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
        if (courPos == 19 || courPos == 22 || courPos == 23) {
            String bankNo = getText().toString().trim().replaceAll(space, "");
            getBank(bankNo);
        } else {
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
     *
     * @param cardId
     * @return
     */
    public boolean checkBankCard(String cardId) {
        if (TextUtils.isEmpty(cardId)) {
            return false;
        }
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }


    /**
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
        void success(String name, String type);

        void failure();

    }

    /**
     * 获取所属银行
     */
    private void getBank(String kaNo) {
        if (task == null) {
            task = new AsyncTask<String, Integer, String>() {
                @Override
                protected String doInBackground(final String... params) {
                    final ExecutorService exec = Executors.newFixedThreadPool(2);
                    Callable<String> callable = new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            //https://apimg.alipay.com/combo.png?d=cashier&t=ICBC //获取logo图标
                            String urls = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json" +
                                    "?input_charset=utf-8" +
                                    "&cardNo="+params[0]+"&cardBinCheck=true";
                            StringBuilder inputLine = new StringBuilder();
                            String read = "";
                            URL url = null;
                            HttpURLConnection urlConnection = null;
                            BufferedReader in = null;
                            try {
                                url = new URL(urls);
                                urlConnection = (HttpURLConnection) url.openConnection();
                                in = new BufferedReader( new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
                                while((read=in.readLine())!=null){
                                    inputLine.append(read+"\r\n");
                                }
//                                System.out.println(inputLine.toString());
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }finally{
                                if(in!=null){
                                    try {
                                        in.close();
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }
                            return inputLine.toString();
                        }
                    };
                    //{"cardType":"DC","bank":"CMBC","key":"6216912900342240","messages":[],"validated":true,"stat":"ok"}
                    // 开始任务执行服务
                    Future<String> task=exec.submit(callable);
                    String string="";
                    try {
                        string=task.get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        // 停止任务执行服务
                        exec.shutdown();
                    }
                    if (TextUtils.isEmpty(string)){
                       return "";
                    }
                    JSONObject jsonObject=JSON.parseObject(string);
                    if (!jsonObject.getString("stat").equals("ok")){
                        return "";
                    }
                    String bank=jsonObject.getString("bank");
                    if (TextUtils.isEmpty(bank)){
                        return "";
                    }
                    String cardType=jsonObject.getString("cardType");
                    if (cardType.equals("DC")){
                        cardType="储蓄卡";
                    } else if (cardType.equals("CC")) {
                        cardType="信用卡";
                    }
                    ArrayMap<String,String> result=new ArrayMap<>();
                    JSONObject Map = JSON.parseObject(bankName);
                    result.put("bankName",Map.getString(bank));
                    result.put("cardType",cardType);
                    result.put("cardKey",jsonObject.getString("key"));
                    return JSON.toJSONString(result);
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    if (TextUtils.isEmpty(s))return;
                    JSONObject Map = JSON.parseObject(s);
                    String name = "";
                    String type = "";
                    if (Map != null) {
                        if (!TextUtils.isEmpty(Map.getString("bankName"))) {
                            name = Map.getString("bankName");
                            type =Map.getString("cardType");
                        }
                    }
                    if (listener != null) {
                        if (name.trim().length() > 0) {
                            listener.success(name, type);
                        } else {
                            listener.failure();
                        }
                    }
                    task.cancel(true);
                    task=null;
                }
            };
        }
        task.execute(kaNo);
    }
}
