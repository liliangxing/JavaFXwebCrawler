package application.utils;

import java.io.File;

public class MoveFiles {

    static String oldpath = "F:" + File.separator + "迅雷下载" + File.separator + "javamail";
    static String newpath = "f:" + File.separator + "file" + File.separator;
    static String contains = ".avi";

    public static void main(String[] args) {
        File filePath = new File(oldpath);
        if (filePath.exists()) {
            showAllFiles(filePath);

            System.out.println("success");

        } else {
            System.out.println("error");

        }

    }

    final static void showAllFiles(File dir) {
        File[] fs = dir.listFiles();
        for (int i = 0; i < fs.length; i++) {
            String str = fs[i].getAbsolutePath();
            if (str.contains(contains)) {
                File oldFile = new File(str);

                File fnewpath = new File(newpath);
                if (!fnewpath.exists()) {
                    fnewpath.mkdirs();
                }
                File fnew = new File(newpath + oldFile.getName());
                oldFile.renameTo(fnew);

            }
            if (fs[i].isDirectory()) {
                try {
                    showAllFiles(fs[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
