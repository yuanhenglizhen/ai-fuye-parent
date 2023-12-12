package com.hanzaitu.ai.business.service;

import com.hanzaitu.ai.business.domain.AiAppRelease;
import com.hanzaitu.ai.business.dto.GetVersionDto;

import java.util.List;

public interface AiAppReleaseService {
    List<AiAppRelease> getVersionList(GetVersionDto getVersionDto);
}
