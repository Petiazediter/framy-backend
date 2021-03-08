package com.codecool.framybackend.controllers;

import com.codecool.framybackend.model.Account;
import com.codecool.framybackend.model.Group;
import com.codecool.framybackend.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class GroupController {

    @Autowired
    private GroupRepository repository;

    @CrossOrigin("*")
    @PostMapping("/api/group")
    public Group createGroup(@RequestBody(required = true)Group group){
        if (group != null) {
            return repository.save(group);
        }
        return null;
    }

    @CrossOrigin("*")
    @GetMapping("/api/groups")
    public List<Group> getGroups(){
        return repository.findAll();
    }

    @CrossOrigin("*")
    @GetMapping("/api/groupaccounts")
    public Set<Account> getAccounts(@RequestParam(name = "id", required = true) Long id){
        Group group = repository.findById(id).orElse(null);
        if ( group != null){
            return group.getAccounts();
        }
        return null;
    }
}
