package ml.socshared.auth.config;


import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.auth.entity.Client;
import ml.socshared.auth.entity.Role;
import ml.socshared.auth.entity.SocsharedService;
import ml.socshared.auth.entity.User;
import ml.socshared.auth.exception.impl.HttpNotFoundException;
import ml.socshared.auth.repository.ClientRepository;
import ml.socshared.auth.repository.RoleRepository;
import ml.socshared.auth.repository.SocsharedServiceRepository;
import ml.socshared.auth.repository.UserRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class InitDatabase implements InitializingBean {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
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
            Role contentManager = roleRepository.findByName("CONTENT_MANAGER").orElseThrow(() -> new HttpNotFoundException("Not found role with name CONTENT_MANAGER"));
            User user = new User();
            user.setUsername("admin");
            user.setEmail("admin@socshared.local");
            Set<Role> set = new HashSet<>();
            set.add(admin);
            set.add(contentManager);
            user.setRoles(set);
            user.setPassword(Hashing.sha256().hashString("admin", StandardCharsets.UTF_8).toString());
            user.setEmailVerified(true);
            user.setAccountNonLocked(true);
            user.setResetPassword(false);
            userRepository.save(user);
            log.info("HIBERNATE init user admin");
        } catch (Exception ignore) {}
        try {
            Role admin = roleRepository.findByName("ADMIN").orElseThrow(() -> new HttpNotFoundException("Not found role with name ADMIN"));
            Role contentManager = roleRepository.findByName("CONTENT_MANAGER").orElseThrow(() -> new HttpNotFoundException("Not found role with name CONTENT_MANAGER"));
            Client client = new Client();
            client.setUser(null);
            client.setClientId(UUID.fromString("360dad92-ecb1-44e7-990a-3152d2642919"));
            client.setClientSecret(UUID.fromString("cb456410-85ca-43b5-9a12-87171ad84516"));
            client.setAccessType(Client.AccessType.PUBLIC);
            client.setRoles(new HashSet<>() {{add(admin); add(contentManager);}});
            client.setValidRedirectUri("/");
            client.setName("frontend-service");
            clientRepository.save(client);
            log.info("HIBERNATE init client frontend-service");
        } catch (Exception ignore) { }
        try {
            SocsharedService socsharedService = new SocsharedService();
            socsharedService.setHostUrl("https://fb.socshared.ml");
            socsharedService.setServiceName("FB Service Adapter");
            socsharedService.setServiceId(UUID.fromString("f7e14d85-415c-4ab9-b285-a6481d79f507"));
            socsharedService.setServiceSecret(UUID.fromString("427d82bb-b367-40b4-bee8-b18e32480899"));
            socsharedServiceRepository.save(socsharedService);
            log.info("HIBERNATE init service FB Service Adapter");
        } catch (Exception ignore) {}
        try {
            SocsharedService socsharedService = new SocsharedService();
            socsharedService.setHostUrl("https://worker.socshared.ml");
            socsharedService.setServiceName("Service Worker");
            socsharedService.setServiceId(UUID.fromString("25086e71-269b-46ff-aa48-23f7ffba3bf9"));
            socsharedService.setServiceSecret(UUID.fromString("880bc772-a207-4357-b7c9-821fcee85662"));
            socsharedServiceRepository.save(socsharedService);
            log.info("HIBERNATE init service Service Worker");
        } catch (Exception ignore) {}
        try {
            SocsharedService socsharedService = new SocsharedService();
            socsharedService.setHostUrl("https://storage.socshared.ml");
            socsharedService.setServiceName("Service Storage");
            socsharedService.setServiceId(UUID.fromString("64141ce5-5604-4ade-ada2-e38cf7d2522c"));
            socsharedService.setServiceSecret(UUID.fromString("5b21977e-166f-471b-a7a7-c60b20e18cf9"));
            socsharedServiceRepository.save(socsharedService);
            log.info("HIBERNATE init service Service Storage");
        } catch (Exception ignore) {}

    }

}
