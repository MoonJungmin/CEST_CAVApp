package moon.urpcest_proto.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.AssetManager.AssetInputStream;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;



public class Util {
    /**
     * 키보드 숨김
     */
    public static void hideKeyboard(Context con, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        catch (Exception e) {
        }
    }

    /**
     * 키보드 보이기
     */
    public static void showKeyPad(Context con, View v) {
        try {
            InputMethodManager imm = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(v, 0);
        }
        catch (Exception e) {
        }
    }

    public static void sendEmail(Context aContext, String aTitle, String aBody, String aFilePath) {
        String title = "";
        String body = "";

        title = aTitle;
        body = aBody;

        File files = new File(aFilePath);
        Uri fileUri = Uri.fromFile(files);
        Intent it = new Intent(Intent.ACTION_SEND);
        it.setType("plain/text");

        it.putExtra(Intent.EXTRA_SUBJECT, title);
        it.putExtra(Intent.EXTRA_TEXT, body);
        it.putExtra(Intent.EXTRA_STREAM, fileUri);
        aContext.startActivity(it);
    }

    public static String readAssetString(Context context, String aFile) {
        String jString = "";
        try {
            AssetManager assetManager = context.getResources().getAssets();
            AssetInputStream ais = (AssetInputStream) assetManager.open(aFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(ais));
            StringBuilder sb = new StringBuilder();

            int bufferSize = 1024 * 1024;

            char readBuf[] = new char[bufferSize];
            int resultSize = 0;

            while ((resultSize = br.read(readBuf)) != -1) {
                if (resultSize == bufferSize) {
                    sb.append(readBuf);
                }
                else {
                    for (int i = 0; i < resultSize; i++) {
                        sb.append(readBuf[i]);
                    }
                }
            }
            jString = sb.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return jString;
    }

    /**
     * 단말기 density 구함
     *
     * @param con
     *                  사용법 : if(getDensity(context) == 2f && (float으로
     *                  형변환해서 사용 해야함.)
     */
    public static float getDensity(Context con) {
        float density = 0.0f;
        density = con.getResources().getDisplayMetrics().density;
        return density;
    }

    /**
     * px을 dp로 변환
     *
     * @param con
     * @param px
     * @return dp
     */
    public static int getPxToDp(Context con, int px) {
        float density = 0.0f;
        density = con.getResources().getDisplayMetrics().density;
        return (int) (px / density);
    }

    /**
     * dp를 px로 변환
     *
     * @param con
     * @param dp
     * @return px
     */
    public static int getDpToPix(Context con, double dp) {
        float density = 0.0f;
        density = con.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

    /**
     * 단말기 가로 해상도 구하기
     *
     * @param activity
     * @return width
     */
    @SuppressWarnings("all")
    public static int getScreenWidth(Activity activity) {
        int width = 0;
        width = activity.getWindowManager().getDefaultDisplay().getWidth();
        return width;
    }

    /**
     * 단말기 세로 해상도 구하기
     *
     * @param activity
     * @return hight
     */
    @SuppressWarnings("all")
    public static int getScreenHeight(Activity activity) {
        int height = 0;
        height = activity.getWindowManager().getDefaultDisplay().getHeight();
        return height;
    }

    /**
     * 단말기 가로 해상도 구하기
     *
     * @param context
     */
    @SuppressWarnings("all")
    public static int getScreenWidth(Context context) {
        Display dis = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = dis.getWidth();
        return width;
    }

    /**
     * 단말기 세로 해상도 구하기
     *
     * @param context
     */
    public static int getScreenHeight(Context context) {
        Display dis = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int height = dis.getHeight();
        return height;
    }

    static public long GetAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();

        return availableBlocks * blockSize;
    }

    public static String FormatSize(double size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
                if (size >= 1024) {
                    suffix = "GB";
                    size /= 1024;
                }
            }
        }
        if (suffix != null) {
            size = ((double) ((int) (size * 100)) / 100);
        }

        StringBuilder resultBuffer = null;
        if (suffix == null) {
            suffix = "B";
            resultBuffer = new StringBuilder(Long.toString((long) size));
        }
        else {
            resultBuffer = new StringBuilder(Double.toString(size));
        }
        // int commaOffset = resultBuffer.length() - 3;
        // while (commaOffset > 0)
        // {
        // resultBuffer.insert(commaOffset, ',');
        // commaOffset -= 3;
        // }
        if (suffix != null) {
            resultBuffer.append(suffix);
        }

        return resultBuffer.toString();
    }

    static public long GetTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();

        return totalBlocks * blockSize;
    }

    public static String getIpaddress(Context con) {
        WifiManager wifiManager = (WifiManager) con.getSystemService(con.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String sIp = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return sIp;
    }

    static public String appVersion(Context context) {
        String version = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
            // version = Integer.toString(pInfo.versionCode);
        }
        catch (Exception e) {
            version = "0.0.0";
        }

        return version;
    }

    static public String androidId(Context context) {

        Uri URI = Uri.parse("content://com.google.android.gsf.gservices");
        String ID_KEY = "android_id";
        String[] params = { ID_KEY };

        Cursor c = context.getContentResolver().query(URI, null, null, params, null);

        if (!c.moveToFirst() || c.getColumnCount() < 2)
            return "";

        try {
            return Long.toHexString(Long.parseLong(c.getString(1)));
        }
        catch (NumberFormatException e) {
            return "";
        }
    }

    static public boolean checkNetwokState(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo lte_4g = manager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
        boolean blte_4g = false;
        boolean bWifi = false;
        boolean bMobile = false;
        if (lte_4g != null) {
            blte_4g = lte_4g.isConnected();
        }
        if (wifi != null) {
            bWifi = wifi.isConnected();
        }
        if (mobile != null) {
            bMobile = mobile.isConnected();
        }
        if (bMobile || bWifi || blte_4g) {
            return true;
        }
        else {
            return false;
        }

    }

    static public String getMacAddress(Context context) {
        String mac = "";
        try {
            WifiManager mng = (WifiManager) context.getSystemService(Activity.WIFI_SERVICE);
            WifiInfo info = mng.getConnectionInfo();
            mac = info.getMacAddress().replaceAll("[^0-9a-fA-F]", "");
        }
        catch (Exception e) {
            mac = "";
            e.printStackTrace();
        }

        return mac;
    }

    static public String getCreateIdx(Context context) {
        String aMacAddress = getMacAddress(context);
        String Idx = "";
        try {
            DecimalFormat DF2 = new DecimalFormat("00");
            DecimalFormat DF3 = new DecimalFormat("000");
            DecimalFormat DF4 = new DecimalFormat("0000");
            Calendar tCal = Calendar.getInstance();
            Idx += DF4.format(tCal.get(Calendar.YEAR));
            Idx += DF2.format(tCal.get(Calendar.MONTH));
            Idx += DF2.format(tCal.get(Calendar.DATE));
            Idx += DF2.format(tCal.get(Calendar.HOUR_OF_DAY));
            Idx += DF2.format(tCal.get(Calendar.MINUTE));
            Idx += DF2.format(tCal.get(Calendar.SECOND));
            Idx += DF3.format(tCal.get(Calendar.MILLISECOND));
            Idx += aMacAddress;
        }
        catch (Exception e) {
            Idx = null;
        }
        return Idx;
    }

    /**
     * byte[] → int
     *
     * @param bytes
     *                  must is 1 ~ 4 bytes.
     */
    static public int byteArrayToInt(byte[] bytes) {
        int newValue = 0;
        switch (bytes.length) {
            case 1:
                newValue |= ((int) bytes[0]) & 0xFF;
                break;
            case 2:
                newValue |= (((int) bytes[0]) << 8) & 0xFF00;
                newValue |= ((int) bytes[1]) & 0xFF;
                break;
            case 3:
                newValue |= (((int) bytes[0]) << 16) & 0xFF0000;
                newValue |= (((int) bytes[1]) << 8) & 0xFF00;
                newValue |= ((int) bytes[2]) & 0xFF;
                break;
            case 4:
                newValue |= (((int) bytes[0]) << 24) & 0xFF000000;
                newValue |= (((int) bytes[1]) << 16) & 0xFF0000;
                newValue |= (((int) bytes[2]) << 8) & 0xFF00;
                newValue |= ((int) bytes[3]) & 0xFF;
        }
        return newValue;
    }



}
