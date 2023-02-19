package application.utils;

import application.constant.Constant;
import sun.font.FontDesignMetrics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 功能描述
 *
 * @Author: liliangxing
 * @Date: 2023/2/19 17:31
 */
public class TextToImageLong {
    /**
     * 图片宽度
     */
    private static int width = 720;
    /**
     * 每一行的高度
     */
    private static int line_height = 50;
    /**
     * 字体
     */
    private static Font font = new Font("宋体", Font.PLAIN, Constant.fontSize);

    public static void main(String[] args) {
        String message = "“两国交兵，不斩来使”在后世流传下来的交战规则主要只有“两国交兵，不斩来使”。春秋时期诸侯派出的外交使节是不可侵犯的。公元前596年楚国派出申舟出使齐国，楚庄王特意嘱咐不要从宋国经过。宋国执政华元听说了，觉得这是对宋国的莫大侮辱，就设伏击杀死楚国使者。楚庄王为此“投袂而起”，出动大军包围宋国国都整整9个月。宋国派出使者到晋国告急，晋国上一年刚被楚军打败，不敢冒与楚国全面冲突的危险，只是派解扬为使者劝宋国坚守，不要投降。解扬经过郑国，被郑国抓起来交给楚国。楚庄王亲自接见解扬，企图买通他，要他向宋军喊话，说晋军不再提供救援，断绝宋军的希望，解扬不同意。经楚庄王几次威逼利诱，解扬才答应下来。可是当解扬来到了望城中的楼车上，就大声疾呼，说晋国援军不日就到，请宋国无论如何要坚持下去。楚庄王大怒，解扬说：“我答应你的条件只是为了实现使命，现在使命实现了，请立刻处死我。”楚庄王无话可说，反而释放他回晋国。长期围困而无战果，楚庄王打算退兵，可申舟的父亲拦在车前，说：“我儿子不惜生命以完成国王的使命，难道国王要食言了吗？”楚庄王无言以对。申舟父亲建议在宋国建造住房、耕种土地，表示要长期占领宋国，宋国就会表示屈服。宋国见楚军不肯撤退，就派华元为使者来谈判。华元半夜里潜入楚军大营，劫持了楚军统帅子反，说：“我的国君要我为使者来谈判，现在城内确实已是‘易子而食，析骸以爨’，但是如果订立城下之盟则情愿举国牺牲。贵军退到三十里外，我国唯命是听。”子反就在睡床上保证做到。第二天报告了楚庄王，楚军真的退30里外，和宋国停战，双方保证不再互相欺瞒，华元作为这项和约的人质到楚国居住。\n" +
                "后世将这一交战规则称之为“两国交兵，不斩来使”。历史上最著名的战时两国使节以礼相见的故事是“彭城相会”。450年南朝刘宋与北魏发生战争，刘宋发起北伐，先胜后败，战略据点彭城被包围。江夏王刘义恭率领军队死守彭城（今徐州），北魏太武帝想一举打过长江，派出李孝伯为使节进彭城劝降。刘义恭派了张畅为代表与李孝伯谈判。两人都是当时的“名士”，互相代表各自的君主赠送礼品，尽管处在极其残酷的战争环境，但他们在谈判中却仍然是文质彬彬、礼貌周全。这次谈判本身并没有什么实质性的结果，可双方的礼节及言辞，一直被后世誉为战场佳话。";
        generateImage(message);
    }

    public static void generateImage(String message) {
        String[] strArr = message.split("\n");
        createImage(strArr);
    }

    public static void createImage(String[] strArr) {


        FontMetrics fm = FontDesignMetrics.getMetrics(font);
        int stringWidth = fm.charWidth('字');// 标点符号也算一个字
        //计算每行多少字 = 宽/每个字占用的宽度
        int line_string_num = width % stringWidth == 0 ? (width / stringWidth) : (width / stringWidth) + 1;

        System.out.println("每行字数=" + line_string_num);
        //将数组转为list
        List<String> strList = new ArrayList<>(Arrays.asList(strArr));

        //按照每行多少个字进行分割
        for (int j = 0; j < strList.size(); j++) {
            //当字数超过限制，就进行分割
            String tempStr = strList.get(j);
            int length = TabUtil.calcCharacterLength(tempStr);
            if (length > line_string_num) {
                //将多的那一端放入本行下一行，等待下一个循环处理
                strList.add(j + 1, tempStr.substring(line_string_num));
                //更新本行的内容
                strList.set(j, tempStr.substring(0, line_string_num));
            }
        }

        //计算图片的高度，多预留一行
        int image_height = strList.size() * line_height + line_height;

        //每张图片有多少行文字
        int every_line = image_height / line_height;


        for (int m = 0; m < 1; m++) {
            String filePath = "E:\\AndroidProject\\E\\d" + m + ".jpg";
            File outFile = new File(filePath);
            // 创建图片  宽度多预留一点
            int actualWide = width + Constant.fontSize;
            BufferedImage image = new BufferedImage(actualWide, image_height,
                    BufferedImage.TYPE_INT_BGR);
            Graphics g = image.getGraphics();
            g.setClip(0, 0, actualWide, image_height);
            g.setColor(Color.white); // 背景色白色
            g.fillRect(0, 0, actualWide, image_height);

            g.setColor(Color.black);//  字体颜色黑色
            g.setFont(font);// 设置画笔字体

            // 每张多少行，当到最后一张时判断是否填充满
            for (int i = 0; i < every_line; i++) {
                g.setFont(Constant.NARROW_FONT);
                int index = i + m * every_line;
                if (strList.size() - 1 >= index) {
//                    System.out.println("每行实际=" + newList.get(index).length());
                    int x = (int) (Constant.fontSize * Constant.leak);
                    g.drawString(strList.get(index), x, line_height * (i + 1));
                }
            }
            g.dispose();
            try {
                ImageIO.write(image, "jpg", outFile);// 输出png图片
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
