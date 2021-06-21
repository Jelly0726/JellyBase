package com.jelly.baselibrary.Utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 移除文本中的HTML标签
 */
public class HtmlUtil {
    private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
    private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
    private static final String regEx_space = "\\s*|\t|\r|\n";//定义空格回车换行符


    public static void test() {
        String str = "&lt;p&gt;     &lt;/p&gt;&lt;p style=&quot;white-space: normal;&quot;&gt;      廖姓，是炎黄子孙的重要宗族，得姓已逾4000多年。&lt;/p&gt;&lt;p style=&quot;white-space: normal;&quot;&gt;     自从盘古来天地，三皇五帝代如今，中华名族绵延繁衍，为人类创造了灿烂的文化。炎帝和皇帝，是中华儿女的共同祖先。炎帝，即传说中的神农氏，生于渭河流域的姜水，故姓姜；皇帝即传说中的轩辕氏，发祥于泾河流域的姬水，故姓姬。炎帝和皇帝；是中国最早的有姓者。尔后不断派衍，至今已达2500余个姓（一说8000余个姓）。&lt;/p&gt;&lt;p style=&quot;white-space: normal;&quot;&gt;姓的得来，除氏族图腾，始祖母的某种遭遇或梦幻，始祖母的生育地点等外，今天繁杂分坛的&ldquo;百家姓&rdquo;，有的以祖先封国为姓，有的以祖先的字、号、名为姓，有的以封邑、食邑和居住地命名，有的以官职、爵位命名，有的以黄帝赐姓、贬姓，还有的因避讳产生的姓，以技为姓，以天干地支为姓，林林总总，不一而足。&lt;/p&gt;&lt;p style=&quot;white-space: normal;&quot;&gt;      廖姓最早发源于河南省境内。廖姓历史上最大的郡望&ldquo;汝南郡&rdquo;早期即出此地，当时河南廖姓之昌荣，自不必言。秦汉之际，廖姓始有迁往周边地区者。伯廖一支形成巨大鹿望郡。另有汝南廖姓后裔廖惠避秦之乱而迁河南（据《廖氏源流序》）。魏晋南北朝时期，继&ldquo;永嘉之乱&rdquo;始，北方廖姓大6举南迁，此期，廖惠后裔廖化自襄阳（金湖北省襄樊）迁入四川，是为入蜀始祖。传自廖延龄，任武威（今属甘肃省）太守。另有晋代隐士廖堂，将乐（今属福建省）人，为最早入闽者。唐时，入闽者甚众。唐初有廖姓随陈元光父子开漳入闽，唐末有廖姓随王潮、王审知入闽。廖惠一支传至廖崇德，壬江西虔化令，其后人又有迁居福建汀州宁化石壁寨，进而迁上杭等地者。宋代，廖姓已是福建大姓，名士辈出。元代以前廖惠一支迁徙情况，《兴廖氏族谱》所述较为明晰：&ldquo;其先祖世居汝南，魏晋南北朝时，因北方之乱，播迁于江南各地。唐时其祖由江西雩都，避唐末之乱，迁于福建汀州宁化石壁寨。后子孙因乱，又迁顺昌，廖氏居于闽者益众。至宋末，再由宁化经长汀、上杭、永定，而入广东--大埔、梅县、兴宁、五华等地区。&rdquo;明代，山西大槐树廖姓分迁于河南、河北、江苏、北京等地。清代，闽粤廖姓有入台进而移居泰国、新加坡等地者。今日廖姓以江西、湖南、四川、广西、广东等省居多，上述五省廖姓约占全国汉族廖姓人口的百分之七十三。廖姓是当地今中国姓氏排行第六十六位的大姓，人口较多，约占全国汉族人口的百分之零点三四。&lt;/p&gt;&lt;p&gt;&lt;br/&gt;&lt;/p&gt;&lt;p&gt;     &lt;/p&gt;&lt;p style=&quot;white-space: normal;&quot;&gt;  ";
        System.out.println("HTML DECODE结果----->" + htmlDecode(str));
    }



    /**
     * @param htmlStr
     * @return 删除Html标签
     */
    public static String delHTMLTag(String htmlStr) {
        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // 过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); // 过滤html标签

        Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(htmlStr);
        htmlStr = m_space.replaceAll(""); // 过滤空格回车标签
        return htmlStr.trim(); // 返回文本字符串
    }

    public static String getTextFromHtml(String htmlStr) {
        htmlStr = delHTMLTag(htmlStr);
        htmlStr = htmlStr.replaceAll(" ", "");
        return htmlStr;
    }



    /**
     * html 编码
     * @param source
     * @return
     */
    public static String htmlEncode(String source) {
        if (source == null) {
            return "";
        }
        String html = "";
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            switch (c) {
                case '<':
                    buffer.append("&lt;");
                    break;
                case '>':
                    buffer.append("&gt;");
                    break;
                case '&':
                    buffer.append("&amp;");
                    break;
                case '"':
                    buffer.append("&quot;");
                    break;
                case ' ':
                    buffer.append("&nbsp;");
                    break;

                default:
                    buffer.append(c);
            }
        }
        html = buffer.toString();
        return html;
    }


    /**
     * html 解码
     * @param source
     * @return
     */
    public static String htmlDecode(String source) {
        if (TextUtils.isEmpty(source)) {
            return "";
        }
        source = source.replace("&lt;", "<");
        source = source.replace("&gt;", ">");
        source = source.replace("&amp;", "&");
        source = source.replace("&quot;", "\"");
        source = source.replace("&nbsp;", " ");
        source = source.replace("&ldquo;", "\"");
        source = source.replace("&rdquo;", "\"");

        return getTextFromHtml(source);
    }
}