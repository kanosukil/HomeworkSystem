package cn.summer.homework.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author VHBin
 * @date 2022/8/11-14:09
 */
public interface UploadService {
    String imageUpload(MultipartFile image, Integer uid);

    String imagesUpload(MultipartFile[] images, Integer uid);

    String fileUpload(MultipartFile file, Integer uid);

    String filesUpload(MultipartFile[] files, Integer uid);
}
