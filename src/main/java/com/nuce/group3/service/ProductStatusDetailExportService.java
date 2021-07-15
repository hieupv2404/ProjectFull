package com.nuce.group3.service;

import com.nuce.group3.controller.dto.response.ProductStatusDetailResponse;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.util.List;

public interface ProductStatusDetailExportService {
    ByteArrayResource exportReport(List<ProductStatusDetailResponse> vatDetails) throws IOException;

}
