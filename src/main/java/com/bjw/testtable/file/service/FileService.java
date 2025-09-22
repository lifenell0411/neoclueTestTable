package com.bjw.testtable.file.service;


import com.bjw.testtable.domain.file.FileEntity;
import com.bjw.testtable.file.dto.FileDownloadDto;
import org.springframework.web.multipart.MultipartFile;


public interface FileService {

    FileDownloadDto getFile(Long id);

    FileEntity uploadFile(MultipartFile file, Long postId, String userId);
}
