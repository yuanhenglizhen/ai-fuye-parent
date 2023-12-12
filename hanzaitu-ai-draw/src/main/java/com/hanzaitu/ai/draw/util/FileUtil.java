package com.hanzaitu.ai.draw.util;

import com.hanzaitu.ai.draw.support.Task;
import com.hanzaitu.common.utils.RandomUtil;
import com.hanzaitu.common.utils.StringUtils;
import com.hanzaitu.common.utils.file.ImageUtils;
import com.hanzaitu.common.utils.uuid.UUID;
import eu.maxschuster.dataurl.*;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;
import retrofit2.http.Url;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    // 图片允许的后缀扩展名
    public static String[] IMAGE_FILE_EXTD = new String[] { "png", "jpg", "jpeg", "webp"};


    public static boolean isFileAllowed(String fileName) {
        for (String ext : IMAGE_FILE_EXTD) {
            if (ext.equals(fileName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 根据url获取文件名
     * @param urls
     * @return
     * @throws MalformedURLException
     */
    public static String getFileNameByUrl(String urls) throws MalformedURLException {
        URL url = new URL(urls);
        return FilenameUtils.getName(url.getPath());
    }



    /**
     * 从文件路径得到文件名。
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        File file = new File(filePath);
        return file.getName();
    }


    /**
     *  通过图片路径将图片文件转化为字符数组
     *
     * @param url 图片路径
     * @return byte[]
     */
    public static byte[] imageToBytes(String url){
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File(url));
            ImageIO.write(bufferedImage,"jpg",byteOutput);
            return byteOutput.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (byteOutput != null)
                    byteOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 判断指定的文件是否存在。
     *
     * @param fileName
     * @return
     */
    public static boolean isFileExist(String fileName) {
        return new File(fileName).isFile();
    }

    /**
     * 获取当前工作路径
     * @return
     */
    public static String getAbsolutePath(String dir){
        File currentDir = new File("storage"+File.separator+dir);
        return currentDir.getAbsolutePath();
    }

    /**
     * 创建指定的目录。 如果指定的目录的父目录不存在则创建其目录书上所有需要的父目录。
     * 注意：可能会在返回false的时候创建部分父目录。
     *
     * @param file
     * @return
     */
    public static boolean makeDirectory(File file) {
        File parent = file.getParentFile();
        if (parent != null) {
            return parent.mkdirs();
        }
        return false;
    }

    /**
     * 删除一个文件。
     *
     * @param filename
     * @throws IOException
     */
    public static void deleteFile(String filename) throws IOException {
        File file = new File(filename);
        if (file.isDirectory()) {
            throw new IOException("IOException -> BadInputException: not a file.");
        }
        if (!file.exists()) {
            throw new IOException("IOException -> BadInputException: file is not exist.");
        }
        if (!file.delete()) {
            throw new IOException("Cannot delete file. filename = " + filename);
        }
    }

    /**
     * @Name: downRemoteFile。
     * @Description: 下载远程文件。
     * @Parameters: remoteFileUrl，要下载的远程文件地址。
     * @Parameters: saveFileName，下载后保存的文件名。
     * @Parameters: saveDir，下载后保存的文件路径。
     * @Return: String，文件保存的地址。
     * @Version: V1.00
     * @Create Date: 2017-8-12
     */
    public static String downRemoteFile(String remoteFileUrl, String saveDir, String fileNameSuffix, String proxyHost, Integer proxyPort) {
        HttpURLConnection conn = null;
        OutputStream oputstream = null;
        InputStream iputstream = null;
        String saveFileName = "";
        try {
            saveFileName = FileUtil.getFileName(new URL(remoteFileUrl), fileNameSuffix);
        }catch (Exception e) {
            e.printStackTrace();
        }
        try {
            // 创建保存文件的目录
            File savePath = new File(saveDir);
            if (!savePath.exists()) {
                savePath.mkdirs();
            }

            // 创建保存的文件
            File file = new File(savePath + File.separator + saveFileName);
            if (file != null && !file.exists()) {
                file.createNewFile();
            }


            URL url = new URL(remoteFileUrl);
            // 将url以open方法返回的urlConnection连接强转为HttpURLConnection连接(标识一个url所引用的远程对象连接)
            // 此时cnnection只是为一个连接对象,待连接中
            // 判断是否有代理 如果有代理 就走代理
            if (StringUtils.isNotEmpty(proxyHost) && proxyPort != null) {
                conn = (HttpURLConnection) url.openConnection(new Proxy(java.net.Proxy.Type.HTTP,
                        new InetSocketAddress(proxyHost, proxyPort)));
            } else{
                conn = (HttpURLConnection) url.openConnection();
            }
            //conn.setRequestProperty("User-Agent", "NING/1.0");

            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
            // 设置是否要从 URL连接读取数据,默认为true
            conn.setDoInput(true);
            // 建立连接
            conn.connect();
            // 连接发起请求,处理服务器响应 (从连接获取到输入流)
            iputstream = conn.getInputStream();
            // 创建文件输出流，用于保存下载的远程文件
            oputstream = new FileOutputStream(file);
            //  用来存储响应数据
            byte[] buffer = new byte[4 * 1024];
            int byteRead = -1;
            //  循环读取流
            while ((byteRead = (iputstream.read(buffer))) != -1) {
                oputstream.write(buffer, 0, byteRead);
            }
            //  输出完成后刷新并关闭流
            oputstream.flush();

            String originalUrl = saveDir + File.separator + saveFileName;

            //ImageUtils.densityImg(originalUrl,);
            // 返回保存后的文件路径
            return originalUrl;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //  重要且易忽略步骤 (关闭流,切记!)
                if (iputstream != null) {
                    iputstream.close();
                }
                if (oputstream != null) {
                    oputstream.close();
                }
                // 销毁连接
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     *
     * @Title: upload
     * @Description: (将文件保存到指定的路径下)
     * @param file

     * @return
     * @date 2019年9月30日 上午10:22:31
     * @author 马超伟
     */
    public static String upload(MultipartFile file, String specifiedPath) {
        try {
            String extName = file.getOriginalFilename();
            // 获取文件后缀
            if (extName.lastIndexOf(".") <= 0) {
                throw new RuntimeException("不支持该文件类型");
            }
            extName = extName.substring(extName.lastIndexOf("."));
            String fileName = getFileName();
            // 获取文件名字
            fileName = getFileName() + extName;
            File file2 = new File(specifiedPath);
            if (!file2.exists()) {
                file2.mkdirs();
            }
            file.transferTo(new File(specifiedPath + File.separator+ fileName));
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取文件名
     * @return
     */
    public static String getFileName() {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        return uuid.toLowerCase();
    }


    public static String getFileName(URL url, String fileNameSuffix) {
        if (fileNameSuffix == null){
            fileNameSuffix="";
        }
        return RandomUtil.getRandomString(10) + "_mj_" + fileNameSuffix +"."+ FilenameUtils.getExtension(url.getPath());
    }

    /**
     * 文件名拼接
     * @param url
     * @param fileNameSuffix
     * @return
     */
    public static String getStitchingName(URL url, String fileNameSuffix) {
        if (fileNameSuffix == null){
            fileNameSuffix="";
        }
        return FilenameUtils.getBaseName(url.getPath()) + "_mj_" + fileNameSuffix +"."+ FilenameUtils.getExtension(url.getPath());
    }
    /**
     * 将图片转换成Base64编码
     * @param imgFile 待处理图片
     * @return
     */
    public static String ImgToBase64(String imgFile)  {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try
        {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        IDataUrlSerializer serializer = new DataUrlSerializer();
        DataUrl unserialized = new DataUrlBuilder()
                .setMimeType("image/png")
                .setEncoding(DataUrlEncoding.BASE64)
                .setData(data)
                .build();
        try {
            return serializer.serialize(unserialized);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //return new String(Base64.getEncoder().encode(data));
        return null;
    }


    /**
     * 将图片转换成Base64编码
     * @param imgFile 待处理图片
     * @return
     */
    public static String ImgToBase64(MultipartFile imgFile)  {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try
        {
            data = imgFile.getBytes();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        IDataUrlSerializer serializer = new DataUrlSerializer();
        DataUrl unserialized = new DataUrlBuilder()
                .setMimeType("image/png")
                .setEncoding(DataUrlEncoding.BASE64)
                .setData(data)
                .build();
        try {
            return serializer.serialize(unserialized);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //return new String(Base64.getEncoder().encode(data));
        return null;
    }


    /**
     * 获取指定文件夹下所有文件，不含文件夹里的文件
     *
     * @param dirFilePath 文件夹路径
     * @return
     */
    public List<File> getAllFile(String dirFilePath) {
        if (StringUtils.isBlank(dirFilePath))
            return null;
        return getAllFile(new File(dirFilePath));
    }


    /**
     * 获取指定文件夹下所有文件，不含文件夹里的文件
     *
     * @param dirFile 文件夹
     * @return
     */
    public List<File> getAllFile(File dirFile) {
        // 如果文件夹不存在或着不是文件夹，则返回 null
        if (Objects.isNull(dirFile) || !dirFile.exists() || dirFile.isFile())
            return null;

        File[] childrenFiles = dirFile.listFiles();
        if (Objects.isNull(childrenFiles) || childrenFiles.length == 0)
            return null;

        List<File> files = new ArrayList<>();
        for (File childFile : childrenFiles) {
            // 如果是文件，直接添加到结果集合
            if (childFile.isFile()) {
                files.add(childFile);
            }
        }
        return files;
    }

    // 获取文件创建时间
    private static Long getFileCreateTime(String filePath) {
        File file = new File(filePath);
        try {
            Path path = Paths.get(filePath);
            BasicFileAttributeView basicview = Files.getFileAttributeView(path, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
            BasicFileAttributes attr = basicview.readAttributes();
            Long lastModified = file.lastModified();
            return attr.creationTime().toMillis();
        } catch (Exception e) {
            e.printStackTrace();
            return file.lastModified();
        }
    }





    /**
     * 图片压缩
     * @param originalUrl   图片原地址
     * @param densityUrl    图片压缩后的地址
     * @return
     */
    public static String densityImg(String originalUrl, String baseDir) {
        log.debug("压缩原路径:{},压缩新路径：{}",originalUrl,baseDir);
        String densityImgPath = null;
        try {
            String reduceFilePath = baseDir+File.separator+"reduce_"+FileUtil.getFileName(originalUrl);
            switch (FilenameUtils.getExtension(originalUrl)) {
                case "png":

                    String newJpg = reduceFilePath.substring(0,reduceFilePath.length() - 3) + "jpg";
                    Thumbnails.of(originalUrl)
                            .scale(0.8)                 //尺寸压缩比例，0.8
                            .outputQuality(0.6)         //质量压缩比例
                            .toFile(newJpg);
                    densityImgPath = newJpg;
                    break;
                default:
                    Thumbnails.of(originalUrl)
                            .scale(0.8)                 //尺寸压缩比例，0.8
                            .outputQuality(0.6)         //质量压缩比例
                            .toFile(reduceFilePath);        //压缩后的文件地址
                    densityImgPath = reduceFilePath;
                    break;
            }
        } catch (Exception ioException) {
            ioException.printStackTrace();
            log.error(ioException.getMessage());
            log.debug("图片压缩失败");
        }
        return densityImgPath;
    }


    /**
     * 读取图片信息
     * @param path
     * @return
     */
    public static BufferedImage getImgSize(String path) {
        try {
            File picture = new File(path);

            if (!picture.exists()) {
                return null;
            }
            FileInputStream fileInputStream = new FileInputStream(picture);
            BufferedImage bufferedImage = ImageIO.read(fileInputStream);
            fileInputStream.close();
            return bufferedImage;
        } catch (Exception e) {
            log.error(e.getMessage());
            log.debug("图片压缩失败");
            return null;
        }
    }


    /**
     * 获取远程图片的信息
     */
    public static BufferedImage getWebImgSize(String imgUrl, String proxyHost, Integer proxyPort) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(imgUrl);
            // 将url以open方法返回的urlConnection连接强转为HttpURLConnection连接(标识一个url所引用的远程对象连接)
            // 判断是否有代理 如果有代理 就走代理
            if (StringUtils.isNotEmpty(proxyHost) && proxyPort != null) {
                conn = (HttpURLConnection) url.openConnection(new Proxy(java.net.Proxy.Type.HTTP,
                        new InetSocketAddress(proxyHost, proxyPort)));
            } else{
                conn = (HttpURLConnection) url.openConnection();
            }

            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
            // 设置是否要从 URL连接读取数据,默认为true
            conn.setDoInput(true);
            // 建立连接
            conn.connect();
            return ImageIO.read(conn.getInputStream());
        } catch (Exception e) {

            log.error(e.toString());
            return null;
        }finally {
            assert conn != null;
            conn.disconnect();
        }
    }

}
