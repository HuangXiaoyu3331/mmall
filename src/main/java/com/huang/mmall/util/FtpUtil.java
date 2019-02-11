package com.huang.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * FtpUtil class
 *
 * @author hxy
 * @date 2019/1/19
 */
@Slf4j
public class FtpUtil {

    private static String ip = PropertiesUtil.getProperty("ftp.server.ip");
    private static String user = PropertiesUtil.getProperty("ftp.server.user");
    private static String password = PropertiesUtil.getProperty("ftp.server.password");
    private static String port = PropertiesUtil.getProperty("ftp.server.port", "21");
    private static String remotePath = PropertiesUtil.getProperty("ftp.server.remotePath");

    private static FTPClient ftpClient;

    /**
     * 连接ftp服务器
     *
     * @return
     */
    private static boolean connect() {
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip, Integer.parseInt(port));
            isSuccess = ftpClient.login(user, password);
        } catch (IOException e) {
            log.error("连接FTP服务器异常" + e);
        }
        return isSuccess;
    }

    /**
     * 上传文件
     *
     * @param fileList 文件列表
     * @return 成功|失败
     * @throws IOException
     */
    public static boolean uploadFile(List<File> fileList) {
        boolean uploaded = false;
        FileInputStream fileInputStream = null;
        String fileName;
        if (connect()) {
            try {
                createDir(remotePath);
                //改变工作目录
                ftpClient.changeWorkingDirectory(remotePath);
                //设置缓冲区大小
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                //二进制
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File file : fileList) {
                    fileInputStream = new FileInputStream(file);
                    //ftp服务器默认编码格式维iso-8859-1,所以需要把文件名设置为iso-8859-1才不会导致上传文件名乱码
                    fileName = new String(file.getName().getBytes("UTF-8"), "iso-8859-1");
                    ftpClient.storeFile(fileName, fileInputStream);
                }
                uploaded = true;
            } catch (IOException e) {
                uploaded = false;
                log.error("上传文件异常", e);
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                        ftpClient.disconnect();
                    } catch (IOException e) {
                        log.error("文件上传失败，关闭流或断开ftp服务器连接异常", e);
                    }
                }
            }
        }
        return uploaded;
    }

    /**
     * 如果文件路径不存在，则创建
     * 创建文件夹不能依赖业务，在代码中应该自动创建
     *
     * @param remotePath ftp服务器的路径
     */
    private static void createDir(String remotePath) {
        String[] dirs = remotePath.split("/");
        StringBuffer currentDirectory = new StringBuffer(File.separator);
        for (String dir : dirs) {
            currentDirectory = currentDirectory.append(dir).append(File.separator);
            try {
                if (ftpClient.makeDirectory(String.valueOf(currentDirectory))) {
                    log.info("创建文件夹[{}]成功", dir);
                }
            } catch (IOException e) {
                log.error("创建文件夹异常");
                e.printStackTrace();
            }
        }
    }
}
