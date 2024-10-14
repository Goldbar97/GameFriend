package com.gamefriend.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

  String uploadProfileImage(MultipartFile file);
}