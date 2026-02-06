package tools;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {

    private String contentType = "text/html";
    private String charSet = "UTF-8";
    //final String username = "01086205562a@gmail.com";
    //final String password = "tfseupudeftjgmqp";
    final String username = "balmain_s@naver.com";
    final String password = "";

    public void send(String from, String to, String name, String subjectz, String content) {
        try {
            Properties props = System.getProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            //props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.host", "smtp.naver.com");
            //props.put("mail.smtp.port", "587");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            Session sess = Session.getInstance(props, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            MimeMessage msg = new MimeMessage(sess);

            // 보낸 시간
            msg.setSentDate(new Date());

            // 발신자
            msg.setFrom(new InternetAddress(username, name, "UTF-8"));

            // 수신자
            msg.setRecipients(Message.RecipientType.TO, to);

            // 제목
            msg.setSubject(subjectz, charSet);

            // 내용
            msg.setContent(content, "text/html; charset=UTF-8");

            // 헤더
            msg.setHeader("Content-Type", contentType);

            // 전송
            Transport.send(msg);
        } catch (AddressException ae) {
            System.out.println("[Error : " + ae.getMessage() + "]");
            ae.printStackTrace(System.err);
        } catch (MessagingException me) {
            System.out.println("[Error : " + me.getMessage() + "]");
            me.printStackTrace(System.err);
        } catch (UnsupportedEncodingException ue) {
            System.out.println("[Error : " + ue.getMessage() + "]");
            ue.printStackTrace(System.err);
        }
    }
}
