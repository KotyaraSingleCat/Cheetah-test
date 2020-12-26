package com.ncedu.cheetahtest.service.cloud;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ncedu.cheetahtest.exception.helpers.FailedFileUploadingException;
import com.ncedu.cheetahtest.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonClientService {
    private AmazonS3 s3client;
    private final UserService userService;
    private static final String USER_PHOTO_PASS = "userPhoto/";
    private static final String SCREENSHOT_PASS = "screenshots/";

    @Value("${cloud.aws.endpointUrl}")
    private String endpointUrl;
    @Value("${cloud.aws.bucketName}")
    private String bucketName;
    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3client = new AmazonS3Client(credentials);
    }

    // its maximum permitted size of 20971520 bytes.
    public String uploadUserPhoto(MultipartFile multipartFile, int id) {
        String fileUrl;
        try {
            File file = convertMultiPartToFile(multipartFile);
            fileUrl = getUrl(file, generateFileName(multipartFile), USER_PHOTO_PASS);

            userService.setUserPhotoUrl(id, fileUrl);
        } catch (Exception e) {
            log.error(e.toString());
            throw new FailedFileUploadingException();
        }
        return fileUrl;
    }

    // its maximum permitted size of 20971520 bytes.
    public String uploadScreenshot(File file) {
        String fileUrl;
        try {
            fileUrl = getUrl(file, generateFileName(file), SCREENSHOT_PASS);
        } catch (Exception e) {
            log.error(e.toString());
            throw new FailedFileUploadingException();
        }
        return fileUrl;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }

        return convFile;
    }

    private String getUrl(File file, String s, String userPhotoPass) {
        String fileUrl;
        fileUrl = "https://" + bucketName + endpointUrl + "/"+ userPhotoPass + s;
        uploadFileTos3bucket(userPhotoPass + s, file);

        if (file.delete()) {
            log.info(file.getName() + " deleted locally");
        } else {
            log.info("failed locally deleting file");
        }
        return fileUrl;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + Objects.requireNonNull(multiPart.getOriginalFilename()).replace(" ", "_");
    }

    private String generateFileName(File file) {
        return new Date().getTime() + "-" + file.getName().replace(" ", "_");
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public String deleteUserPhotoFromS3Bucket(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        s3client.deleteObject(new DeleteObjectRequest(bucketName, USER_PHOTO_PASS + fileName));
        return "Successfully deleted";
    }

    public String deleteScreenshotFromS3Bucket(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        s3client.deleteObject(new DeleteObjectRequest(bucketName, SCREENSHOT_PASS + fileName));
        return "Successfully deleted";
    }
}
