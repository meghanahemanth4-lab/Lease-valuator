package com.spinteg.lv.controller;

import com.spinteg.lv.dto.AccountingEntryDTO;
import com.spinteg.lv.dto.AccountingEntryUpdate;
import com.spinteg.lv.service.AccountingEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class AccountingEntryController {

    @Autowired
    private AccountingEntryService accountingEntryService;

    /**
     * This endpoint returns accounting entry information.
     * Treated as a read operation: allow DEPLOYER + all admin/creators who need view.
     */
    @PostMapping("/accounting-entry")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_LEASE_CREATOR','ROLE_DEPLOYER')")
    public AccountingEntryDTO getAccountingEntry(@RequestBody AccountingEntryUpdate update) {
        return accountingEntryService.getAccountingEntry(update);
    }
}
