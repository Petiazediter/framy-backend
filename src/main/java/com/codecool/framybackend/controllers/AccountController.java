package com.codecool.framybackend.controllers;

import com.codecool.framybackend.model.Account;
import com.codecool.framybackend.model.AccountGroupWrapper;
import com.codecool.framybackend.model.Group;
import com.codecool.framybackend.repositories.AccountRepository;
import com.codecool.framybackend.repositories.GroupRepository;
import com.codecool.framybackend.utils.PasswordUtils;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.awt.*;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600,allowedHeaders = "*", allowCredentials = "true")
@RestController
public class AccountController {

    private static final String COOKIE_USERID = "userId";

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private GroupRepository groupRepository;


    /*
        /api/cookieLogin
        Finding the userId cookie, and trying to log in with it.
     */
    @GetMapping("/api/cookielogin")
    public Account loginWithCookie ( HttpServletResponse response,HttpServletRequest request) throws Exception {
        PasswordUtils passwordUtils = new PasswordUtils();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
             List<Cookie> cookieList = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(COOKIE_USERID)).collect(Collectors.toList());
             if ( cookieList.size() > 0){
                 Cookie cookie = cookieList.get(0);
                 Long id = Long.parseLong(passwordUtils.decrypt(cookie.getValue()));
                 return accountRepository.findById( id ).orElse(null);
             }
        }
        return null;
    }
    @PostMapping("/api/login")
    public Account loginWithUserCredentials(@RequestBody(required = true) Account account, HttpServletResponse response) throws Exception {
        if (account != null) {
            PasswordUtils passwordUtils = new PasswordUtils();
            String password = account.getPassword();
            String encryptedPassword = passwordUtils.encrypt(password);
            Account account1 = accountRepository.findAccountByUsernameAndPassword(account.getUsername(),encryptedPassword);
            if ( account1 != null){
                Cookie cookie = createCookieFromAccount(account1);
                response.addCookie(cookie);
            }

            return account1;
        }
        return null;
    }

    private Cookie createCookieFromAccount(Account account) throws Exception {
        PasswordUtils passwordUtils = new PasswordUtils();
        Cookie cookie = new Cookie(COOKIE_USERID, passwordUtils.encrypt(account.getId().toString()));
        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
        cookie.setPath("/");
        return cookie;
    }

    /*
        /api/register
        Register with user credentials
     */
    @PostMapping("/api/register")
    public Account registerAccount(@RequestBody(required = true) Account account,HttpServletResponse response) throws Exception {
        if ( account != null){
            PasswordUtils passwordUtils = new PasswordUtils();
            String encryptedPassword = passwordUtils.encrypt(account.getPassword());
            account.setPassword(encryptedPassword);
            if (accountRepository.getAccountsByUsernameAndEmail(account.getUsername(),account.getPassword()).size() == 0){
                Account savedAccount = accountRepository.save(account);
                Cookie cookie = createCookieFromAccount(savedAccount);
                response.addCookie(cookie);
                return savedAccount;
            } else{
                System.out.println("Already used.");
                return null;
            }
        }else {
            System.out.println("No account");
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
