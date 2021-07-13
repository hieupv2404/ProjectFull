package com.nuce.group3.service;

import com.nuce.group3.controller.dto.response.VatDetailResponse;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.util.List;

public interface VatDetailExportService {
    ByteArrayResource exportReport(List<VatDetailResponse> vatDetails) throws IOException;

}
