package cn.summer.homework.controller;

import cn.summer.homework.service.DownloadService;
import org.springframework.web.bind.annotation.*;

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
    @ResponseBody
    public void download(@RequestParam("name") String name, HttpServletResponse response) {
        down.download(name, response);
    }

    @GetMapping("show")
    @ResponseBody
    public void show(@RequestParam("image-name") String name, HttpServletResponse response) {
        down.showImage(name, response);
    }
}
