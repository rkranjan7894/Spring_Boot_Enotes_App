package com.spring.Enotes.service;

import com.spring.Enotes.entity.User;
import com.spring.Enotes.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender mailSender;
    @Override
    public User saveUser(User user,String url) {
        String password=passwordEncoder.encode(user.getPassword());
        user.setPassword(password);
        // user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        user.setEnable(false);
        user.setVerificationCode(UUID.randomUUID().toString());
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        user.setLockTime(null);
//

        User newUser=userRepo.save(user);
         if (newUser!=null){
            sendEmail(newUser,url);
         }
        return newUser;
    }
    @Override
    public boolean existEmailCheck(String email) {
        return userRepo.existsByEmail(email);
    }
    public void removeSessionMessage(){
        HttpSession session=((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest().getSession();
        session.removeAttribute("msg");
    }

    @Override
    public void sendEmail(User user, String url) {
        String from="rkranjan7894@gmail.com";
        String to=user.getEmail();
        String subject="Account Verification";
        String content = "Dear [[name]],<br>"+"Please click the link below to verify your registration:<br>"+
                "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"+"Thank you,<br>"+"ErRanjan";

        try{
            MimeMessage message=mailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(message);
            helper.setFrom(from,"ErRanjan");
            helper.setTo(to);
            helper.setSubject(subject);
            content= content.replace("[[name]]",user.getName());
            String siteUrl = url+"/verify?code="+user.getVerificationCode();
            System.out.println(siteUrl);
            content=content.replace("[[URL]]",siteUrl);
            helper.setText(content,true);
            mailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean verifyAccount(String verificationCode) {
        User user=userRepo.findByVerificationCode(verificationCode);
        if (user == null){
            return false;
        }else {
            user.setEnable(true);
            user.setVerificationCode(null);
            userRepo.save(user);
            return true;
        }
    }
    @Override
    public void increaseFailedAttempt(User user) {

        int attempt=user.getFailedAttempt()+1;
        userRepo.updateFailedAttempt(attempt,user.getEmail());
    }
  // private static final long lock_duration_time=10*60*60*1000;
    private static final long lock_duration_time=30000;
    public static final long ATTEMPT_TIME=3;

    @Override
    public void resetAttempt(String email) {
        userRepo.updateFailedAttempt(0,email);

    }

    @Override
    public void lock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepo.save(user);

    }
//    @Override
//    public boolean unlockAccountTimeExpired(User user) {
//
//        long lockTimeInMills= user.getLockTime().getTime();
//        long currentTimeMillis=System.currentTimeMillis();
//
//        if (lockTimeInMills + lock_duration_time < currentTimeMillis){
//
//            user.setAccountNonLocked(true);
//            user.setLockTime(null);
//            //
//            user.setFailedAttempt(0);
//            userRepo.save(user);
//            return true;
//        }
//
//        return false;
//    }


    @Override
  public boolean unlockAccountTimeExpired(User user) {
        if (user.getLockTime() == null) {
            // If lockTime is null, no lock has been applied
            return false;
        }

       long lockTimeInMills= user.getLockTime().getTime();
      long currentTimeMillis=System.currentTimeMillis();
        if (lockTimeInMills + lock_duration_time < currentTimeMillis){
       // if (currentTimeMillis - lockTimeInMills > lock_duration_time){
        user.setAccountNonLocked(true);
        user.setLockTime(null);
        user.setFailedAttempt(0);
            userRepo.save(user);
          return true;
       }

       return false;
    }

}
