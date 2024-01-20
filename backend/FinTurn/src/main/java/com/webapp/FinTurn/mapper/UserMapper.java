package com.webapp.FinTurn.mapper;

import com.webapp.FinTurn.domain.dto.UserDto;
import com.webapp.FinTurn.domain.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class UserMapper {
    private ModelMapper modelMapper;

    public UserEntity mapUserDtoToEntity(UserDto userDto) {
        return modelMapper.map(userDto, UserEntity.class);
    }

    public UserDto mapUserEntityToDto(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserDto.class);
    }
}
