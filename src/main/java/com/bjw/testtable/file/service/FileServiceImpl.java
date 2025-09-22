package com.bjw.testtable.file.service;


import com.bjw.testtable.domain.file.FileEntity;
import com.bjw.testtable.file.dto.FileDownloadDto;
import com.bjw.testtable.file.repository.FileRepository;
import com.bjw.testtable.file.storage.FileStorageResult;
import com.bjw.testtable.file.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileServiceImpl implements FileService { //여기서 아이디를 찾아오면 fileEntity랑 db정보 1:1매핑시킴. file 변수 안에 들어가니까 file.하면 db의 다른 컬럼도 접근가능함



    private final FileStorageService fileStorageService;
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

    @Override
    @Transactional // DB 작업은 트랜잭션 안에서 수행되어야 합니다.
    public FileEntity uploadFile(MultipartFile file, Long postId, String userId) {
        // 1. 기존 파일 스토리지 서비스를 사용해 파일을 디스크에 저장한다.
        FileStorageResult result = fileStorageService.save(file);

        // 2. 저장된 파일 정보를 바탕으로 FileEntity를 생성한다.
        FileEntity fileEntity = FileEntity.builder()
                .postId(postId)
                .userId(userId)
                .filepath(result.getPath()) // FileStorageService가 반환한 실제 저장 경로
                .contentType(result.getContentType()) // FileStorageService가 반환한 MIME 타입
                .originalFilename(file.getOriginalFilename())
                .size(file.getSize())
                .build();

        // 3. 파일 메타데이터를 데이터베이스에 저장한다.
        return fileRepository.save(fileEntity);
    }
}
