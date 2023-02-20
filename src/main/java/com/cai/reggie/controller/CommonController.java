package com.cai.reggie.controller;

import com.cai.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RequestMapping("/common")
@RestController
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info(file.toString());
        //使用原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID随机生成文件名，防止重复
        String fileName = UUID.randomUUID().toString()+suffix;
        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if (!dir.exists()){
            //不存在则创建
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }


    /**
     *  文件下载
     * @param response
     * @param name
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        //输入流，读取文件内容
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //输出流，文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
