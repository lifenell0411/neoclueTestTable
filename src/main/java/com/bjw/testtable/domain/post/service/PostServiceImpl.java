package com.bjw.testtable.domain.post.service;

import com.bjw.testtable.domain.file.entity.FileEntity;
import com.bjw.testtable.domain.file.repository.FileRepository;
import com.bjw.testtable.domain.file.storage.FileStorageResult;
import com.bjw.testtable.domain.file.storage.FileStorageService;
import com.bjw.testtable.domain.post.dto.*;
import com.bjw.testtable.domain.post.entity.Post;
import com.bjw.testtable.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    @Override
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
    @Transactional(readOnly = true)
    public Page<PostListResponse> list(String query, Pageable pageable) {
        Page<Post> page = postRepository.search(query, pageable);
        return page.map(p -> PostListResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .authorUserId(p.getUserId())
                .bodyPreview(p.getBody() == null ? "" :
                        (p.getBody().length() > 80 ? p.getBody().substring(0, 80) + "…" : p.getBody()))
                .createdAt(p.getCreatedAt())
                .updateAt(p.getUpdateAt())
                .build());
    }

    @Override
    public void update(Long id,
                       String currentUserId,
                       Collection<? extends GrantedAuthority> authorities,
                       PostUpdateRequest req,
                       List<MultipartFile> newFiles,
                       List<Long> deleteFileIds) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. id=" + id));

        boolean isOwner = post.getUserId().equals(currentUserId);
        boolean isAdmin = authorities.stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        // 1) 본문/제목 수정
        post.setTitle(req.getTitle());
        post.setBody(req.getBody());
        post.setUpdateAt(LocalDateTime.now());

        // 2) 첨부파일 삭제(선택 항목)
        if (deleteFileIds != null && !deleteFileIds.isEmpty()) {
            List<FileEntity> toDelete = fileRepository.findAllById(deleteFileIds);
            for (FileEntity f : toDelete) {
                // 소유권 추가 방어선 (선택) : 다른 글 파일 못지우게
                if (!f.getPostId().equals(post.getId()) && !isAdmin) continue;
                try {
                    fileStorageService.delete(f.getFilepath()); // 물리 파일 삭제
                } catch (RuntimeException e) {
                    // 로그만 남기고 계속 진행해도 됨
                }
            }
            fileRepository.deleteAllInBatch(toDelete); // 메타 삭제
        }

        // 3) 새 파일 추가
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
                fileRepository.save(fe);
            }
        }
        // JPA 영속 상태라 별도 save() 없어도 커밋 시 반영됨
    }
    @Override
    @Transactional(readOnly = true)
    public boolean canEdit(Long id, String currentUserId, Collection<? extends GrantedAuthority> authorities) {
        return postRepository.findById(id)
                .map(post -> {
                    boolean isOwner = post.getUserId().equals(currentUserId);
                    boolean isAdmin = authorities.stream()
                            .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
                    return isOwner || isAdmin;
                })
                .orElse(false); // 글이 없으면 편집 불가
    }
    @Override
    public void delete(Long id, String currentUserId) {
        Post p = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + id));
        if (!p.getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }
        postRepository.delete(p);
    }
}
