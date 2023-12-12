package com.hanzaitu.ai.util.mail;

import com.hanzaitu.common.domain.EmailConfig;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.mailer.MailerBuilder;

public class MailerBuilderInstance {

    private static volatile Mailer MAILER;

    public static Mailer getInstance(EmailConfig emailConfig){
        if(MAILER == null){
            synchronized (MailerBuilderInstance.class){
                if(MAILER == null){

                    //587
                    MAILER = MailerBuilder
                            .withSMTPServer("smtp.qq.com", 465, emailConfig.getEmail(),
                                    emailConfig.getAuthCode())
                            //协议
                            .withTransportStrategy(TransportStrategy.SMTPS)
                            // .withProxy("socksproxy.host.com", 1080, "proxy user", "proxy password")
                            .withSessionTimeout(10 * 1000)
                            .clearEmailAddressCriteria() // turns off email validation
                //            .withProperty("mail.smtp.sendpartial", true)
                            .withDebugLogging(true)
                            .buildMailer();
                }
            }
        }
        return MAILER;
    }


}
