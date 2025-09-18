package com.bjw.testtable.post.controller;

import com.bjw.testtable.domain.post.PostCreateRequest;
import com.bjw.testtable.domain.post.PostDetailResponse;
import com.bjw.testtable.post.dto.PostListItemDto;
import com.bjw.testtable.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;
@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostRowController {

    private final PostService postService;

    @PreAuthorize("isAuthenticated()") //비동기로 데려온 데이터 여기서 실행
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
        //가져온 데이터로 db에 저장
        Long id = postService.create(user.getUsername(), req, files);

        // 방금 저장한 데이터 다시 조회
        PostDetailResponse detail = postService.get(id);

        PostListItemDto item = PostListItemDto.fromDetail(detail);
        //ajax에서 submit으로 챙겨온 title이나 body를 여기다 꽂아넣음
        //item안에는 dto에 지정된 값들이 들어있음, 예를들어 title이나 body같은거. 이걸 post-row.html로 전송
        model.addAttribute("item", item);
        return "posts/_post-row :: row"; // <tr>만 응답
    }
}
