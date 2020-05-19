package ml.socshared.service.auth.util;

import com.google.common.hash.Hashing;
import ml.socshared.service.auth.domain.model.UserModel;
import ml.socshared.service.auth.domain.request.AuthRequest;
import ml.socshared.service.auth.domain.request.NewUserRequest;
import ml.socshared.service.auth.domain.request.UpdatePasswordRequest;
import ml.socshared.service.auth.domain.request.UpdateUserRequest;
import ml.socshared.service.auth.domain.response.SuccessResponse;
import ml.socshared.service.auth.domain.response.UserResponse;
import ml.socshared.service.auth.entity.Role;
import ml.socshared.service.auth.entity.User;
import org.springframework.data.domain.Page;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

public class GeneratorLinks {

    public static String build() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder(20);
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();

        return Hashing.sha256().hashString(output, StandardCharsets.UTF_8).toString();
    }

}