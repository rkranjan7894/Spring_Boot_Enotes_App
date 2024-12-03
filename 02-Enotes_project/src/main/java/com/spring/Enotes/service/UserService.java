package com.spring.Enotes.service;

import com.spring.Enotes.entity.User;

public interface UserService {

    public User saveUser(User user,String url);
    public boolean existEmailCheck(String email);
    public void removeSessionMessage();
    public void sendEmail(User user,String path);
    public boolean verifyAccount(String verificationCode);
    public void increaseFailedAttempt(User user);
    public void resetAttempt(String email);
    public void lock(User user);
    public boolean unlockAccountTimeExpired(User user);


}
