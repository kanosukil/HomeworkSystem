package cn.summer.homework.controller;

import cn.summer.homework.feignClient.FileStoreClient;
import feign.Response;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author VHBin
 * @date 2022/8/10-23:44
 */

@RestController
@RequestMapping("download")
public class DownloadController {
    private static final Logger logger = LoggerFactory.getLogger(DownloadController.class);
    @Resource
    private FileStoreClient client;

    @GetMapping("/download/{name}")
    @ResponseBody
    public void fileDownload(@PathVariable("name") String name,
                             HttpServletResponse response) {
//        response.setContentType("application/octet-stream");
//        response.setCharacterEncoding("UTF-8");
        Response download = client.download(name);
        logger.info("Response Headers: {}", download.headers().get("error"));
        try (
                InputStream input = download.body().asInputStream();
                ServletOutputStream output = response.getOutputStream()
        ) {
            IOUtils.copy(input, output);
        } catch (IOException e) {
            logger.error("下载文件异常: {}", e.getMessage(), e);
        }
    }

    @GetMapping("/show/image/{image-name}")
    @ResponseBody
    public void imageShow(@PathVariable("image-name") String imageName,
                          HttpServletResponse response) {
        Response res = client.show(imageName);
        try (
                InputStream input = res.body().asInputStream();
                ServletOutputStream output = response.getOutputStream()
        ) {
            IOUtils.copy(input, output);
        } catch (IOException e) {
            logger.error("图片读写异常: {}", e.getMessage(), e);
        }
    }
}
