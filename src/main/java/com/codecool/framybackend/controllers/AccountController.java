package com.codecool.framybackend.controllers;

import com.codecool.framybackend.model.Account;
import com.codecool.framybackend.model.AccountGroupWrapper;
import com.codecool.framybackend.model.Group;
import com.codecool.framybackend.repositories.AccountRepository;
import com.codecool.framybackend.repositories.GroupRepository;
import com.codecool.framybackend.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private GroupRepository groupRepository;


    /*
        /api/cookieLogin
        Finding the userId cookie, and trying to log in with it.
     */
    @CrossOrigin("*")
    @PostMapping("/api/cookielogin")
    public Account loginWithCookie ( HttpServletResponse response,HttpServletRequest request) throws Exception {
        PasswordUtils passwordUtils = new PasswordUtils();
        System.out.println(request.getHeader( "Cookie" ));
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            System.out.println("1");
             List<Cookie> cookieList = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("userId")).collect(Collectors.toList());
             if ( cookieList.size() > 0){
                 Cookie cookie = cookieList.get(0);
                 System.out.println(cookie);
                 Long id = Long.parseLong(passwordUtils.decrypt(cookie.getValue()));
                 return accountRepository.findById( id ).orElse(null);
             }
        }
        System.out.println("3");
        return null;
    }

    /*
        /api/login
        Login with user credentials
     */
    @CrossOrigin("*")
    @PostMapping("/api/login")
    public Account loginWithUserCredentials(@RequestBody(required = true) Account account) throws Exception {
        if (account != null) {
            PasswordUtils passwordUtils = new PasswordUtils();
            String password = account.getPassword();
            String encryptedPassword = passwordUtils.encrypt(password);
            System.out.println(encryptedPassword);
            return accountRepository.findAccountByCredentials(account.getUsername(),encryptedPassword, account.getEmailAddress());
        }
        return null;
    }

    /*
        /api/register
        Register with user credentials
     */
    @CrossOrigin("*")
    @PostMapping("/api/register")
    public Account registerAccount(@RequestBody(required = true) Account account,HttpServletResponse response) throws Exception {
        if ( account != null){
            PasswordUtils passwordUtils = new PasswordUtils();
            String encryptedPassword = passwordUtils.encrypt(account.getPassword());
            account.setPassword(encryptedPassword);
            if (accountRepository.getAccountsByUsernameAndEmail(account.getUsername(),account.getPassword()).size() == 0){
                Account savedAccount = accountRepository.save(account);
                Cookie cookie = new Cookie("userId", passwordUtils.encrypt(savedAccount.getId().toString()));
                cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
                cookie.setPath("/");
                response.addCookie(cookie);
                return savedAccount;
            }
        }
        System.out.println("No account");
        return null;
    }

    @GetMapping("/api/accounts")
    @CrossOrigin("*")
    public List<Account> getAccounts(){
        return accountRepository.findAll();
    }

    @CrossOrigin("*")
    @PostMapping("/api/account")
    public Account insertAccount(@RequestBody(required = true)Account account){
        if ( account != null) {
            return accountRepository.save(account);
        }
        return null;
    }

    @CrossOrigin("*")
    @PostMapping("/api/add-to-group")
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
