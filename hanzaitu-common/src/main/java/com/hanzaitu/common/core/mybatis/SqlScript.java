package com.hanzaitu.common.core.mybatis;

import cn.hutool.core.io.FileUtil;
import com.hanzaitu.common.core.SystemInfo;
import com.hanzaitu.common.core.result.ResultException;
import com.hanzaitu.common.utils.SpringApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 执行sql脚本
 *
 * @author
 * @date 2023-04-24 13:25
 **/
@Slf4j
public class SqlScript {
    private final String CLASSPATH = "classpath:";

    public SystemInfo systemInfo;

    public JdbcTemplate jdbcTemplate;

    public SqlScript(SystemInfo systemInfo, JdbcTemplate jdbcTemplate) {
        this.systemInfo = systemInfo;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 执行指定版本的sql脚本
     * 类路径：classpath:
     * 文件绝对路径：/home/spd/data/sql
     */
    public void exec(String version){
        String fileName = version+".sql";
        // 获取已更新脚本
        String updatedSqlPath = getSqlAbsolutePath(systemInfo.getUpdatedSqlPath());
        // 检查脚本文件是否已存在，如果已存在，则认为脚本文件已执行，直接返回, 如果不是线上模式，则每次都执行
        File updatedSqlFile = new File(updatedSqlPath, fileName);
        if(updatedSqlFile.exists() && SpringApplicationUtils.isOnlineMode()){
            log.info("{} 脚本文件已执行", fileName);
            return;
        }
        // 获取指定版本的sql脚本
        String sqlPath = systemInfo.getSystemSqlPath();
        Resource resource = null;
        String sqlFilePath = null;

        if(sqlPath.startsWith(CLASSPATH)){
            sqlPath = sqlPath.substring(CLASSPATH.length());
            sqlFilePath = StringUtils.join(sqlPath, fileName);
            resource = new ClassPathResource(sqlFilePath);
        }else {
            sqlFilePath = StringUtils.join(sqlPath, fileName);
            resource = new FileSystemResource(sqlFilePath);
        }
        log.debug("sql path->{}", sqlFilePath);
        if(!resource.exists()){
            log.warn("sql脚本不存在 {}", sqlFilePath);
            return;
        }
        // 获取系统版本脚本文件
        List<StringBuffer> lineList = null;
        try {
            lineList = IOUtils.readLines(resource.getInputStream())
                    .stream().map(StringBuffer::new).collect(Collectors.toList());
        }catch (Exception e){
            log.error("sql脚本读失败"+sqlFilePath, e);
        }
        if(CollectionUtils.isEmpty(lineList)){
            return;
        }
        // 处理sql脚本
        List<SqlText> sqlTextList = parse(lineList);
        // 执行sql脚本
        process(lineList, sqlTextList);
        // 将执行结果写入到磁盘
        writeSql(lineList, fileName);
    }



    /**
     * 执行当前版本的sql脚本
     */
    public void execCurrentVersion(){
        String version = systemInfo.getVersion();
        exec(version);
    }

    /**
     * 解析sql文本
     * @return
     */
    private List<SqlText> parse(List<StringBuffer> lineList){
        List<SqlText> sqlTextList = new ArrayList<>();
        SqlText sqlText = new SqlText();
        StringBuffer sqlBuffer = new StringBuffer();
        for (int i = 0; i < lineList.size(); i++) {
            String line = lineList.get(i).toString().trim();
            if(StringUtils.isBlank(line) || line.startsWith("#")){
                continue;
            }
            sqlBuffer.append(line).append(" ");
            if(Objects.isNull(sqlText.startLine)){
                sqlText.startLine = i;
            }
            if(line.endsWith(";")){
                sqlText.endLine = i;
                sqlText.sql = sqlBuffer.toString();
                sqlBuffer.delete(0, sqlBuffer.length());
                sqlTextList.add(sqlText);
                sqlText = new SqlText();
            }
        }
        return sqlTextList;
    }

    /**
     * 执行sql语句
     */
    private void process(List<StringBuffer> lineList, List<SqlText> sqlTextList){
        if(CollectionUtils.isEmpty(sqlTextList)){
            return;
        }
        for (SqlText sqlText : sqlTextList) {
            try {
                jdbcTemplate.execute(sqlText.sql);
                sqlText.result = "成功";
            }catch (Exception e){
                sqlText.result = e.getMessage();
            }
            log.info("\n"+
                    sqlText.startLine+"******************\n"+
                    sqlText.sql+"\n"+
                    sqlText.result+"\n" +
                    sqlText.endLine+"******************");
            writeResult(lineList, sqlText);
        }
    }

    /**
     * 回写sql执行结果
     */
    private void writeResult(List<StringBuffer> lineList, SqlText sqlText){
        for (int i = sqlText.startLine; i <= sqlText.endLine; i++) {
            StringBuffer line = lineList.get(i);
            line.insert(0, "#");
            if(i == sqlText.endLine){
                line.append(sqlText.result);
            }
        }
    }

    private void writeSql(List<StringBuffer> lineList, String fileName){
        String filePath = getSqlAbsolutePath(systemInfo.getUpdatedSqlPath());
        try {
            org.apache.commons.io.FileUtils.forceMkdir(new File(filePath));
        } catch (IOException e) {
            throw ResultException.createResultException("目录创建失败，路径：{}", filePath);
        }
        File file = new File(filePath, fileName);
        try {
            org.apache.commons.io.FileUtils.writeLines(file, lineList);
        } catch (IOException e) {
            log.warn("sql file write file ："+file.getAbsolutePath(), e);
        }
    }

    private String getSqlAbsolutePath(String filePath){
        if(!filePath.startsWith("/")){
            ApplicationHome h = new ApplicationHome(getClass());
            String parentPath = h.getDir().getAbsolutePath();
            if(Objects.nonNull(h.getSource())){
                parentPath = h.getSource().getParentFile().getAbsolutePath();
            }
            log.debug("project path->{}", parentPath);
            filePath = StringUtils.join(parentPath, filePath);
        }
        log.debug("sql path->{}", filePath);
        return filePath;
    }

    private static class SqlText{

        /**
         * 开始行号
         */
        private Integer startLine;

        /**
         * 结束行号
         */
        private Integer endLine;

        /**
         * sql语句
         */
        private String sql ;

        /**
         * sql执行结果
         */
        private String result;
    }


}
