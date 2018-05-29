package com.example.try4.controller;

import com.example.try4.dao.AppUserDAO;
import com.example.try4.dao.ApplicationDAO;
import com.example.try4.dao.PostDAO;
import com.example.try4.entity.AppUser;
import com.example.try4.entity.Application;
import com.example.try4.entity.Post;
import com.example.try4.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@Transactional
public class UserController {

    @Autowired
    StorageService storageService;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private AppUserDAO appUserDAO;

    @Autowired
    private PostDAO postDAO;

    @RequestMapping("/userPage")
    public String showUser(Model model, @RequestParam("username")String username){
        AppUser user=appUserDAO.findAppUserByUserName(username);
        List<Application> list=applicationDAO.getUsersApplication(username);
        model.addAttribute("apps",list);
        model.addAttribute("user",user);
        model.addAttribute("post",new Post());
        model.addAttribute("posts",postDAO.findComment(username));
        return "userPage";
    }
    @RequestMapping(value = "/newPost", method = RequestMethod.POST)
    public String saveComment(@ModelAttribute("post") @Valid Post post, BindingResult result, Principal principal, @RequestParam("username") String username){
        if (result.hasErrors()) {
            return "redirect:/userPage?username="+username;
        }
        post.setUsername(principal.getName());
        post.setUser(username);
        post.setImage(appUserDAO.findAppUserByUserName(principal.getName()).getUrlImage());
        postDAO.addPost(post);
        return "redirect:/userPage?username="+username;
    }
    @RequestMapping("/deletePost")
    public String deletePost(@RequestParam("id") long id,@RequestParam("username") String username){
        postDAO.deletePost(id);
        return "redirect:/userPage?username="+username;
    }

    @RequestMapping("/deleteUser")
    public String deleteUser(@RequestParam("id") long id){
        appUserDAO.delete(id);
        return "redirect:/";
    }

    @RequestMapping("/updateUser")
    public String updateUser(@RequestParam("id") long id,Model model){
        model.addAttribute("user",appUserDAO.findAppUserByUserId(id));
        return "updateUser";
    }
    @RequestMapping(value = "/updateUser",method =RequestMethod.POST)
    public String updateUsera(@ModelAttribute("user") @Valid AppUser user){
        System.out.println(user.getDepartment());
        appUserDAO.updateUser(user);
        return "redirect:/userPage?username="+user.getUserName();
    }
}
