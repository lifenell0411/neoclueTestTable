package com.bjw.testtable.post.service;

import com.bjw.testtable.domain.file.FileEntity;
import com.bjw.testtable.domain.post.Post;
import com.bjw.testtable.file.dto.FileResponse;
import com.bjw.testtable.file.repository.FileRepository;
import com.bjw.testtable.file.storage.FileStorageResult;
import com.bjw.testtable.file.storage.FileStorageService;
import com.bjw.testtable.post.dto.PostCreateRequest;
import com.bjw.testtable.post.dto.PostDetailResponse;
import com.bjw.testtable.post.dto.PostListResponse;
import com.bjw.testtable.post.dto.PostUpdateRequest;
import com.bjw.testtable.post.repository.PostRepository;
import com.bjw.testtable.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import java.util.stream.Collectors;

import static com.bjw.testtable.util.Util.preview;


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

                    // ✅ 정적 팩토리 메서드를 사용하여 한 줄로 생성
                    FileEntity fe = FileEntity.create(mf, fr, saved.getId(), currentUserId);

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
                .updateAt(post.getUpdateAt())
                .files(files)
                .build();
    }


    @Override
    public Page<PostListResponse> list(String field, String query, Pageable pageable) {
        //Repository로부터 이미 DTO로 변환된 페이지를 받음 page
        Page<PostListResponse> page = postRepository.search(field, query, pageable);//검색어있으면 그 리스트, 없으면 전체리스트

        // 2. 내용물(List<PostListResponse>)에 대해서만 'preview' 로직을 적용
        List<PostListResponse> processedContent = page.getContent().stream() // processedContent에 page리스트의 내용물만 꺼내서 가공하고 그 결과물 넣는다
                .map(p -> { //형변환 아니고 각각의 요소를 수정/변경한다. 객체 하나가 p
                    // 기존 DTO는 그대로 두고, title과 bodyPreview 필드만 잘라서 교체
                    p.setTitle(preview(p.getTitle(), 10));
                    p.setBodyPreview(preview(p.getBodyPreview(), 20));
                    return p;
                })
                .collect(Collectors.toList());//p들을 모아서 다시 정렬

        return new PageImpl<>(processedContent, pageable, page.getTotalElements());//결과보고서:프리뷰적용된p들의 정렬된리스트, 페이지정보, 토탈갯수를 Page객체로 반환
    }


    @Override
    @Transactional //메서드가 끝나면 sql날려서 db변경함
    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwner(#id, authentication)")
    public void update(Long id,
                       String currentUserId,
                       PostUpdateRequest req,
                       List<MultipartFile> newFiles,
                       List<Long> deleteFileIds) {

        Post post = postRepository.findById(id) //영속엔티티 // 내부적으로 거의 EntityManager.find(Post.class, id)와 같음
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다. id=" + id));


        // 본문/제목 수정
        post.setTitle(req.getTitle()); //더티체킹?영속엔티티의 필드를 바꾸면 커밋 시점에 자동으로 updateSQL날림 그래서 save가 필요없음
        post.setBody(req.getBody());//

        if (hasUpdateAt(post)) {
            post.setUpdateAt(LocalDateTime.now());
        }

        // 2) 파일 "소프트 삭제" (해당 글의 파일만)
        if (deleteFileIds != null && !deleteFileIds.isEmpty()) {
            List<FileEntity> toDelete = fileRepository
                    .findByIdInAndPostIdAndDeletedFalse(deleteFileIds, post.getId());
            for (FileEntity f : toDelete) {
                // 물리 파일은 건드리지 않음 (정책: 나중에 배치로 정리)
                f.markDeleted(currentUserId);
            }
            fileRepository.saveAll(toDelete);
        }

        // 새 파일 추가
        if (newFiles != null) {
            for (MultipartFile mf : newFiles) {
                if (mf == null || mf.isEmpty()) continue;

                FileStorageResult fr = fileStorageService.save(mf);
                FileEntity fe = FileEntity.create(mf, fr, post.getId(), currentUserId);
                fileRepository.save(fe); //new 객체는 비영속이라 save 소환해줘야 db에 insert됨
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
    @Transactional //ADMIN이거나 해당 글의 소유자만 접근 가능
    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwner(#id, authentication)")
    public void delete(Long id, String currentUserId, Collection<? extends GrantedAuthority> authorities) {


        //post획득
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));


        // 파일 삭제 안된것들 획득
        List<FileEntity> files = fileRepository.findByPostIdAndDeletedFalse(id);

        // 여기서 deleted, deleted_at등등 값 변경
        post.markDeleted(currentUserId);
        files.forEach(f -> f.markDeleted(currentUserId));

        // 변경된 값으로 업데이트쳐줌
        postRepository.save(post);
        fileRepository.saveAll(files);

        // ※ 물리 파일은 당장 지우지 않음.

    }
}
