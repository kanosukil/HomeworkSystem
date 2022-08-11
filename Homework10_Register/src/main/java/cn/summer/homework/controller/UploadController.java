package cn.summer.homework.controller;

import cn.summer.homework.VO.FileVO;
import cn.summer.homework.feignClient.FileStoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/8/10-22:40
 */

@RestController
@RequestMapping("upload")
public class UploadController {
    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Resource
    private FileStoreClient client;

    private FileVO getRes(String res) {
        if (res == null) {
            return new FileVO(400, "Error", "请重新上传文件/图片");
        } else if (res.equals("")) {
            return new FileVO(500, "Exception", "文件/图片上传异常");
        } else {
            return new FileVO(200, "OK", res);
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
        return getRes(client.image(image, uid));
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
        return getRes(client.file(file, uid));
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
        return getRes(client.images(images, uid));
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
        return getRes(client.files(files, uid));
    }
}
