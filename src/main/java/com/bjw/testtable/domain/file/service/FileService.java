package com.bjw.testtable.domain.file.service;


import com.bjw.testtable.domain.file.dto.FileDownloadDto;



public interface FileService {

    FileDownloadDto getFile(Long id);
}
