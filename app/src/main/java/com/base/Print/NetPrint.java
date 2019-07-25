package com.base.Print;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * pos = new Pos(Content.PRINTER_IP, 9100, "GBK"); // 第一个参数是打印机网口IP
 *
 * 					// 初始化打印机
 * 					pos.initPos();
 *
 * 					// 初始化订单数据
 * 					initData();
 *
 * 					pos.bold(true);
 * 					pos.printTabSpace(2);//两个tab距离，一个tab=4个汉字
 * 					pos.printWordSpace(1);//一个汉字距离
 * 					pos.printText("**测试店铺");
 *
 * 					pos.printLocation(0);//打印位置调整
 * 					pos.printTextNewLine("----------------------------------------");
 * 					pos.bold(false);
 * 					pos.printTextNewLine("订 单 号：1005199");
 * 					pos.printTextNewLine("用 户 名：15712937281");
 * 					pos.printTextNewLine("桌    号：3号桌");
 * 					pos.printTextNewLine("订单状态：订单已确认");
 * 					pos.printTextNewLine("订单日期：2016/2/19 12:34:53");
 * 					pos.printTextNewLine("付 款 人：线下支付（服务员：宝哥）");
 * 					pos.printTextNewLine("服 务 员：1001");
 * 					pos.printTextNewLine("订单备注：不要辣，少盐");
 * 					pos.printLine(2);
 *
 * 					pos.printText("项目");
 * 					pos.printLocation(20, 1);
 * 					pos.printText("单价");
 * 					pos.printLocation(99, 1);
 * 					pos.printWordSpace(1);
 * 					pos.printText("数量");
 * 					pos.printWordSpace(3);
 * 					pos.printText("小计");
 * 					pos.printLocation(0);//打印位置调整
 * 					pos.printTextNewLine("----------------------------------------");
 *
 * 					for (FoodChoose foods : foodChoose) {
 * 						pos.printTextNewLine(foods.getFoodname());
 * 						pos.printLocation(20, 1);
 * 						pos.printText(foods.getFoodunitprice()+"");
 * 						pos.printLocation(99, 1);
 * 						pos.printWordSpace(1);
 * 						pos.printText(foods.getFoodnum()+"");
 * 						pos.printWordSpace(3);
 *                                        }
 *
 * 					pos.printTextNewLine("----------------------------------------------");
 *
 * 					//pos.printLocation(1);
 * 					//pos.printLine(2);
 * 					// 打印二维码
 * 					//pos.qrCode("http://blog.csdn.net/haovip123");
 *
 * 					// 切纸
 * 					//pos.feedAndCut();
 *
 * 					pos.closeIOAndSocket();
 * 					pos = null;
 *
 * ---------------------
 * 作者：bilichen006
 * 来源：CSDN
 * 原文：https://blog.csdn.net/bilichen006/article/details/54347853
 * 版权声明：本文为博主原创文章，转载请附上博文链接！
 */
public class NetPrint {
    //定义编码方式
    private static String encoding = null;

    private Socket sock = null;
    // 通过socket流进行读写
    private OutputStream socketOut = null;
    private OutputStreamWriter writer = null;

    /**
     * 初始化Pos实例
     *
     * @param ip       打印机IP
     * @param port     打印机端口号
     * @param encoding 编码
     * @throws IOException
     */
    public NetPrint(String ip, int port, String encoding) throws IOException {
        sock = new Socket(ip, port);
        socketOut = new DataOutputStream(sock.getOutputStream());
        this.encoding = encoding;
        writer = new OutputStreamWriter(socketOut, encoding);
    }

    /**
     * 关闭IO流和Socket
     *
     * @throws IOException
     */
    public void closeIOAndSocket() throws IOException {
        writer.close();
        socketOut.close();
        sock.close();
    }

    /**
     * 打印二维码
     *
     * @param qrData 二维码的内容
     * @throws IOException
     */
    public void qrCode(String qrData) throws IOException {
        int moduleSize = 8;
        int length = qrData.getBytes(encoding).length;

        //打印二维码矩阵
        writer.write(0x1D);// init
        writer.write("(k");// adjust height of barcode
        writer.write(length + 3); // pl
        writer.write(0); // ph
        writer.write(49); // cn
        writer.write(80); // fn
        writer.write(48); //
        writer.write(qrData);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3);
        writer.write(0);
        writer.write(49);
        writer.write(69);
        writer.write(48);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3);
        writer.write(0);
        writer.write(49);
        writer.write(67);
        writer.write(moduleSize);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3); // pl
        writer.write(0); // ph
        writer.write(49); // cn
        writer.write(81); // fn
        writer.write(48); // m

        writer.flush();

    }

    /**
     * 进纸并全部切割
     *
     * @return
     * @throws IOException
     */
    public void feedAndCut() throws IOException {
        writer.write(0x1D);
        writer.write(86);
        writer.write(65);
        //        writer.write(0);
        //切纸前走纸多少
        writer.write(100);
        writer.flush();

        //另外一种切纸的方式
        //        byte[] bytes = {29, 86, 0};
        //        socketOut.write(bytes);
    }

    /**
     * 打印换行
     *
     * @return length 需要打印的空行数
     * @throws IOException
     */
    public void printLine(int lineNum) throws IOException {
        for (int i = 0; i < lineNum; i++) {
            writer.write("\n");
        }
        writer.flush();
    }

    /**
     * 打印换行(只换一行)
     *
     * @throws IOException
     */
    public void printLine() throws IOException {
        writer.write("\n");
        writer.flush();
    }

    /**
     * 打印空白(一个Tab的位置，约4个汉字)
     *
     * @param length 需要打印空白的长度,
     * @throws IOException
     */
    public void printTabSpace(int length) throws IOException {
        for (int i = 0; i < length; i++) {
            writer.write("\t");
        }
        writer.flush();
    }

    /**
     * 打印空白（一个汉字的位置）
     *
     * @param length 需要打印空白的长度,
     * @throws IOException
     */
    public void printWordSpace(int length) throws IOException {
        for (int i = 0; i < length; i++) {
            writer.write("  ");
        }
        writer.flush();
    }

    /**
     * 打印位置调整
     *
     * @param position 打印位置  0：居左(默认) 1：居中 2：居右
     * @throws IOException
     */
    public void printLocation(int position) throws IOException {
        writer.write(0x1B);
        writer.write(97);
        writer.write(position);
        writer.flush();
    }

    /**
     * 绝对打印位置
     *
     * @throws IOException
     */
    public void printLocation(int light, int weight) throws IOException {
        writer.write(0x1B);
        writer.write(0x24);
        writer.write(light);
        writer.write(weight);
        writer.flush();
    }

    /**
     * 打印文字
     *
     * @param text
     * @throws IOException
     */
    public void printText(String text) throws IOException {
        String s = text;
        byte[] content = s.getBytes("gbk");
        socketOut.write(content);
        socketOut.flush();
    }

    /**
     * 新起一行，打印文字
     *
     * @param text
     * @throws IOException
     */
    public void printTextNewLine(String text) throws IOException {
        //换行
        writer.write("\n");
        writer.flush();

        String s = text;
        byte[] content = s.getBytes("gbk");
        socketOut.write(content);
        socketOut.flush();
    }

    /**
     * 初始化打印机
     *
     * @throws IOException
     */
    public void initPos() throws IOException {
        writer.write(0x1B);
        writer.write(0x40);
        writer.flush();
    }

    /**
     * 加粗
     *
     * @param flag false为不加粗
     * @return
     * @throws IOException
     */
    public void bold(boolean flag) throws IOException {
        if (flag) {
            //常规粗细
            writer.write(0x1B);
            writer.write(69);
            writer.write(0xF);
            writer.flush();
        } else {
            //加粗
            writer.write(0x1B);
            writer.write(69);
            writer.write(0);
            writer.flush();
        }
    }
}
