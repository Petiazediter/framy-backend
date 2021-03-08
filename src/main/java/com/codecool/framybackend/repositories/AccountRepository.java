package com.codecool.framybackend.repositories;

import com.codecool.framybackend.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account,Long> {

    @Query("SELECT a FROM account a WHERE a.username = :username OR a.emailAddress = :email")
    List<Account> getAccountsByUsernameAndEmail(@Param("username") String username, @Param("email") String email);

    @Query("SELECT a FROM account a WHERE a.username = :username AND a.password = :password AND a.emailAddress = :email")
    Account findAccountByCredentials(@Param("username") String username, @Param("password") String password, @Param("email") String email);
}
