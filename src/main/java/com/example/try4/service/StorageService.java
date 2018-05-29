package com.example.try4.service;

import com.example.try4.entity.Application;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class StorageService {

    public static final Path rootApp = Paths.get("upload-dir");
    public static final Path avatar = Paths.get("up-avatar");
    public Application preStore(MultipartFile f1, MultipartFile f2, MultipartFile f3, Application app){
        if(!f1.isEmpty()){
            app.setPhoto1(f1.getOriginalFilename());
            store(f1);}
            else
                app.setPhoto1("lostfound-share.jpg");
        if(!f2.isEmpty()){
            app.setPhoto2(f2.getOriginalFilename());
            store(f2);}
        if(!f3.isEmpty()) {
            app.setPhoto3(f3.getOriginalFilename());
            store(f3);}
        return app;
    }

    public void store(MultipartFile file){
        try {
            Files.copy(file.getInputStream(), this.rootApp.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            throw new RuntimeException("FAIL! Such file already exist");
        }
    }

    public Resource loadFile(String filename) {
        try {
            Path file = rootApp.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }else{
                throw new RuntimeException("No such file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("FAIL!");
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootApp.toFile());
    }

    public void init() {
        try {
            Files.createDirectory(rootApp);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage!");
        }
    }
    public void saveAvatar(MultipartFile file){
        try {
            Files.copy(file.getInputStream(), this.avatar.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            throw new RuntimeException("FAIL! Such file already exist");
        }
    }
    public Resource loadAvatar(String filename) {
        try {
            Path file = avatar.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }else{
                throw new RuntimeException("cannot load such avatar!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("FAIL!");
        }
    }
}