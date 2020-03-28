package application.service;

/**
 * @Author: lilx
 * @Date: 2020/3/28 10:34
 * @Description:
 */
import controller.tab.Tab2Controller;
import org.apache.commons.lang3.StringUtils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * 剪贴板监控器
 * 负责对剪贴板文本的监控和操作
 * 由于监控需要一个对象作为ClipboardOwner，故不能用静态类
 *
 */
public class SystemClipboardMonitor implements ClipboardOwner{
    private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public SystemClipboardMonitor(){
        //如果剪贴板中有文本，则将它的ClipboardOwner设为自己
        if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)){
            clipboard.setContents(clipboard.getContents(null), this);
        }
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
    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // 如果不暂停一下，经常会抛出IllegalStateException
        // 猜测是操作系统正在使用系统剪切板，故暂时无法访问
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 取出文本并进行一次文本处理
        String text = null;
        if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)){
            try {
                text = (String)clipboard.getData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //String clearedText = Text.handle(text); // 自定义的处理方法
        // 存入剪贴板，并注册自己为所有者
        // 用以监控下一次剪贴板内容变化
        StringSelection tmp = new StringSelection(text);
        clipboard.setContents(tmp, this);
        if(StringUtils.isNotEmpty(text)) {
            doAutoPaste(text);
        }
    }

    private static void doAutoPaste(String text){
        if(!(!text.endsWith("\n")&& text.contains("\n")) && text.startsWith("http")) {
            Tab2Controller.instance.txt3.setText(text);
        }
    }
}
