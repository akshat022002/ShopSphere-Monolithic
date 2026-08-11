package com.akshat.shopsphere.auth.mapper;

import com.akshat.shopsphere.auth.dto.RegisterRequest;
import com.akshat.shopsphere.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

        User toUser(RegisterRequest request);

}
