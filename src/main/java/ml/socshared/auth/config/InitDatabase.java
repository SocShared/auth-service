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
        try {
            SocsharedService socsharedService = new SocsharedService();
            socsharedService.setHostUrl("https://api.socshared.ml");
            socsharedService.setServiceName("Gateway API Service");
            socsharedService.setServiceId(UUID.fromString("9e671e7d-976f-40d6-a8c4-67912ae12ede"));
            socsharedService.setServiceSecret(UUID.fromString("10318fc5-aa65-400a-9ed4-0f4ed2e46e09"));
            socsharedServiceRepository.save(socsharedService);
            log.info("HIBERNATE init service Gateway API Service");
        } catch (Exception ignore) {}
        try {
            SocsharedService socsharedService = new SocsharedService();
            socsharedService.setHostUrl("https://vk.socshared.ml");
            socsharedService.setServiceName("VK Service");
            socsharedService.setServiceId(UUID.fromString("cb43eee3-3468-4cc2-b6ed-63419e8726ce"));
            socsharedService.setServiceSecret(UUID.fromString("f769cb1c-bf08-478d-8218-0bb347369dd7"));
            socsharedServiceRepository.save(socsharedService);
            log.info("HIBERNATE init service VK Service");
        } catch (Exception ignore) {}
        try {
            SocsharedService socsharedService = new SocsharedService();
            socsharedService.setHostUrl("https://bstat.socshared.ml");
            socsharedService.setServiceName("BSTAT Service");
            socsharedService.setServiceId(UUID.fromString("e7ee788d-c59e-4a96-bdaf-52d6b33df1f3"));
            socsharedService.setServiceSecret(UUID.fromString("b8500899-b1a1-4b99-984f-08aed46d1aea"));
            socsharedServiceRepository.save(socsharedService);
            log.info("HIBERNATE init service BSTAT Service");
        } catch (Exception ignore) {}
        try {
            SocsharedService socsharedService = new SocsharedService();
            socsharedService.setHostUrl("https://ms.socshared.ml");
            socsharedService.setServiceName("Mail Sender Service");
            socsharedService.setServiceId(UUID.fromString("68c5c6d9-fb18-4adb-800e-faac3ac745b9"));
            socsharedService.setServiceSecret(UUID.fromString("a981045d-e269-4b28-b7b7-af4a885b9dc4"));
            socsharedServiceRepository.save(socsharedService);
            log.info("HIBERNATE init service Mail Sender Service");
        } catch (Exception ignore) {}
        try {
            SocsharedService socsharedService = new SocsharedService();
            socsharedService.setHostUrl("https://text.socshared.ml");
            socsharedService.setServiceName("Text Analyzer Service");
            socsharedService.setServiceId(UUID.fromString("58aeed0d-d092-455b-a1a6-dccfea5b89c6"));
            socsharedService.setServiceSecret(UUID.fromString("98650932-32df-495a-afeb-9c08bdccd200"));
            socsharedServiceRepository.save(socsharedService);
            log.info("HIBERNATE init service Text Analyzer Service");
        } catch (Exception ignore) {}
        try {
            SocsharedService socsharedService = new SocsharedService();
            socsharedService.setHostUrl("https://support.socshared.ml");
            socsharedService.setServiceName("Support Service");
            socsharedService.setServiceId(UUID.fromString("31a2ee92-0e6c-45b7-b6cb-810eec2f1041"));
            socsharedService.setServiceSecret(UUID.fromString("48733b84-9434-4893-9091-cb855c586ca2"));
            socsharedServiceRepository.save(socsharedService);
            log.info("HIBERNATE init service Support Service");
        } catch (Exception ignore) {}
        try {
            SocsharedService socsharedService = new SocsharedService();
            socsharedService.setHostUrl("https://stat.socshared.ml");
            socsharedService.setServiceName("System Statistic Service");
            socsharedService.setServiceId(UUID.fromString("eeb4585c-1d8f-463c-b441-e5dbb27ec94d"));
            socsharedService.setServiceSecret(UUID.fromString("fcf25e23-fe55-4df7-b8f1-e5e56d1277fc"));
            socsharedServiceRepository.save(socsharedService);
            log.info("HIBERNATE init service System Statistic Service");
        } catch (Exception ignore) {}
        try {
            SocsharedService socsharedService = new SocsharedService();
            socsharedService.setHostUrl("https://auth.socshared.ml");
            socsharedService.setServiceName("Auth Service");
            socsharedService.setServiceId(UUID.fromString("58c2b3d5-dfad-41af-9451-d0a26fdc9019"));
            socsharedService.setServiceSecret(UUID.fromString("0cb9bb2e-ee6a-48b7-b36a-23fb07f3fa28"));
            socsharedServiceRepository.save(socsharedService);
            log.info("HIBERNATE init service Auth Service");
        } catch (Exception ignore) {}
    }

}
