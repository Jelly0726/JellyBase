package diff.strazzere.anti.emulator;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import diff.strazzere.anti.common.Property;
import diff.strazzere.anti.common.Utilities;
import diff.strazzere.anti.debugger.FindDebugger;

/**
 * Class used to determine functionality specific to the Android QEmu.
 *
 * @author tstrazzere
 */
public class FindEmulator {
    private static String[] known_pipes = {"/dev/socket/qemud", "/dev/qemu_pipe"};
    private static String[] known_files = {"/system/lib/libc_malloc_debug_qemu.so", "/sys/qemu_trace",
            "/system/bin/qemu-props"};
    private static String[] known_geny_files = {"/dev/socket/genyd", "/dev/socket/baseband_genyd"};
    private static String[] known_qemu_drivers = {"goldfish"};
    /**
     * Known props, in the format of [property name, value to seek] if value to seek is null, then it is assumed that
     * the existence of this property (anything not null) indicates the QEmu environment.
     */
    private static Property[] known_props = {new Property("init.svc.qemud", null),
            new Property("init.svc.qemu-props", null), new Property("qemu.hw.mainkeys", null),
            new Property("qemu.sf.fake_camera", null), new Property("qemu.sf.lcd_density", null),
            new Property("ro.bootloader", "unknown"), new Property("ro.bootmode", "unknown"),
            new Property("ro.hardware", "goldfish"), new Property("ro.kernel.android.qemud", null),
            new Property("ro.kernel.qemu.gles", null), new Property("ro.kernel.qemu", "1"),
            new Property("ro.product.device", "generic"), new Property("ro.product.model", "sdk"),
            new Property("ro.product.name", "sdk"),
            // Need to double check that an "empty" string ("") returns null
            new Property("ro.serialno", null)};
    /**
     * The "known" props have the potential for false-positiving due to interesting (see: poorly) made Chinese
     * devices/odd ROMs. Keeping this threshold low will result in better QEmu detection with possible side affects.
     */
    private static int MIN_PROPERTIES_THRESHOLD = 0x5;

    static {
        // This is only valid for arm, so gate it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (String abi : Build.SUPPORTED_ABIS) {
                if (abi.equalsIgnoreCase("armeabi-v7a")) {
                    System.loadLibrary("anti");
                    break;
                }
            }
        }
    }

    /**
     * Check the existence of known pipes used by the Android QEmu environment.
     *
     * @return {@code true} if any pipes where found to exist or {@code false} if not.
     */
    public static boolean hasPipes() {
        for (String pipe : known_pipes) {
            File qemu_socket = new File(pipe);
            if (qemu_socket.exists()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check the existence of known files used by the Android QEmu environment.
     *
     * @return {@code true} if any files where found to exist or {@code false} if not.
     */
    public static boolean hasQEmuFiles() {
        for (String pipe : known_files) {
            File qemu_file = new File(pipe);
            if (qemu_file.exists()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check the existence of known files used by the Genymotion environment.
     *
     * @return {@code true} if any files where found to exist or {@code false} if not.
     */
    public static boolean hasGenyFiles() {
        for (String file : known_geny_files) {
            File geny_file = new File(file);
            if (geny_file.exists()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Reads in the driver file, then checks a list for known QEmu drivers.
     *
     * @return {@code true} if any known drivers where found to exist or {@code false} if not.
     */
    public static boolean hasQEmuDrivers() {
        for (File drivers_file : new File[]{new File("/proc/tty/drivers"), new File("/proc/cpuinfo")}) {
            if (drivers_file.exists() && drivers_file.canRead()) {
                // We don't care to read much past things since info we care about should be inside here
                byte[] data = new byte[1024];
                try {
                    InputStream is = new FileInputStream(drivers_file);
                    is.read(data);
                    is.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                String driver_data = new String(data);
                for (String known_qemu_driver : FindEmulator.known_qemu_drivers) {
                    if (driver_data.indexOf(known_qemu_driver) != -1) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
    public static boolean hasEmulatorBuild(Context context) {
        String BOARD = Build.BOARD; // The name of the underlying board, like "unknown".
        // This appears to occur often on real hardware... that's sad
        // String BOOTLOADER = android.os.Build.BOOTLOADER; // The system bootloader version number.
        String BRAND = Build.BRAND; // The brand (e.g., carrier) the software is customized for, if any.
        // "generic"
        String DEVICE = Build.DEVICE; // The name of the industrial design. "generic"
        String HARDWARE = Build.HARDWARE; // The name of the hardware (from the kernel command line or
        // /proc). "goldfish"
        String MODEL = Build.MODEL; // The end-user-visible name for the end product. "sdk"
        String PRODUCT = Build.PRODUCT; // The name of the overall product.
        if ((BOARD.compareTo("unknown") == 0) /* || (BOOTLOADER.compareTo("unknown") == 0) */
                || (BRAND.compareTo("generic") == 0) || (DEVICE.compareTo("generic") == 0)
                || (MODEL.compareTo("sdk") == 0) || (PRODUCT.compareTo("sdk") == 0)
                || (HARDWARE.compareTo("goldfish") == 0)) {
            return true;
        }
        return false;
    }

    public static boolean isOperatorNameAndroid(Context paramContext) {
        String szOperatorName = ((TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperatorName();
        boolean isAndroid = szOperatorName.equalsIgnoreCase("android");
        return isAndroid;
    }
    /**
     * 用途:判断蓝牙是否有效来判断是否为模拟器
     * 真机蓝牙从来没有开启过获取的名称和硬件地址可能为null 部分模拟器也可以获得名称和硬件地址
     * 返回:true 为模拟器
     */
    public static boolean notHasBlueTooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.i("SSSS", "设备不支持蓝牙");
            return true;
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Log.i("SSSS", "设备支持蓝牙,但蓝牙未开启");
                return false;
                //未打开蓝牙，才需要打开蓝牙
//                bluetoothAdapter.enable();
            }
            //蓝牙不一定有效的。获取蓝牙名称和硬件地址，若为 null 则默认为模拟器
            String name = bluetoothAdapter.getName();
            String addree = bluetoothAdapter.getAddress();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(addree)) {
                Log.i("SSSS", "无效蓝牙设备");
                return true;
            } else {
                Log.i("SSSS", "有效蓝牙设备");
                return false;
            }
        }
    }

    /**
     * 用途:依据是否存在光传感器来判断是否为模拟器(小部分手机没有光传感器)
     * 返回:true 为模拟器
     */
    public static boolean notHasLightSensorManager(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (sensor == null) {
            Log.i("SSSS", "不存在光传感器");
            return true;
        } else {
            Log.i("SSSS", "存在光传感器");
            return false;
        }
    }

    /**
     * 用途:根据部分特征参数设备信息来判断是否为模拟器
     * 返回:true 为模拟器
     */
    public static boolean isFeatures() {
        boolean isf = Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.toLowerCase().contains("vbox")
                || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
        if (isf) {
            Log.i("SSSS", "存在模拟器特征参数");
        } else {
            Log.i("SSSS", "不存在模拟器特征参数");
        }
        return isf;
    }

    /**
     * 根据CPU是否为电脑来判断是否为模拟器
     * 返回:true 为模拟器
     */
    public static boolean checkIsNotRealPhone() {
        String cpuInfo = readCpuInfo();
        if ((cpuInfo.contains("intel") || cpuInfo.contains("amd"))) {
            Log.i("SSSS", "CPU为电脑CPU");
            return true;
        }
        Log.i("SSSS", "CPU不是电脑CPU");
        return false;
    }

    /**
     * 根据 CPU 是否为电脑来判断是否为模拟器
     *
     * @return
     */
    private static String readCpuInfo() {
        String result = "";
        try {
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            ProcessBuilder processBuilder = new ProcessBuilder(args);
            Process process = processBuilder.start();
            StringBuffer stringBuffer = new StringBuffer();
            String readLine = "";
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            while ((readLine = responseReader.readLine()) != null) {
                stringBuffer.append(readLine);
            }
            responseReader.close();
            result = stringBuffer.toString().toLowerCase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 检测是否存在eth0(光纤以太网接口卡)网卡.
     *
     * @return
     */
    public static boolean hasEth0Interface() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().equals("eth0")) {
                    Log.i("SSSS", "存在eth0(光纤以太网接口卡)网卡");
                    return true;
                }
            }
        } catch (SocketException ex) {
        }
        Log.i("SSSS", "不存在eth0(光纤以太网接口卡)网卡");
        return false;
    }

    /**
     * 这个其实是用于检测当前操作到底是用户还是脚本在要求应用执行.
     *
     * @return
     */
    public static boolean isUserAMonkey() {
        return ActivityManager.isUserAMonkey();
    }
    //jni
    public native static int qemuBkpt();
    public static boolean checkQemuBreakpoint() {
        // 这个检测比较耗时
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (String abi : Build.SUPPORTED_ABIS) {
                if (abi.equalsIgnoreCase("armeabi-v7a")) {
                    boolean hit_breakpoint = false;
                    // Potentially you may want to see if this is a specific value
                    int result = qemuBkpt();

                    if (result > 0) {
                        hit_breakpoint = true;
                    }
                    return hit_breakpoint;
                }
            }
        }
        return false;
    }

    public static boolean hasEmulatorAdb() {
        try {
            return FindDebugger.hasAdbInEmulator();
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Will query specific system properties to try and fingerprint a QEmu environment. A minimum threshold must be met
     * in order to prevent false positives.
     *
     * @param context A {link Context} object for the Android application.
     * @return {@code true} if enough properties where found to exist or {@code false} if not.
     */
    public boolean hasQEmuProps(Context context) {
        int found_props = 0;

        for (Property property : known_props) {
            String property_value = Utilities.getProp(context, property.name);
            // See if we expected just a non-null
            if ((property.seek_value == null) && (property_value != null)) {
                found_props++;
            }
            // See if we expected a value to seek
            if ((property.seek_value != null) && (property_value.indexOf(property.seek_value) != -1)) {
                found_props++;
            }

        }

        if (found_props >= MIN_PROPERTIES_THRESHOLD) {
            return true;
        }

        return false;
    }
    private static void log(String msg) {
        Log.d("FindEmulator", msg);
    }

}
