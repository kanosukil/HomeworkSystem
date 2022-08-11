package cn.summer.homework.controller;

import cn.summer.homework.VO.FileVO;
import cn.summer.homework.feignClient.FileStoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

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

    private FileVO getRes(String res) {
        if (res == null) {
            return new FileVO(400, "Error", "文件/图片不存在");
        } else if (res.equals("")) {
            return new FileVO(500, "Exception", "文件/图片获取异常");
        } else if (res.equals("no")) {
            return new FileVO(400, "Error", "不能显示非图片文件");
        } else {
            return new FileVO(200, "OK", res);
        }
    }

    @GetMapping("download")
    public FileVO fileDownload(@RequestParam("name") String name,
                               HttpServletResponse response) {
        return getRes(client.download(name, response));
    }

    @GetMapping("/show/image")
    public FileVO imageShow(@RequestParam("image-name") String imageName,
                            HttpServletResponse response) {
        return getRes(client.show(imageName, response));
    }
}
