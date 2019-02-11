package com.huang.mmall.serviceimpl;

import com.google.common.collect.Lists;
import com.huang.mmall.service.FileService;
import com.huang.mmall.util.FtpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * FileServiceImpl class
 *
 * @author hxy
 * @date 2019/1/20
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {
    @Override
    public String upload(MultipartFile file, String path) {
        //文件名
        String fileName = file.getOriginalFilename();
        //文件拓展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        //上传文件名，用uuid做唯一区分
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        log.info("开始上传文件，上传文件的文件名：{}，上传的路径：{}，新文件名：{}", fileName, path, uploadFileName);
        File fileDir = new File(path);
        //如果文件夹路径不存在则创建
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, uploadFileName);
        try {
            //将file上传到tomcat目录下的upload文件夹下
            file.transferTo(targetFile);
            //将file上传到文件服务器上
            if (FtpUtil.uploadFile(Lists.newArrayList(targetFile))) {
                //上传完删除upload下的文件
                targetFile.delete();
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return null;
        }
        return targetFile.getName();
    }
}
