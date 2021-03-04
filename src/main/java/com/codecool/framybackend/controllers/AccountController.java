package com.codecool.framybackend.controllers;

import com.codecool.framybackend.model.Account;
import com.codecool.framybackend.model.AccountGroupWrapper;
import com.codecool.framybackend.model.Group;
import com.codecool.framybackend.repositories.AccountRepository;
import com.codecool.framybackend.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private GroupRepository groupRepository;

    @GetMapping("/accounts")
    @CrossOrigin("*")
    public List<Account> getAccounts(){
        return accountRepository.findAll();
    }

    @CrossOrigin("*")
    @PostMapping("/account")
    public Account insertAccount(@RequestBody(required = true)Account account){
        if ( account != null) {
            return accountRepository.save(account);
        }
        return null;
    }

    @CrossOrigin("*")
    @PostMapping("/add-to-group")
    public Account addToGroup(@RequestBody(required = true) AccountGroupWrapper accountGroupWrapper){
        Account account = accountRepository.findById(accountGroupWrapper.getAccount().getId()).orElse(null);
        Group group = groupRepository.findById(accountGroupWrapper.getGroup().getId()).orElse(null);
        if ( account != null && group != null){
            account.getGroups().add(group);
            group.getAccounts().add(account);
            accountRepository.save(account);
            groupRepository.save(group);
        }
        return null;
    }

}
