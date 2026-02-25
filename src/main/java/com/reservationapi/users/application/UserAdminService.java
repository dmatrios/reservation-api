package com.reservationapi.users.application;

import com.reservationapi.shared.exceptions.ErrorCode;
import com.reservationapi.shared.exceptions.ForbiddenException;
import com.reservationapi.shared.exceptions.NotFoundException;
import com.reservationapi.users.application.mapper.UserMapper;
import com.reservationapi.users.domain.User;
import com.reservationapi.users.domain.UserStatus;
import com.reservationapi.users.infra.UserRepository;
import com.reservationapi.users.web.dto.UserAdminListItemResponse;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserAdminListItemResponse> listUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(UserMapper::toAdminListItem)
                .toList();
    }

    @Transactional
    public UserAdminListItemResponse updateStatus(Long targetUserId, UserStatus newStatus, Long currentAdminUserId) {
        if (Objects.equals(targetUserId, currentAdminUserId)) {
            throw new ForbiddenException(ErrorCode.CANNOT_CHANGE_OWN_STATUS.name());
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND.name()));

        if (user.getStatus() != newStatus) {
            user.setStatus(newStatus);
            userRepository.save(user);
        }

        return UserMapper.toAdminListItem(user);
    }
}