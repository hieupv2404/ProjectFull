package com.nuce.group3.service;

import com.nuce.group3.controller.dto.response.IssueDetailResponse;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.util.List;

public interface IssueDetailExportService {
    ByteArrayResource exportReport(List<IssueDetailResponse> issueDetailResponses) throws IOException;

}
