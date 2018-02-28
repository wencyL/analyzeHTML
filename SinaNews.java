package com.comba.wency;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/** 
 * 新浪网新闻抓取与解析
 * @param 
 * @author wency
 */
public class SinaNews {
	public static void main(String args[]){
		Long startTime = System.currentTimeMillis();
		SinaNews.newsList("http://news.sina.cn/gn","guonei");//国内  http://news.sina.cn/gn（新浪网）
		SinaNews.newsList("http://news.sina.cn/gj","guoji");//国际  http://news.sina.cn/gj（新浪网）
		SinaNews.newsList("http://tech.sina.cn/i","hulianwang");//互联网  http://tech.sina.cn/i（新浪网）
		SinaNews.newsList("http://jmqmil.sina.cn/","junshi");//军事  http://jmqmil.sina.cn/（新浪网）
		SinaNews.newsList("http://finance.sina.cn/china/","caijing");//财经    http://finance.sina.cn/china/（新浪网）
		SinaNews.newsList("http://finance.jrj.com.cn/list/fc.shtml?to=pc","fangchan");//房产   http://finance.jrj.com.cn/list/fc.shtml?to=pc（今日房产）
		SinaNews.newsList("http://auto.sina.cn/news/","qiche");//汽车   http://auto.sina.cn/news/（新浪网）
		SinaNews.newsList("http://sports.sina.cn/others","tiyu");//体育   http://sports.sina.cn/others（新浪网）
		SinaNews.newsList("http://ent.sina.cn/zy","yule");//娱乐    http://ent.sina.cn/zy（新浪网）
		SinaNews.newsList("http://games.sina.cn/gnews","youxi");//游戏   http://games.sina.cn/gnews（新浪网）
		SinaNews.newsList("http://edu.sina.cn/eduonline","jiaoyu");//教育    http://edu.sina.cn/eduonline（新浪网）
		SinaNews.newsList("http://eladies.sina.cn/feel","nvren");//女人    http://eladies.sina.cn/feel（新浪网）
		SinaNews.newsList("http://tech.sina.cn/csj","keji");//科技   http://tech.sina.cn/csj（新浪网）	
		SinaNews.newsList("http://news.sina.com.cn/society/","shehui");//社会 http://news.sina.com.cn/society/（新浪网）
		//打成zip包
		FileOutputStream fos;
		try {
			long mills = System.currentTimeMillis();
			fos = new FileOutputStream(new File("f:/analyzeHTML/news_"+mills+".zip"));
			Util.toZip("F:/analyzeHTML/sinaNews/news", fos,true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Long endTime = System.currentTimeMillis();
		Long usedTime = endTime-startTime;
		System.out.println("耗时："+usedTime+"ms");
	}
	public static void newsList(String url,String channelName){
		try{
			Document doc = Jsoup.connect(url).get();
			Elements section_a = doc.select("section>a");
			Elements detail = section_a;//国内、国际、互联网、军事(新浪)
			Elements carditems_a  = doc.select(".carditems>a");//财经(新浪)
			Elements list2_li_a  = doc.select(".list2>li>a");//房产(今日房产)
			Elements small_inner_a  = doc.select(".small>.inner>a");//汽车(新浪)
			Elements m_f_a  = doc.select(".m_f_a");//体育、教育、女人(新浪)
			Elements dl_dd_a  = doc.select("dl>dd>a");//游戏(新浪)
			Elements blk122_a  = doc.select(".blk122>a");//社会(新浪)
			
			if(carditems_a .size()>0){
				detail = carditems_a;
			}
			if(list2_li_a.size()>0){
				detail = list2_li_a;
			}
			if(small_inner_a.size()>0){
				detail = small_inner_a;
			}
			if(m_f_a.size()>0){
				detail = m_f_a;
			}
			if(dl_dd_a.size()>0){
				detail = dl_dd_a;
			}
			if(blk122_a.size()>0){
				detail = blk122_a;
			}
			List<String[]> news =new ArrayList<String[]>();
			File dir = new File("f:/analyzeHTML/sinaNews/news/"+channelName+"/");  //频道目录
			File cssDir = new File("f:/analyzeHTML/sinaNews/news/"+channelName+"/css/"); //css
        	File htmlDir = new File("f:/analyzeHTML/sinaNews/news/"+channelName+"/html/"); //html
        	File imgDir = new File("f:/analyzeHTML/sinaNews/news/"+channelName+"/img/"); //新闻中的图片
	        if (!dir.exists()) {  
	        	dir.mkdirs();   
	        } 
	        if (!cssDir.exists()) {  
	        	cssDir.mkdirs();   
	        }
	        if (!htmlDir.exists()) {  
	        	htmlDir.mkdirs();   
	        }
	        if (!imgDir.exists()) {  
	        	imgDir.mkdirs();   
	        }
			
			for(Element det:detail){
				String[] newsInfo = new String[4];
				//列表页链接,新闻详情
				String htmlPath = null;
				String title = null;
				if(detail == section_a || detail == carditems_a ||detail == small_inner_a || detail == m_f_a ||detail == dl_dd_a ){//国内、国际、互联网、军事、财经、(新浪)
					if(!det.attr("href").equals("")&& det.attr("href")!= null &&det.attr("href").indexOf("photo")<0 &&det.attr("href").indexOf("video")<0 &&det.attr("href").indexOf("download")<0){
						htmlPath = newsDetail(det.attr("href"),channelName);
					}else{
						continue;
					}
					//列表页标题
					
					if(dl_dd_a.size()>0){
						title = det.text();
					}
					Elements h2 = det.select("h2");
					Elements h3 = det.select("h3");
					Elements qiche_title = det.select("img");
					if(qiche_title.size()>0){
						title = qiche_title.get(0).attr("alt");
					}
					if(h2.size()>0){//国内、国际、互联网、军事
						title = h2.get(0).text();
					}
					if(h3.size()>0){//财经
						title = h3.get(0).text();
					}
				}
				if(detail == blk122_a){//社会(新浪)
					String href = det.attr("href").substring(det.attr("href").indexOf("doc-")+4,det.attr("href").length());
					href = "https://news.sina.cn/2018-02-27/detail-"+href;
					htmlPath = newsDetail(href,channelName);
					title = det.text();
				}
				if(detail == list2_li_a){//房产(今日房产)
					String href = det.attr("href").substring(det.attr("href").indexOf("finance.jrj.com.cn/")+19,det.attr("href").length());
					href = "http://m.jrj.com.cn/madapter/finance/"+href;
					htmlPath = newsDetail(href,channelName);
					title = det.text();
				}
				//列表页封面小图
				Long nowTime = System.currentTimeMillis();
	    		List<String> Rcode = Util.genCodes(6, 1);
	    		String imgName = Rcode.get(0)+nowTime.toString()+".jpg";
				if(det.select("img").size()>0){
					Util.downLoadFromUrl(det.select("img").get(0).attr("data-src"), imgName, "f:/analyzeHTML/sinaNews/news/"+channelName+"/img/");
					newsInfo[3] =imgName;
				}else{
					newsInfo[3] =null;
				}
				newsInfo[0] =channelName;
				newsInfo[1] =htmlPath;
				newsInfo[2] =title;
				
				news.add(newsInfo);
			}
			 SaveHtml(news);
		}catch(Exception e){
			System.out.println("新闻列表抓取失败,列表来源："+url);
		}
	}
	public static String  newsDetail(String url,String fileName){
		try{
			Document doc = Jsoup.connect(url).get();
			Elements article = doc.getElementsByTag("article");//国内、国际、互联网、军事、财经正文(新浪)
			String content = null;
			Elements wenzi = article.select(".wenzi");//房产正文(今日房产)
			if(article.size()>0){
				content = article.get(0).toString();
			}
			if(wenzi.size()>0){
				Elements hd = article.select(".hd");//房产标题（今日房产）
				content = hd.get(0).toString()+wenzi.get(0).toString();
			}
			Elements card_box  = doc.select(".card_box");//汽车正文（新浪）
			if(card_box.size()>0){
				content = card_box.get(0).toString();
			}
			Elements c_mainTxtContainer  = doc.select(".c_mainTxtContainer");//体育正文（新浪）
			if(c_mainTxtContainer.size()>0){
				content = c_mainTxtContainer.get(0).toString();
			}
			
			/*Elements wangyi_shehui_title  = doc.select("article>.head");//社会标题（网易）head
			Elements wangyi_shehui_content  = doc.select("article>.content");//社会正文（网易）content
			if(wangyi_shehui_title.size()>0 &&wangyi_shehui_content.size()>0){
				content = wangyi_shehui_title.get(0).toString() + wangyi_shehui_content.get(0).toString();	
			}*/
			Elements body= doc.getElementsByTag("body");//body标签
			body.empty();
			body.append(content);
			String imgPop = "<div id='imgPop' style='min-height:100vh;width:100vw;background-color:#000;"+
					"position:fixed;top:0;left:0;text-align:center;display:none;line-height:100vh'>"+
					"<img src='' alt='' style='max-width:90%;margin:0 auto;'></div>";
			String imgPopScript = "<script>window.onload =  function(){var imgPop = document.getElementById('imgPop');"+
					"imgPop.onclick=function(){imgPop.style.display ='none';};"+
					"var imgs = document.getElementsByTagName('img');var imgs_len = imgs.length;"+
					"for(var i = 0 ; i < imgs_len ; i++){(function(j){imgs[j].onclick=function(){"+
					"imgPop.style.display ='block';imgPop.children[0].setAttribute('src',imgs[j].getAttribute('src'));};})(i)}}</script>";
			body.append(imgPop+imgPopScript);

			Elements header= doc.getElementsByTag("header");//body标签
			Elements css = doc.select("link[href ~= *.css*]");//css
			Elements script = doc.select("script");//js
			Elements media = doc.select("[src]");//media(img)
			Elements aTag = doc.select("a");//a标签
			for (Element link : css) {//获取css并转存
		    	  try{  
		    		  Long nowTime = System.currentTimeMillis();
		    		  List<String> Rcode = Util.genCodes(6, 1);
		    		  String cssName = Rcode.get(0)+nowTime.toString()+".css";
		    		  String cssPath = "f:/analyzeHTML/sinaNews/news/"+fileName+"/css";
		    		  Util.downLoadFromUrl(link.attr("abs:href"),cssName,cssPath);  
		    		  link.attr("href","../css/"+cssName);
		          }catch (Exception e) {  
		        	 System.out.println("---------------获取css或者css转存异常---------------"); 
		          } 
		    }
			
			for (Element js : script) {//删除script
				if(js.attr("src")!= null && !js.attr("src").equals("")){
					js.remove();
					 /*try{  
			    		  Long nowTime = System.currentTimeMillis();
			    		  List<String> Rcode = Util.genCodes(6, 1);
			    		  String jsName = Rcode.get(0)+nowTime.toString()+".js";
			    		  Util.downLoadFromUrl(js.attr("abs:src"),jsName,"f:/analyzeHTML/sinaNews/news/"+fileName+"/js");  
			    		  js.attr("src","../js/"+jsName);
			          }catch (Exception e) {  
			        	 System.out.println("---------------获取或者js转存异常---------------"); 
			          }*/
				}
	        }
			
			for (Element src : media) {
	            if (src.tagName().equals("img")){//如果是图片就转存
	            	try{  
	            		  Long nowTime = System.currentTimeMillis();
			    		  List<String> Rcode = Util.genCodes(6, 1);
			    		  String imgPath = "f:/analyzeHTML/sinaNews/news/"+fileName+"/img";
			    		  String imgName = Rcode.get(0)+nowTime.toString()+".jpg";
			    		  if(src.attr("data-src") != null &&  !src.attr("data-src").equals("")){
			    			  Util.downLoadFromUrl(src.attr("abs:data-src"),imgName,imgPath);
			    		  }else{
				              Util.downLoadFromUrl(src.attr("abs:src"),imgName,imgPath); 
			    		  }  
			              src.attr("src","../img/"+imgName);
			          }catch (Exception e) {  
			        	  System.out.println("---------------获取img或者img转存异常---------------"); 
			          }
	            }else{//如果不是图片（例如js,video,audio,），将src置空(音视频以后可能会保留暂不考虑)
	            	src.attr("src","");
	            }
	        }
			
			
			for (Element a : aTag) {//删除a标签的href属性
				a.removeAttr("href");
	        }
			for (Element h : header) {//删除header
				h.remove();
	        }
			
			return Util.saveDocument(doc,fileName);
		}catch(Exception e){
			System.out.println("新闻详情抓取失败:"+url);
			return null;
		}
	}
	
	/*
	 * *将新闻的相关信息存放到json中。
	*根据新闻类型（channelName）判断文件应该存放的位置。
	*并将json写入.json文件。组装完成后打成zip包
	**/
	public static void SaveHtml(List<String[]> news){
		JSONObject htmlJson =new JSONObject();
		JSONArray contentlist = new JSONArray();
		htmlJson.put("contentlist", contentlist);
		if(news.size()>0){
        	String jsonFilePath ="f:/analyzeHTML/sinaNews/news/"+news.get(0)[0]+"/news.json";
        	File jsonFile = new File(jsonFilePath);
			for(String[] strArr:news){
				JSONObject oneContent =new JSONObject();
				//channelId
				oneContent.put("channelId", "");
				//channelName
				oneContent.put("channelName",strArr[0]);
				//link
				String link = strArr[1].substring(strArr[1].indexOf("/html/"), strArr[1].length());
				oneContent.put("link", link);
				//desc
				oneContent.put("desc", strArr[2]);
				//id
				oneContent.put("id", "");
				//mediaType
				oneContent.put("mediaType", "0");
				//source
				oneContent.put("source", "新浪网");
				//title
				oneContent.put("title", strArr[2]);
				//imageUrls
				JSONArray imageurls = new JSONArray();
				JSONObject oneImg = new JSONObject();
				oneImg.put("url", "/img/"+strArr[3]);
				imageurls.add(oneImg);
				oneContent.put("imageurls", imageurls);
				//发布时间pubDate
				Date curr =new Date();  
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String timeStr = formatter.format(curr);
				oneContent.put("pubDate", timeStr);
				/*if(contentlist.getJSONObject(i).getString("pubDate") ==null){
				    Date curr =new Date();  
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String timeStr = formatter.format(curr);
					oneContent.put("pubDate", timeStr);
				}else{
					oneContent.put("pubDate", contentlist.getJSONObject(i).getString("pubDate"));
				}*/
				contentlist.add(oneContent);
			}
			//保存json
			Util.strToFile(htmlJson.toString(),jsonFilePath);
		}
	}
	
}
