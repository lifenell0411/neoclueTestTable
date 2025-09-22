package com.bjw.testtable.image.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EditorController {

    @GetMapping("/editor/photo-uploader")
    public String photoUploader() {
        return "se2/sample/photo_uploader/photo_uploader";
    }
}