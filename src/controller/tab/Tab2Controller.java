package controller.tab;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import controller.DownFile;
import controller.CallBack;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import controller.MainController;

public class Tab2Controller implements CallBack{

	public static ExecutorService service = Executors.newFixedThreadPool(1);
	private MainController main;

	@FXML public  TextArea txtArea;
	@FXML public  TextField txt3;
	@FXML public  TextField txt4;
	@FXML public  TextField txt5;
	@FXML public  TextField txt6;
	@FXML private  Button btn2save;

	private String keyword, site;
	private static List<String> urls = new ArrayList<>();
	private int index=0;
	private int n = 15000;
	private String wynik1[] = new String[n];
    private List<String> wynik2 = new ArrayList<>(100);

	private int maxPage = 0;
	private int includeCount = 0 ;
	private Set<String> similarLinks = new TreeSet<>();
	private Set<String> similarLinksPart = new TreeSet<>();
	private Map<Integer,String> map = new HashMap<>();

	BufferedImage image2 = null;
	private  Document  doc2;

	private void print(Tab2Controller tab2Controller,String msg, Object... args) {
		String newText = tab2Controller.txtArea.getText();
		newText = (StringUtils.isNotBlank(newText)?newText + "\n":"") + String.format(msg, args);
		tab2Controller.txtArea.setText(newText);
	}

	private void print(String msg, Object... args) {
		this.print(this,msg,args);
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
		similarLinksPart.add(site);
		similarLinks.add(site);
		for (Element link : links)
		{
			String lin = link.attr("abs:href");
			if(lin.startsWith(mustInclude) && !lin.endsWith("#")){
				similarLinks.add(lin);
				if(lin.startsWith(site.substring(0,site.lastIndexOf(".")))){
					similarLinksPart.add(lin);
					includeCount++;
				}
				if(lin.substring(lin.lastIndexOf("/") + 1).contains(".")) {
					try {
						int imageName = Integer.parseInt(lin.substring(lin.lastIndexOf("/") + 1, lin.lastIndexOf("."))
								.replaceAll("_", ""));
						maxPage = maxPage > imageName ? maxPage : imageName;
						map.put(imageName, lin);
					}catch (NumberFormatException e){
						printS(e.getMessage());
					}
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

	private int GoogleImSelected(String site,Tab2Controller tab2Controller){
		firstGet(site);

		while(doFirstGet()) {

		}

		String mustInclude = txt5.getText();
		Set<String> picList  = new TreeSet<>();
		if(includeCount>1){
			similarLinks = similarLinksPart;
		}
		for(String link:similarLinks){

			printS("similarLinks: %s", link);
			Document doc3;
			Elements media;
			doc3 = doGet(link);
			media = doc3.select("[src]");
			for (Element src : media)
			{
				String width = src.attr("width");
				String pic = src.attr("abs:src");
				if(pic.contains("cover")||pic.endsWith(".js")
				||StringUtils.isNotBlank(mustInclude) && !pic.contains(mustInclude)
				||(!StringUtils.isEmpty(width) && Integer.parseInt(width)<300)
				) continue;
					picList.add(pic);
			}
		}
		for(String pic:picList){
			wynik2.add(pic);
		}
		//print(tab2Controller,"%s", StringUtils.join(wynik2,"\n"));
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
		print("\nLinks: (%d)", links.size());
		for (Element link : links) {
	       	 print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
	       	 if(link.attr("abs:href").contains("deviantart.com/art/"))
	       	 {
	       		 wynik1[i] = link.attr("abs:href");
	       		 System.out.println("o: "+wynik1[i]);
	       		 doc3 = Jsoup.connect(wynik1[i]).timeout(60*1000).get();
	       		 media = doc3.select("[src]");
	       		 for (Element src : media)
	       		 {
	       			 print(" * src: <%s>  (%s)", src.attr("abs:src"), trim(src.text(), 35));
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
		wynik2.clear();
		index = 0;

		maxPage = 0;
		similarLinks = null;
		similarLinks = new TreeSet<>();
		map = null;
		map = new HashMap<>();
		txtArea.clear();
	}
	@FXML private synchronized void btn2searchClicked(ActionEvent event) throws IOException{
		keyword = txt3.getText();
		if(urls.contains(keyword)||StringUtils.isEmpty(keyword)){
			//已经点击过
			txt3.setText("");
			txt3.requestFocus();
			return;
		}else{
			urls.add(keyword);
		}
		txt3.requestFocus();
		txt3.selectAll();
		System.out.println("Btn 2 search clicked");
		//txt3.setDisable(true);
		reset();
		/*new Thread(new Runnable() {
			@Override
			public void run() {
					GoogleImSelected(keyword,Tab2Controller.this);
					txt3.selectEnd();
					//Tab2Controller.this.txtArea.setText(StringUtils.join(wynik2,"\n"));

			}
		}).start();*/
		OutsourceThread outsourceThread = new OutsourceThread(this);
		Thread thread = new Thread(outsourceThread);
		thread.start();
		txtArea.requestFocus();
		//btn2saveClicked(event);

	}

	/**
	 * 外包线程
	 */
	public class OutsourceThread implements Runnable {

		CallBack callBack;
		public static final String name = "外包:";

		public OutsourceThread(CallBack callBack) {
			this.callBack = callBack;
		}

		@Override
		public void run() {
			try {
				GoogleImSelected(keyword,Tab2Controller.this);

				//解决了返回给雇员线程
				callBack.call();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void  call(){
		print("%s", StringUtils.join(wynik2,"\n"));
		//txtArea.requestFocus();
		txtArea.selectAll();
		/*txt3.selectAll();
		txt3.setDisable(false);*/
	}

	@FXML private void btn2browseClicked(ActionEvent event){
    	
    	System.out.println("Btn 2 browse clicked");
    	
    		Node node = (Node) event.getSource();
            DirectoryChooser directoryChooser = new DirectoryChooser();
			File selectedDirectory = 
                    directoryChooser.showDialog(node.getScene().getWindow());
			
            if(selectedDirectory == null){
                txt4.setText("No Directory selected");
                btn2save.setDisable(true);
            }else{
                txt4.setText(selectedDirectory.getAbsolutePath());
                btn2save.setDisable(false);
            }
        
    }

    private String doMatchPath(String fileName) {
		Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
		Matcher matcher = pattern.matcher(fileName);
        fileName = matcher.replaceAll(""); // 将匹配到的非法字符以空替换
		return fileName;
	}

	private String doDomain(String fileName) {

		String reg = ".*\\/\\/([^\\/\\:]*).*";
		String domain = fileName.replaceAll (reg, "$1");

		return domain;
	}
	@FXML private void btn2saveClicked(ActionEvent event) {
		System.out.println("Btn 2 save clicked");
		int threadCount = 3;
		String threadCountStr = txt6.getText();
		if(StringUtils.isNotBlank(threadCountStr)){
			threadCount = Integer.parseInt(threadCountStr);
		}
		for(String picUrl:wynik2){
			try {
				String theDir = txt4.getText()+"\\"+doDomain(picUrl)+"\\"+doMatchPath(picUrl);
				File file =new File(theDir);
				file.getParentFile().mkdirs();
				if(file.exists()&& file.length() > 1024){
					continue;
				}else {
					file.createNewFile();
				}
				System.out.println(picUrl);
    			URL url = new URL(picUrl);

				new DownFile(url,threadCount,theDir).startDown();
        		/*image2 = ImageIO.read(url);
    			ImageIO.write(image2, imgFormat, new File(theDir));*/
			}
			catch(Exception e) {
                System.out.println(e.toString());
            }
		}
		/*alert.close();
        alert.setHeaderText("Done!");
        alert.showAndWait();*/
        wynik1 = null;
        wynik1 = new String[n];
        wynik2.clear();
        index = 0;
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
	
	public void init(MainController mainController) {
		main = mainController;
	}
}