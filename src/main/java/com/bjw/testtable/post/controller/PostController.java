package com.bjw.testtable.post.controller;

import com.bjw.testtable.domain.file.FileEntity;
import com.bjw.testtable.post.dto.PostCreateRequest;
import com.bjw.testtable.post.dto.PostDetailResponse;
import com.bjw.testtable.post.dto.PostListResponse;
import com.bjw.testtable.post.dto.PostUpdateRequest;
import com.bjw.testtable.file.repository.FileRepository;
import com.bjw.testtable.post.service.PostService;
import com.bjw.testtable.security.PostSecurity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostSecurity postSecurity;
    private final FileRepository fileRepository;


    @PreAuthorize("isAuthenticated()")// 목록 + 검색 + 페이징
    @GetMapping("/list")
    public String list(@RequestParam(value = "query", required = false) String query,
                       @RequestParam(defaultValue = "title") String field,
                       @RequestParam(defaultValue = "0") int page, //페이지네이션
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<PostListResponse> posts = postService.list(field, query, pageable);
        //id list뽑음

                                         //지금 표시된 리스트 리스폰스의 list를 ids로 가져옴
        java.util.List<Long> ids = posts.getContent().stream()
                .map(PostListResponse::getId)   // ← PostListResponse에서 id만 꺼냄(long타입)
                .collect(java.util.stream.Collectors.toList());

        // 파일이 있는 글의 id만 한 번에 조회 (QueryDSL 구현 메서드)
        java.util.Set<Long> idsWithFiles = ids.isEmpty()
                ? java.util.Collections.emptySet()
                : new java.util.HashSet<>(fileRepository.findPostIdsHavingFiles(ids)); //삭제되지않은 파일이 있는 게시글묶음 조회해서 idsWithFiles에 담음
//              이걸 list에서 Y로 표기되게 써먹음

//        사용자 → Controller.list → Service.list → Repository.search → DB
//→ Repository 반환 → Service 가공 → Controller에서 idsWithFiles 조회 → Model에 담기 → Thymeleaf 렌더 → HTML 응답 → 브라우저 표시(+JS 초기화)



        model.addAttribute("posts", posts); // 게시글 목록
        model.addAttribute("query", query);         // 검색어
        model.addAttribute("field", field); //검색 필드 (제목, 내용, 아이디)
        model.addAttribute("idsWithFiles", idsWithFiles);
        model.addAttribute("post", new PostCreateRequest());
        return "posts/list"; // templates/posts/list.html 로 렌더링
    }

    // 상세
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, Authentication auth) {
        PostDetailResponse post = postService.get(id);

        boolean canEdit = false;
        if (auth != null && auth.isAuthenticated()) {
            // 보안 빈 재사용 (컨트롤러에서 ROLE 문자열 직접 만지지 않기)
            canEdit = postSecurity.isOwner(id, auth)
                    || auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            // ↑ 간단히 auth의 ADMIN 권한만 체크. (원하면 보안 빈에 canEdit(...) 만들어도 됨)
        }
        // ✅ 보이는 파일만(soft delete 제외)
        List<FileEntity> files = fileRepository.findByPostIdAndDeletedFalse(id);

        model.addAttribute("post", post);
        model.addAttribute("canEdit", canEdit);
        model.addAttribute("files", files); // ← 뷰에서는 이거만 사용
        return "posts/detail";
    }

    // 작성 폼 페이지
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("post", new PostCreateRequest());

        return "posts/create";
    }

    @PreAuthorize("isAuthenticated()")
    // 글 저장 (POST /posts/create)
    @PostMapping("/create")
    public String createSubmit(
            @Valid @ModelAttribute("post") PostCreateRequest req,
            BindingResult binding,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserDetails user, RedirectAttributes ra
    ) {
        if (binding.hasErrors()) return "posts/create";
        ra.addFlashAttribute("msg", "등록되었습니다.");
        Long id = postService.create(user.getUsername(), req, files);
        return "redirect:/posts/" + id;
    }


    // 수정 폼 페이지

    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwner(#id, authentication)")
    @GetMapping("/{id}/update")
    public String editForm(@PathVariable Long id,
                           Model model,
                           Authentication auth,
                           RedirectAttributes ra) {

        PostDetailResponse post = postService.get(id);
        model.addAttribute("post", post);
        return "posts/update";
    }


    // 수정 처리 (files + deleteFileIds 받기)
    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwner(#id, authentication)")
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("post") PostUpdateRequest req,
                         BindingResult binding,
                         @RequestParam(value = "files", required = false) List<MultipartFile> files,
                         @RequestParam(value = "deleteFileIds", required = false) List<Long> deleteFileIds,
                         @AuthenticationPrincipal UserDetails user,
                         RedirectAttributes ra) {


        if (binding.hasErrors()) return "posts/update";

        postService.update(id, user.getUsername(), req, files, deleteFileIds);
        ra.addFlashAttribute("msg", "수정되었습니다.");
        return "redirect:/posts/" + id;
    }





    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isOwner(#id, authentication)")
    // 삭제 처리 (버튼 눌러서 POST or GET으로 날리면 됨)
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails user,
                         RedirectAttributes ra) {

            postService.delete(id, user.getUsername(), user.getAuthorities());
            ra.addFlashAttribute("msg", "삭제되었습니다.");
            return "redirect:/posts/list";


    }




}
