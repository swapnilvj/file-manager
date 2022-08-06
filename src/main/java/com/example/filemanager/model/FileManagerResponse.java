package com.example.filemanager.model;

import java.util.List;

public class FileManagerResponse {
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}
