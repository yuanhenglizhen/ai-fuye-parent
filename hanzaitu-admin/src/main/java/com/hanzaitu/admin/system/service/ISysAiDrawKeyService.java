package com.hanzaitu.admin.system.service;

import com.hanzaitu.admin.system.domain.DiscordAuthToken;

import java.util.List;

public interface ISysAiDrawKeyService {
    List<DiscordAuthToken> selectList();

    int save(DiscordAuthToken discordAuthToken);

    int del(Integer id);
}
