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
git�������ܽ�
0.������ʾ��a.��ͷ���b.���O2O,c.o2o��̨,d.o2o��esb,e.���ķ���������루������������ö��Ÿ�����
1.���㿪ʼ���ڣ���ֹ����
2.��ȡ���и�������X��Ŀ��������־�����ں����ݣ�
3.������Ŀ��־���б���ʽ��ȡ�󣬰�ʱ��������������
4.������ͬ��ɾ���������Ե�һ��Ϊ׼
5.���������
6.����������excel
 */
public class WeekReportMaker {
	//�˴�ע�⣬���ʱһ���㣬����ʱ������
	
//	private static String CONFIG_URL = "../config/config";//����
	private static String CONFIG_URL = "./config/config";//��ʽ
	
	public static void main(String[] args) throws Exception {
		//0.������ʾ��a.��ͷ���b.���O2O,c.o2o��̨,d.o2o��esb,e.���ķ���������루������������ö��Ÿ�����
	
		List<String> dataLines = ReadFile.toArrayArrayByFileReader(CONFIG_URL);
		ConfigData configData = getRoots(dataLines);
		List<String> roots = configData.roots;
		List<String> projects = configData.projects;
		String outPath = configData.outPath;
		String myName = configData.myName;
		System.out.println("��ѡ����Ŀ��");
		for(int i=0; i < projects.size(); i++) {
			System.out.println((char)('a'+i)+"."+projects.get(i));
			
		}
		
		List<String> selectPros = getPros(projects);
		System.out.print("��ѡ��Ŀ��");
		for(String pro:selectPros) {
			System.out.print(pro+" ");
		}
		System.out.println();
		//1.���㿪ʼ���ڣ���ֹ����
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
		System.out.println("��ʼ���ڣ�"+startDate);
		System.out.println("�������ڣ�"+endDate);
//		2.��ȡ���и�������X��Ŀ��������־�����ں����ݣ�
		System.out.println("���ڴ���"+startDate+"��"+endDate+"����־�ļ�....");
		createLogs(roots,selectPros,startDate,endDate,outPath);
		System.out.println("������־�ļ��ɹ���");
//		3.������Ŀ��־���б���ʽ��ȡ�󣬰�ʱ��������������
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try {
			System.out.println("���ڻ�ȡ���ɵ�"+startDate+"��"+endDate+"����־�ļ�����....");
			List<LogMsg> logMsgs = getLogMsgs(selectPros,outPath);
			if(logMsgs==null || logMsgs.size() == 0) {
				System.out.println("��ȡ��־ʧ�ܣ��޵�ǰ�ܵĴ����ύ��¼��");
				return;
			}
			System.out.println("��ȡ��־���ݳɹ���");
			configData.logMsgs = logMsgs;
//			4.������ͬ��ɾ���������Ե�һ��Ϊ׼
//			for(LogMsg logMsg:logMsgs) {
//				System.out.println(sdf.format(logMsg.logTime));
//				System.out.println(logMsg.content);
//			}
//			5.���������
//			6.����������excel
			outputExcel(outPath+"\\"+endDate+"�ܱ�-���ĿƼ�"+myName+".xls",configData);
			//7.�����ʼ�
			sendEmail(getEmailData(outPath,endDate,myName,configData));
			System.exit(0);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	private static EmailData getEmailData(String outPath, String endDate, String myName,ConfigData configData) {
		String fullUrl = outPath+"\\"+endDate+"�ܱ�-���ĿƼ�"+myName+".xls";
		EmailData emailData = new EmailData();
    	emailData.from = configData.from;
    	emailData. pwd = configData.pwd;
        String[] tos = configData.to.split(",");
        emailData.tos = tos;
        emailData.filePath=fullUrl;
        emailData.fileName=endDate+"�ܱ�-���ĿƼ�"+myName+".xls";
        emailData.title=myName;
        emailData.content="����";
	return emailData;
}



	private static void sendEmail(EmailData emailData) {
		System.out.println("�ʼ���Ϣ��");
		System.out.println("���⣺"+emailData.title);
		System.out.println("�����ˣ�"+emailData.from);
		System.out.println("�ռ��ˣ�");
		
		for(String to:emailData.tos) {
			System.out.print(to+" ");
		}System.out.println();
		System.out.println("�ļ���"+emailData.fileName);
		System.out.println("���ݣ�"+emailData.content);
		System.out.println();
		Scanner scan = new Scanner(System.in);
		while(true) {
			System.out.println("�Ƿ�ʼ�����ʼ����������ԭ�ļ�����ر��ٽ��з��ͣ�����Ҫ�����뷢�ͣ�����Ҫ�����벻");
			String str = scan.nextLine();
			if("��".equals(str)) {
				break;
			}else if("����".equals(str)) {
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
            System.out.println("��ʼ����...");
            //���ļ� 
            book = Workbook.createWorkbook(new File(fileName));
            
            //������Ϊ����һҳ���Ĺ���������0��ʾ���ǵ�һҳ  
            WritableSheet sheet = book.createSheet("sheet_one", 0);
            sheet.setColumnView(0, 20); // �����еĿ��
            sheet.setColumnView(1, 104); // �����еĿ��
            sheet.setColumnView(2, 56); // �����еĿ��
            sheet.setColumnView(3, 56); // �����еĿ��
            sheet.setColumnView(4, 56); // �����еĿ��
            sheet.setColumnView(5, 15); // �����еĿ��
            sheet.setColumnView(6, 23); // �����еĿ��
            sheet.setColumnView(7, 17); // �����еĿ��
            sheet.setColumnView(8, 44); // �����еĿ��
            sheet.setRowView(configData.logMsgs.size()+3, 2000);
            sheet.setRowView(configData.logMsgs.size()+5, 4000);
            //����1
            WritableFont wf_title = new WritableFont(WritableFont.createFont("����"), 16,  
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,  
                    jxl.format.Colour.BLACK); // �����ʽ ���� �»��� б�� ���� ��ɫ  
            WritableCellFormat wcf_title = new WritableCellFormat(wf_title); // ��Ԫ����  
            wcf_title.setBackground(jxl.format.Colour.WHITE); // ���õ�Ԫ��ı�����ɫ  
            wcf_title.setAlignment(jxl.format.Alignment.CENTRE); // ���ö��뷽ʽ  
            wcf_title.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //���ñ߿�  
            
            Label titleName = new Label(0,0,"ÿ�ܹ����ƻ��ͽ��ȼ�¼��",wcf_title);
            sheet.addCell(titleName);
            sheet.mergeCells(0, 0, 8, 0);   
            
            //��������23��
            WritableFont wf_title2 = new WritableFont(WritableFont.createFont("����"), 11,  
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,  
                    jxl.format.Colour.BLACK); // �����ʽ ���� �»��� б�� ���� ��ɫ  
            WritableCellFormat wcf_title2 = new WritableCellFormat(wf_title2); // ��Ԫ����  
            wcf_title2.setBackground(jxl.format.Colour.WHITE); // ���õ�Ԫ��ı�����ɫ  
            wcf_title2.setAlignment(jxl.format.Alignment.LEFT); // ���ö��뷽ʽ  
            wcf_title2.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //���ñ߿�  
            Label head2 = new Label(0,1,"������"+configData.myName,wcf_title2);
            sheet.addCell(head2);
            head2 = new Label(1,1,"��λ��"+configData.career,wcf_title2);
            sheet.addCell(head2);
            head2 = new Label(2,1,"��¼���ڣ�"+configData.startDate+"-"+configData.endDate,wcf_title2);
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
            
            //������
            Label head3 = new Label(0,2,"����",wcf_title2);
            sheet.addCell(head3);
            head3 = new Label(1,2,"������������",wcf_title2);
            sheet.addCell(head3);
            head3 = new Label(2,2,"������ʼʱ��",wcf_title2);
            sheet.addCell(head3);
            head3 = new Label(3,2,"�ƻ����ʱ��",wcf_title2);
            sheet.addCell(head3);
            head3 = new Label(4,2,"ʵ�����ʱ��",wcf_title2);
            sheet.addCell(head3);
            head3 = new Label(5,2,"Ŀǰ���ȣ�%��",wcf_title2);
            sheet.addCell(head3);
            head3 = new Label(6,2,"���ڣ��죩",wcf_title2);
            sheet.addCell(head3);
            head3 = new Label(7,2,"��ǰ����ʣ�%��",wcf_title2);
            sheet.addCell(head3);
            head3 = new Label(8,2,"��ע�����",wcf_title2);
            sheet.addCell(head3);
            
            	//���п�ͷ
            WritableFont wf_title3 = new WritableFont(WritableFont.createFont("����"), 12,  
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,  
                    jxl.format.Colour.BLACK); // �����ʽ ���� �»��� б�� ���� ��ɫ  
            WritableCellFormat wcf_title3 = new WritableCellFormat(wf_title3); // ��Ԫ����  
            wcf_title3.setBackground(jxl.format.Colour.WHITE); // ���õ�Ԫ��ı�����ɫ  
            wcf_title3.setAlignment(jxl.format.Alignment.CENTRE); // ���ö��뷽ʽ  
            wcf_title3.setVerticalAlignment(VerticalAlignment.CENTRE);
            wcf_title3.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //���ñ߿�  
            wcf_title3.setWrap(true);
            //��ͨ
            WritableFont wf_title4 = new WritableFont(WritableFont.createFont("����"), 11,  
                    WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,  
                    jxl.format.Colour.BLACK); // �����ʽ ���� �»��� б�� ���� ��ɫ  
            WritableCellFormat wcf_title4 = new WritableCellFormat(wf_title4); // ��Ԫ����  
            wcf_title4.setBackground(jxl.format.Colour.WHITE); // ���õ�Ԫ��ı�����ɫ  
            wcf_title4.setAlignment(jxl.format.Alignment.LEFT); // ���ö��뷽ʽ  
            wcf_title4.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //���ñ߿�  
            wcf_title4.setWrap(true);
            
            Label label3 = new Label(0,3,"���ܹ���������������¼",wcf_title3);
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
            	label3 = new Label(8,i+3,"ȫ�����",wcf_title4);
            	sheet.addCell(label3);
            }
            
            WritableFont wf_title5 = new WritableFont(WritableFont.createFont("����"), 12,  
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,  
                    jxl.format.Colour.BLACK); // �����ʽ ���� �»��� б�� ���� ��ɫ  
            WritableCellFormat wcf_title5 = new WritableCellFormat(wf_title5); // ��Ԫ����  
            wcf_title5.setBackground(jxl.format.Colour.WHITE); // ���õ�Ԫ��ı�����ɫ  
            wcf_title5.setAlignment(jxl.format.Alignment.CENTRE); // ���ö��뷽ʽ  
            wcf_title5.setVerticalAlignment(VerticalAlignment.CENTRE);
            wcf_title5.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //���ñ߿�  
            wcf_title5.setWrap(true);
            wcf_title5.setBackground(Colour.SKY_BLUE);
            Label label4 = new Label(0,configData.logMsgs.size()+3,"���ܹ����ƻ����ż�¼",wcf_title5);
            sheet.addCell(label4);
            for(int i = 1; i <=8; i++) {
            	 label4 = new Label(i,configData.logMsgs.size()+3,"",wcf_title4);
                 sheet.addCell(label4);
            }
            
            for(int i = 1; i <=8; i++) {
            	if(i==1) {
            		 label4 = new Label(i,configData.logMsgs.size()+4,"��������",wcf_title3);
                     sheet.addCell(label4);
            	}else if(i == 4) {
            		 label4 = new Label(i,configData.logMsgs.size()+4,"����취",wcf_title3);
                     sheet.addCell(label4);
            	}else if(i==7) {
            		 label4 = new Label(i,configData.logMsgs.size()+4,"���������",wcf_title3);
                     sheet.addCell(label4);
            	}else {
            		label4 = new Label(i,configData.logMsgs.size()+4,"",wcf_title3);
                    sheet.addCell(label4);
            	}
           	
           }
           
            
            WritableFont wf_title6 = new WritableFont(WritableFont.createFont("����"), 12,  
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,  
                    jxl.format.Colour.BLACK); // �����ʽ ���� �»��� б�� ���� ��ɫ  
            WritableCellFormat wcf_title6 = new WritableCellFormat(wf_title6); // ��Ԫ����  
            wcf_title6.setBackground(jxl.format.Colour.WHITE); // ���õ�Ԫ��ı�����ɫ  
            wcf_title6.setAlignment(jxl.format.Alignment.CENTRE); // ���ö��뷽ʽ  
            wcf_title6.setVerticalAlignment(VerticalAlignment.CENTRE);
            wcf_title6.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //���ñ߿�  
            wcf_title6.setWrap(true);
            wcf_title6.setBackground(Colour.SKY_BLUE);
            wcf_title6.setBackground(Colour.LIGHT_ORANGE);
            Label label5 = new Label(0,configData.logMsgs.size()+4,"�����д��ڵ����⼰�������",wcf_title6);
            sheet.addCell(label5);
            sheet.mergeCells(0,configData.logMsgs.size()+4, 0,configData.logMsgs.size()+5);
            for(int i = 1; i <=8;i++) {
            	label5 = new Label(i,configData.logMsgs.size()+5,"",wcf_title4);
                sheet.addCell(label5);
            }
            
            //д�����ݲ��ر���
            book.write();
            book.close();
            System.out.println("���ɳɹ�������excel·����"+fileName);
            
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
					System.out.println(path+"��·�������ɹ���������ӿ�ִ������");
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
					System.out.println(path+"��·������ʧ��");
				}
			}
		}
		System.out.println("����������"+commands.size());
		excuteCMDBatFile(outPath,commands);
	}

	//fileNameΪȫ·��
	 public static boolean appendWriter(String fileName,String content){

	        FileWriter fw=null;
	        PrintWriter toFile=null;

	        try{
	        	System.out.println("д��·����"+fileName);
	        	System.out.println("��д�����ݣ�"+content);
	            fw=new FileWriter(fileName,true);  //������������FileWriter      //�����׳�IOException�쳣
	            toFile=new PrintWriter(fw);         //��������outStream���ӵ���Ϊf.txt���ļ�  //�����׳�FileNotFoundException�쳣
	            System.out.println("д��ɹ���");
	        } catch (FileNotFoundException e) {
	            //e.printStackTrace();
	            System.out.println("PrintWriter error opening the file:"+fileName);
	           return false;
	        } catch (IOException e) {
	            //e.printStackTrace();
	            System.out.println("FileWriter error opening the file:"+fileName);
	            return false;
	        }
	            toFile.println(content);         //PrintWriter��printlnд�ļ�������System.out.printlnд��Ļ��������

	        toFile.close();      //��ʾ�ر����������������ݶ�ʧ
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
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//�������ڸ�ʽ
		Calendar cld = Calendar.getInstance(Locale.CHINA);
		cld.setFirstDayOfWeek(Calendar.MONDAY);//����һΪ����
		cld.setTimeInMillis(System.currentTimeMillis());//��ǰʱ��

		cld.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);//��һ
		cld.add(Calendar.DATE, -1);// ���ڼ�1
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
				 System.out.println("�������������ѡ��");
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
				System.out.println("��������ȱʧ����ȷ�ϰ���root,direct���ã���������key=value��ʽ");
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
