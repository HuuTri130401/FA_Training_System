package com.fptacademy.training.service.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.fptacademy.training.exception.ResourceBadRequestException;
import com.fptacademy.training.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Component
public class S3UploadFileUtil {

    private final AmazonS3 s3Client;

    @Value("${amazon.s3.bucketName}")
    private String bucketName;

    @Value("${resource.file.upload}")
    private String rootFile;

    public boolean isS3ClientValid() {
        try {
            List<Bucket> buckets = s3Client.listBuckets();
            for (Bucket b : buckets) {
                if(b.getName().equals(bucketName)){
                    return true;
                }
            }
            return false;
        } catch (AmazonS3Exception e) {
            return false;
        }
    }

    public Object handleFileUpload(MultipartFile file, String type) {
        if(isS3ClientValid()) {
            try {
                // Generate a unique filename for the uploaded file
                String filename = rootFile + Instant.now() + file.getOriginalFilename();
                // Upload the file to S3
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(file.getContentType());
                metadata.setContentLength(file.getSize());
                metadata.addUserMetadata(type, filename);
                PutObjectRequest putRequest = new PutObjectRequest(bucketName, filename, file.getInputStream(), metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead);
                ;
                PutObjectResult result = s3Client.putObject(putRequest);
                String url = s3Client.getUrl(bucketName, filename).toString();
//          Return the URL of the uploaded file
                return url;
            } catch (IOException e) {
                throw new ResourceBadRequestException("S3UploadFileUtil-HandleFileUpload-FAIL");
            }
        } else {
                return null;
//            throw new ResourceNotFoundException("S3UploadFileUtil Fail. Please check bucket name, access key, secret key");
        }
    }
}
