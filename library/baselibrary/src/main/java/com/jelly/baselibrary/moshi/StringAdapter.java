package com.jelly.baselibrary.moshi;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.math.BigDecimal;

/** Converts strings like 1.00000280057E11 to the corresponding String. */
public class StringAdapter {
    //科学计数法正则表达式
    String SCIENTIFIC="^([\\+|-]?\\d+(.{0}|.\\d+))[Ee]{1}([\\+|-]?\\d+)$";
    @ToJson
    String toJson(String str) {
        if (str.matches(SCIENTIFIC)){
            BigDecimal bd = new BigDecimal(str);
            String result = bd.toPlainString();
            return result;
        }
        return str;
    }

    @FromJson
    String fromJson(String str) {
        if (str.matches(SCIENTIFIC)){
            BigDecimal bd = new BigDecimal(str);
            String result = bd.toPlainString();
            return result;
        }
        return str;
    }
}