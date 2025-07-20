package com.mysite.sbb.user;

import com.mysite.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        //비밀번호는 보안을 위해 반드시 암호화
        //이렇게 BCryptPasswordEncoder 객체를 직접 new로 생성하는 방식보다는
        //PasswordEncoder 객체를 빈으로 등록해서 사용하는 것이 좋다.
        // 왜냐하면 암호화 방식을 변경하면 BCryptPasswordEncoder를 사용한 모든 프로그램을 일일이 찾아다니며 수정해야 하기 때문이다.
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }
}