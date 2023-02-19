package application.constant;

import java.awt.*;

/**
 * 常量变量文件
 *
 * @Author: liliangxing
 * @Date: 2023/2/19 16:43
 */
public class Constant {
    public static final String SEPARATOR = "-";
    public static int fontSize = 46;
    public static int imageFeight = (int) (1362 * 0.9); // 每张图片的高度
    public static float leak = 1 / 4f;
    public static final Font NORMAL_FONT = new Font("宋体", Font.PLAIN, Constant.fontSize);
    public static final Font NARROW_FONT = new Font("宋体", Font.PLAIN,
            (int) (Constant.fontSize * 0.85));
}
