package util;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class SendMailUtil {
	public static void sendEmail(EmailData emailData)  throws AddressException,MessagingException, UnsupportedEncodingException {
		Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");// 连接协议
        properties.put("mail.smtp.host", "smtp.263.net");// 主机名
        properties.put("mail.smtp.port", 25);// 端口号
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");// 设置是否使用ssl安全连接 ---一般都使用
        properties.put("mail.debug", "true");// 设置是否显示debug信息 true 会在控制台显示相关信息
        // 得到回话对象
        Session session = Session.getInstance(properties);
        // 获取邮件对象
        Message message = new MimeMessage(session);
        // 设置发件人邮箱地址
        message.setFrom(new InternetAddress(emailData.from));
        // 设置收件人邮箱地址 
        InternetAddress[] addresses = new InternetAddress[emailData.tos.length];
        for(int i = 0; i < emailData.tos.length; i++) {
        	addresses[i]=new InternetAddress(emailData.tos[i]);
        }
        message.setRecipients(Message.RecipientType.TO, addresses);
        // 设置邮件标题
        message.setSubject(emailData.title);
        if(emailData.filePath!=null && emailData.filePath.length()>0) {
        	 // 创建消息部分
            BodyPart messageBodyPart = new MimeBodyPart();

            // 消息
            messageBodyPart.setText(emailData.content);

            // 创建多重消息
            Multipart multipart = new MimeMultipart();

            // 设置文本消息部分
            multipart.addBodyPart(messageBodyPart);
        	 // 附件部分
            messageBodyPart = new MimeBodyPart();
            // 设置要发送附件的文件路径
            DataSource source = new FileDataSource(emailData.filePath);
            messageBodyPart.setDataHandler(new DataHandler(source));

            // messageBodyPart.setFileName(filename);
            // 处理附件名称中文（附带文件路径）乱码问题
            messageBodyPart.setFileName(MimeUtility.encodeText(emailData.fileName));
            multipart.addBodyPart(messageBodyPart);

            // 发送完整消息
            message.setContent(multipart);
         // 得到邮差对象
            Transport transport = session.getTransport();
            // 连接自己的邮箱账户
            transport.connect("smtp.263.net",emailData.from, emailData.pwd);// 密码为QQ邮箱开通的stmp服务后得到的客户端授权码
            // 发送邮件
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        }else {
        	 
             message.setText(emailData.content);
          // 得到邮差对象
             Transport transport = session.getTransport();
             // 连接自己的邮箱账户
             transport.connect("smtp.263.net",emailData.from, emailData.pwd);// 密码为QQ邮箱开通的stmp服务后得到的客户端授权码
             // 发送邮件
             transport.sendMessage(message, message.getAllRecipients());
             transport.close();
        }
        
        
	}
	
	
    public static void main(String[] args){
    	EmailData emailData = new EmailData();
    	emailData.from = "duanyuying@yonxin.com";
    	emailData. pwd = "3344ybfl";
    	String to = "duanyuying@yonxin.com";
        String[] tos = new String[] {to};
        emailData.tos = tos;
        emailData.filePath="D:\\资料\\周报\\2020\\2020-02-03周报-用心科技段钰莹.xls";
        emailData.fileName="2020-02-03周报-用心科技段钰莹.xls";
        emailData.title="2020-02-03日报-用心科技段钰莹";
        emailData.content="2020-02-03日报-用心科技段钰莹";
        try {
        	sendEmail(emailData);
        }catch(Exception e) {
        	e.printStackTrace();
        }
    }
}