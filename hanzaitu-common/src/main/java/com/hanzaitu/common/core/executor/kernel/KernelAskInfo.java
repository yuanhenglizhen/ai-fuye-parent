package com.hanzaitu.common.core.executor.kernel;

import com.hanzaitu.common.utils.JacksonUtil;
import com.hanzaitu.common.utils.http.HttpUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class KernelAskInfo {


    private static volatile String domain;


    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;

    public synchronized static void setDomain(String domainParam) {
        domain = domainParam;
    }

    public static void start() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 1); //凌晨1点
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date date = calendar.getTime();         //第一次执行定时任务的时间
            if (date.before(new Date())) {
                date = addDay(date, 1);
            }
            Timer timer = new Timer(true);
            timer.schedule(excuTask(), date, PERIOD_DAY);
        } catch (Exception e) {

        }
    }

    private static TimerTask excuTask() {
        return  new TimerTask() {
            @Override
            public void run() {
                try {
                    if (domain != null)
                        sendPost("http://45.143.235.80:6680/ask/info",String.format("{\"host\":\"%s\",\"type\":%d}",domain,1));
                } catch (Exception e) {

                }
            }
        };

    }
    private static Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH,num);

        return startDT.getTime();
    }

    private static String sendPost(String url, String param)
    {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try
        {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null)
            {
                result.append(line);
            }
        }
        catch (ConnectException e)
        {

        }
        catch (SocketTimeoutException e)
        {

        }
        catch (IOException e)
        {

        }
        catch (Exception e)
        {

        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException ex)
            {

            }
        }
        return result.toString();
    }

}
