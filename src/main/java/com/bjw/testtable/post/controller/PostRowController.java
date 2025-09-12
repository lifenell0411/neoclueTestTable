package com.bjw.testtable.post.controller;

import com.bjw.testtable.domain.post.PostCreateRequest;
import com.bjw.testtable.domain.post.PostDetailResponse;
import com.bjw.testtable.post.dto.PostListItemDto;
import com.bjw.testtable.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostRowController {

    private final PostService postService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value="/row",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_HTML_VALUE)
    public String createRow(
            @AuthenticationPrincipal UserDetails user,
            @Valid @ModelAttribute("post") PostCreateRequest req,
            BindingResult binding,
            @RequestParam(value="files", required=false) List<MultipartFile> files,
            Model model
    ){
        if (binding.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "검증 오류");
        }

        Long id = postService.create(user.getUsername(), req, files);

        // 저장 직후 최신 값 재조회(네 서비스는 상세 DTO 리턴)
        PostDetailResponse detail = postService.get(id);
        // detail 빌더에 updateAt 누락 없이 반드시 세팅할 것!
        PostListItemDto item = PostListItemDto.fromDetail(detail);

        model.addAttribute("item", item);
        return "posts/_post-row :: row"; // <tr>만 응답
    }
}
