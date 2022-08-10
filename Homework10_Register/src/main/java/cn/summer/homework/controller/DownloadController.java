package cn.summer.homework.controller;

import cn.summer.homework.VO.FileVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author VHBin
 * @date 2022/8/10-23:44
 */

@RestController
@RequestMapping("download")
public class DownloadController {
    private static final Logger logger = LoggerFactory.getLogger(DownloadController.class);
    @Value("{path.img}")
    private String imagePath;
    @Value("{path.file}")
    private String filePath;

    @PostMapping("file")
    public FileVO fileDownload(@RequestParam("file-name") String fileName,
                               HttpServletResponse response) {
        File file = new File(fileName);
        if (!file.exists()) {
            return new FileVO(400, "Error", "文件不存在");
        }
        response.reset();
        response.setContentType("application/octet-stream");
//        response.setContentType("application/force-download");
        response.setCharacterEncoding("utf-8");
        response.setContentLengthLong(file.length());
        response.setHeader("Content-Disposition",
                "attachment;filename=" + fileName);
        try (
                BufferedInputStream in
                        = new BufferedInputStream(new FileInputStream(file));
                ServletOutputStream out
                        = response.getOutputStream()
        ) {

            byte[] bytes = new byte[1024];
            int i;
            while ((i = in.read(bytes)) != -1) {
                out.write(bytes, 0, i);
                out.flush();
            }
        } catch (IOException e) {
            logger.error("文件读取/传输异常", e);
            return new FileVO(500, "Exception", e.toString());
        }
        return new FileVO(200, "OK", fileName);
    }

    @PostMapping("/show/image")
    public FileVO imageShow(@RequestParam("image-name") String imageName,
                            HttpServletResponse response) {
        File image = new File(imageName);
        if (!image.exists()) {
            return new FileVO(400, "Error", "图片不存在");
        }
        response.reset();
        String suffix = imageName.substring(imageName.lastIndexOf(".")).trim();
        switch (suffix) {
            case ".jpg":
                suffix = "image/jpeg";
                break;
            case ".gif":
                suffix = "image/gif";
                break;
            case ".png":
                suffix = "image/png";
                break;
            default:
                return new FileVO(500, "Exception", "图片格式不支持");
        }
        response.setContentType(suffix);
        response.setContentLengthLong(image.length());
        try (
                BufferedInputStream in
                        = new BufferedInputStream(new FileInputStream(image));
                ServletOutputStream out
                        = response.getOutputStream()
        ) {
            byte[] bytes = new byte[(int) image.length()];
            int i = in.read(bytes);
            out.write(bytes);
            out.flush();
        } catch (IOException e) {
            logger.error("文件读取/传输异常", e);
            return new FileVO(500, "Exception", e.toString());
        }
        return new FileVO(200, "OK", imageName);
    }
}
