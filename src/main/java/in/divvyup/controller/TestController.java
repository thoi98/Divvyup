package in.divvyup.controller;

import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import in.divvyup.annotation.NoLogin;
import in.divvyup.persistence.dao.TestDao;
import in.divvyup.persistence.model.Test;

@RestController
@RequestMapping ("/test")
public class TestController {
    @Autowired
    TestDao testDao;

    @GetMapping
    public String helloWorld() {
        return "Hello World";
    }

    @PostMapping ("/insert")
    public void insert() {
        testDao.insert(Test.builder().details(RandomStringUtils.randomAlphanumeric(10)).build());
    }

    @GetMapping ("/all")
    public List<Test> getAll() {
        return testDao.getAll();
    }
}
