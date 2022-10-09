package com.mumomu.exquizme.production.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        //System.out.println("multipart : " + multipartFile.toString());
        //System.out.println("dirname : " + dirName);
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));

        return upload(uploadFile, dirName);
    }

    private String upload(File uploadFile, String dirName) {
        String fileName = dirName + "/" + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        //System.out.println("uploadImageUrl : " + uploadImageUrl);
        return uploadImageUrl;
    }

    public String upload(String url, String dirName) throws Exception {
        try {
            String format = "png";
            BufferedImage image = ImageIO.read(new URL(url));
            File file = new File(UUID.randomUUID() + ".png");
            ImageIO.write(image, format, file);

            return upload(file, dirName);
        } catch (Exception e) {
            throw new Exception();
        }
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        //System.out.println(file.getOriginalFilename());
        File convertFile = new File(UUID.randomUUID() + "-" + file.getOriginalFilename());
        //System.out.println("convertFile : " + convertFile);
        if(convertFile.createNewFile()) {
        //    System.out.println("In if statement");
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
        //        System.out.println("fileoutputstream : " + fos);
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        //System.out.println("Out of if statement");
        return Optional.empty();
    }
}
