package com.bjw.testtable.file.service;


import com.bjw.testtable.domain.file.FileDownloadDto;
import com.bjw.testtable.domain.file.FileEntity;
import com.bjw.testtable.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileServiceImpl implements FileService { //여기서 아이디를 찾아오면 fileEntity랑 db정보 1:1매핑시킴. file 변수 안에 들어가니까 file.하면 db의 다른 컬럼도 접근가능함

    private final FileRepository fileRepository;

    public FileDownloadDto getFile(Long id) {
        FileEntity file = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("파일 없음"));

        Path path = Paths.get(file.getFilepath());
        try {
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("파일을 읽을 수 없음");
            }
            return new FileDownloadDto(resource, file.getOriginalFilename(), file.getContentType());
        } catch (MalformedURLException e) {
            throw new RuntimeException("파일 경로 오류", e);
        }
    }
}
