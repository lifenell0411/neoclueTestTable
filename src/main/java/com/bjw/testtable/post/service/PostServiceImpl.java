package com.bjw.testtable.post.service;

import com.bjw.testtable.domain.file.FileEntity;
import com.bjw.testtable.domain.post.*;
import com.bjw.testtable.file.repository.FileRepository;
import com.bjw.testtable.file.storage.FileStorageResult;
import com.bjw.testtable.file.storage.FileStorageService;
import com.bjw.testtable.post.repository.PostRepository;
import com.bjw.testtable.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;



@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;
    private final Util html;

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()") //글 작성은 로그인한 사용자만
    public Long create(String currentUserId, PostCreateRequest req, List<MultipartFile> files) {

        Post saved = postRepository.save(
                Post.builder()
                        .userId(currentUserId)
                        .title(req.getTitle())
                        .body(req.getBody())
                        .build()
        );

        if (files != null) {
            for (MultipartFile mf : files) {
                if (mf != null && !mf.isEmpty()) {

                    FileStorageResult fr = fileStorageService.save(mf);

                    FileEntity fe = FileEntity.builder()                    //fileStorygeResul에서 반환된 path, contentType을 받아서 FileEntity로 저장
                            .postId(saved.getId())                         // FK: post_id (엔티티 연관관계라면 post 객체 그대로 넘김)
                            .userId(currentUserId)               // FK: users.user_id
                            .filepath(fr.getPath())              // 저장된 파일 경로
                            .contentType(fr.getContentType())    // MIME 타입
                            .originalFilename(mf.getOriginalFilename()) // 업로드 당시 파일명
                            .size(mf.getSize())                  // 파일 크기 (bytes)
                            .build();


                    fileRepository.save(fe);
                }
            }
        }

        return saved.getId();
    }



    @Override
    @PreAuthorize("isAuthenticated()") // 읽기도 로그인만 허용
    public PostDetailResponse get(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));


        List<FileResponse> files = fileRepository.findByPostId(id).stream()
                .map(f -> FileResponse.builder()
                        .id(f.getId())
                        .originalFilename(f.getOriginalFilename())
                        .size(f.getSize())
                        .build())
                .toList();

        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .authorUserId(post.getUserId())
                .createdAt(post.getCreatedAt())
                .files(files)
                .build();
    }

    @Override
    public Page<PostListResponse> list(String query, Pageable pageable) {
        Page<Post> page = postRepository.search(query, pageable);
        return page.map(p -> PostListResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .authorUserId(p.getUserId())
                .bodyPreview(html.preview(p.getBody(), 120))
                .createdAt(p.getCreatedAt())
                .updateAt(p.getUpdateAt())
                .build());
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwner(#id, authentication)")
    public void update(Long id,
                       String currentUserId,
                       PostUpdateRequest req,
                       List<MultipartFile> newFiles,
                       List<Long> deleteFileIds) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다. id=" + id));


        // 본문/제목 수정
        post.setTitle(req.getTitle());
        post.setBody(req.getBody());

        if (hasUpdateAt(post)) {
            post.setUpdateAt(LocalDateTime.now());
        }

        // 파일 삭제(해당 글의 파일만)
        if (deleteFileIds != null && !deleteFileIds.isEmpty()) {
            List<FileEntity> toDelete = fileRepository.findByIdInAndPostId(deleteFileIds, post.getId());
            for (FileEntity f : toDelete) {
                try {
                    fileStorageService.delete(f.getFilepath()); // 디스크(S3) 삭제
                } catch (RuntimeException ex) {
                    // 디스크 삭제 실패는 로그만 남기고 DB는 지울지 정책 결정 (여기선 같이 삭제)
                }
            }
            fileRepository.deleteAll(toDelete);
        }

        // 새 파일 추가
        if (newFiles != null) {
            for (MultipartFile mf : newFiles) {
                if (mf == null || mf.isEmpty()) continue;

                FileStorageResult fr = fileStorageService.save(mf);
                FileEntity fe = FileEntity.builder()
                        .postId(post.getId())
                        .userId(currentUserId)
                        .filepath(fr.getPath())
                        .contentType(fr.getContentType())
                        .originalFilename(mf.getOriginalFilename())
                        .size(mf.getSize())
                        .build();
                log.info("upload: name={} ct={}", mf.getOriginalFilename(), mf.getContentType());
                fileRepository.save(fe);
            }
        }

    }


    private boolean hasUpdateAt(Post post) {
        try {
            post.getClass().getDeclaredField("updateAt");
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwner(#id, authentication)")
    public void delete(Long id, String currentUserId, Collection<? extends GrantedAuthority> authorities) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));


        // 1) 첨부파일 엔티티들 조회
        List<FileEntity> files = fileRepository.findByPostId(id);

        // 2) 물리 파일 삭제
        for (FileEntity f : files) {
            fileStorageService.delete(f.getFilepath()); // default 메서드/impl 둘 다 OK
        }

        // 3) 파일 엔티티 삭제
        fileRepository.deleteAll(files);

        // 4) 게시글 삭제
        postRepository.delete(post);
    }
}
