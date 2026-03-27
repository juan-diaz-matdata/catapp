package com.test.catapp.infrastructure.adapter.out.persistence.mapper;

import com.test.catapp.domain.model.User;
import com.test.catapp.infrastructure.adapter.out.persistence.entity.UserDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDocumentMapper {

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserDocument toDocument(User user);

    @Mapping(target = "role", expression = "java(com.test.catapp.domain.model.User.Role.valueOf(doc.getRole()))")
    User toDomain(UserDocument doc);
}
