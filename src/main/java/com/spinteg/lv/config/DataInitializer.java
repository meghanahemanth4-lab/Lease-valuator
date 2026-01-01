package com.spinteg.lv.config;

import com.spinteg.lv.entity.User;
import com.spinteg.lv.entity.enums.Role;
import com.spinteg.lv.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("superadmin@example.com")) {
            User superAdmin = new User();
            superAdmin.setFirstName("Super");
            superAdmin.setLastName("Administrator");
            superAdmin.setPassword(passwordEncoder.encode("superadmin123"));
            superAdmin.setEmail("superadmin@example.com");
            superAdmin.setCompanyName("Admin Company");
            superAdmin.setRoles(EnumSet.of(Role.ROLE_SUPER_ADMIN));
            userRepository.save(superAdmin);
        }

        if (!userRepository.existsByEmail("deputy@example.com")) {
            User deputyAdmin = new User();
            deputyAdmin.setFirstName("Deputy");
            deputyAdmin.setLastName("Administrator");
            deputyAdmin.setPassword(passwordEncoder.encode("deputy123"));
            deputyAdmin.setEmail("deputy@example.com");
            deputyAdmin.setCompanyName("Admin Company");
            deputyAdmin.setRoles(EnumSet.of(Role.ROLE_DEPUTY_ADMIN));
            userRepository.save(deputyAdmin);
        }

        if (!userRepository.existsByEmail("contract.creator@example.com")) {
            User contractCreator = new User();
            contractCreator.setFirstName("Contract");
            contractCreator.setLastName("Creator");
            contractCreator.setPassword(passwordEncoder.encode("contract123"));
            contractCreator.setEmail("contract.creator@example.com");
            contractCreator.setCompanyName("Contracting Company");
            contractCreator.setRoles(EnumSet.of(Role.ROLE_CONTRACT_TYPE_CREATOR));
            userRepository.save(contractCreator);
        }

        if (!userRepository.existsByEmail("lease.creator@example.com")) {
            User leaseCreator = new User();
            leaseCreator.setFirstName("Lease");
            leaseCreator.setLastName("Creator");
            leaseCreator.setPassword(passwordEncoder.encode("lease123"));
            leaseCreator.setEmail("lease.creator@example.com");
            leaseCreator.setCompanyName("Leasing Company");
            leaseCreator.setRoles(EnumSet.of(Role.ROLE_LEASE_CREATOR));
            userRepository.save(leaseCreator);
        }

        if (!userRepository.existsByEmail("deployer@example.com")) {
            User deployer = new User();
            deployer.setFirstName("Deployer");
            deployer.setLastName("User");
            deployer.setPassword(passwordEncoder.encode("deployer123"));
            deployer.setEmail("deployer@example.com");
            deployer.setCompanyName("Deployment Company");
            deployer.setRoles(EnumSet.of(Role.ROLE_DEPLOYER));
            userRepository.save(deployer);
        }
    }
}
