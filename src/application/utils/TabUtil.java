package application.utils;

import controller.InstallCert;
import org.jsoup.nodes.Document;

import javax.net.ssl.SSLHandshakeException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: lilx
 * @Date: 2020/3/30 16:20
 * @Description:
 */
public class TabUtil {
    public static void printS(String msg, Object... args) {
        System.out.println(String.format(msg, args));
        //System.out.println(newText);
    }

    public static String doMatchPath(String fileName2) {
        String fileName = null;
        String tempName = fileName2;
        String lastName = fileName2.substring(tempName.lastIndexOf("/") + 1);
        while (true) {
            if (lastName.length() > 20) {
                fileName = lastName;
                break;
            } else {
                tempName = tempName.substring(0, tempName.lastIndexOf("/"));
                lastName = fileName2.substring(tempName.lastIndexOf("/") + 1);
            }
        }
        // fileName处理
        Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
        Matcher matcher = pattern.matcher(fileName);
        fileName = matcher.replaceAll("_"); // 将匹配到的非法字符以空替换

        if (fileName2.contains("weishi")) {
            Matcher m = Pattern.compile(".*\\/(.*\\.mp4).*").matcher(fileName2);
            if (m.find()) {
                fileName = m.group(1);
            }
        } else if (fileName2.contains("video_id=")) {
            Matcher m = Pattern.compile("video_id=([\\w-][^&]+)").matcher(fileName2);
            if (m.find()) {
                fileName = m.group(1) + ".mp4";
            }
        } else if (fileName2.contains("video/tos")) {
            Matcher m = Pattern.compile("\\/([\\w-][^\\/]+\\/[\\w-][^\\/]+)\\/video/tos").matcher(fileName2);
            if (m.find()) {
                fileName = m.group(1).replaceAll("\\/", "") + ".mp4";
            }
        }
        return fileName;
    }

    public static String doDomain(String fileName) {

        String reg = ".*\\/\\/([^\\/\\:]*).*";
        String domain = fileName.replaceAll(reg, "$1");

        return domain;
    }

    public static Document doGet(String url) {
        try {
            return DownFile.doGet(url).get();
        } catch (SocketTimeoutException e) {
            try {
                Thread.currentThread().sleep(5000);
                return doGet(url);
            } catch (Exception ex) {
                TabUtil.printS(ex.getMessage());
            }
        } catch (SSLHandshakeException e) {
            //下载证书
            try {
                InstallCert.main(new String[]{doDomain(url)});
            } catch (Exception e1) {
                TabUtil.printS(e1.getMessage());
            }
        } catch (Exception e) {
            TabUtil.printS(e.getMessage());
        }
        return null;
    }

    /**
     * 计算字符串内容字符长度
     * 一个中文相当于两个英文
     */
    public static int calcCharacterLength(String checkStr) {
        try {
            if (null == checkStr || checkStr.length() == 0) {
                return 0;
            }
            String newString = new String(checkStr.getBytes("GB2312"), StandardCharsets.ISO_8859_1);
            int strLen = (int) (newString.length() / 2.0);
            return strLen;
        } catch (UnsupportedEncodingException  e) {
            e.printStackTrace();
        }
        return 0;
    }
}
