package com.hanzaitu.admin.config;

import com.google.common.base.Verify;
import com.hanzaitu.common.manager.AsyncManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.TimerTask;

/**
 * @ProjectName LicenseCheckListener
 * @version 1.0.0
 * @Description 在项目启动时安装证书
 */

@Slf4j
@Component
public class LicenseCheckAdminListener implements ApplicationListener<ContextRefreshedEvent> {


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //root application context 没有parent
        ApplicationContext context = event.getApplicationContext().getParent();
//        if(context == null){
//            if (Verify.verifyInstallCertificate() == null){
//                System.out.println("当前项目没有授权哦~~~");
//            }
//            pollLicense();
//        }
    }

    /**
     * 监听证书文件是否存在
     */
    public void pollLicense() {
//        AsyncManager.me().execute(new TimerTask() {
//            @Override
//            public void run() {
//                while (true){
//                    try {
//                        if (!Verify.findLicenseExist() && Verify.verifyLicense != null){
//                            log.debug("授权文件不存在或授权数据异常");
//                            Verify.uninstallLicense();
//                        }
//                    }catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//                        try {
//                            Thread.sleep(3000);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }
//            }
//        });

    }


}
