package cn.summer.homework.controller;

import cn.summer.homework.Util.PathUtil;
import cn.summer.homework.VO.FileVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author VHBin
 * @date 2022/8/10-22:40
 */

@RestController
@RequestMapping("upload")
public class UploadController {
    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    @Value("{path.img}")
    private String imagePath;
    @Value("{path.file}")
    private String filePath;

    private FileVO combine(MultipartFile file, Integer uid, String flag, String filePath) {
        if (file.isEmpty()) {
            return new FileVO(400, "Error", "请重新上传文件/图片");
        }
        try {
//            BufferedImage image = ImageIO.read(file.getInputStream()); // 检测图片
            String beforeName = file.getOriginalFilename();
            if (beforeName == null) {
                throw new Exception("传入文件/图片名为空");
            }
            String afterName = PathUtil.pathJudge(filePath) + uid + "-" +
                    System.currentTimeMillis() % 1000 + "-"
                    + flag + beforeName.substring(
                    beforeName.lastIndexOf("."));
            logger.info("{}-Name:{}开始写入", flag, afterName);
            // file.transferTo(Paths.get(afterName)); // java.io
            Files.write(
                    Paths.get(afterName),
                    file.getBytes()); // java.nio
            logger.info("{}-Name:{}写入完成", flag, afterName);
            return new FileVO(200, flag + "-path", afterName);
        } catch (Exception ex) {
            logger.error("文件/图片上传异常", ex);
            return new FileVO(500, "Exception", "文件/图片上传异常");
        }
    }

    /**
     * 单张图片上传
     *
     * @param image 图片
     * @param uid   用户ID
     * @return FileVO 状态码, 信息, 路径
     */
    @PostMapping("image")
    public FileVO imageUpload(@RequestParam("image") MultipartFile image,
                              @RequestParam("uid") Integer uid) {
        return combine(image, uid, "image", imagePath);
    }

    /**
     * 单个文件上传
     *
     * @param file 图片
     * @param uid  用户ID
     * @return FileVO 状态码, 信息, 路径
     */
    @PostMapping("file")
    public FileVO fileUpload(@RequestParam("file") MultipartFile file,
                             @RequestParam("uid") Integer uid) {
        return combine(file, uid, "file", filePath);
    }

    private FileVO combines(MultipartFile[] images, Integer uid, String flag, String imagePath) {
        int code = 200;
        FileVO tmp;
        String message = flag + "s-path";
        StringBuilder temp = new StringBuilder();
        for (MultipartFile image : images) {
            tmp = combine(image, uid, flag, imagePath);
            if (tmp.getCode() == 200) {
                temp.append(tmp.getPath()).append(",");
            } else {
                message = tmp.getMessage();
                code = tmp.getCode();
                break;
            }
        }
        return new FileVO(code, message,
                code == 200 ? temp.deleteCharAt(temp.length() - 1).toString() : "");
    }

    /**
     * 多张图片上传
     *
     * @param images 图片数组
     * @param uid    用户ID
     * @return FileVO 状态码, 信息, 路径
     */
    @PostMapping("images")
    public FileVO imagesUpload(@RequestParam("images") MultipartFile[] images,
                               @RequestParam("uid") Integer uid) {
        return combines(images, uid, "image", imagePath);
    }

    /**
     * 多个文件上传
     *
     * @param files 文件数组
     * @param uid   用户ID
     * @return FileVO 状态码, 信息, 路径
     */
    @PostMapping("files")
    public FileVO filesUpload(@RequestParam("files") MultipartFile[] files,
                              @RequestParam("uid") Integer uid) {
        return combines(files, uid, "file", filePath);
    }
}
