package com.bjw.testtable.service;


import com.bjw.testtable.dto.post.file.FileDownloadDto;
import com.bjw.testtable.entity.FileEntity;
import com.bjw.testtable.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileService {

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
