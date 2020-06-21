package ml.socshared.auth.service;

import ml.socshared.auth.domain.model.UserModel;
import ml.socshared.auth.domain.request.AuthRequest;
import ml.socshared.auth.domain.request.NewUserRequest;
import ml.socshared.auth.domain.request.UpdatePasswordRequest;
import ml.socshared.auth.domain.request.UpdateUserRequest;
import ml.socshared.auth.domain.response.stat.AllUsersResponse;
import ml.socshared.auth.domain.response.stat.NewUsersResponse;
import ml.socshared.auth.domain.response.SuccessResponse;
import ml.socshared.auth.domain.response.UserResponse;
import ml.socshared.auth.domain.response.stat.OnlineUsersResponse;
import ml.socshared.auth.entity.GeneratingCode;
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
    GeneratingCode processGenerationLink(String generatingLink);
    SuccessResponse resetPassword(String email);
    OnlineUsersResponse onlineUsers();
    NewUsersResponse newUsers();
    AllUsersResponse allUsers();

}
