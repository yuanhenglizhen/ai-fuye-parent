package com.hanzaitu.ai.util;
import javax.net.ssl.SSLSocketFactory;
import cn.hutool.core.util.ObjUtil;
import com.hanzaitu.common.core.result.ResultException;
import com.hanzaitu.common.domain.EmailConfig;
import com.hanzaitu.common.utils.file.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;*/
import java.util.Objects;
import java.util.Properties;

/*public class MailUtils {

    private static final Logger log = LoggerFactory.getLogger(MailUtils.class);

    private static final String USER = "m18571141152@163.com"; // 发件人称号，同邮箱地址※ 841590004@qq.com
    private static final String PASSWORD = "VETQZIWLIKEDMKCN"; // 授权码，开启SMTP时显示※ ivmvlkiuzufzbcgd

    *//**
     *
     * @param to 收件人邮箱
     * @param text 邮件正文
     * @param title 标题
     *//*
    *//* 发送验证信息的邮件 *//*
    public static boolean sendMail(String to, String text, String title, EmailConfig emailConfig){
        try {
            if (ObjUtil.isEmpty(emailConfig)) {
                throw ResultException.createResultException("邮箱未配置");
            }
            final Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
//            注意发送邮件的方法中，发送给谁的，发送给对应的app，※
//            要改成对应的app。扣扣的改成qq的，网易的要改成网易的。※
            if (emailConfig.getType().equals(1)) {
                props.put("mail.smtp.host", "smtp.qq.com");
            } else if (emailConfig.getType().equals(2)) {
                props.put("mail.smtp.host", "smtp.163.com");
            } else if (emailConfig.getType().equals(3)) {
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.starttls.enable", "true");         //TLS
            }
            // 发件人的账号
            props.put("mail.user", emailConfig.getEmail());

            //发件人的密码
            props.put("mail.password", emailConfig.getAuthCode());

            props.put("mail.smtp.connectiontimeout", "10000");// 设置接收超时时间

            props.put("mail.smtp.timeout", "10000");// 设置读取超时时间

            props.put("mail.smtp.writetimeout", "10000");// 设置写入超时时间
            // 构建授权信息，用于进行SMTP进行身份验证
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    // 用户名、密码
                    String userName = props.getProperty("mail.user");
                    String password = props.getProperty("mail.password");
                    return new PasswordAuthentication(userName, password);
                }
            };
            // 使用环境属性和授权信息，创建邮件会话
            Session mailSession = Session.getDefaultInstance(props, authenticator);

            // 创建邮件消息
            MimeMessage message = new MimeMessage(mailSession);

            // 设置发件人
            String username = props.getProperty("mail.user");
            InternetAddress form = new InternetAddress(username);
            message.setFrom(form);

            // 设置收件人
            InternetAddress toAddress = new InternetAddress(to);
            message.setRecipient(Message.RecipientType.TO, toAddress);

            // 设置邮件标题
            message.setSubject(title);

            // 设置邮件的内容体
            message.setContent(text, "text/html;charset=UTF-8");
            // 发送邮件

            log.debug("开始发送邮件：{} -> {}",emailConfig.getEmail(),to);
            Transport.send(message);
            log.debug("邮件发送完毕：{} -> {}",emailConfig.getEmail(),to);

            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean sendOutlookEmail(String receiver,String text, String title, EmailConfig emailConfig)
    {

        try{

            Properties props = new Properties();
            // 开启debug调试
            props.setProperty("mail.debug", "true");  //false
            // 发送服务器需要身份验证
            props.setProperty("mail.smtp.auth", "true");
            // 设置邮件服务器主机名
            props.setProperty("mail.host", "smtp.office365.com");
            // 发送邮件协议名称 这里使用的是smtp协议
            props.setProperty("mail.transport.protocol", "smtp");
            // 服务端口号
            props.setProperty("mail.smtp.port", "587");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.connectiontimeout", "10000");// 设置接收超时时间

            props.put("mail.smtp.timeout", "10000");// 设置读取超时时间

            props.put("mail.smtp.writetimeout", "10000");// 设置写入超时时间
            // 设置环境信息
            Session session = Session.getInstance(props);

            // 创建邮件对象
            MimeMessage msg = new MimeMessage(session);

            // 设置发件人
            msg.setFrom(new InternetAddress(emailConfig.getEmail()));

            // 设置收件人
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));

            // 设置邮件主题
            msg.setSubject(title);

            // 设置邮件内容
            Multipart multipart = new MimeMultipart();

            MimeBodyPart textPart = new MimeBodyPart();
            //发送邮件的文本内容
            textPart.setText(text);
            multipart.addBodyPart(textPart);

            msg.setContent(multipart);

            Transport transport = session.getTransport();
            // 连接邮件服务器
            transport.connect(emailConfig.getEmail(), emailConfig.getAuthCode());


            log.debug("开始发送邮件：{} -> {}",emailConfig.getEmail(),receiver);
            // 发送邮件
            transport.sendMessage(msg, new Address[]{new InternetAddress(receiver)});

            log.debug("邮件发送完毕：{} -> {}",emailConfig.getEmail(),receiver);
            // 关闭连接
            transport.close();

            return true;
        }catch( Exception e ){
            e.printStackTrace();
            return false;
        }
    }
    public static void main(String[] args) throws Exception { // 做测试用
//        String sender = "banchishenkan@outlook.com";
//        String password = "Chenrui19921113"; //填写你的outlook帐户的密码
//
//        // 收件人邮箱地址
//        String receiver = "841590004@qq.com";
//
//        // office365 邮箱服务器地址及端口号
//        //这个就是之前的Server Name，注意：你使用的Outlook应用可能使用了不同的服务器，根据自己刚才拿到的地址为准
//        String host = "smtp.office365.com";
//        String port = "587";    //这个就是拿到的port
//        boolean b = sendOutlookEmail(sender, password, host, port, receiver);
//        if(b)
//        {
//            System.out.println("发送成功");
//        }else
//        {
//            System.out.println("发送失败");
//        }
    }


    *//*
     * gmail邮箱SSL方式
     *//*
    private static void gmailssl(Properties props) {
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        props.put("mail.debug", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.auth", "true");
    }


    //gmail邮箱的TLS方式
    private static void gmailtls(Properties props) {
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
    }

}*/
