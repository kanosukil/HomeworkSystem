package cn.summer.homework.controller;

import cn.summer.homework.service.DownloadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * @author VHBin
 * @date 2022/8/11-18:07
 */
@RestController
@RequestMapping("download")
public class DownloadController {
    @Resource
    private DownloadService down;

    @GetMapping("download")
    public String download(@RequestParam("name") String name, HttpServletResponse response) {
        return down.download(name, response);
    }

    @GetMapping("show")
    public String show(@RequestParam("image-name") String name, HttpServletResponse response) {
        return down.showImage(name, response);
    }
}
