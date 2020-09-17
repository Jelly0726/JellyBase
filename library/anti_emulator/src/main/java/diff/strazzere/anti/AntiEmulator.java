package diff.strazzere.anti;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import diff.strazzere.anti.debugger.FindDebugger;
import diff.strazzere.anti.emulator.FindEmulator;
import diff.strazzere.anti.monkey.FindMonkey;
import diff.strazzere.anti.taint.FindTaint;

/**
 * 反模拟器工具<br>
 * GitHub地址：https://github.com/strazzere/anti-emulator
 * <pre>
 *     author  : Fantasy
 *     version : 1.0, 2020-06-20
 *     since   : 1.0, 2020-06-20
 * </pre>
 */
public class AntiEmulator {

    /**
     * 检测是否是模拟器
     *
     * @param context 上下文
     * @return true：是模拟器；false：不是模拟器
     */
    public static boolean check(Context context) {
        try {
            return isTaintTrackingDetected(context) || isMonkeyDetected() || isDebugged()
                    || isQEmuEnvDetected(context);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 检测是否是模拟器，安全模式<br>
     * 机制较弱，可避免部分真机被识别为模拟器，但是这也造成了有些模拟器无法被识别
     *
     * @param context 上下文
     * @return true：是模拟器；false：不是模拟器
     */
    public static boolean checkSafely(Context context) {
        try {
            return isTaintTrackingDetected(context) || isMonkeyDetected() || isDebugged()
                    || isQEmuEnvSafeDetected(context);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isTaintTrackingDetected(Context context) {
        boolean hasAppAnalysisPackage = FindTaint.hasAppAnalysisPackage(context.getApplicationContext());
        boolean hasTaintClass = FindTaint.hasTaintClass();
        boolean hasTaintMemberVariables = FindTaint.hasTaintMemberVariables();
        log("Checking for Taint tracking...");
        log("hasAppAnalysisPackage : " + hasAppAnalysisPackage);
        log("hasTaintClass : " + hasTaintClass);
        log("hasTaintMemberVariables : " + hasTaintMemberVariables);
        if (hasAppAnalysisPackage || hasTaintClass || hasTaintMemberVariables) {
            log("Taint tracking was detected.");
            return true;
        } else {
            log("Taint tracking was not detected.");
            return false;
        }
    }

    private static boolean isMonkeyDetected() {
        boolean isUserAMonkey = FindMonkey.isUserAMonkey();
        log("Checking for Monkey user...");
        log("isUserAMonkey : " + isUserAMonkey);
        if (isUserAMonkey) {
            log("Monkey user was detected.");
            return true;
        } else {
            log("Monkey user was not detected.");
            return false;
        }
    }

    private static boolean isDebugged() {
        log("Checking for debuggers...");
        boolean tracer = false;
        try {
            tracer = FindDebugger.hasTracerPid();
        } catch (Exception e) {
            //Logger.write("AntiEmulator isDebugged Exception", e);
        }

        boolean isBeingDebugged = FindDebugger.isBeingDebugged();
        log("isBeingDebugged : " + isBeingDebugged);
        log("hasTracerPid : " + tracer);
        if (FindDebugger.isBeingDebugged() || tracer) {
            log("Debugger was detected.");
            return true;
        } else {
            log("No debugger was detected.");
            return false;
        }
    }

    private static boolean isQEmuEnvDetected(Context context) {
        boolean hasLightSensorManager = FindEmulator.notHasLightSensorManager(context);
        boolean hasEmulatorBuild = FindEmulator.hasEmulatorBuild(context.getApplicationContext());
        boolean isOperatorNameAndroid = FindEmulator.isOperatorNameAndroid(context.getApplicationContext());
        boolean hasBlueTooth = FindEmulator.notHasBlueTooth();
        boolean hasFeatures = FindEmulator.isFeatures();
        boolean hasPipes = FindEmulator.hasPipes();
        boolean hasQEmuDrivers = FindEmulator.hasQEmuDrivers();
        boolean hasEmulatorAdb = FindEmulator.hasEmulatorAdb();
        boolean hasQEmuFiles = FindEmulator.hasQEmuFiles();
        boolean hasGenyFiles = FindEmulator.hasGenyFiles();
        boolean haskIsNotRealPhone = FindEmulator.checkIsNotRealPhone();
        boolean hasEth0Interface = FindEmulator.hasEth0Interface();
        boolean breakpoint=false;
        // 这个检测比较耗时
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (String abi : Build.SUPPORTED_ABIS) {
                if (abi.equalsIgnoreCase("armeabi-v7a")) {
                    breakpoint =FindEmulator.checkQemuBreakpoint();
                }
            }
        }
        log("Checking for QEmu env...");
        log("hasLightSensorManager : " + hasLightSensorManager);
        log("hasEmulatorBuild : " + hasEmulatorBuild);
        log("isOperatorNameAndroid : " + isOperatorNameAndroid);
        log("hasBlueTooth : " + hasBlueTooth);
        log("hasFeatures : " + hasFeatures);
        log("hasPipes : " + hasPipes);
        log("hasQEmuDriver : " + hasQEmuDrivers);
        log("hasEmulatorAdb :" + hasEmulatorAdb);
        log("hasQEmuFiles : " + hasQEmuFiles);
        log("hasGenyFiles : " + hasGenyFiles);
        log("haskIsNotRealPhone : " + haskIsNotRealPhone);
        log("hasEth0Interface : " + hasEth0Interface);
        log("breakpoint : " + breakpoint);


        if (hasBlueTooth || hasLightSensorManager || hasEmulatorBuild || hasFeatures || hasPipes
                || hasQEmuDrivers || hasEmulatorAdb || hasQEmuFiles
                || hasGenyFiles || haskIsNotRealPhone || hasEth0Interface || breakpoint) {
            log("QEmu environment detected.");
            return true;
        } else {
            log("QEmu environment not detected.");
            return false;
        }
    }

    private static boolean isQEmuEnvSafeDetected(Context context) {
//        boolean hasLightSensorManager = FindEmulator.notHasLightSensorManager(context);
        //boolean hasEmulatorBuild = FindEmulator.hasEmulatorBuild(context.getApplicationContext());
        //boolean isOperatorNameAndroid = FindEmulator.isOperatorNameAndroid(context.getApplicationContext());
        boolean hasBlueTooth = FindEmulator.notHasBlueTooth();
        boolean hasFeatures = FindEmulator.isFeatures();
        boolean hasPipes = FindEmulator.hasPipes();
        boolean hasQEmuDrivers = FindEmulator.hasQEmuDrivers();
        //boolean hasEmulatorAdb = FindEmulator.hasEmulatorAdb();
        //boolean hasQEmuFiles = FindEmulator.hasQEmuFiles();
        boolean hasGenyFiles = FindEmulator.hasGenyFiles();
        boolean haskIsNotRealPhone = FindEmulator.checkIsNotRealPhone();
        boolean hasEth0Interface = FindEmulator.hasEth0Interface();
        boolean breakpoint=false;
        // 这个检测比较耗时
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (String abi : Build.SUPPORTED_ABIS) {
                if (abi.equalsIgnoreCase("armeabi-v7a")) {
                    breakpoint =FindEmulator.checkQemuBreakpoint();
                }
            }
        }
        log("Checking for QEmu env Safe...");
//        log("hasLightSensorManager : " + hasLightSensorManager);
        //log("hasEmulatorBuild : " + hasEmulatorBuild);
        //log("isOperatorNameAndroid : " + isOperatorNameAndroid);
        log("hasBlueTooth : " + hasBlueTooth);
        log("hasFeatures : " + hasFeatures);
        log("hasPipes : " + hasPipes);
        log("hasQEmuDriver : " + hasQEmuDrivers);
        //log("hasEmulatorAdb :" + hasEmulatorAdb);
        //log("hasQEmuFiles : " + hasQEmuFiles);
        log("haskIsNotRealPhone : " + haskIsNotRealPhone);
        log("hasEth0Interface : " + hasEth0Interface);
        log("hasGenyFiles : " + hasGenyFiles);
        log("breakpoint : " + breakpoint);

        // 有些检测方法被屏蔽掉的原因：在部分真机上，也会被识别为true，所以屏蔽掉
        if (
                //|| hasEmulatorBuild
                hasBlueTooth || hasFeatures || hasPipes || hasQEmuDrivers
                //|| hasEmulatorAdb || hasQEmuFiles
                || hasGenyFiles || haskIsNotRealPhone || hasEth0Interface || breakpoint) {
            log("QEmu environment detected.");
            return true;
        } else {
            log("QEmu environment not detected.");
            return false;
        }
    }

    private static void log(String msg) {
        Log.d("AntiEmulator", msg);
    }

}
