package com.huang.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * FileService class
 *
 * @author hxy
 * @date 2019/1/20
 */
public interface FileService {
    /**
     * 上传文件
     *
     * @param file 文件
     * @param path 上传路径
     * @return 文件名
     */
    String upload(MultipartFile file, String path);
}
