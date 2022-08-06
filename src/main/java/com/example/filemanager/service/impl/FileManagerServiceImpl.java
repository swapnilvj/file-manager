package com.example.filemanager.service.impl;

import com.example.filemanager.model.Data;
import com.example.filemanager.repository.DataRepository;
import com.example.filemanager.service.FileManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FileManagerServiceImpl implements FileManagerService {

    private final static Logger logger = LoggerFactory.getLogger(FileManagerServiceImpl.class);

    private DataRepository dataRepository;

    @Autowired
    public FileManagerServiceImpl(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    private List<Data> processInputFile(InputStream inputFS) {

        List<Data> inputList = new ArrayList<Data>();

        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));

            // skip the header of the csv
            inputList = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
            br.close();
            dataRepository.saveAll(inputList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputList ;
    }

    private Function<String, Data> mapToItem = (line) -> {

        String[] p = line.replaceAll("\"", "").split(",");// a CSV has comma separated lines

        Data data = new Data();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        for(int i = 0; i < p.length; i++) {
            switch (i) {
                case 0:
                    data.setSource(p[0]);
                    break;
                case 1:
                    data.setCodeListCode(p[1]);
                    break;
                case 2:
                    data.setCode(p[2]);
                    break;
                case 3:
                    data.setDisplayValue(p[3]);
                    break;
                case 4:
                    data.setLongDescription(p[4]);
                    break;
                case 5:
                    try {
                        data.setFromDate(p[5] != null && !p[5].equals("") ? formatter.parse(p[5]) : null);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case 6:
                    try {
                        data.setToDate(p[6] != null && !p[6].equals("") ? formatter.parse(p[6]) : null);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case 7:
                    data.setSortingPriority(p[7] != null ? Integer.valueOf(p[7]) : null);
                    break;
            }
        }
        return data;
    };

    @Override
    public void saveUploadedFiles(List<MultipartFile> files) throws IOException, ParseException {
        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                continue;
            }
            processInputFile(file.getInputStream());
        }


    }

    @Override
    public List<Data> getAll() {
        return (List<Data>) dataRepository.findAll();
    }

    @Override
    public Data getByCode(String code) {
        return dataRepository.findById(code).get();
    }
}
