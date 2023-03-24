package application.constant;

import java.awt.*;

/**
 * 常量变量文件
 *
 *   public static int fontSize = 44;
 *     public static int lineHeight = 50;
 *     public static double lineHeightMulti = 1.3;
 *     public static int imageHeight = (int) (1362 * 0.80); // 每张图片的高度
 *     public static float leak = 2 / 4f;
 * @Author: liliangxing
 * @Date: 2023/2/19 16:43
 */
public class Constant {
    public static final String SEPARATOR = "-";
    public static int fontSize = 42;
    public static int lineHeight = 50;
    public static double lineHeightMulti = 1.5;
    public static int imageHeight = (int) (1362 * 0.82); // 每张图片的高度
    public static float leak = 1 / 4f;
    public static final Font NORMAL_FONT = new Font("宋体", Font.PLAIN, Constant.fontSize);
    public static final Font NARROW_FONT = new Font("宋体", Font.PLAIN,
            (int) (Constant.fontSize * 0.86));
    public static final Font NARROW_FONT_LIGNTHEIGHT = new Font("宋体", Font.PLAIN,
            (int) (Constant.fontSize * 0.86));
}
