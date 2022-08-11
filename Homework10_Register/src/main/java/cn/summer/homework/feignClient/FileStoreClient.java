package cn.summer.homework.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author VHBin
 * @date 2022/8/11-18:11
 */
@FeignClient("FileStoreService")
public interface FileStoreClient {
    @PostMapping("/upload/image")
    String image(@RequestParam("image") MultipartFile image,
                 @RequestParam("uid") Integer uid);

    @PostMapping("/upload/file")
    String file(@RequestParam("file") MultipartFile file,
                @RequestParam("uid") Integer uid);

    @PostMapping("/upload/images")
    String images(@RequestParam("images") MultipartFile[] images,
                  @RequestParam("uid") Integer uid);

    @PostMapping("/upload/files")
    String files(@RequestParam("files") MultipartFile[] files,
                 @RequestParam("uid") Integer uid);

    @GetMapping("/download/download")
    String download(@RequestParam("name") String name,
                    HttpServletResponse response);

    @GetMapping("/download/show")
    String show(@RequestParam("image-name") String name,
                HttpServletResponse response);
}
