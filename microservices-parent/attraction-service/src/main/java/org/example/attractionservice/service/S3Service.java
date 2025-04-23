package org.example.attractionservice.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.bucket.name}")
    private String bucketName;

    public String uploadFile(MultipartFile file) {
        try{
            String path = "travelpoints_audio_files/";
            String filename = path + file.getOriginalFilename();
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(filename)
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes())
            );
            return filename;
        }
        catch(IOException e){
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public List<Object> listFiles() {
        ListObjectsV2Response listObjects = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build());
        List<Object> fileList = listObjects.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
        return fileList;
    }

    public String getFileUrl(String fileName) {
        String url = s3Client.utilities().getUrl(GetUrlRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build()).toString();
        return url;
    }

    public Boolean fileExists(MultipartFile file) {
        try{
            String path = "travelpoints_audio_files/";
            String filename = path + file.getOriginalFilename();
            return true;
        }
        catch(Exception e){

        }
        return false;
    }
}
