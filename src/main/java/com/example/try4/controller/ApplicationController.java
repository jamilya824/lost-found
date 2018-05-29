package com.example.try4.controller;


import com.example.try4.dao.AppUserDAO;
import com.example.try4.dao.ApplicationDAO;
import com.example.try4.dao.CommentDAO;
import com.example.try4.entity.Application;
import com.example.try4.entity.Comment;
import com.example.try4.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.List;

@Controller
@Transactional
public class ApplicationController {

    @Autowired
    StorageService storageService;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private AppUserDAO appUserDAO;

    @Autowired
    private CommentDAO commentDAO;

    @RequestMapping(value = "/newApplication", method = RequestMethod.GET)
    public String viewRegister(Model model) {
        Application form = new Application();
        model.addAttribute("app", form);
        List<String> list = form.getCategoties();
        model.addAttribute("categories", list);

        return "formpage";
    }

    @RequestMapping(value = "/newApplication", method = RequestMethod.POST)
    public String saveRegister( @ModelAttribute("app")@Valid Application app,
                                BindingResult result,Model model, //
                              Principal principal, @RequestParam(name = "file1", required = false)MultipartFile file1,
                               @RequestParam(name = "file2", required = false)MultipartFile file2, @RequestParam(name = "file3", required = false)MultipartFile file3) {
        if (result.hasErrors()) {
            model.addAttribute("app", app);
            List<String> list = app.getCategoties();
            model.addAttribute("categories", list);
            return "formpage";
        }
        try {
            app=storageService.preStore(file1,file2,file3,app);
            appUserDAO.findAppUserByUserName(principal.getName());
            app.setUsarname(principal.getName());
            app.setImage(appUserDAO.findAppUserByUserName(principal.getName()).getUrlImage());
            applicationDAO.addApp(app);
        } catch (Exception e) {

            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            model.addAttribute("app", app);
            List<String> list = app.getCategoties();
            model.addAttribute("categories", list);
            model.addAttribute("message","there is already exist such image");
            return "formpage";
        }

        return "redirect:/";
    }
    @GetMapping("/image/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        Resource file = storageService.loadFile(filename);
        String mimeType = "";
        try {
            mimeType = Files.probeContentType(file.getFile().toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + file.getFilename() + "\"")

                .body(file);
    }
    @GetMapping("/avatar/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getAvatar(@PathVariable String filename) {
        Resource file = storageService.loadAvatar(filename);
        String mimeType = "";
        try {
            mimeType = Files.probeContentType(file.getFile().toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + file.getFilename() + "\"")

                .body(file);
    }
    @RequestMapping("/appInfo")
    public String showApplications(Model model,@RequestParam("id")long id){
        Application ap=applicationDAO.findAppId(id);
        System.out.println(ap.getApId()+"ghbgbb");
        System.out.println(ap.getUsarname()+"hffjgbdjnd");
        ap.setView(ap.getView()+1);
        ap=applicationDAO.addApp(ap);
        List<Comment> list=commentDAO.findComment(id);
        List<Application> popular=applicationDAO.findPopular();
        model.addAttribute("comments",list);
        model.addAttribute("app",ap);
        model.addAttribute("popular",popular);
        Comment comment=new Comment();
        model.addAttribute("comment",comment);
        return "single-post-2";
    }
    @RequestMapping(value = "/newComment", method = RequestMethod.POST)
    public String saveComment(@ModelAttribute ("comment") @Valid Comment comment, BindingResult result,Principal principal,@RequestParam("appId") long appId){
        if (result.hasErrors()) {
            return "redirect:/appInfo?id="+appId;
        }
        comment.setUsername(principal.getName());
        comment.setId_app(appId);
        comment.setImage(appUserDAO.findAppUserByUserName(principal.getName()).getUrlImage());
        commentDAO.addComment(comment);
        applicationDAO.updateCommentNum(comment.getId_app(),1);
        return "redirect:/appInfo?id="+appId;
    }
    @RequestMapping("/deleteComment")
    public String deleteComment(@RequestParam("id") long id,@RequestParam("apId") long appId){
        applicationDAO.updateCommentNum(appId,-1);
        commentDAO.deleteComment(id);
        return "redirect:/appInfo?id="+appId;
    }

    @RequestMapping("/deleteApp")
    public String deleteApp(@RequestParam("id") long id){
        applicationDAO.deleteApp(id);
        return "redirect:/";
    }
}
