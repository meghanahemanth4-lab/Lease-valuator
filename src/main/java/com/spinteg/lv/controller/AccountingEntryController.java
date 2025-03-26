package com.spinteg.lv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spinteg.lv.dto.AccountingEntryDTO;
import com.spinteg.lv.dto.AccountingEntryUpdate;
import com.spinteg.lv.service.AccountingEntryService;

@RestController
@CrossOrigin
public class AccountingEntryController {

    @Autowired
    private AccountingEntryService accountingEntryService;

    @PostMapping("/accounting-entry")
    public AccountingEntryDTO getAccountingEntry(@RequestBody AccountingEntryUpdate update) {
        return accountingEntryService.getAccountingEntry(update);
    }
}
