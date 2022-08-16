package cn.summer.homework.feignClient;

import feign.Logger;
import feign.Response;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author VHBin
 * @date 2022/8/11-18:11
 */
@FeignClient("FileStoreService")
public interface FileStoreClient {
    @PostMapping(value = "/upload/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String image(@RequestPart("image") MultipartFile image,
                 @RequestParam("uid") Integer uid);

    @PostMapping(value = "/upload/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String file(@RequestPart("file") MultipartFile file,
                @RequestParam("uid") Integer uid);

    @PostMapping(value = "/upload/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String images(@RequestPart("images") MultipartFile[] images,
                  @RequestParam("uid") Integer uid);

    @PostMapping(value = "/upload/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String files(@RequestPart("files") MultipartFile[] files,
                 @RequestParam("uid") Integer uid);

    @GetMapping("/download/download")
    @ResponseBody
    Response download(@RequestParam("name") String name);

    @GetMapping("/download/show")
    @ResponseBody
    Response show(@RequestParam("image-name") String name);

    class FeignMultipartSupportConfig {
        @Bean
        @Primary
        @Scope("prototype")
        public Encoder multipartFormEncoder() {
            return new SpringFormEncoder();
        }

        @Bean
        public Logger.Level multipartLoggerLevel() {
            return Logger.Level.FULL;
        }
    }
}
