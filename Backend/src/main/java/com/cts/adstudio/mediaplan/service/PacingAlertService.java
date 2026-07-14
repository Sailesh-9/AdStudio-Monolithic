package com.cts.adstudio.mediaplan.service;

import com.cts.adstudio.mediaplan.dto.response.PacingAlertResponse;
import java.util.List;

public interface PacingAlertService {
    int runPacingCheck(); // the engine — returns # alerts created
    List<PacingAlertResponse> getAlertsByStatus(String status);
    PacingAlertResponse updateAlertStatus(Integer alertId, String status);
}