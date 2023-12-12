package com.hanzaitu.common.certificate.utils;

import com.hanzaitu.common.certificate.constant.CoreConstant;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 证书操作工具类
 */
public class LicenseFileUtils {
    /**
     * 获取证书存储路径
     * @return
     */
    public static String getLicenseStorage(){
        File currentDir = new File("storage"+File.separator+ CoreConstant.LICENSE_DIR);
        return currentDir.getAbsolutePath();
    }


    /**
     * 删除证书文件
     * @param path
     */
    public static void deleteAllLicense(String path) {
        File filePar = new File(path);
        if (filePar.exists()) {
            File files[] = filePar.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile() && files[i].getName().endsWith(CoreConstant.CERT_FILE_NAME+".lic")) {
                    files[i].delete();
                }
            }
        }
    }

    /**
     * 获取文件夹下最新的证书
     * @param folderPath
     * @return
     * @throws IOException
     */
    public static String getNewestFile(String folderPath) throws IOException {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if (files.length == 0) {
            return null;
        }
        File[] filterFilesArrays = Arrays.stream(files).filter(f -> f.getName().endsWith(
                CoreConstant.CERT_FILE_NAME + ".lic")).toArray(File[]::new);
        if (filterFilesArrays.length == 0) {
            return null;
        }
        filesSort(filterFilesArrays);
        File newest = filterFilesArrays[filterFilesArrays.length -1];
        return newest.getAbsolutePath();
    }



    public static void filesSort(File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;//如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
            }
            public boolean equals(Object obj) {
                return true;
            }
        });
    }

}
