import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import util.EmailData;
import util.ReadFile;
import util.SendMailUtil;

/**
git生成周总结
0.给出提示：a.万和服务，b.万和O2O,c.o2o中台,d.o2o的esb,e.用心服务，输入编码（可连续多个，用逗号隔开）
1.计算开始日期，截止日期
2.获取所有该日期内X项目的所有日志（日期和内容）
3.所有项目日志以列表形式获取后，按时间内容排序，正序
4.内容相同的删掉，日期以第一次为准
5.初步：输出
6.进化：生成excel
 */
public class WeekReportMaker {
	//此处注意，打包时一个点，调试时两个点
	
//	private static String CONFIG_URL = "../config/config";//测试
	private static String CONFIG_URL = "./config/config";//正式
	
	public static void main(String[] args) throws Exception {
		//0.给出提示：a.万和服务，b.万和O2O,c.o2o中台,d.o2o的esb,e.用心服务，输入编码（可连续多个，用逗号隔开）
	
		List<String> dataLines = ReadFile.toArrayArrayByFileReader(CONFIG_URL);
		ConfigData configData = getRoots(dataLines);
		List<String> roots = configData.roots;
		List<String> projects = configData.projects;
		String outPath = configData.outPath;
		String myName = configData.myName;
		System.out.println("请选择项目：");
		for(int i=0; i < projects.size(); i++) {
			System.out.println((char)('a'+i)+"."+projects.get(i));
			
		}
		
		List<String> selectPros = getPros(projects);
		System.out.print("已选项目：");
		for(String pro:selectPros) {
			System.out.print(pro+" ");
		}
		System.out.println();
		//1.计算开始日期，截止日期
		String startDate="",endDate="";
		
		try {
			if(configData.start!=null && configData.start.length()>0) {
				startDate = configData.start;
			}else {
				startDate = getStartDate();
			}
			if(configData.start!=null && configData.start.length()>0) {
				endDate = configData.end;
			}else {
				endDate = getEndDate();
			}
			
			 configData.startDate = startDate.replaceAll("-", "/");
			 configData.endDate = endDate.replaceAll("-", "/");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("开始日期："+startDate);
		System.out.println("结束日期："+endDate);
//		2.获取所有该日期内X项目的所有日志（日期和内容）
		System.out.println("正在创建"+startDate+"到"+endDate+"的日志文件....");
		createLogs(roots,selectPros,startDate,endDate,outPath);
		System.out.println("创建日志文件成功！");
//		3.所有项目日志以列表形式获取后，按时间内容排序，正序
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try {
			System.out.println("正在获取生成的"+startDate+"到"+endDate+"的日志文件内容....");
			List<LogMsg> logMsgs = getLogMsgs(selectPros,outPath);
			if(logMsgs==null || logMsgs.size() == 0) {
				System.out.println("获取日志失败，无当前周的代码提交记录！");
				return;
			}
			System.out.println("获取日志内容成功！");
			configData.logMsgs = logMsgs;
//			4.内容相同的删掉，日期以第一次为准
//			for(LogMsg logMsg:logMsgs) {
//				System.out.println(sdf.format(logMsg.logTime));
//				System.out.println(logMsg.content);
//			}
//			5.初步：输出
//			6.进化：生成excel
			outputExcel(outPath+"\\"+endDate+"周报-用心科技"+myName+".xls",configData);
			//7.发送邮件
			sendEmail(getEmailData(outPath,endDate,myName,configData));
			System.exit(0);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	private static EmailData getEmailData(String outPath, String endDate, String myName,ConfigData configData) {
		String fullUrl = outPath+"\\"+endDate+"周报-用心科技"+myName+".xls";
		EmailData emailData = new EmailData();
    	emailData.from = configData.from;
    	emailData. pwd = configData.pwd;
        String[] tos = configData.to.split(",");
        emailData.tos = tos;
        emailData.filePath=fullUrl;
        emailData.fileName=endDate+"周报-用心科技"+myName+".xls";
        emailData.title=myName;
        emailData.content="用心";
	return emailData;
}



	private static void sendEmail(EmailData emailData) {
		System.out.println("邮件信息：");
		System.out.println("标题："+emailData.title);
		System.out.println("发件人："+emailData.from);
		System.out.println("收件人：");
		
		for(String to:emailData.tos) {
			System.out.print(to+" ");
		}System.out.println();
		System.out.println("文件："+emailData.fileName);
		System.out.println("内容："+emailData.content);
		System.out.println();
		Scanner scan = new Scanner(System.in);
		while(true) {
			System.out.println("是否开始发送邮件？（处理好原文件后请关闭再进行发送）如需要请输入发送，不需要请输入不");
			String str = scan.nextLine();
			if("不".equals(str)) {
				break;
			}else if("发送".equals(str)) {
				confirmEmail(emailData);
				break;
			}
		}
	}



	private static void confirmEmail(EmailData emailData) {
		try {
        	SendMailUtil.sendEmail(emailData);
        }catch(Exception e) {
        	e.printStackTrace();
        }
	}



	private static void outputExcel(String fileName,ConfigData configData) {
	
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		WritableWorkbook book;
        try {
        	File f = new File(fileName);
        	if(f.exists()) f.delete();
            System.out.println("开始生成...");
            //打开文件 
            book = Workbook.createWorkbook(new File(fileName));
            
            //生成名为“第一页”的工作表，参数0表示这是第一页  
            WritableSheet sheet = book.createSheet("sheet_one", 0);
            sheet.setColumnView(0, 20); // 设置列的宽度
            sheet.setColumnView(1, 104); // 设置列的宽度
            sheet.setColumnView(2, 56); // 设置列的宽度
            sheet.setColumnView(3, 56); // 设置列的宽度
            sheet.setColumnView(4, 56); // 设置列的宽度
            sheet.setColumnView(5, 15); // 设置列的宽度
            sheet.setColumnView(6, 23); // 设置列的宽度
            sheet.setColumnView(7, 17); // 设置列的宽度
            sheet.setColumnView(8, 44); // 设置列的宽度
            sheet.setRowView(configData.logMsgs.size()+3, 2000);
            sheet.setRowView(configData.logMsgs.size()+5, 4000);
            //标题1
            WritableFont wf_title = new WritableFont(WritableFont.createFont("宋体"), 16,  
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,  
                    jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色  
            WritableCellFormat wcf_title = new WritableCellFormat(wf_title); // 单元格定义  
            wcf_title.setBackground(jxl.format.Colour.WHITE); // 设置单元格的背景颜色  
            wcf_title.setAlignment(jxl.format.Alignment.CENTRE); // 设置对齐方式  
            wcf_title.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //设置边框  
            
            Label titleName = new Label(0,0,"每周工作计划和进度记录表",wcf_title);
            sheet.addCell(titleName);
            sheet.mergeCells(0, 0, 8, 0);   
            
            //标题其他23行
            WritableFont wf_title2 = new WritableFont(WritableFont.createFont("宋体"), 11,  
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,  
                    jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色  
            WritableCellFormat wcf_title2 = new WritableCellFormat(wf_title2); // 单元格定义  
            wcf_title2.setBackground(jxl.format.Colour.WHITE); // 设置单元格的背景颜色  
            wcf_title2.setAlignment(jxl.format.Alignment.LEFT); // 设置对齐方式  
            wcf_title2.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //设置边框  
            Label head2 = new Label(0,1,"姓名："+configData.myName,wcf_title2);
            sheet.addCell(head2);
            head2 = new Label(1,1,"岗位："+configData.career,wcf_title2);
            sheet.addCell(head2);
            head2 = new Label(2,1,"记录周期："+configData.startDate+"-"+configData.endDate,wcf_title2);
            sheet.addCell(head2);
            sheet.mergeCells(2, 1, 4, 1);   
            head2 = new Label(5,1,"",wcf_title2);
            sheet.addCell(head2);
            head2 = new Label(6,1,"",wcf_title2);
            sheet.addCell(head2);
            head2 = new Label(7,1,"",wcf_title2);
            sheet.addCell(head2);
            head2 = new Label(8,1,"",wcf_title2);
            sheet.addCell(head2);
            
            //第三行
            Label head3 = new Label(0,2,"类型",wcf_title2);
            sheet.addCell(head3);
            head3 = new Label(1,2,"工作任务名称",wcf_title2);
            sheet.addCell(head3);
            head3 = new Label(2,2,"工作开始时间",wcf_title2);
            sheet.addCell(head3);
            head3 = new Label(3,2,"计划完成时间",wcf_title2);
            sheet.addCell(head3);
            head3 = new Label(4,2,"实际完成时间",wcf_title2);
            sheet.addCell(head3);
            head3 = new Label(5,2,"目前进度（%）",wcf_title2);
            sheet.addCell(head3);
            head3 = new Label(6,2,"工期（天）",wcf_title2);
            sheet.addCell(head3);
            head3 = new Label(7,2,"提前完成率（%）",wcf_title2);
            sheet.addCell(head3);
            head3 = new Label(8,2,"备注（必填）",wcf_title2);
            sheet.addCell(head3);
            
            	//各行开头
            WritableFont wf_title3 = new WritableFont(WritableFont.createFont("宋体"), 12,  
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,  
                    jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色  
            WritableCellFormat wcf_title3 = new WritableCellFormat(wf_title3); // 单元格定义  
            wcf_title3.setBackground(jxl.format.Colour.WHITE); // 设置单元格的背景颜色  
            wcf_title3.setAlignment(jxl.format.Alignment.CENTRE); // 设置对齐方式  
            wcf_title3.setVerticalAlignment(VerticalAlignment.CENTRE);
            wcf_title3.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //设置边框  
            wcf_title3.setWrap(true);
            //普通
            WritableFont wf_title4 = new WritableFont(WritableFont.createFont("宋体"), 11,  
                    WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,  
                    jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色  
            WritableCellFormat wcf_title4 = new WritableCellFormat(wf_title4); // 单元格定义  
            wcf_title4.setBackground(jxl.format.Colour.WHITE); // 设置单元格的背景颜色  
            wcf_title4.setAlignment(jxl.format.Alignment.LEFT); // 设置对齐方式  
            wcf_title4.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //设置边框  
            wcf_title4.setWrap(true);
            
            Label label3 = new Label(0,3,"本周工作任务完成情况记录",wcf_title3);
            sheet.addCell(label3);
            sheet.mergeCells(0,3, 0, configData.logMsgs.size()+2);   
            
            for(int i = 0; i < configData.logMsgs.size(); i++) {
            	LogMsg logMsg = configData.logMsgs.get(i);
            	String proName = configData.projectNames.get(configData.projects.indexOf(logMsg.proName));
            	label3 = new Label(1,i+3,proName+":"+logMsg.content,wcf_title4);
            	sheet.addCell(label3);
            	label3 = new Label(2,i+3,sdf.format(logMsg.logTime).replaceAll("-", "/").substring(0,10)+" 8:30:00",wcf_title4);
            	sheet.addCell(label3);
            	label3 = new Label(3,i+3,sdf.format(logMsg.logTime).replaceAll("-", "/").substring(0,10)+" 18:00:00",wcf_title4);
            	sheet.addCell(label3);
            	label3 = new Label(4,i+3,sdf.format(logMsg.logTime).replaceAll("-", "/").substring(0,10)+" 18:00:00",wcf_title4);
            	sheet.addCell(label3);
            	label3 = new Label(5,i+3,"100%",wcf_title4);
            	sheet.addCell(label3);
            	label3 = new Label(6,i+3,"0.4",wcf_title4);
            	sheet.addCell(label3);
            	label3 = new Label(7,i+3,"0%",wcf_title4);
            	sheet.addCell(label3);
            	label3 = new Label(8,i+3,"全部完成",wcf_title4);
            	sheet.addCell(label3);
            }
            
            WritableFont wf_title5 = new WritableFont(WritableFont.createFont("宋体"), 12,  
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,  
                    jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色  
            WritableCellFormat wcf_title5 = new WritableCellFormat(wf_title5); // 单元格定义  
            wcf_title5.setBackground(jxl.format.Colour.WHITE); // 设置单元格的背景颜色  
            wcf_title5.setAlignment(jxl.format.Alignment.CENTRE); // 设置对齐方式  
            wcf_title5.setVerticalAlignment(VerticalAlignment.CENTRE);
            wcf_title5.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //设置边框  
            wcf_title5.setWrap(true);
            wcf_title5.setBackground(Colour.SKY_BLUE);
            Label label4 = new Label(0,configData.logMsgs.size()+3,"下周工作计划安排记录",wcf_title5);
            sheet.addCell(label4);
            for(int i = 1; i <=8; i++) {
            	 label4 = new Label(i,configData.logMsgs.size()+3,"",wcf_title4);
                 sheet.addCell(label4);
            }
            
            for(int i = 1; i <=8; i++) {
            	if(i==1) {
            		 label4 = new Label(i,configData.logMsgs.size()+4,"问题描述",wcf_title3);
                     sheet.addCell(label4);
            	}else if(i == 4) {
            		 label4 = new Label(i,configData.logMsgs.size()+4,"解决办法",wcf_title3);
                     sheet.addCell(label4);
            	}else if(i==7) {
            		 label4 = new Label(i,configData.logMsgs.size()+4,"问题解决结果",wcf_title3);
                     sheet.addCell(label4);
            	}else {
            		label4 = new Label(i,configData.logMsgs.size()+4,"",wcf_title3);
                    sheet.addCell(label4);
            	}
           	
           }
           
            
            WritableFont wf_title6 = new WritableFont(WritableFont.createFont("宋体"), 12,  
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,  
                    jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色  
            WritableCellFormat wcf_title6 = new WritableCellFormat(wf_title6); // 单元格定义  
            wcf_title6.setBackground(jxl.format.Colour.WHITE); // 设置单元格的背景颜色  
            wcf_title6.setAlignment(jxl.format.Alignment.CENTRE); // 设置对齐方式  
            wcf_title6.setVerticalAlignment(VerticalAlignment.CENTRE);
            wcf_title6.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //设置边框  
            wcf_title6.setWrap(true);
            wcf_title6.setBackground(Colour.SKY_BLUE);
            wcf_title6.setBackground(Colour.LIGHT_ORANGE);
            Label label5 = new Label(0,configData.logMsgs.size()+4,"工作中存在的问题及解决方法",wcf_title6);
            sheet.addCell(label5);
            sheet.mergeCells(0,configData.logMsgs.size()+4, 0,configData.logMsgs.size()+5);
            for(int i = 1; i <=8;i++) {
            	label5 = new Label(i,configData.logMsgs.size()+5,"",wcf_title4);
                sheet.addCell(label5);
            }
            
            //写入数据并关闭文
            book.write();
            book.close();
            System.out.println("生成成功！生成excel路径："+fileName);
            
        } catch (IOException e) {
            System.out.println(e);            
        } catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static List<LogMsg> getLogMsgs(List<String> selectPros, String outPath) throws ParseException {
		List<LogMsg> logMsgs = new ArrayList<LogMsg>();
		LogMsg logMsg = null;
		for(String pro:selectPros) {
			String path = outPath+"\\"+pro+".txt";
			List<String> dataLines = ReadFile.toArrayArrayByFileReader(path);
			for(String data:dataLines) {
				data = data.replaceAll("\\*", "");
				data = data.replaceAll("\\|", "");
				if(data.replaceAll(" ","").startsWith("commit")) {
					if(logMsg!=null && logMsg.logTime!=null) {
						logMsgs.add(logMsg);
					} 
						
					logMsg = new LogMsg();
					logMsg.proName=pro;
					logMsg.commitVersion = data.substring(data.lastIndexOf(" ")).trim();
				}else if(data.replaceAll(" ","").startsWith("Merge")) {
					String[] test = data.split(": ");
					if(test.length < 2)
						continue;
					logMsg.Merge = test[1].trim();
				}else if(data.replaceAll(" ","").startsWith("Author")) {
					logMsg.author = data.split(": ")[1].trim();
				}else if(data.replaceAll(" ","").startsWith("Date")) {
					String dateStr = data.split(": ")[1].trim();
					dateStr = dateStr.substring(0,dateStr.length()-" +0800".length());
					logMsg.logTime  = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US).parse(dateStr);
				}else if(data.trim().length()>0) {
					if(!data.trim().startsWith("Merge branch") && 
							!data.trim().startsWith("See merge request") ) {
						logMsg.content = data.trim();
					}
				}
			}
		}
		if(logMsg!=null) {
			logMsgs.add(logMsg);
			Collections.sort(logMsgs);
		}
		
		return logMsgs;
	}

	private static void createLogs(List<String> roots, List<String> selectPros, 
			String startDate, String endDate, String outPath) {
		List<String> commands = new ArrayList<String>();
		for(String pro:selectPros) {
			for(String root:roots) {
				String path = root+"\\"+pro;
				File f = new File (path);
				if(f.exists() && f.isDirectory()) {
					System.out.println(path+"，路径检索成功，正在添加可执行命令");
					String name = path.split(":")[0];
					commands.add(name+":");
					commands.add("cd "+path);
					commands.add("for /F %%i in ('git config --get user.name') do ( set name=%%i)");
					File beforeFile = new File(outPath+"\\"+pro+".txt");
					if(beforeFile.exists())
						beforeFile.delete();
					commands.add("git log --graph --all --author=%name%"
							+ " --since ="+startDate+" --until="+endDate+" > "+outPath+"\\"+pro+".txt");
					break;
				}else {
					System.out.println(path+"，路径检索失败");
				}
			}
		}
		System.out.println("总命令数："+commands.size());
		excuteCMDBatFile(outPath,commands);
	}

	//fileName为全路径
	 public static boolean appendWriter(String fileName,String content){

	        FileWriter fw=null;
	        PrintWriter toFile=null;

	        try{
	        	System.out.println("写入路径："+fileName);
	        	System.out.println("待写入内容："+content);
	            fw=new FileWriter(fileName,true);  //本代码中增加FileWriter      //可以抛出IOException异常
	            toFile=new PrintWriter(fw);         //将数据流outStream连接到名为f.txt的文件  //可以抛出FileNotFoundException异常
	            System.out.println("写入成功！");
	        } catch (FileNotFoundException e) {
	            //e.printStackTrace();
	            System.out.println("PrintWriter error opening the file:"+fileName);
	           return false;
	        } catch (IOException e) {
	            //e.printStackTrace();
	            System.out.println("FileWriter error opening the file:"+fileName);
	            return false;
	        }
	            toFile.println(content);         //PrintWriter的println写文件方法与System.out.println写屏幕方法类似

	        toFile.close();      //显示关闭数据流，避免数据丢失
	        return true;
	    }
	
	public static boolean excuteCMDBatFile(String path,List<String> cmd) {
        final String METHOD_NAME = "excuteCMDBatFile#";
        boolean result = true;
        Process p;
        path = path+"\\cmd.bat";
        File batFile = new File(path);
        if(batFile.exists())
        	batFile.delete();
        try {
			batFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        for(String cmdLine:cmd) {
        	boolean isSuccess = appendWriter(path, cmdLine);
            if(!isSuccess) {
                return false;
            }
        }
        
        
        try {
            p = Runtime.getRuntime().exec(path);
            InputStream fis = p.getErrorStream();//p.getInputStream();
            InputStreamReader isr = new InputStreamReader(fis, System.getProperty("file.encoding"));
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            
            p.waitFor();
            int i = p.exitValue();
            if (i != 0) {
                result = false;
                System.out.println(METHOD_NAME + "excute cmd failed, [result = " + result + ", error message = " + builder.toString() + "]");
            }else {
                // logger.debug(METHOD_NAME + "excute cmd result = " + result);
//                System.out.println(METHOD_NAME + "result = " + result);
            }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }

        return result;
    }
	
	private static String getStartDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		Calendar cld = Calendar.getInstance(Locale.CHINA);
		cld.setFirstDayOfWeek(Calendar.MONDAY);//以周一为首日
		cld.setTimeInMillis(System.currentTimeMillis());//当前时间

		cld.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);//周一
		cld.add(Calendar.DATE, -1);// 日期减1
		return df.format(cld.getTime());
	}
	
	private static String getEndDate() throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return df.format(date);
	}
	
	private static List<String> getPros(List<String> projects) {
		List<String> selects = new ArrayList<String>();
		 Scanner scan = new Scanner(System.in);
		 String selectPros = scan.next();
		 while(true) {
			 try {
				 char[] pros = selectPros.toCharArray();
				 for(char pro:pros) {
					 selects.add(projects.get(pro-'a'));
				 }
				 break;
			 }catch(Exception e) {
				 System.out.println("输入错误，请重新选择：");
				selectPros = scan.next();
			 }
		 }
		 
		
		return selects;
	}
	
	private static ConfigData getRoots(List<String> dataLines){
		ConfigData configData = new ConfigData();
		List<String> roots = new ArrayList<String>();
		List<String> projects = new ArrayList<String>();
		List<String> projectNames = new ArrayList<String>();
		String outPath = "";
		String myName = "";
		String career = "";
		String start = "";
		String end = "";
		String from = "";
		String to = "";
		String pwd = "";
		for(String dataLine:dataLines) {
			try {
				String[] datas = dataLine.split("=");
				String key = datas[0];
				String value = datas[1];
				String[] arrRoots = value.split(",");
				if("root".equals(key)) {
					for(String arrRoot:arrRoots) {
						roots.add(arrRoot);
					}
				}else if("direct".equals(key)) {
					for(String arrRoot:arrRoots) {
						projects.add(arrRoot);
					}
				}else if("names".equals(key)) {
					for(String arrRoot:arrRoots) {
						projectNames.add(arrRoot);
					}
				}else if("out".equals(key)) {
					outPath = value;
				}else if("myname".equals(key)) {
					myName = value;
				}else if("career".equals(key)) {
					career = value;
				}else if("start".equals(key)) {
					start = value;
				}else if("end".equals(key)) {
					end = value;
				}else if("from".equals(key)) {
					from = value;
				}else if("to".equals(key)) {
					to = value;
				}else if("pwd".equals(key)) {
					pwd = value;
				}
				
			}catch(Exception e) {
				System.out.println("配置数据缺失，请确认包括root,direct配置，并且满足key=value格式");
			}
		}
		configData.roots = roots;
		configData.projects = projects;
		configData.projectNames = projectNames;
		configData.outPath = outPath;
		configData.myName = myName;
		configData.career = career;
		configData.start = start;
		configData.end = end;
		configData.from = from;
		configData.to = to;
		configData.pwd = pwd;
		return configData;
	}
	
}
