package com.bjw.testtable.service;

import com.bjw.testtable.dto.post.*;
import com.bjw.testtable.entity.Post;
import com.bjw.testtable.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public Long create(String currentUserId, PostCreateRequest req) {
        Post p = Post.builder()
                .userId(currentUserId)
                .title(req.getTitle())
                .body(req.getBody())
                .build();
        return postRepository.save(p).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public PostDetailResponse get(Long id) {
        Post p = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + id));
        return PostDetailResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .body(p.getBody())
                .authorUserId(p.getUserId())
                .createdAt(p.getCreatedAt())
                .updateAt(p.getUpdateAt())
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
