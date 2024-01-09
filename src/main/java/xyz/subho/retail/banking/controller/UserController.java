package xyz.subho.retail.banking.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import xyz.subho.retail.banking.model.User;
import xyz.subho.retail.banking.service.UserService;

@Controller@CrossOrigin(origins = "http://localhost:3000") // Replace with your frontend app's URL
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    //-------------------------new methods----------------------------

    @GetMapping("/profile")
    public ResponseEntity<?> profile(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/profile")
    public ResponseEntity<?> profilePost(@RequestBody User newUser, Principal principal) {
        try {
            User user = userService.findByUsername(principal.getName());
            user.setUsername(newUser.getUsername());
            user.setFirstName(newUser.getFirstName());
            user.setLastName(newUser.getLastName());
            user.setAadhaarId(newUser.getAadhaarId());
            user.setEmail(newUser.getEmail());
            user.setPhone(newUser.getPhone());

            userService.saveUser(user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating profile", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //-------------------------old methods----------------------------

    // @GetMapping("/profile")
    // public String profile(Principal principal, Model model) {
    	
    //     User user = userService.findByUsername(principal.getName());

    //     model.addAttribute("user", user);

    //     return "profile";
        
    // }

    // @PostMapping("/profile")
    // public String profilePost(@ModelAttribute("user") User newUser, Model model) {
    	
    //     User user = userService.findByUsername(newUser.getUsername());
    //     user.setUsername(newUser.getUsername());
    //     user.setFirstName(newUser.getFirstName());
    //     user.setLastName(newUser.getLastName());
    //     user.setAadhaarId(newUser.getAadhaarId());
    //     user.setEmail(newUser.getEmail());
    //     user.setPhone(newUser.getPhone());

    //     model.addAttribute("user", user);

    //     userService.saveUser(user);

    //     return "profile";
        
    // }

}
