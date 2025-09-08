package com.bjw.testtable.controller;

import com.bjw.testtable.dto.post.PostCreateRequest;
import com.bjw.testtable.dto.post.PostDetailResponse;
import com.bjw.testtable.dto.post.PostListResponse;
import com.bjw.testtable.dto.post.PostUpdateRequest;
import com.bjw.testtable.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 목록 + 검색 + 페이징
    @GetMapping("/list")
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<PostListResponse> posts = postService.list(q, pageable);

        model.addAttribute("posts", posts); // 게시글 목록
        model.addAttribute("q", q);         // 검색어 유지용
        return "posts/list"; // templates/posts/list.html 로 렌더링
    }

    // 상세
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        PostDetailResponse post = postService.get(id);
        model.addAttribute("post", post);
        return "posts/detail"; // templates/posts/detail.html
    }

    // 작성 폼 페이지
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("postCreateRequest", new PostCreateRequest());
        return "posts/create"; // templates/posts/create.html
    }

    // 작성 처리 (form action="/posts/create" method="post")
    @PostMapping("/create")
    public String create(@Valid PostCreateRequest req,
                         @AuthenticationPrincipal UserDetails user) {
        postService.create(user.getUsername(), req);
        return "redirect:/posts/list"; // 작성 후 목록으로 이동
    }

    // 수정 폼 페이지
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        PostDetailResponse post = postService.get(id);
        model.addAttribute("post", post);
        return "posts/edit"; // templates/posts/edit.html
    }

    // 수정 처리
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid PostUpdateRequest req,
                         @AuthenticationPrincipal UserDetails user) {
        postService.update(id, user.getUsername(), req);
        return "redirect:/posts/" + id; // 수정 후 상세 페이지로
    }

    // 삭제 처리 (버튼 눌러서 POST or GET으로 날리면 됨)
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails user) {
        postService.delete(id, user.getUsername());
        return "redirect:/posts/list";
    }
}
