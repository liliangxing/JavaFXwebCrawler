package application.service;

/**
 * @Author: lilx
 * @Date: 2020/3/28 10:34
 * @Description:
 */
import controller.tab.Tab2Controller;

import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.io.IOException;

/**
 * 剪贴板监控器
 * 负责对剪贴板文本的监控和操作
 * 由于监控需要一个对象作为ClipboardOwner，故不能用静态类
 *
 */
public class SystemClipboardMonitor implements FlavorListener {
    private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public SystemClipboardMonitor(){
        clipboard.addFlavorListener(this) ;
    }

    /************
     * 测试代码 *
     * **********
     */
    public static void main(String[] args) {
        SystemClipboardMonitor temp = new SystemClipboardMonitor();
        //new JFrame().setVisible(true); // 软件窗口
    }

    /**********************************************
     * 如果剪贴板的内容改变，则系统自动调用此方法 *
     **********************************************
     */
    public void flavorsChanged(FlavorEvent flavorEvent) {
        // 如果不暂停一下，经常会抛出IllegalStateException
        // 猜测是操作系统正在使用系统剪切板，故暂时无法访问
        Tab2Controller.printS("-------------");
        try {
            Thread.currentThread().sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String text = "";
        try {

            // 取出文本并进行一次文本处理
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)){
                try {
                    text = (String)clipboard.getData(DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        Tab2Controller.printS("flavorsChanged触发：%s",text);

        if(!(!text.endsWith("\n")&& text.contains("\n")) && text.startsWith("http")) {
            Tab2Controller.instance.txt3.setText(text);
        }else {
            Tab2Controller.instance.txt3.setText(text);
        }
        //Tab2Controller.instance.txt3.requestFocus();
        Tab2Controller.instance.txt3.selectAll();
    }

}
