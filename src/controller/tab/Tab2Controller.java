package controller.tab;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import application.downloader.Downloader;
import application.service.SystemClipboardMonitor;
import application.utils.ClipboardUtil;
import application.utils.DownFile;
import application.utils.TabUtil;
import controller.CallBack;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;

public class Tab2Controller implements CallBack  {

	public final static  ExecutorService threadPool =  Executors.newCachedThreadPool();
	@FXML public  TextArea txtArea;
	@FXML public  TextField txt3;
	@FXML public  TextField txt4;
	@FXML public  TextField txt5;
	@FXML public  TextField txt6;
	@FXML private  Button btn2save;
	private String keyword, site;
	private static List<String> urls = new ArrayList<>();
	private int index=0;
    private List<String> wynik2 = new ArrayList<>(100);

	private int maxPage = 0;
	private int includeCount = 0 ;
	private int previousKeySet;
	private Set<String> similarLinks = new TreeSet<>();
	private Set<String> similarLinksPart = new TreeSet<>();
	private Map<Integer,String> pageLinks = new HashMap<>();
	private Map<Integer,String> beginPageUrl;
	public static Tab2Controller instance ;

	BufferedImage image2 = null;
	private  Document  doc2;


	@FXML public void initialize() {
		instance = this;
		new SystemClipboardMonitor();
	}

	private void print(Tab2Controller tab2Controller,String msg, Object... args) {
		String newText = tab2Controller.txtArea.getText();
		newText = (StringUtils.isNotBlank(newText)?newText + "\n":"") + String.format(msg, args);
		tab2Controller.txtArea.setText(newText);
	}

	private void print(String msg, Object... args) {
		this.print(this,msg,args);
	}


	private void  firstGet(String site){
		if(null==site) return;
		String mustInclude = site.substring(0,site.lastIndexOf("/") );
		String fileName = getImageName(site).split("\\.")[0];
		if(fileName.contains("_")){
			mustInclude = site.split("_")[0];
		}else if(fileName.length()>2){
			if(site.lastIndexOf(".") < 0) return;
			mustInclude = site.substring(0,site.lastIndexOf(".") );
			if( fileName.matches(".*[a-zA-z]+[\\d]+.*")
			&& !fileName.matches(".*[a-zA-z]+[\\d]{3}.*")){
				mustInclude=  site.substring(0,site.lastIndexOf("/")+1)+fileName.replaceAll("[\\d]+","");
			}
		}
		doc2 = TabUtil.doGet(site);
		if(null == doc2) return;
		Elements links = doc2.select("a[href]");
		similarLinksPart.add(site);
		similarLinks.add(site);
		links.add(new Element(Tag.valueOf("a"),site, new Attributes().put("href",site)));
		for (Element link : links)
		{
			String lin = link.attr("abs:href");
			if(lin.startsWith(mustInclude) && !lin.endsWith("#")){
				similarLinks.add(lin);
				if(lin.startsWith(site.substring(0,site.lastIndexOf(".")))
				&& site.equals(keyword) && !fileName.matches(".*[a-zA-z]+[\\d]+.*")){
					similarLinksPart.add(lin);
					includeCount++;
				}
				if(lin.substring(lin.lastIndexOf("/") + 1).contains(".")) {
					try {
						String[] mylinArr =lin.substring(lin.lastIndexOf("/") + 1, lin.lastIndexOf(".")).split("_");
						String myLin;
						if(mylinArr.length>1){
							myLin = mylinArr[mylinArr.length-1];
						}else {
							myLin =  mylinArr[0];
						}
						myLin =myLin.replaceAll("[\\D]+", "");
						int imageName = StringUtils.isEmpty(myLin)?0:Integer.parseInt(myLin);
						if(imageName<200) {
							maxPage = maxPage > imageName ? maxPage : imageName;
						}
						pageLinks.put(imageName, lin);
					}catch (NumberFormatException e){
						TabUtil.printS(e.getMessage());
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
		firstGet(pageLinks.get(maxPage));
		if(maxPage> tempPage){
			firstGet(pageLinks.get(maxPage));
			//继续
			return true;
		}else {
			tempPage = maxPage;
			int tempMax = doRemove();
			if(tempMax > 0 ) {
				firstGet(pageLinks.get(tempMax));
				//继续
				return true;
			}else {
				if(maxPage> tempPage){
					firstGet(pageLinks.get(maxPage));
					//继续
					return true;
				}else {
					int pageKeySet = (int)beginPageUrl.keySet().toArray()[0];
					if(pageKeySet != previousKeySet) {
						firstGet(beginPageUrl.get(pageKeySet));
						//有爬到新的页面才继续，不然死循环在一个url
							previousKeySet = pageKeySet;
							return true;
					}
				}
			}
		}
		//终止循环
		return false;
	}

	private int doRemove(){
		if(pageLinks.size()==1){
			beginPageUrl.clear();
			beginPageUrl.putAll(pageLinks);
		}
		pageLinks.remove(maxPage);
		if(pageLinks.isEmpty()){
			return 0;
		}
		Set<Integer> set = pageLinks.keySet();
		Object[] obj = set.toArray();
		Arrays.sort(obj);
		int tempMax = (int)obj[obj.length - 1];
		if(maxPage-tempMax == 1 || Math.abs(maxPage-tempMax)>100){
			maxPage = tempMax;
			return doRemove();
		}else {
			return tempMax;
		}
	}

	private int GoogleImSelected(String site,Tab2Controller tab2Controller){
		site = site.replaceAll("(.*_)[\\d]+.html","$1"+"2.html");
		firstGet(site);
		while(doFirstGet()) {

		}

		String mustInclude = txt5.getText();
		Set<String> picList  = new TreeSet<>();
		if(includeCount>1){
			//similarLinks = similarLinksPart;
		}
		String reg = "(.*[page|p]=)[\\d]+(.*)";
		if(site.matches(reg)){
			String siteBuilder = site.replaceAll (reg, "$1"+"xxxx"+"$2");
			for(int i=0;i< 100;i++){
				similarLinks.add(siteBuilder.replaceAll("xxxx",i+""));
			}
		}
		for(String link:similarLinks){

			TabUtil.printS("similarLinks: %s", link);
			Document doc3;
			Elements media;
			doc3 = TabUtil.doGet(link);
			media = doc3.select("img");
			for (Element img : media)
			{
				String width = img.attr("width");
				String pic = img.attr("abs:src");
				if(StringUtils.isEmpty(pic)){
					pic = img.attr("abs:data-original");
				}
				if(pic.contains("cover")||pic.endsWith("_s.jpg")
				||StringUtils.isNotBlank(mustInclude) && !pic.contains(mustInclude)
				||(!StringUtils.isEmpty(width) && width.toLowerCase().contains("px") &&
						Integer.parseInt(width.replaceAll("[\\D]+", ""))<300)
				||pic.contains("count.php")
				) continue;
					picList.add(pic);
			}
			Elements links = doc3.select("a[href]");
			for (Element linkPic : links) {
				String lin = linkPic.attr("abs:href");
				if(lin.toLowerCase().contains("jpg")){
					picList.add(lin);
				}
			}
		}
		for(String pic:picList){
			wynik2.add(pic);
		}
		//print(tab2Controller,"%s", StringUtils.join(wynik2,"\n"));
		return index;
	}


	private void reset(){

		wynik2.clear();
		index = 0;

		maxPage = 0;
		includeCount = 0;
		similarLinksPart.clear();
		similarLinks.clear();
		pageLinks.clear();
		txtArea.clear();
		previousKeySet=0;
		beginPageUrl = new HashMap<>();
	}
	@FXML private synchronized void btn2searchClicked(ActionEvent event) throws IOException{
		keyword = txt3.getText();
		if (urls.contains(keyword) || StringUtils.isEmpty(keyword)) {
			//已经点击过
			if(StringUtils.isEmpty(txt3.getText())){
				//获取剪贴板
				if(txt3.isDisable()){
					txt3.setDisable(false);
				}
				txt3.setText(ClipboardUtil.getSysClipboardText());
				return;
			}
			txt3.setText("");
			txt3.requestFocus();
			if(StringUtils.isEmpty(txt5.getText())){
				return;
			}
		} else {
			urls.add(keyword);

		}
		txt3.requestFocus();
		txt3.selectAll();
		System.out.println("Btn 2 search clicked");
		txt3.setDisable(true);
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
				callBack.call(Tab2Controller.this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void  call(Tab2Controller tab2Controller){
		print("%s", StringUtils.join(wynik2,"\n"));
		//txtArea.requestFocus();
        txt3.setDisable(false);
		txtArea.selectAll();
	//	tab2Controller.txt3.requestFocus();
//		txt3.selectAll();
		//txt3.setDisable(false);
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

	@FXML private void btn2saveClicked(ActionEvent event) {
		System.out.println("Btn 2 save clicked");
		int threadCount = 3;
		String threadCountStr = txt6.getText();
		if(StringUtils.isNotBlank(threadCountStr)){
			threadCount = Integer.parseInt(threadCountStr);
		}
		String text =txtArea.getText();
		if(TextUtils.isBlank(text)) return;
		if(!TextUtils.isBlank(keyword)) {
			DownFile.referrer = keyword;
		}
		int count = 10;
		CountDownLatch latch = new CountDownLatch(count);
		final int threadCountFinal = threadCount;
		for(String picUrl:text.split("\n")){
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					if(TextUtils.isBlank(picUrl)) return;
					String[] prefix = getImageName(picUrl).split("\\.");
					String theDir = txt4.getText()+"\\"+ TabUtil.doDomain(picUrl)+"\\"+TabUtil.doMatchPath(picUrl);
					if(prefix.length>1 &&prefix[1].contains("?")
							&&prefix[1].contains("jpg")){
						theDir=theDir+".jpg";
					}
					try {
						File file =new File(theDir);
						file.getParentFile().mkdirs();
						if(file.exists()&& file.length() > 1024){
							return;
						}else {
							file.createNewFile();
						}
						System.out.println(picUrl);
						URL url = new URL(picUrl);
						//new Downloader(picUrl).start();
						new DownFile(url,threadCountFinal,theDir).startDown();
						/*image2 = ImageIO.read(url);
						ImageIO.write(image2, imgFormat, new File(theDir));*/
						}catch(HttpStatusException e) {
							System.out.println(e.getUrl());
							try {
								DownFile.referrer = picUrl;
								new DownFile(new URL(e.getUrl()), 1, theDir).startDown();
							}catch(Exception err) {
								System.out.println(err);
							}
						}catch(IOException e) {
							e.printStackTrace();
						}catch(Exception e) {
							System.out.println(e.toString());
					}
						latch.countDown();
				}
			});

		}
		try {
		latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}