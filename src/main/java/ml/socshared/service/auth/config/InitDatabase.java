package ml.socshared.service.auth.config;


import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.service.auth.entity.Role;
import ml.socshared.service.auth.entity.User;
import ml.socshared.service.auth.entity.base.Status;
import ml.socshared.service.auth.exception.impl.HttpNotFoundException;
import ml.socshared.service.auth.repository.RoleRepository;
import ml.socshared.service.auth.repository.SocsharedServiceRepository;
import ml.socshared.service.auth.repository.UserRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Component
@Slf4j
public class InitDatabase implements InitializingBean {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SocsharedServiceRepository socsharedServiceRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            Role role = new Role();
            role.setName("ADMIN");
            roleRepository.save(role);
            log.info("HIBERNATE init role ADMIN");
        } catch (Exception ignore) { }
        try {
            Role role = new Role();
            role.setName("CONTENT_MANAGER");
            roleRepository.save(role);
            log.info("HIBERNATE init role CONTENT_MANAGER");
        } catch (Exception ignore) {}
        try {
            Role admin = roleRepository.findByName("ADMIN").orElseThrow(() -> new HttpNotFoundException("Not found role with name ADMIN"));
            User user = new User();
            user.setUsername("admin");
            user.setEmail("admin@socshared.local");
            Set<Role> set = new HashSet<>();
            set.add(admin);
            user.setRoles(set);
            user.setPassword(Hashing.sha256().hashString("admin", StandardCharsets.UTF_8).toString());
            user.setStatus(Status.ACTIVE);
            user.setEmailVerified(true);
            user.setAccountNonLocked(true);
            user.setResetPassword(false);
            userRepository.save(user);
            log.info("HIBERNATE init user admin");
        } catch (Exception ignore) {}
    }

}
