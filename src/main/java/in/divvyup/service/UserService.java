package in.divvyup.service;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import in.divvyup.exception.InvalidRequestException;
import in.divvyup.persistence.dao.UserAuthDao;
import in.divvyup.persistence.dao.UserDao;
import in.divvyup.persistence.model.BaseModel;
import in.divvyup.persistence.model.User;
import in.divvyup.persistence.model.UserAuth;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthDao userAuthDao;

    public User getUserById(String userId) {
        return userDao.getById(userId);
    }

    public User createUser(User user, String identityProvider, String ipUserId) {
        user.setStatus(BaseModel.Status.ACTIVE);
        userDao.save(user);
        userAuthDao.save(
                UserAuth.builder().id(UUID.randomUUID().toString()).provider(identityProvider).ipUserId(ipUserId).status(UserAuth.Status.ACTIVE)
                        .userId(user.getId()).build());
        user.setNewUser(true);
        return user;
    }
}

