package com.codecool.framybackend.repositories;

import com.codecool.framybackend.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long> {}
