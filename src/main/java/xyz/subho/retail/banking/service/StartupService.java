package xyz.subho.retail.banking.service;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.subho.retail.banking.security.Role;
import xyz.subho.retail.banking.dao.RoleDao;

@Service
public class StartupService {

    @Autowired
    private RoleDao roleDao;

    @PostConstruct
    public void init() {
        // if (roleDao.findByName("ROLE_ADMIN") == null) {
        //     Role adminRole = new Role();
        //     adminRole.setName("ROLE_ADMIN");
        //     adminRole.setRoleId(0);
        //     roleDao.save(adminRole);
        // }
        if (roleDao.findByName("ROLE_USER") == null) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleDao.save(userRole);
        }
        
    }
}
