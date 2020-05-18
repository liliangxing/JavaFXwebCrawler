package application.utils;

import controller.InstallCert;
import org.jsoup.nodes.Document;

import javax.net.ssl.SSLHandshakeException;
import java.net.SocketTimeoutException;
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
        Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
        Matcher matcher = pattern.matcher(fileName2);
        String fileName = matcher.replaceAll(""); // 将匹配到的非法字符以空替换
        if(fileName.contains("weishi")){
            Matcher m = Pattern.compile(".*\\/(.*\\.mp4).*").matcher(fileName2);
            if(m.find()){
                fileName = m.group(1);
            }
        }
        return fileName;
    }

    public static String doDomain(String fileName) {

        String reg = ".*\\/\\/([^\\/\\:]*).*";
        String domain = fileName.replaceAll (reg, "$1");

        return domain;
    }
    public static Document doGet(String url) {
        try {
            return DownFile.doGet(url).get();
        }catch (SocketTimeoutException e){
            try {
                Thread.currentThread().sleep(5000);
                return doGet(url);
            }catch (Exception ex){
                TabUtil.printS(ex.getMessage());
            }
        }catch (SSLHandshakeException e){
           //下载证书
            try {
                InstallCert.main(new String[]{doDomain(url)});
            }catch (Exception e1){
                TabUtil.printS(e1.getMessage());
            }
        }catch (Exception e){
            TabUtil.printS(e.getMessage());
        }
        return null;
    }
}
