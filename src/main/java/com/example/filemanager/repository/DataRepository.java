package com.example.filemanager.repository;

import com.example.filemanager.model.Data;
import org.springframework.data.repository.CrudRepository;

public interface DataRepository extends CrudRepository<Data, String> {
}
