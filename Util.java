package com.comba.wency;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jsoup.nodes.Document;

public class Util {
	//根据url下载文件
	   public static void  downLoadFromUrl(String urlStr,String fileName,String savePath) { 
		   try{
			   URL url = new URL(urlStr);    
		        HttpURLConnection conn = (HttpURLConnection)url.openConnection(); 
		        conn.setConnectTimeout(3*1000);  
		        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
		        InputStream inputStream = conn.getInputStream();    
		        byte[] getData = readInputStream(inputStream);      
		        File saveDir = new File(savePath);  
		        if(!saveDir.exists()){  
		            saveDir.mkdir();  
		        }  
		        File file = new File(saveDir+File.separator+fileName);      
		        FileOutputStream fos = new FileOutputStream(file);       
		        fos.write(getData);   
		        if(fos!=null){  
		            fos.close();    
		        }  
		        if(inputStream!=null){  
		            inputStream.close();  
		        }  
		        //System.out.println("info:"+url+" download success");
		   }catch(Exception e){
			  System.out.println("文件下载异常:"+urlStr); 
		   }   
	    }  
	  
	   //输入流转byte[] 
	    public static  byte[] readInputStream(InputStream inputStream) throws IOException {    
	        byte[] buffer = new byte[2048];    
	        int len = 0;    
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();    
	        while((len = inputStream.read(buffer)) != -1) {    
	            bos.write(buffer, 0, len);    
	        }    
	        bos.close();    
	        return bos.toByteArray();    
	    }    
	    //document转字符串再写入文件
	    public static  String saveDocument(Document doc,String fName) throws IOException{
	    	Long nowTime = System.currentTimeMillis();
	  		List<String> Rcode = genCodes(6, 1);
	  		String filePath = "f:/analyzeHTML/sinaNews/news/"+fName+"/html/"+Rcode.get(0)+nowTime.toString()+".html";
		     File file=new File(filePath);
		     if(!file.exists()){
		    	 try {
		             FileOutputStream fs2 = new FileOutputStream(file, true); //在该文件的末尾添加内容
		             fs2.write(doc.toString().getBytes(Charset.forName("utf-8")));
		             fs2.flush();   //清空缓存里的数据，并通知底层去进行实际的写操作
		             fs2.close();
		         } catch (FileNotFoundException e) {
		             // TODO Auto-generated catch block
		             System.out.println("document保存异常");
		         }
		     }
		     
		     return	filePath;
	    }
	    
	    //生成随机串
	    public static List<String> genCodes(int length,long num){
	        List<String> results=new ArrayList<String>();
	        for(int j=0;j<num;j++){
	          String val = "";   
	          Random random = new Random();   
	          for(int i = 0; i < length; i++){   
	            String charOrNum =  "char"; // 输出字母还是数字   
	            if("char".equalsIgnoreCase(charOrNum)){// 字符串   
		              int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; //取得大写字母还是小写字母   
		              val += (char) (choice + random.nextInt(26));   
		            }   
	            else if("num".equalsIgnoreCase(charOrNum)){ // 数字   
		              val += String.valueOf(random.nextInt(10));   
		            }   
	          }
	          val=val.toLowerCase();
	          if(results.contains(val)){
		            continue;
		          }else{
		            results.add(val);
		          }
		        }
	        return results;  
	        }  
	    
	  //字符串写入文件
		public static void strToFile(String str,String filePath){
	        FileWriter writer;
	        try {
	            writer = new FileWriter(filePath);
	            writer.write(str);
	            writer.flush();
	            writer.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		} 
		
		
		
		/**
		 * 压缩成ZIP 方法1
		 * @param srcDir 压缩文件夹路径 
		 * @param out    压缩文件输出流
		 * @param KeepDirStructure  是否保留原来的目录结构,true:保留目录结构; 
		 * 							false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
		 * @throws RuntimeException 压缩失败会抛出运行时异常
		 */
		public static void toZip(String srcDir, OutputStream out, boolean KeepDirStructure)
				throws RuntimeException{
			
			long start = System.currentTimeMillis();
			ZipOutputStream zos = null ;
			try {
				zos = new ZipOutputStream(out);
				File sourceFile = new File(srcDir);
				compress(sourceFile,zos,sourceFile.getName(),KeepDirStructure);
				long end = System.currentTimeMillis();
				System.out.println("压缩完成，耗时：" + (end - start) +" ms");
			} catch (Exception e) {
				throw new RuntimeException("zip error from ZipUtils",e);
			}finally{
				if(zos != null){
					try {
						zos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		/**
		 * 递归压缩方法
		 * @param sourceFile 源文件
		 * @param zos		 zip输出流
		 * @param name		 压缩后的名称
		 * @param KeepDirStructure  是否保留原来的目录结构,true:保留目录结构; 
		 * 							false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
		 * @throws Exception
		 */
		private static void compress(File sourceFile, ZipOutputStream zos, String name,
				boolean KeepDirStructure) throws Exception{
			byte[] buf = new byte[2048];
			if(sourceFile.isFile()){
				// 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
				zos.putNextEntry(new ZipEntry(name));
				// copy文件到zip输出流中
				int len;
				FileInputStream in = new FileInputStream(sourceFile);
				while ((len = in.read(buf)) != -1){
					zos.write(buf, 0, len);
				}
				// Complete the entry
				zos.closeEntry();
				in.close();
			} else {
				File[] listFiles = sourceFile.listFiles();
				if(listFiles == null || listFiles.length == 0){
					// 需要保留原来的文件结构时,需要对空文件夹进行处理
					if(KeepDirStructure){
						// 空文件夹的处理
						zos.putNextEntry(new ZipEntry(name + "/"));
						// 没有文件，不需要文件的copy
						zos.closeEntry();
					}
					
				}else {
					for (File file : listFiles) {
						// 判断是否需要保留原来的文件结构
						if (KeepDirStructure) {
							// 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
							// 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
							compress(file, zos, name + "/" + file.getName(),KeepDirStructure);
						} else {
							compress(file, zos, file.getName(),KeepDirStructure);
						}
						
					}
				}
			}
		}
}
