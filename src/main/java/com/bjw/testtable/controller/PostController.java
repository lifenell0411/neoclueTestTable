package com.bjw.testtable.controller;

import com.bjw.testtable.domain.post.dto.PostCreateRequest;
import com.bjw.testtable.domain.post.dto.PostDetailResponse;
import com.bjw.testtable.domain.post.dto.PostListResponse;
import com.bjw.testtable.domain.post.dto.PostUpdateRequest;
import com.bjw.testtable.domain.post.service.PostService;
import com.bjw.testtable.security.PostSecurity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostSecurity postSecurity;



    @PreAuthorize("isAuthenticated()")// 목록 + 검색 + 페이징
    @GetMapping("/list")
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(defaultValue = "0") int page, //페이지네이션
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<PostListResponse> posts = postService.list(q, pageable);

        model.addAttribute("posts", posts); // 게시글 목록
        model.addAttribute("q", q);         // 검색어 유지용
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
        model.addAttribute("post", post);
        model.addAttribute("canEdit", canEdit);
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
            @AuthenticationPrincipal UserDetails user
    ) {
        if (binding.hasErrors()) return "posts/create";
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
