package cn.summer.homework.service.impl;

import cn.summer.homework.service.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/11-17:10
 */
@Service
public class UploadServiceImpl implements UploadService {

    private static final Logger logger = LoggerFactory.getLogger(UploadServiceImpl.class);
    private static final List<String> picContentType
            = Arrays.asList("image/jpeg", "image/gif", "image/png", "image/bmg");
    @Value("${path.img}")
    private String imagePath;
    @Value("${path.file}")
    private String filePath;

    private String combine(MultipartFile file, Integer uid) {
        if (file.isEmpty()) {
            return null;
        }
        try {
//            BufferedImage image = ImageIO.read(file.getInputStream()); // 检测图片
            String beforeName = file.getOriginalFilename();
            if (beforeName == null) {
                throw new Exception("传入文件/图片名为空");
            }
            String flag = "file";
            if (picContentType.contains(file.getContentType())) {
                flag = "image";
            }
            String pa = flag.equals("file") ? filePath : imagePath;
            String afterName = pa + "uid_" + uid + "-time_" +
                    System.currentTimeMillis() % 1000 + "-"
                    + flag + beforeName.substring(
                    beforeName.lastIndexOf("."));
            logger.info("{}-Name:{}开始写入", flag, afterName);
            // file.transferTo(Paths.get(afterName)); // java.io
            File fi = new File(pa);
            if (!fi.exists()) {
                if (!fi.mkdirs()) {
                    throw new Exception("文件夹创建异常");
                }
            }
            Files.write(
                    Paths.get(afterName),
                    file.getBytes()); // java.nio
            logger.info("{}-Name:{}写入完成", flag, afterName);
            return afterName;
        } catch (Exception ex) {
            logger.error("文件/图片上传异常", ex);
            return "";
        }
    }

    private String combines(MultipartFile[] files, Integer uid) {
        StringBuilder temp = new StringBuilder();
        for (MultipartFile file : files) {
            String tmp = combine(file, uid);
            if (tmp == null || tmp.equals("")) {
                return tmp;
            }
            temp.append(tmp).append(",");
        }
        return temp.deleteCharAt(temp.length() - 1).toString();
    }


    /**
     * 单张图片上传
     *
     * @param image 图片
     * @param uid   用户ID
     * @return String 路径/null=Error/""=Exception
     */
    @Override
    public String imageUpload(MultipartFile image, Integer uid) {
        return combine(image, uid);
    }

    /**
     * 多张图片上传
     *
     * @param images 图片数组
     * @param uid    用户ID
     * @return String 路径/null=Error/""=Exception
     */
    @Override
    public String imagesUpload(MultipartFile[] images, Integer uid) {
        return combines(images, uid);
    }

    /**
     * 单个文件上传
     *
     * @param file 图片
     * @param uid  用户ID
     * @return String 路径/null=Error/""=Exception
     */
    @Override
    public String fileUpload(MultipartFile file, Integer uid) {
        return combine(file, uid);
    }

    /**
     * 多个文件上传
     *
     * @param files 文件数组
     * @param uid   用户ID
     * @return String 路径/null=Error/""=Exception
     */
    @Override
    public String filesUpload(MultipartFile[] files, Integer uid) {
        return combines(files, uid);
    }
}
