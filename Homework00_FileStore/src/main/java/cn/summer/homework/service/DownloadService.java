package cn.summer.homework.service;

import javax.servlet.http.HttpServletResponse;

/**
 * @author VHBin
 * @date 2022/8/11-17:07
 */
public interface DownloadService {
    void download(String name, HttpServletResponse response);

    void showImage(String name, HttpServletResponse response);
}
