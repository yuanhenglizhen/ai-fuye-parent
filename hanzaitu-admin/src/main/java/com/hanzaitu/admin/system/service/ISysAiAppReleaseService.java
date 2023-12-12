package com.hanzaitu.admin.system.service;

import com.hanzaitu.admin.system.domain.AiAppRelease;

import java.util.List;

public interface ISysAiAppReleaseService {
    List<AiAppRelease> getReleaseList();

    int saveAiAppRelease(AiAppRelease aiAppRelease);

    int deleteAiAppRelease(Integer ids);
}
