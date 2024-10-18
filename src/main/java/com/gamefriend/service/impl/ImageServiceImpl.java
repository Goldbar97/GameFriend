package com.gamefriend.service.impl;

import com.gamefriend.exception.CustomException;
import com.gamefriend.exception.ErrorCode;
import com.gamefriend.service.ImageService;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

  private final S3Client s3Client;
  private final List<String> TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif",
      "image/bmp", "image/tiff", "image/webp", "image/svg+xml", "image/heif", "image/heic");

  @Value("${cloud.vultr.bucket.name}")
  private String bucketName;

  @Value("${cloud.vultr.file.size}")
  private long fileSize;

  @Override
  public String uploadProfileImage(MultipartFile file) {

    try {
      if (!TYPES.contains(file.getContentType())) {
        throw new CustomException(ErrorCode.WRONG_FILE_TYPE);
      }

      if (file.getSize() > fileSize) {
        throw new CustomException(ErrorCode.FILE_TOO_BIG);
      }

      String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(fileName)
          .acl(ObjectCannedACL.PUBLIC_READ)
          .contentType(file.getContentType())
          .build();

      s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

      return s3Client.utilities().getUrl(b -> b.bucket(bucketName).key(fileName)).toExternalForm();
    } catch (Exception e) {
      e.printStackTrace();
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public void deleteProfileImage(String url) {

    String key = retrieveKey(url);

    DeleteObjectRequest request = DeleteObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    s3Client.deleteObject(request);
  }

  private String retrieveKey(String url) {

    String key =  url.substring(url.lastIndexOf("/") + 1);

    System.out.println(key);
    return key;
  }
}