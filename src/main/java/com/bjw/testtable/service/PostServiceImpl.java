package com.bjw.testtable.service;

import com.bjw.testtable.dto.post.*;
import com.bjw.testtable.entity.FileEntity;
import com.bjw.testtable.entity.Post;
import com.bjw.testtable.repository.FileRepository;
import com.bjw.testtable.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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

                    FileEntity fe = FileEntity.builder()
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
    public void update(Long id, String currentUserId, PostUpdateRequest req) {
        Post p = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + id));
        // 소유자만 수정 가능
        if (!p.getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        p.setTitle(req.getTitle());
        p.setBody(req.getBody());
        // @PreUpdate가 updateAt 갱신
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
