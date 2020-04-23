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
        properties.put("mail.transport.protocol", "smtp");// ����Э��
        properties.put("mail.smtp.host", "smtp.263.net");// ������
        properties.put("mail.smtp.port", 25);// �˿ں�
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");// �����Ƿ�ʹ��ssl��ȫ���� ---һ�㶼ʹ��
        properties.put("mail.debug", "true");// �����Ƿ���ʾdebug��Ϣ true ���ڿ���̨��ʾ�����Ϣ
        // �õ��ػ�����
        Session session = Session.getInstance(properties);
        // ��ȡ�ʼ�����
        Message message = new MimeMessage(session);
        // ���÷����������ַ
        message.setFrom(new InternetAddress(emailData.from));
        // �����ռ��������ַ 
        InternetAddress[] addresses = new InternetAddress[emailData.tos.length];
        for(int i = 0; i < emailData.tos.length; i++) {
        	addresses[i]=new InternetAddress(emailData.tos[i]);
        }
        message.setRecipients(Message.RecipientType.TO, addresses);
        // �����ʼ�����
        message.setSubject(emailData.title);
        if(emailData.filePath!=null && emailData.filePath.length()>0) {
        	 // ������Ϣ����
            BodyPart messageBodyPart = new MimeBodyPart();

            // ��Ϣ
            messageBodyPart.setText(emailData.content);

            // ����������Ϣ
            Multipart multipart = new MimeMultipart();

            // �����ı���Ϣ����
            multipart.addBodyPart(messageBodyPart);
        	 // ��������
            messageBodyPart = new MimeBodyPart();
            // ����Ҫ���͸������ļ�·��
            DataSource source = new FileDataSource(emailData.filePath);
            messageBodyPart.setDataHandler(new DataHandler(source));

            // messageBodyPart.setFileName(filename);
            // �������������ģ������ļ�·������������
            messageBodyPart.setFileName(MimeUtility.encodeText(emailData.fileName));
            multipart.addBodyPart(messageBodyPart);

            // ����������Ϣ
            message.setContent(multipart);
         // �õ��ʲ����
            Transport transport = session.getTransport();
            // �����Լ��������˻�
            transport.connect("smtp.263.net",emailData.from, emailData.pwd);// ����ΪQQ���俪ͨ��stmp�����õ��Ŀͻ�����Ȩ��
            // �����ʼ�
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        }else {
        	 
             message.setText(emailData.content);
          // �õ��ʲ����
             Transport transport = session.getTransport();
             // �����Լ��������˻�
             transport.connect("smtp.263.net",emailData.from, emailData.pwd);// ����ΪQQ���俪ͨ��stmp�����õ��Ŀͻ�����Ȩ��
             // �����ʼ�
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
        emailData.filePath="D:\\����\\�ܱ�\\2020\\2020-02-03�ܱ�-���ĿƼ�����Ө.xls";
        emailData.fileName="2020-02-03�ܱ�-���ĿƼ�����Ө.xls";
        emailData.title="2020-02-03�ձ�-���ĿƼ�����Ө";
        emailData.content="2020-02-03�ձ�-���ĿƼ�����Ө";
        try {
        	sendEmail(emailData);
        }catch(Exception e) {
        	e.printStackTrace();
        }
    }
}