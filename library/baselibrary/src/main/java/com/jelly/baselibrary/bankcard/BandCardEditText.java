package com.jelly.baselibrary.bankcard;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.ArrayMap;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import com.jelly.baselibrary.appManager.FixMemLeak;
import com.jelly.baselibrary.moshi.JsonTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 欢迎关注微信公众号：aikaifa
 */
public class BandCardEditText extends AppCompatEditText {
    private boolean shouldStopChange = false;
    private final String space = " ";

    private BankCardListener listener;
    private final String bankName = "{" +
            "\"SRCB\":\"深圳农村商业银行\"," +
            "\"BGB\":\"广西北部湾银行\"," +
            "\"SHRCB\":\"上海农村商业银行\"," +
            "\"BJBANK\":\"北京银行\"," +
            "\"WHCCB\":\"威海市商业银行\"," +
            "\"BOZK\":\"周口银行\"," +
            "\"KORLABANK\":\"库尔勒市商业银行\"," +
            "\"SPABANK\":\"平安银行\"," +
            "\"SDEB\":\"顺德农商银行\"," +
            "\"HURCB\":\"湖北省农村信用社\"," +
            "\"WRCB\":\"无锡农村商业银行\"," +
            "\"BOCY\":\"朝阳银行\"," +
            "\"CZBANK\":\"浙商银行\"," +
            "\"HDBANK\":\"邯郸银行\"," +
            "\"BOC\":\"中国银行\"," +
            "\"BOD\":\"东莞银行\"," +
            "\"CCB\":\"中国建设银行\"," +
            "\"ZYCBANK\":\"遵义市商业银行\"," +
            "\"SXCB\":\"绍兴银行\"," +
            "\"GZRCU\":\"贵州省农村信用社\"," +
            "\"ZJKCCB\":\"张家口市商业银行\"," +
            "\"BOJZ\":\"锦州银行\"," +
            "\"BOP\":\"平顶山银行\"," +
            "\"HKB\":\"汉口银行\"," +
            "\"SPDB\":\"上海浦东发展银行\"," +
            "\"NXRCU\":\"宁夏黄河农村商业银行\"," +
            "\"NYNB\":\"广东南粤银行\"," +
            "\"GRCB\":\"广州农商银行\"," +
            "\"BOSZ\":\"苏州银行\"," +
            "\"HZCB\":\"杭州银行\"," +
            "\"HSBK\":\"衡水银行\"," +
            "\"HBC\":\"湖北银行\"," +
            "\"JXBANK\":\"嘉兴银行\"," +
            "\"HRXJB\":\"华融湘江银行\"," +
            "\"BODD\":\"丹东银行\"," +
            "\"AYCB\":\"安阳银行\"," +
            "\"EGBANK\":\"恒丰银行\"," +
            "\"CDB\":\"国家开发银行\"," +
            "\"TCRCB\":\"江苏太仓农村商业银行\"," +
            "\"NJCB\":\"南京银行\"," +
            "\"ZZBANK\":\"郑州银行\"," +
            "\"DYCB\":\"德阳商业银行\"," +
            "\"YBCCB\":\"宜宾市商业银行\"," +
            "\"SCRCU\":\"四川省农村信用\"," +
            "\"KLB\":\"昆仑银行\"," +
            "\"LSBANK\":\"莱商银行\"," +
            "\"YDRCB\":\"尧都农商行\"," +
            "\"CCQTGB\":\"重庆三峡银行\"," +
            "\"FDB\":\"富滇银行\"," +
            "\"JSRCU\":\"江苏省农村信用联合社\"," +
            "\"JNBANK\":\"济宁银行\"," +
            "\"CMB\":\"招商银行\"," +
            "\"JINCHB\":\"晋城银行JCBANK\"," +
            "\"FXCB\":\"阜新银行\"," +
            "\"WHRCB\":\"武汉农村商业银行\"," +
            "\"HBYCBANK\":\"湖北银行宜昌分行\"," +
            "\"TZCB\":\"台州银行\"," +
            "\"TACCB\":\"泰安市商业银行\"," +
            "\"XCYH\":\"许昌银行\"," +
            "\"CEB\":\"中国光大银行\"," +
            "\"NXBANK\":\"宁夏银行\"," +
            "\"HSBANK\":\"徽商银行\"," +
            "\"JJBANK\":\"九江银行\"," +
            "\"NHQS\":\"农信银清算中心\"," +
            "\"MTBANK\":\"浙江民泰商业银行\"," +
            "\"LANGFB\":\"廊坊银行\"," +
            "\"ASCB\":\"鞍山银行\"," +
            "\"KSRB\":\"昆山农村商业银行\"," +
            "\"YXCCB\":\"玉溪市商业银行\"," +
            "\"DLB\":\"大连银行\"," +
            "\"DRCBCL\":\"东莞农村商业银行\"," +
            "\"GCB\":\"广州银行\"," +
            "\"NBBANK\":\"宁波银行\"," +
            "\"BOYK\":\"营口银行\"," +
            "\"SXRCCU\":\"陕西信合\"," +
            "\"GLBANK\":\"桂林银行\"," +
            "\"BOQH\":\"青海银行\"," +
            "\"CDRCB\":\"成都农商银行\"," +
            "\"QDCCB\":\"青岛银行\"," +
            "\"HKBEA\":\"东亚银行\"," +
            "\"HBHSBANK\":\"湖北银行黄石分行\"," +
            "\"WZCB\":\"温州银行\"," +
            "\"TRCB\":\"天津农商银行\"," +
            "\"QLBANK\":\"齐鲁银行\"," +
            "\"GDRCC\":\"广东省农村信用社联合社\"," +
            "\"ZJTLCB\":\"浙江泰隆商业银行\"," +
            "\"GZB\":\"赣州银行\"," +
            "\"GYCB\":\"贵阳市商业银行\"," +
            "\"CQBANK\":\"重庆银行\"," +
            "\"DAQINGB\":\"龙江银行\"," +
            "\"CGNB\":\"南充市商业银行\"," +
            "\"SCCB\":\"三门峡银行\"," +
            "\"CSRCB\":\"常熟农村商业银行\"," +
            "\"SHBANK\":\"上海银行\"," +
            "\"JLBANK\":\"吉林银行\"," +
            "\"CZRCB\":\"常州农村信用联社\"," +
            "\"BANKWF\":\"潍坊银行\"," +
            "\"ZRCBANK\":\"张家港农村商业银行\"," +
            "\"FJHXBC\":\"福建海峡银行\"," +
            "\"ZJNX\":\"浙江省农村信用社联合社\"," +
            "\"LZYH\":\"兰州银行\"," +
            "\"JSB\":\"晋商银行\"," +
            "\"BOHAIB\":\"渤海银行\"," +
            "\"CZCB\":\"浙江稠州商业银行\"," +
            "\"YQCCB\":\"阳泉银行\"," +
            "\"SJBANK\":\"盛京银行\"," +
            "\"XABANK\":\"西安银行\"," +
            "\"BSB\":\"包商银行\"," +
            "\"JSBANK\":\"江苏银行\"," +
            "\"FSCB\":\"抚顺银行\"," +
            "\"HNRCU\":\"河南省农村信用\"," +
            "\"COMM\":\"交通银行\"," +
            "\"XTB\":\"邢台银行\"," +
            "\"CITIC\":\"中信银行\"," +
            "\"HXBANK\":\"华夏银行\"," +
            "\"HNRCC\":\"湖南省农村信用社\"," +
            "\"DYCCB\":\"东营市商业银行\"," +
            "\"ORBANK\":\"鄂尔多斯银行\"," +
            "\"BJRCB\":\"北京农村商业银行\"," +
            "\"XYBANK\":\"信阳银行\"," +
            "\"ZGCCB\":\"自贡市商业银行\"," +
            "\"CDCB\":\"成都银行\"," +
            "\"HANABANK\":\"韩亚银行\"," +
            "\"CMBC\":\"中国民生银行\"," +
            "\"LYBANK\":\"洛阳银行\"," +
            "\"GDB\":\"广东发展银行\"," +
            "\"ZBCB\":\"齐商银行\"," +
            "\"CBKF\":\"开封市商业银行\"," +
            "\"H3CB\":\"内蒙古银行\"," +
            "\"CIB\":\"兴业银行\"," +
            "\"CRCBANK\":\"重庆农村商业银行\"," +
            "\"SZSBK\":\"石嘴山银行\"," +
            "\"DZBANK\":\"德州银行\"," +
            "\"SRBANK\":\"上饶银行\"," +
            "\"LSCCB\":\"乐山市商业银行\"," +
            "\"JXRCU\":\"江西省农村信用\"," +
            "\"ICBC\":\"中国工商银行\"," +
            "\"JZBANK\":\"晋中市商业银行\"," +
            "\"HZCCB\":\"湖州市商业银行\"," +
            "\"NHB\":\"南海农村信用联社\"," +
            "\"XXBANK\":\"新乡银行\"," +
            "\"JRCB\":\"江苏江阴农村商业银行\"," +
            "\"YNRCC\":\"云南省农村信用社\"," +
            "\"ABC\":\"中国农业银行\"," +
            "\"GXRCU\":\"广西省农村信用\"," +
            "\"PSBC\":\"中国邮政储蓄银行\"," +
            "\"BZMD\":\"驻马店银行\"," +
            "\"ARCU\":\"安徽省农村信用社\"," +
            "\"GSRCU\":\"甘肃省农村信用\"," +
            "\"LYCB\":\"辽阳市商业银行\"," +
            "\"JLRCU\":\"吉林农信\"," +
            "\"URMQCCB\":\"乌鲁木齐市商业银行\"," +
            "\"XLBANK\":\"中山小榄村镇银行\"," +
            "\"CSCB\":\"长沙银行\"," +
            "\"JHBANK\":\"金华银行\"," +
            "\"BHB\":\"河北银行\"," +
            "\"NBYZ\":\"鄞州银行\"," +
            "\"LSBC\":\"临商银行\"," +
            "\"BOCD\":\"承德银行\"," +
            "\"SDRCU\":\"山东农信\"," +
            "\"NCB\":\"南昌银行\"," +
            "\"TCCB\":\"天津银行\"," +
            "\"WJRCB\":\"吴江农商银行\"," +
            "\"CBBQS\":\"城市商业银行资金清算中心\"," +
            "\"HBRCU\":\"河北省农村信用社\"" +
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
        new AsyncTask<String, Integer, String>() {
            private ExecutorService exec= Executors.newCachedThreadPool();
            @Override
            protected String doInBackground(final String... params) {
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
                Map jsonObject = JsonTool.get().fromJson(string, Map.class);
                if (!jsonObject.get("stat").equals("ok")){
                    return "";
                }
                String bank=jsonObject.get("bank").toString();
                if (TextUtils.isEmpty(bank)){
                    return "";
                }
                String cardType=jsonObject.get("cardType").toString();
                if (cardType.equals("DC")){
                    cardType="储蓄卡";
                } else if (cardType.equals("CC")) {
                    cardType="信用卡";
                }
                ArrayMap<String,String> result=new ArrayMap<>();
                Map Map = JsonTool.get().fromJson(bankName, Map.class);
                result.put("bankName",Map.get(bank).toString());
                result.put("cardType",cardType);
                result.put("cardKey",jsonObject.get("key").toString());
                return JsonTool.get().toJson(result);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (TextUtils.isEmpty(s))return;
                Map Map = JsonTool.get().fromJson(s, Map.class);
                String name = "";
                String type = "";
                if (Map != null) {
                    if (!TextUtils.isEmpty(Map.get("bankName").toString())) {
                        name = Map.get("bankName").toString();
                        type =Map.get("cardType").toString();
                    }
                }
                if (listener != null) {
                    if (name.trim().length() > 0) {
                        listener.success(name, type);
                    } else {
                        listener.failure();
                    }
                }
            }
        }.execute(kaNo);
    }
}
