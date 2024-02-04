package com.staroot.im;

import com.staroot.im.controller.UserController;
import com.staroot.im.entity.Employee;
import com.staroot.im.entity.SysItem;
import com.staroot.im.entity.User;
import com.staroot.im.repository.EmployeeRepository;
import com.staroot.im.repository.SysItemRepository;
import com.staroot.im.repository.UserRepository;
import com.staroot.im.util.PwdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SysItemRepository sysItemRepository;
    private final EmployeeRepository employeeRepository;
    private static final Logger logger = LoggerFactory.getLogger(CommandLineRunner.class);


    public DataLoader(UserRepository userRepository, SysItemRepository sysItemRepository, EmployeeRepository employeeRepository) {
        this.userRepository = userRepository;
        this.sysItemRepository = sysItemRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        String salt = "R6ct90MgIYZh8l81"; // Replace this with a random value stored in the database during user registration.
        String hashedPassword = PwdUtil.hashPassword("1234", salt);

        logger.debug("creating user data...");
        for(int i=0;i<5;i++){
            User user = new User();
            user.setUserid("user"+i);
            user.setPassword(hashedPassword); // You can use SHA-256 encoding here if needed
            user.setSalt(salt);
            user.setEmail("user"+i+"@gmail.com");
            userRepository.save(user);
        }
        logger.debug("completed user data...");


        logger.debug("creating emp data...");
        for(int i=0;i<50;i++){
        //for(int i=0;i<10;i++){
            Employee employee = new Employee();
            employee.setUserid("user"+i);
            employee.setPassword(hashedPassword); // You can use SHA-256 encoding here if needed
            employee.setSalt(salt);
            employee.setEmail("user"+i+"@gmail.com");
            employeeRepository.save(employee);
        }
        logger.debug("completed emp data...");


        SysItem sysItem = new SysItem();
        sysItem.setCode("sysname");
        sysItem.setName("시스템명");
        sysItem.setDescription("system name");
        sysItem.setDatatype("intputbox");
        sysItem.setSeq(1);
        sysItem.setParent("ROOT");
        sysItemRepository.save(sysItem);

        sysItem = new SysItem();
        sysItem.setCode("ostype");
        sysItem.setName("OS종류");
        sysItem.setDescription("operating system");
        sysItem.setDatatype("intputbox");
        sysItem.setSeq(2);
        sysItem.setParent("ROOT");
        sysItemRepository.save(sysItem);

        sysItem = new SysItem();
        sysItem.setCode("sysmgr");
        sysItem.setName("시스템담당자");
        sysItem.setDescription("System Manager");
        sysItem.setDatatype("intputbox");
        sysItem.setSeq(3);
        sysItem.setParent("ROOT");
        sysItemRepository.save(sysItem);

        sysItem = new SysItem();
        sysItem.setCode("description");
        sysItem.setName("시스템설명");
        sysItem.setDescription("System Description");
        sysItem.setDatatype("textarea");
        sysItem.setSeq(4);
        sysItem.setParent("ROOT");
        sysItemRepository.save(sysItem);
    }
}
