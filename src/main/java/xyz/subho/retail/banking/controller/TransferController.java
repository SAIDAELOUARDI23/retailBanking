package xyz.subho.retail.banking.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import xyz.subho.retail.banking.model.CurrentAccount;
import xyz.subho.retail.banking.model.Recipient;
import xyz.subho.retail.banking.model.SavingsAccount;
import xyz.subho.retail.banking.model.User;
import xyz.subho.retail.banking.service.TransactionService;
import xyz.subho.retail.banking.service.UserService;
import java.util.Map;

@Controller@CrossOrigin(origins = "http://localhost:3000") // Replace with your frontend app's URL
@RestController
@RequestMapping("/transfer")
public class TransferController {

    static class TransferRequest {
        private String transferFrom;
        private String transferTo;
        private String amount;

        public String getTransferFrom() {
            return transferFrom;
        }

        public void setTransferFrom(String transferFrom) {
            this.transferFrom = transferFrom;
        }

        public String getTransferTo() {
            return transferTo;
        }

        public void setTransferTo(String transferTo) {
            this.transferTo = transferTo;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }
    }

    static class TransferToSomeoneRequest {
        private String recipientName;
        private String accountType;
        private String amount;
    
        public String getRecipientName() {
            return recipientName;
        }

        public void setRecipientName(String recipientName) {
            this.recipientName = recipientName;
        }

        public String getAccountType() {
            return accountType;
        }

        public void setAccountType(String accountType) {
            this.accountType = accountType;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }
    }

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    //-------------------------new methods----------------------------

    @GetMapping("/betweenAccounts")
    public ResponseEntity<?> betweenAccounts() {
        Map<String, String> response = new HashMap<>();
        response.put("transferFrom", "");
        response.put("transferTo", "");
        response.put("amount", "");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/betweenAccounts")
    public ResponseEntity<?> betweenAccountsPost(@RequestBody TransferRequest transferRequest, Principal principal) {
        try {
            User user = userService.findByUsername(principal.getName());
            CurrentAccount currentAccount = user.getCurrentAccount();
            SavingsAccount savingsAccount = user.getSavingsAccount();
            transactionService.betweenAccountsTransfer(transferRequest.getTransferFrom(), transferRequest.getTransferTo(), transferRequest.getAmount(), currentAccount, savingsAccount);
            return ResponseEntity.ok("Transfer successful");
        } catch (Exception e) {
            return new ResponseEntity<>("Error during transfer", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/recipient")
    public ResponseEntity<?> recipient(Principal principal) {
        List<Recipient> recipientList = transactionService.findRecipientList(principal);
        return ResponseEntity.ok(recipientList);
    }

    @PostMapping("/recipient/save")
    public ResponseEntity<?> recipientPost(@RequestBody Recipient recipient, Principal principal) {
        try {
            User user = userService.findByUsername(principal.getName());
            recipient.setUser(user);
            transactionService.saveRecipient(recipient);
            return ResponseEntity.ok("Recipient saved successfully");
        } catch (Exception e) {
            return new ResponseEntity<>("Error saving recipient", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @GetMapping("/recipient/edit")
    public ResponseEntity<?> recipientEdit(@RequestParam(value = "recipientName") String recipientName, Principal principal) {
        Recipient recipient = transactionService.findRecipientByName(recipientName);
        List<Recipient> recipientList = transactionService.findRecipientList(principal);
        Map<String, Object> response = new HashMap<>();
        response.put("recipient", recipient);
        response.put("recipientList", recipientList);
        return ResponseEntity.ok(response);
    }
        
    @GetMapping("/recipient/delete")
    @Transactional
    public ResponseEntity<?> recipientDelete(@RequestParam(value = "recipientName") String recipientName, Principal principal) {
        transactionService.deleteRecipientByName(recipientName);
        List<Recipient> recipientList = transactionService.findRecipientList(principal);
        return ResponseEntity.ok(recipientList);
    }

    @GetMapping("/toSomeoneElse")
    public ResponseEntity<?> toSomeoneElse(Principal principal) {
        List<Recipient> recipientList = transactionService.findRecipientList(principal);
        return ResponseEntity.ok(recipientList);
    }

    @PostMapping("/toSomeoneElse")
    public ResponseEntity<?> toSomeoneElsePost(@RequestBody TransferToSomeoneRequest request, Principal principal) {
        try {
            User user = userService.findByUsername(principal.getName());
            Recipient recipient = transactionService.findRecipientByName(request.getRecipientName());
            transactionService.toSomeoneElseTransfer(recipient, request.getAccountType(), request.getAmount(), user.getCurrentAccount(), user.getSavingsAccount());
            return ResponseEntity.ok("Transfer successful");
        } catch (Exception e) {
            return new ResponseEntity<>("Error during transfer", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }


    //-------------------------old methods----------------------------

    // @RequestMapping(value = "/betweenAccounts", method = RequestMethod.GET)
    // public String betweenAccounts(Model model) {
    	
    //     model.addAttribute("transferFrom", "");
    //     model.addAttribute("transferTo", "");
    //     model.addAttribute("amount", "");

    //     return "betweenAccounts";
        
    // }

    // @RequestMapping(value = "/betweenAccounts", method = RequestMethod.POST)
    // public String betweenAccountsPost(
    //         @ModelAttribute("transferFrom") String transferFrom,
    //         @ModelAttribute("transferTo") String transferTo,
    //         @ModelAttribute("amount") String amount,
    //         Principal principal
    // ) throws Exception {
    	
    //     User user = userService.findByUsername(principal.getName());
    //     CurrentAccount currentAccount = user.getCurrentAccount();
    //     SavingsAccount savingsAccount = user.getSavingsAccount();
    //     transactionService.betweenAccountsTransfer(transferFrom, transferTo, amount, currentAccount, savingsAccount);

    //     return "redirect:/userFront";
        
    // }

    // @RequestMapping(value = "/recipient", method = RequestMethod.GET)
    // public String recipient(Model model, Principal principal) {
    	
    //     List<Recipient> recipientList = transactionService.findRecipientList(principal);

    //     Recipient recipient = new Recipient();

    //     model.addAttribute("recipientList", recipientList);
    //     model.addAttribute("recipient", recipient);

    //     return "recipient";
        
    // }

    // @RequestMapping(value = "/recipient/save", method = RequestMethod.POST)
    // public String recipientPost(@ModelAttribute("recipient") Recipient recipient, Principal principal) {

    //     User user = userService.findByUsername(principal.getName());
    //     recipient.setUser(user);
    //     transactionService.saveRecipient(recipient);

    //     return "redirect:/transfer/recipient";
        
    // }

    // @RequestMapping(value = "/recipient/edit", method = RequestMethod.GET)
    // public String recipientEdit(@RequestParam(value = "recipientName") String recipientName, Model model, Principal principal) {

    //     Recipient recipient = transactionService.findRecipientByName(recipientName);
    //     List<Recipient> recipientList = transactionService.findRecipientList(principal);

    //     model.addAttribute("recipientList", recipientList);
    //     model.addAttribute("recipient", recipient);

    //     return "recipient";
        
    // }

    // @RequestMapping(value = "/recipient/delete", method = RequestMethod.GET)
    // @Transactional
    // public String recipientDelete(@RequestParam(value = "recipientName") String recipientName, Model model, Principal principal) {

    //     transactionService.deleteRecipientByName(recipientName);

    //     List<Recipient> recipientList = transactionService.findRecipientList(principal);

    //     Recipient recipient = new Recipient();
    //     model.addAttribute("recipient", recipient);
    //     model.addAttribute("recipientList", recipientList);

    //     return "recipient";
        
    // }

    // @RequestMapping(value = "/toSomeoneElse", method = RequestMethod.GET)
    // public String toSomeoneElse(Model model, Principal principal) {
    	
    //     List<Recipient> recipientList = transactionService.findRecipientList(principal);

    //     model.addAttribute("recipientList", recipientList);
    //     model.addAttribute("accountType", "");

    //     return "toSomeoneElse";
        
    // }

    // @RequestMapping(value = "/toSomeoneElse", method = RequestMethod.POST)
    // public String toSomeoneElsePost(@ModelAttribute("recipientName") String recipientName, @ModelAttribute("accountType") String accountType, @ModelAttribute("amount") String amount, Principal principal) {
    	
    //     User user = userService.findByUsername(principal.getName());
    //     Recipient recipient = transactionService.findRecipientByName(recipientName);
    //     transactionService.toSomeoneElseTransfer(recipient, accountType, amount, user.getCurrentAccount(), user.getSavingsAccount());

    //     return "redirect:/userFront";
        
    // }
    
}