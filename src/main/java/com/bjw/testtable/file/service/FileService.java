package com.bjw.testtable.file.service;


import com.bjw.testtable.domain.file.FileDownloadDto;



public interface FileService {

    FileDownloadDto getFile(Long id);
}
