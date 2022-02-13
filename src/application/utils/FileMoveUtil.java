package application.utils;


import java.io.File;
import java.util.Scanner;

/******************
 * 文件的移动和重命名
 *******************/
public class FileMoveUtil {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        System.out.println("请输入文件当前目录：");
        String oldpath = sc.next();
        System.out.println("请输入目的目录：");
        String newpath = sc.next();
        File newpaths = new File(newpath);
        if (newpaths.exists()) {
            System.out.println("请输入要移动的文件名：");
            String files = sc.next();
            removefile(files, oldpath, newpath);
        } else
            System.out.println("该目录不存在！");
    }

    public static void removefile(String filename, String oldpath, String newpath) {
        if (!oldpath.equals(newpath)) {
            File oldfile = new File(oldpath + "/" + filename);
            File newfile = new File(newpath + "/" + filename);
            if (oldfile.exists()) {
                if (newfile.exists()) {

                    newfile.delete();
                    oldfile.renameTo(newfile);

                } else {
                    oldfile.renameTo(newfile);
                                 }
            } else {
                System.out.println("文件不存在！");
            }

        }
    }

    public static void renameFile(String path, String oldFileName, String newFileName) {
        if (!oldFileName.equals(newFileName)) {
            File oldfile = new File(path + "/" + oldFileName);
            File newfile = new File(path + "/" + newFileName);
            oldfile.renameTo(newfile);
        } else {
            System.out.println("新名称与旧名称一致,是否重新命名？1：是；2：取消修改。");
            int key = sc.nextInt();
            if (key == 1) {
                System.out.println("请输入新的文件名：");
                String newFileNames = sc.next();
                renameFile(path, oldFileName, newFileNames);
            }
        }
    }
}

