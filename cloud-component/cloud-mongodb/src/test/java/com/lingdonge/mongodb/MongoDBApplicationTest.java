package com.lingdonge.mongodb;

import com.lingdonge.mongodb.dao.UserDao;
import com.lingdonge.mongodb.entity.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoDBApplicationTest {

    @Autowired
    private UserDao userDao;

    @Test
    public void testSaveUser() {
        UserEntity user = new UserEntity();
        user.setId(1);
        user.setName("jihite");
        user.setPasswd("abc");
        userDao.saveUser(user);

    }

    @Test
    public void testGetUserByName() {
        UserEntity user = userDao.getUserByName("jihite");
        System.out.println(user);
    }

    @Test
    public void testUpdateUserEntity() {
        UserEntity user = new UserEntity();
        user.setId(1);
        user.setName("jihite2");
        user.setPasswd("efg");
        userDao.updateUserEntity(user);

        user = userDao.getUserByName("jihite2");
        System.out.println(user);

    }

    @Test
    public void testDeleteUserById() {
        userDao.deleteUserById(1);
        UserEntity user = new UserEntity();

        System.out.println(userDao.getUserByName("jihite"));
        System.out.println(userDao.getUserByName("jihite2"));

    }
}
