package com.bjw.testtable.security;

import com.bjw.testtable.domain.post.Post;
import com.bjw.testtable.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("postSecurity") // SpEL에서 @postSecurity 로 참조 가능하게 이름 고정
@RequiredArgsConstructor
public class PostSecurity { //소유자 판별 공통화

    private final PostRepository postRepository;

    /**
     * 글의 작성자 == 현재 로그인 사용자 인지 여부
     * @param postId 게시글 ID
     * @param authentication 현재 인증 정보 (SpEL에서 주입됨)
     */
    @Transactional(readOnly = true)
    public boolean isOwner(Long postId, Authentication authentication) { //Authentication : SpEL에서 authentication 변수로 현재 로그인 정보를 자동으로 넘겨줌
        if (authentication == null || !authentication.isAuthenticated()) return false;
        String loginId = authentication.getName();

        return postRepository.findById(postId)
                .map(Post::getUserId)
                .map(authorId -> authorId != null && authorId.equals(loginId))
                .orElse(false);
    }
}