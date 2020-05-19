package ml.socshared.service.auth.service;

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

import java.util.UUID;

public interface UserService {

    UserResponse add(NewUserRequest newUserRequest);
    UserResponse update(UUID id, UpdateUserRequest request);
    UserResponse updatePassword(UUID id, UpdatePasswordRequest request);
    SuccessResponse deleteById(UUID id);
    UserResponse findById(UUID id);
    UserResponse findByUsername(String username);
    UserResponse findByEmail(String email);
    Page<UserModel> findAll(Integer page, Integer size);
    UserResponse activation(UUID id);
    UserResponse deactivation(UUID id);
    SuccessResponse checkData(AuthRequest request);
    UserResponse addRole(UUID id, UUID roleId);
    UserResponse removeRole(UUID id, UUID roleId);
    SuccessResponse confirmEmail(String generatingLink);

}
