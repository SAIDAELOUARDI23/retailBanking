package xyz.subho.retail.banking.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;

import xyz.subho.retail.banking.dao.RoleDao;
import xyz.subho.retail.banking.model.CurrentAccount;
import xyz.subho.retail.banking.model.SavingsAccount;
import xyz.subho.retail.banking.model.User;
import xyz.subho.retail.banking.security.UserRole;
import xyz.subho.retail.banking.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//---------------------------new imports--------------------------
import java.util.Map;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = "http://localhost:3000") // Replace with your frontend app's URL
@Controller
//@RestController
public class HomeController implements ErrorController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RoleDao roleDao;

    //------------------------------------new methods--------------------------

    //prob don't need these
    // @GetMapping("/")
    // public ResponseEntity<?> home() {
    //     // Assuming you want to return some base API information
    //     return ResponseEntity.ok(Map.of("message", "Welcome to the API"));
    // }

    // @GetMapping("/index")
    // public ResponseEntity<?> index() {
    //     // Can be used as a health check endpoint
    //     return ResponseEntity.ok(Map.of("status", "API is up and running"));
    // }

    // @GetMapping("/signup")
    // public ResponseEntity<?> signup() {
    //     // If there's any public information to be sent for signup, otherwise just return a success status.
    //     return ResponseEntity.ok(Map.of("message", "Signup endpoint hit. Proceed to POST to /signup for account creation."));
    // }

    @PostMapping("/signup")
    public ResponseEntity<?> signupPost(@RequestBody User user) {
        if (userService.checkUserExists(user.getUsername(), user.getEmail())) {
            // Construct a response entity with a message about the issue
            String message = "";
            if (userService.checkEmailExists(user.getEmail())) {
                message += "Email already exists. ";
            }
            if (userService.checkUsernameExists(user.getUsername())) {
                message += "Username already exists. ";
            }
            return ResponseEntity.badRequest().body(message.trim());
        } else {
            try {
                logger.info("Creating a new HashSet for userRoles");
                Set<UserRole> userRoles = new HashSet<>();

                logger.info("Adding a new UserRole to userRoles" + roleDao.findByName("ROLE_USER"));
                userRoles.add(new UserRole(user, roleDao.findByName("ROLE_USER")));

                logger.info("Calling userService.createUser" + user + userRoles);
                userService.createUser(user, userRoles);
                // Return a success response entity
                return ResponseEntity.ok("User registered successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                // Return a response entity indicating an error
                return new ResponseEntity<>("An error occurred during registration.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @GetMapping("/userFront")
    public ResponseEntity<?> userFront(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user != null) {
            CurrentAccount currentAccount = user.getCurrentAccount();
            SavingsAccount savingsAccount = user.getSavingsAccount();
            
            Map<String, Object> model = new HashMap<>();
            model.put("currentAccount", currentAccount);
            model.put("savingsAccount", savingsAccount);

            return ResponseEntity.ok(model);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/error")
    public ResponseEntity<?> error() {
        return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(Exception.class)
        public ResponseEntity<?> handleException(Exception e) {
            // Log the exception details
            return new ResponseEntity<>("An unexpected error occurred" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // You can add more exception handlers here for specific exceptions
    }


    //--------------------------------old methods--------------------------------

    @RequestMapping("/")
    public String home() {
    	
        return "redirect:/index";
        
    }

    @RequestMapping("/index")
    public String index() {
    	
        return "index";
        
    }

    @GetMapping("/signup")
    public String signup(Model model) {
    	
        User user = new User();

        model.addAttribute("user", user);

        return "signup";
        
    }

    // @SuppressWarnings("finally")
	// @PostMapping("/signup")
    // public String signupPost(@ModelAttribute("user") User user, Model model) {

    //     if (userService.checkUserExists(user.getUsername(), user.getEmail())) {

    //         if (userService.checkEmailExists(user.getEmail())) {
            	
    //             model.addAttribute("emailExists", true);
                
    //         }

    //         if (userService.checkUsernameExists(user.getUsername())) {
            	
    //             model.addAttribute("usernameExists", true);
                
    //         }

    //         return "signup";
            
    //     } else {
    //     	try	{
        	
	//             logger.info("Creating a new HashSet for userRoles");
    //             Set<UserRole> userRoles = new HashSet<>();

    //             logger.info("Adding a new UserRole to userRoles" + roleDao.findByName("ROLE_USER"));
    //             userRoles.add(new UserRole(user, roleDao.findByName("ROLE_USER")));

    //             logger.info("Calling userService.createUser" + user + userRoles);
    //             userService.createUser(user, userRoles);
    //     	}
    //     	catch(Exception e)	{
    //     		model.addAttribute("errorSignUp", true);
    //     		e.printStackTrace();
    //     	}
    //     	finally	{
    //     		model.addAttribute("successSignUp", true);
    //     		return "redirect:/";
    //     	}
            
    //     }
        
    // }

    // @GetMapping("/userFront")
    // public String userFront(Principal principal, Model model) {
    	
    //     User user = userService.findByUsername(principal.getName());
    //     CurrentAccount currentAccount = user.getCurrentAccount();
    //     SavingsAccount savingsAccount = user.getSavingsAccount();

    //     model.addAttribute("currentAccount", currentAccount);
    //     model.addAttribute("savingsAccount", savingsAccount);
	// 	/* model.addAttribute("user", user); */

    //     return "userFront";
        
    // }
    
    // @RequestMapping(value = PATH)
    // public String error() {
    //     return "error";
    // }

    // @Override
    // public String getErrorPath() {
    //     return PATH;
    // }
}
