package com.example.filemanager.controller;

import com.example.filemanager.model.Data;
import com.example.filemanager.model.FileManagerResponse;
import com.example.filemanager.service.impl.FileManagerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class FileManagerController {

    private final static Logger logger = LoggerFactory.getLogger(FileManagerController.class);
    public static final String FILE_UPLOAD_SUCCESS_MSG = "Successfully uploaded - %s";

    private FileManagerServiceImpl service;

    @Autowired
    public FileManagerController(FileManagerServiceImpl service) {
        this.service = service;
    }

    @PostMapping("/filemanager/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile uploadfile) {

        logger.debug("Single file upload!");

        if (uploadfile.isEmpty()) {
            return new ResponseEntity("please select a file!", HttpStatus.OK);
        }

        try {

            service.saveUploadedFiles(Arrays.asList(uploadfile));

        } catch (IOException | ParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info(String.format(FILE_UPLOAD_SUCCESS_MSG,uploadfile.getOriginalFilename()));
        return new ResponseEntity(String.format(FILE_UPLOAD_SUCCESS_MSG,uploadfile.getOriginalFilename()), new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/filemanager/data")
    public ResponseEntity<FileManagerResponse> getData(@RequestParam(required = false) String code) {
        List<Data> dataList = new ArrayList<>();
        FileManagerResponse response = new FileManagerResponse();
        try {
            if(null != code) {
                Data dataById = (Data) service.getByCode(code);
                dataList.add(dataById);
            } else {
                dataList = service.getAll();
            }
            response.setData(dataList);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NoSuchElementException noSuchElementException) {
            logger.error(noSuchElementException.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
