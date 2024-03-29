package xyz.subho.retail.banking.controller;

//-------------------------old imports----------------------------

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import xyz.subho.retail.banking.model.CurrentAccount;
import xyz.subho.retail.banking.model.CurrentTransaction;
import xyz.subho.retail.banking.model.SavingsAccount;
import xyz.subho.retail.banking.model.SavingsTransaction;
import xyz.subho.retail.banking.model.User;
import xyz.subho.retail.banking.service.AccountService;
import xyz.subho.retail.banking.service.TransactionService;
import xyz.subho.retail.banking.service.UserService;

import org.springframework.web.bind.annotation.CrossOrigin;
//-------------------------new imports----------------------------
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.HashMap;

//@Controller
@CrossOrigin(origins = "http://localhost:3000") // Replace with your frontend app's URL
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @RequestMapping({"/", ""})
    public RedirectView redirectAccount() {
        return new RedirectView("/account/currentAccount");
    }

    //-------------------------new methods----------------------------

    @GetMapping("/currentAccount")
    public ResponseEntity<?> currentAccount(Principal principal) {
        List<CurrentTransaction> currentTransactionList = transactionService.findCurrentTransactionList(principal.getName());
        User user = userService.findByUsername(principal.getName());
        CurrentAccount currentAccount = user.getCurrentAccount();

        Map<String, Object> response = new HashMap<>();
        response.put("currentAccount", currentAccount);
        response.put("currentTransactionList", currentTransactionList);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/savingsAccount")
    public ResponseEntity<?> savingsAccount(Principal principal) {
        
    	List<SavingsTransaction> savingsTransactionList = transactionService.findSavingsTransactionList(principal.getName());
        User user = userService.findByUsername(principal.getName());
        SavingsAccount savingsAccount = user.getSavingsAccount();

        Map<String, Object> response = new HashMap<>();
        response.put("savingsAccount", savingsAccount);
        response.put("savingsTransactionList", savingsTransactionList);
        return ResponseEntity.ok(response);
        
    }

    // DTO classes for deposit and withdraw requests
    static class TransactionRequest {
        public String accountType;
        public double amount;
    }

    // @GetMapping("/deposit")
    // public ResponseEntity<?> deposit() {
    //     // You can return default values or necessary info for the deposit
    //     return ResponseEntity.ok(new TransactionRequest());
    // }

    // @PostMapping("/deposit")
    // public ResponseEntity<?> depositPOST(@RequestBody TransactionRequest request, Principal principal) {
    //     accountService.deposit(request.accountType, request.amount, principal);
    //     // Return a confirmation message or relevant data
    //     return ResponseEntity.ok("Deposit successful");
    // }

    // @GetMapping("/withdraw")
    // public ResponseEntity<?> withdraw() {
    //     // You can return default values or necessary info for the withdraw
    //     return ResponseEntity.ok(new TransactionRequest());
    // }

    // @PostMapping("/withdraw")
    // public ResponseEntity<?> withdrawPOST(@RequestBody TransactionRequest request, Principal principal) {
    //     accountService.withdraw(request.accountType, request.amount, principal);
    //     // Return a confirmation message or relevant data
    //     return ResponseEntity.ok("Withdrawal successful");
    // }

    //-------------------------old methods----------------------------

    @RequestMapping("/currentAccount")
    public String currentAccount(Model model, Principal principal) {
       
    	List<CurrentTransaction> currentTransactionList = transactionService.findCurrentTransactionList(principal.getName());

        User user = userService.findByUsername(principal.getName());
        CurrentAccount currentAccount = user.getCurrentAccount();

        model.addAttribute("currentAccount", currentAccount);
        model.addAttribute("currentTransactionList", currentTransactionList);

        return "currentAccount";
        
    }

    @RequestMapping("/savingsAccount")
    public String savingsAccount(Model model, Principal principal) {
        
    	List<SavingsTransaction> savingsTransactionList = transactionService.findSavingsTransactionList(principal.getName());
        User user = userService.findByUsername(principal.getName());
        SavingsAccount savingsAccount = user.getSavingsAccount();

        model.addAttribute("savingsAccount", savingsAccount);
        model.addAttribute("savingsTransactionList", savingsTransactionList);

        return "savingsAccount";
        
    }

    @RequestMapping(value = "/deposit", method = RequestMethod.GET)
    public String deposit(Model model) {
        
    	model.addAttribute("accountType", "");
        model.addAttribute("amount", "");

        return "deposit";
        
    }

    @RequestMapping(value = "/deposit", method = RequestMethod.POST)
    public String depositPOST(@ModelAttribute("amount") String amount, @ModelAttribute("accountType") String accountType, Principal principal) {
        
    	accountService.deposit(accountType, Double.parseDouble(amount), principal);

        return "redirect:/userFront";
        
    }

    @RequestMapping(value = "/withdraw", method = RequestMethod.GET)
    public String withdraw(Model model) {
    	
        model.addAttribute("accountType", "");
        model.addAttribute("amount", "");

        return "withdraw";
        
    }

    @RequestMapping(value = "/withdraw", method = RequestMethod.POST)
    public String withdrawPOST(@ModelAttribute("amount") String amount, @ModelAttribute("accountType") String accountType, Principal principal) {
        
    	accountService.withdraw(accountType, Double.parseDouble(amount), principal);

        return "redirect:/userFront";
        
    }
    
}