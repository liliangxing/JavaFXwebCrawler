package controller.tab;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.*;

import application.utils.DownFile;
import application.utils.FileMoveUtil;
import javafx.stage.DirectoryChooser;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Tab1Controller {

	@FXML private Label lbl1 = new Label();
	@FXML private TextField txt1;
	@FXML private Button btn1Search;
	@FXML private TextArea txtArea;
	private String newText;
	private int media_size;
	@FXML private TextField txt2 = new TextField();
	@FXML private Button btn1browse;
	
	@FXML private Button btn1save;
	@FXML private Label lbl2 = new Label();

    private LinkedList<String> list = new LinkedList<>();

    @FXML
    private void btn1SearchClicked(ActionEvent event)throws IOException{
        String url = txt1.getText();
        txt1.setDisable(true);

        print("Fetching %s...", url);
        reset();
        GoogleImSelected(url,Tab1Controller.this);
        new Thread(new Runnable() {
            @Override
            public void run() {

                //txt1.setDisable(false);
                //Tab2Controller.this.txtArea.setText(StringUtils.join(wynik2,"\n"));

            }
        }).start();

    }


    @FXML public  TextField txt4;
    @FXML public  TextField txt5;
    @FXML public  TextField txt6;
    @FXML private  Button btn2save;

    private String keyword, site;
    private int index=0;
    private int n = 15000;
    private String wynik1[] = new String[n];
    private String wynik2[] = new String[n];

    private int maxPage = 0;
    private Set<String> similarLinks = new TreeSet<>();
    private Map<Integer,String> map = new HashMap<>();

    BufferedImage image2 = null;
    private  Document  doc2;


    private void print(String msg, Object... args) {
        newText = newText + "\n" + String.format(msg, args);
        txtArea.setText(newText);
    }

    private Document doGet(String url) {
        try {
            return DownFile.doGet(url).get();
        }catch (SocketTimeoutException e){
            try {
                Thread.currentThread().sleep(5000);
                return doGet(url);
            }catch (Exception ex){
                printS(ex.getMessage());
            }
        }catch (Exception e){
            printS(e.getMessage());
        }
        return null;
    }


    private void   firstGet(String site){
        String mustInclude = site.substring(0,site.lastIndexOf("/") );
        String fileName = getImageName(site);
        if(fileName.contains("_")){
            mustInclude = site.split("_")[0];
        }else if(fileName.split("\\.")[0].length()>2){
            mustInclude = site.substring(0,site.lastIndexOf(".") );
        }
        doc2 = doGet(site);
        if(null == doc2) return;
        Elements links = doc2.select("a[href]");

        similarLinks.add(site);

        for (Element link : links)
        {
            String lin = link.attr("abs:href");
            if(lin.startsWith(mustInclude) && !lin.endsWith("#")){
                similarLinks.add(lin);
                if(lin.substring(lin.lastIndexOf("/") + 1).contains(".")) {
                    int imageName = Integer.parseInt(lin.substring(lin.lastIndexOf("/") + 1, lin.lastIndexOf("."))
                            .replaceAll("_",""));
                    maxPage = maxPage> imageName?maxPage:imageName;
                    map.put(imageName,lin);
                }
            }
        }
    }

    private String getImageName(String url){
        return url.substring(url.lastIndexOf("/") + 1);
    }

    private boolean doFirstGet(){
        int tempPage = maxPage;
        firstGet(map.get(maxPage));
        if(maxPage> tempPage){
            firstGet(map.get(maxPage));
            return true;
        }
        return false;
    }

    private int GoogleImSelected(String site,Tab1Controller tab2Controller){
        firstGet(site);

        while(doFirstGet()) {

        }

        String mustInclude = txt5==null? "":txt5.getText();
        Set<String> picList  = new TreeSet<>();
        for(String link:similarLinks){

            printS("similarLinks: %s", link);
            Document doc3;
            Elements media;
            doc3 = doGet(link);
            media = doc3.select("[src]");
            for (Element src : media)
            {
                String pic = src.attr("abs:src");
                if(pic.contains("cover")||pic.endsWith(".js")||
                        StringUtils.isNotBlank(mustInclude) && !pic.contains(mustInclude)
                ) continue;
                picList.add(pic);


            }
        }
        for(String pic:picList){
            wynik2[index] = pic;
            print("\n%s", pic);
            index =  index+1;
        }

        return index;
    }


    private int DeviantArtSelected(String keyw)throws IOException{
        site = "http://www.deviantart.com/browse/all/?section=&global=1&q=";
        keyw = keyw.replace(' ', '+');
        site += keyw;
        System.out.println(site);
        int i=0, w=0;
        Document doc3;
        Elements media;
        doc2 = Jsoup.connect(site).followRedirects(true).timeout(10000).get();
        Elements links = doc2.select("a[href]");
        printS("\nLinks: (%d)", links.size());
        for (Element link : links) {
            printS(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
            if(link.attr("abs:href").contains("deviantart.com/art/"))
            {
                wynik1[i] = link.attr("abs:href");
                System.out.println("o: "+wynik1[i]);
                doc3 = Jsoup.connect(wynik1[i]).timeout(60*1000).get();
                media = doc3.select("[src]");
                for (Element src : media)
                {
                    printS(" * src: <%s>  (%s)", src.attr("abs:src"), trim(src.text(), 35));
                    if(!src.attr("width").equals("")) w = Integer.parseInt(src.attr("width"));
                    else w = 0;
                    if(w > 300 && !(src.attr("abs:src").endsWith(".js")))
                    {

                    }
                }
                i++;
            }
            if(i>=n)break;
        }

        return index;
    }

    private void reset(){
        wynik1 = null;
        wynik1 = new String[n];
        wynik2 = null;
        wynik2 = new String[n];
        index = 0;

        maxPage = 0;
        similarLinks = null;
        similarLinks = new TreeSet<>();
        map = null;
        map = new HashMap<>();
    }



    public static void printS(String msg, Object... args) {
        System.out.println(String.format(msg, args));
        //System.out.println(newText);
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
   
    @FXML private void btn1browseClicked(ActionEvent event){
    	
    	System.out.println("Btn 1 browse clicked");
    	
    		Node node = (Node) event.getSource();
            DirectoryChooser directoryChooser = new DirectoryChooser();
			File selectedDirectory = 
                    directoryChooser.showDialog(node.getScene().getWindow());
			
            if(selectedDirectory == null){
                txt2.setText("No Directory selected");
                btn1save.setDisable(true);
            }else{
                txt2.setText(selectedDirectory.getAbsolutePath());
                btn1save.setDisable(false);
            }
        
    }
    
    @FXML private void btn1saveClicked(ActionEvent event) {
        System.out.println("Btn 1 save clicked");
        String oldPath = txt2.getText();
        String preFixPath = oldPath;
        String documentName = null;
        if (oldPath.endsWith(File.separator)) {
            preFixPath = oldPath.substring(0, oldPath.lastIndexOf(File.separator));
        }
        documentName = oldPath.substring(oldPath.lastIndexOf(File.separator) + 1);


        File a = new File(oldPath);
        String[] file = a.list();
        String fileName = null;
        String newPath = null;
        int fileNum = StringUtils.isNotBlank(txt1.getText()) && txt1.getText().matches("[0-9]+") ? Integer.parseInt(txt1.getText()): 500 ;
        for (int i = 0; i < file.length; i++) {
            if(i%fileNum == 0) {
                newPath = preFixPath + File.separator + documentName + "_"+i/fileNum;
                File f = new File(newPath);
                System.out.println("Directory made: " + f.mkdirs());
            }
            //如果以分隔符结尾,
            fileName =  file[i];
            FileMoveUtil.removefile(fileName, oldPath, newPath);
        }
    }
}
