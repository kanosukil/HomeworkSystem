package cn.summer.homework.controller;

import cn.summer.homework.service.UploadService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/8/11-17:59
 */
@RestController
@RequestMapping("upload")
public class UploadController {
    @Resource
    private UploadService up;

    @PostMapping("image")
    public String image(@RequestParam("image") MultipartFile image,
                        @RequestParam("uid") Integer uid) {
        return up.imageUpload(image, uid);
    }

    @PostMapping("file")
    public String file(@RequestParam("file") MultipartFile file,
                       @RequestParam("uid") Integer uid) {
        return up.fileUpload(file, uid);
    }

    @PostMapping("images")
    public String images(@RequestParam("images") MultipartFile[] images,
                         @RequestParam("uid") Integer uid) {
        return up.imagesUpload(images, uid);
    }

    @PostMapping("files")
    public String files(@RequestParam("files") MultipartFile[] files,
                        @RequestParam("uid") Integer uid) {
        return up.filesUpload(files, uid);
    }
}
