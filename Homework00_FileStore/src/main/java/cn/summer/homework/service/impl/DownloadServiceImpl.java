package cn.summer.homework.service.impl;

import cn.summer.homework.Util.PathUtil;
import cn.summer.homework.service.DownloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author VHBin
 * @date 2022/8/11-17:23
 */
@Service
public class DownloadServiceImpl implements DownloadService {

    private static final Logger logger = LoggerFactory.getLogger(DownloadServiceImpl.class);
    private static final HashMap<String, String> picSuffix
            = new HashMap<>() {{
        put(".jpg", "image/jpeg");
        put(".jpeg", "image/jpeg");
        put(".gif", "image/gif");
        put(".png", "image/png");
        put(".bmp", "image/bmp");
    }};
    @Value("${path.img}")
    private String imagePath;
    @Value("${path.file}")
    private String filePath;

    private File getFile(String name, String path) {
        if (PathUtil.isPath(name)) {
            return new File(name);
        } else {
            return new File(PathUtil.pathCombine(name, path));
        }
    }

    /**
     * 文件/图片下载
     *
     * @param name     文件/图片名
     * @param response Servlet response 对象
     */
    @Override
    public void download(String name, HttpServletResponse response) {
        File file = getFile(name,
                picSuffix.containsKey(
                        name.substring(name.lastIndexOf(".")))
                        ? imagePath : filePath);
        if (!file.exists()) {
            logger.error("文件/图片为空");
            response.setHeader("error", "no-file");
            return;
        }
        response.reset();
        response.setContentType("application/octet-stream");
//        response.setContentType("application/force-download");
        response.setCharacterEncoding("utf-8");
        response.setContentLengthLong(file.length());
        response.setHeader("Content-Disposition",
                "attachment;filename=" + name);
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
            logger.error("文件/图片读取/传输异常", e);
            response.setHeader("error", "io-file");
            return;
        }
        response.setHeader("error", "no");
    }

    /**
     * 图片显示
     *
     * @param name     图片名
     * @param response Servlet response 对象
     */
    @Override
    public void showImage(String name, HttpServletResponse response) {
        String contentType;
        String suffix = name.substring(name.lastIndexOf(".")).trim();
        if (picSuffix.containsKey(suffix)) {
            contentType = picSuffix.get(suffix);
        } else {
            response.setHeader("error", "no-suffix");
            return;
        }
        File image = getFile(name, imagePath);
        if (!image.exists()) {
            response.setHeader("error", "no-file");
            return;
        }
        response.reset();
        response.setContentType(contentType);
        response.setContentLengthLong(image.length());
        try (
                BufferedInputStream in
                        = new BufferedInputStream(new FileInputStream(image));
                ServletOutputStream out
                        = response.getOutputStream()
        ) {
            byte[] bytes = new byte[(int) image.length()];
            int i = in.read(bytes);
            out.write(bytes, 0, i);
            out.flush();
        } catch (IOException e) {
            logger.error("图片读取/传输异常", e);
            response.setHeader("error", "io-file");
            return;
        }
        response.setHeader("error", "no");
    }
}
