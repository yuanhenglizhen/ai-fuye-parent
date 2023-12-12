package com.hanzaitu.ai.draw.support;

import com.hanzaitu.common.utils.JacksonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;


/*@Slf4j
@Component
@RequiredArgsConstructor*/
public class ReadyListener implements EventListener {


    //@Override
    public void onEvent(@NotNull GenericEvent event)
    {
        //log.debug(String.valueOf(event.getRawData()));
        if (event instanceof ReadyEvent)
            System.out.println("API is ready!");
    }

}
