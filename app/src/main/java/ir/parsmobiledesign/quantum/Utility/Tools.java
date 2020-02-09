package ir.parsmobiledesign.quantum.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
//import android.support.annotation.RequiresPermission;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.InflaterInputStream;

import javax.crypto.IllegalBlockSizeException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Tools {


    private static char[] persianDigits = {'۰', '۱', '۲', '۳', '۴', '۵', '۶','۷', '۸', '۹'};
    private static char[] latinDigits = {'0', '1', '2', '3', '4', '5', '6','7', '8', '9'};

    public static String rot13(String s) {
        String ret = "";
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 'a' && c <= 'm')
                c += 13;
            else if (c >= 'A' && c <= 'M')
                c += 13;
            else if (c >= 'n' && c <= 'z')
                c -= 13;
            else if (c >= 'N' && c <= 'Z')
                c -= 13;
            ret += c;
        }
        return ret;
    }

    public static String base64encode(String s) {
        return Base64.encodeToString(s.getBytes(), Base64.DEFAULT);
    }

    public static String base64decode(String s) {
        return new String(Base64.decode(s, Base64.DEFAULT));
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isNationalCodeValid(String nationalCode) {

        boolean isValid = true;

        if (nationalCode.length() != 10)
            isValid = false;
        else {
            int sum = 0;
            int checkDigit = Character.getNumericValue(nationalCode.charAt(9));
            for (int i = 0; i < 9; i++)
                sum += (10 - i) * Character.getNumericValue(nationalCode.charAt(i));
            int remained = sum % 11;
            if ((remained >= 2 || remained != checkDigit) && (checkDigit < 2 || checkDigit != (11 - remained)))
                return false;
        }

        return isValid;
    }

    public static String getOverview(String data, int num_of_characters) {
        if (data.length() <= num_of_characters)
            return data;
        return data.substring(0, num_of_characters);
    }

    public static String makeValidPath(String path) {
        String new_path = path.replaceAll("\\:", "_");
        new_path = new_path.replaceAll("\\.", "_");
        return new_path;
    }



    public static String calcDateChrist(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int miladiYear = cal.get(Calendar.YEAR);
        int miladiMonth = cal.get(Calendar.MONTH);
        int miladiDate = cal.get(Calendar.DATE);
        return miladiYear + "." + miladiMonth + "."
                + miladiDate;
    }

    public static String getExtentionFromPath(String path) { // including the
        // dot
        int ind = path.lastIndexOf(".");
        return path.substring(ind);
    }

    public static String formatPriceToFarsi(String number) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < number.length(); i++) {
            if (Character.isDigit(number.charAt(i))) {
                if ((number.length() - i) % 3 == 0 && i > 0
                        && i != number.length() - 1)
                    sb.append("," + number.charAt(i));
                else

                    sb.append(number.charAt(i));
            } else {
                sb.append(i);
            }
        }
        return sb.toString();
    }

    public static String convertToNumber(String farsiNumber) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < farsiNumber.length(); i++)
            if (Character.isDigit(farsiNumber.charAt(i)))
                sb.append(farsiNumber.charAt(i));

        return sb.toString();
    }


    public static String convertToDuration(long seconds) { // 23:34:45
        int hoursNumber = (int) (seconds / 3600);
        int minutesNumber = (int) ((seconds % 3600) / 60);
        int secondsNumber = (int) ((seconds % 3600) % 60);
        return hoursNumber + ":" + minutesNumber + ":" + secondsNumber;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
                .getWindowToken(), 0);
    }

    public static String unzipString(String zippedText) {
        String unzipped = null;
        try {
            byte[] zbytes = zippedText.getBytes();
            // Add extra byte to array when Inflater is set to true
            byte[] input = new byte[zbytes.length + 1];
            System.arraycopy(zbytes, 0, input, 0, zbytes.length);
            input[zbytes.length] = 0;
            ByteArrayInputStream bin = new ByteArrayInputStream(input);
            InflaterInputStream in = new InflaterInputStream(bin);
            ByteArrayOutputStream bout = new ByteArrayOutputStream(512);
            int b;
            while ((b = in.read()) != -1) {
                bout.write(b);
            }
            bout.close();
            unzipped = bout.toString();
        } catch (IOException io) {
            io.printStackTrace();
        }
        return unzipped;
    }


    public static String replacePersianCharsWithLatinChars(String persianNumber) {
        String replaced = persianNumber;
        for (int i = 0; i <= 9; i++) {
            replaced = replaced.replace(persianDigits[i], latinDigits[i]);
        }
        return replaced;
    }

    public static void hideSoftKey(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(context);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }


    public static String toHex(String arg) throws UnsupportedEncodingException {
        return String.format("%x", new BigInteger(1, arg.getBytes("utf-8")));
    }

    public static boolean isInstalledApp(Context context, String packageName) {
        boolean appInstalled = false;
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        } catch (Exception e) {
            appInstalled = false;
        }

        return appInstalled;
    }


//    @RequiresPermission(anyOf = {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION})
//    public static Location getMyLocation(Context mCtx) throws Exception {
//        boolean gps_enabled = false;
//        boolean network_enabled = false;
//
//        LocationManager lm = (LocationManager) mCtx
//                .getSystemService(Context.LOCATION_SERVICE);
//
//        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
//        Location net_loc = null, gps_loc = null, finalLoc = null;
//
//        if (gps_enabled)
//            gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//        if (network_enabled)
//            net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//
//        if (gps_loc != null && net_loc != null) {
//
//            if (gps_loc.getAccuracy() >= net_loc.getAccuracy())
//                finalLoc = gps_loc;
//            else
//                finalLoc = net_loc;
//
//            // I used this just to get an idea (if both avail, its upto you which you want to take as I taken location with more accuracy)
//
//        } else {
//
//            if (gps_loc != null) {
//                finalLoc = net_loc;
//            } else if (net_loc != null) {
//                finalLoc = gps_loc;
//            }
//        }
//
//        return finalLoc;
//    }

    public static String getPackageName(Context mContext) {
        return mContext.getPackageName();
    }

    public static String getFingerPrint(Context mContext) {
        PackageManager pm = mContext.getPackageManager();
        String packageName = mContext.getPackageName();
        int flags = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();
        InputStream input = new ByteArrayInputStream(cert);
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        X509Certificate c = null;
        try {
            c = (X509Certificate) cf.generateCertificate(input);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        String hexString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(c.getEncoded());
            hexString = byte2HexFormatted(publicKey);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return hexString;
    }

    public static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1) h = "0" + h;
            if (l > 2) h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1)) str.append(':');
        }
        return str.toString();
    }

    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static Boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    private static boolean checkRootMethod1() {
        String buildTags = Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT)
                || "goldfish".equals(Build.HARDWARE)
                || "ranchu".equals(Build.HARDWARE);
    }


    /*public static void sendEmail(final String smtpHost, final String port,
                                 final String login, final String password, final String from,
                                 final String body, final String subject, final String[] recipient) {

        new AsyncTask<Void, Void, MimeMessage>() {

            @Override
            protected MimeMessage doInBackground(Void... voids) {
                try {
                    Properties mailProps = new Properties();
                    mailProps.put("mail.smtp.from", from);
                    mailProps.put("mail.smtp.host", smtpHost);
                    mailProps.put("mail.smtp.port", port);
                    mailProps.put("mail.smtp.auth", "true");
                    mailProps.put("mail.smtp.socketFactory.port", port);
                    mailProps.put("mail.smtp.socketFactory.class",
                            "javax.net.ssl.SSLSocketFactory");
                    mailProps.put("mail.smtp.socketFactory.fallback", "false");
                    mailProps.put("mail.smtp.starttls.enable", "true");

                    Session mailSession = Session.getInstance(mailProps,null);

                    MimeMessage message = new MimeMessage(mailSession);

                    message.setFrom(new InternetAddress(from));

                    String[] emails = recipient.clone();
                    InternetAddress dests[] = new InternetAddress[emails.length];
                    for (int i = 0; i < emails.length; i++) {
                        dests[i] = new InternetAddress(emails[i].trim().toLowerCase());
                    }
                    message.setRecipients(Message.RecipientType.BCC, dests);
                    message.setSubject(subject, "UTF-8");
                    message.setContent(body, "text/html; charset=utf-8");

                    Transport.send(message, login, password);
                    return message;
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(MimeMessage message) {

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }

*/


    public static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Takes a byte array as input and provides a Hex String reporesentation
     *
     * @param input
     * @return
     */
    public static String getHexString(byte[] input) {
        StringBuilder strBuilder = new StringBuilder();
        for (byte hexByte : input) {
            int res = 0xFF & hexByte;
            String hexString = Integer.toHexString(res);
            if (hexString.length() == 1) {
                strBuilder.append(0);
            }
            strBuilder.append(hexString);

        }

        return strBuilder.toString();
    }

    /**
     * Takes Hex representation of the key, validates the length and returns the
     * equivallent bytes
     *
     * @param keyString Hex representation of the key. THe allowed length of the
     *                  string are 16 (56 bit), 32 (112 bit), 32 or 48 (for 168 bit).
     *                  If the key Strength is 168 bit and the key length is 32 the
     *                  first 16 chars are repeated.
     * @param keySize   Valid values are 56, 112, 168
     * @return
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     */
    public static byte[] getEncryptionKey(String keyString, int keySize)
            throws IllegalBlockSizeException, InvalidKeyException {
        int keyLength = keyString.length();
        switch (keySize) {
            case 56:
                if (keyLength != 16)
                    throw new InvalidKeyException(
                            "Hex Key length should be 16 for a 56 Bit Encryption, found ["
                                    + keyLength + "]");
                break;
            case 112:
                if (keyLength != 32)
                    throw new InvalidKeyException(
                            "Hex Key length should be 32 for a 112 Bit Encryption, found["
                                    + keyLength + "]");
                break;
            case 168:
                if (keyLength != 32 && keyLength != 48)
                    throw new InvalidKeyException(
                            "Hex Key length should be 32 or 48 for a 168 Bit Encryption, found["
                                    + keyLength + "]");
                if (keyLength == 32) {
                    keyString = keyString + keyString.substring(0, 16);
                }
                break;
            default:
                throw new InvalidKeyException(
                        "Invalid Key Size, expected one of [56, 112, 168], found["
                                + keySize + "]");
        }

        byte[] keyBytes = getHexByteArray(keyString);
        return keyBytes;

    }

    /**
     * Converts a Hex string representation to an byte array
     *
     * @param input Every two character of the string is assumed to be
     * @return byte array containing the Hex String input
     * @throws IllegalBlockSizeException
     */
    public static byte[] getHexByteArray(String input)
            throws IllegalBlockSizeException {

        int[] resultHex = getHexIntArray(input);
        byte[] returnBytes = new byte[resultHex.length];
        for (int cnt = 0; cnt < resultHex.length; cnt++) {
            returnBytes[cnt] = (byte) resultHex[cnt];
        }
        return returnBytes;
    }

    /**
     * Converts a Hex string representation to an int array
     *
     * @param input Every two character of the string is assumed to be
     * @return int array containing the Hex String input
     * @throws IllegalBlockSizeException
     */
    public static int[] getHexIntArray(String input)
            throws IllegalBlockSizeException {
        if (input.length() % 2 != 0) {
            throw new IllegalBlockSizeException(
                    "Invalid Hex String, Hex representation length is not a multiple of 2");
        }
        int[] resultHex = new int[input.length() / 2];
        for (int iCnt1 = 0; iCnt1 < input.length(); iCnt1++) {
            String byteString = input.substring(iCnt1, ++iCnt1 + 1);
            int hexOut = Integer.parseInt(byteString, 16);
            resultHex[iCnt1 / 2] = (hexOut & 0x000000ff);
        }
        return resultHex;
    }

    public static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    public static byte[] byteArrayRightPadding(byte[] initial, int length) {
        int initialByteArrayLength = initial.length;
        byte[] paddedByteArray = new byte[length];
        if (initialByteArrayLength > length)
            System.arraycopy(initial, 0, length, 0, initialByteArrayLength);
        else for (int i = 0; i < length; i++)
            paddedByteArray[i] = i < initialByteArrayLength ? initial[i] : 0x00;
        return paddedByteArray;
    }

    public static int getOperatorType(String cellNumber) {
        if (cellNumber.startsWith("091") || cellNumber.startsWith("099"))
            return 1;
        else if (cellNumber.startsWith("092"))
            return 3;
        else if (cellNumber.startsWith("093") || cellNumber.startsWith("090"))
            return 2;
        else
            return 0;
    }

    public static boolean checkBillID(String billID)

    {
        int ckeckdigit = Integer.parseInt(billID.substring(billID.length() - 1,
                billID.length()));
        String tmp = billID.substring(0, billID.length() - 1);
        int factor = 2;
        long total = 0;
        for (int i = tmp.length() - 1; i >= 0; i--) {
            total += Integer.parseInt(tmp.substring(i, i + 1)) * factor;
            if (++factor > 7)
                factor = 2;
        }
        byte rem = (byte) (total % 11);
        if (rem > 1)
            rem = (byte) (11 - rem);
        else
            rem = 0;
        return (ckeckdigit == rem);
    }

    public static boolean checkPaymentID(String paymentID) {
        int ckeckdigit = Integer.parseInt(paymentID.substring(
                paymentID.length() - 2, paymentID.length() - 1));
        String tmp = paymentID.substring(0, paymentID.length() - 2);
        int factor = 2;
        long total = 0;
        for (int i = tmp.length() - 1; i >= 0; i--) {
            // Error Detected
            total += Integer.parseInt(tmp.substring(i, i + 1)) * factor;
            if (++factor > 7)
                factor = 2;
        }
        byte rem = (byte) (total % 11);
        if (rem > 1)
            rem = (byte) (11 - rem);
        else
            rem = 0;
        return (ckeckdigit == rem);
    }

    public static boolean checkBillIDAndPaymentID(String billID, String paymentID) {
        String ID = billID + paymentID;
        int ckeckdigit = Integer.parseInt(ID.substring(ID.length() - 1,
                ID.length()));
        String tmp = ID.substring(0, ID.length() - 1);
        int factor = 2;

        long total = 0;
        for (int i = tmp.length() - 1; i >= 0; i--) {
            // Error Detected
            total += Integer.parseInt(tmp.substring(i, i + 1)) * factor;
            if (++factor > 7)
                factor = 2;
        }
        byte rem = (byte) (total % 11);
        if (rem > 1)
            rem = (byte) (11 - rem);
        else
            rem = 0;
        return (ckeckdigit == rem);
    }

    public static Bitmap getBitmapFromView(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);

        return bitmap;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = Integer.decode("0x" + s.substring(i, i + 2)).byteValue();
        }
        return data;
    }

    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public static String convertCharArrayToHexString(String charArray) {
        String result = "";
        for (int i = 0; i < charArray.length(); i++)
            result += String.format("%02x", (byte) charArray.charAt(i));
        return result;

    }

    public static String getTimeMMDDHHMMSS() {
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(cDate);
        return fDate.replace("-", "").replace(" ", "").replace(":", "").substring(4);
    }

    public static int countOccurrences(String haystack, char needle) {
        int count = 0;
        for (int i = 0; i < haystack.length(); i++) {
            if (haystack.charAt(i) == needle) {
                count++;
            }
        }
        return count;
    }

}
