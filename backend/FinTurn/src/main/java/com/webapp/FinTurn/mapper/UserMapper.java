package com.webapp.FinTurn.mapper;

import com.webapp.FinTurn.domain.dto.UserDto;
import com.webapp.FinTurn.domain.entity.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class UserMapper {
    private ModelMapper modelMapper;

    public User mapUserDtoToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    public UserDto mapUserEntityToDto(User userEntity) {
        return modelMapper.map(userEntity, UserDto.class);
    }
}
