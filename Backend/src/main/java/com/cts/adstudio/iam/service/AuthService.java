package com.cts.adstudio.iam.service;

import com.cts.adstudio.iam.dto.request.LoginRequest;
import com.cts.adstudio.iam.dto.request.RegisterRequest;
import com.cts.adstudio.iam.dto.response.LoginResponse;
import com.cts.adstudio.iam.dto.response.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
