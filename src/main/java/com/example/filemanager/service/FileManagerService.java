package com.example.filemanager.service;

import com.example.filemanager.model.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface FileManagerService {
    void saveUploadedFiles(List<MultipartFile> asList) throws IOException, ParseException;

   public List<Data> getAll();

   public Data getByCode(String code);

}
