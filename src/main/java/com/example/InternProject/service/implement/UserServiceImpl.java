package com.example.InternProject.service.implement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.InternProject.entity.UserEntity;
import com.example.InternProject.model.Users;
import com.example.InternProject.repository.UserRepository;
import com.example.InternProject.service.interfaces.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder
    // passwordEncoder) {
    // this.userRepository = userRepository;
    // this.passwordEncoder = new BCryptPasswordEncoder(); // Khởi tạo encoder
    // }

    @Override
    // public Users createUser(Users user) throws Exception {
    // try {
    // UserEntity userEntity = new UserEntity();
    // user.setCreateAt(LocalDateTime.now());
    // user.setUpdateAt(LocalDateTime.now());

    // BeanUtils.copyProperties(user, userEntity);
    // userRepository.save(userEntity);

    // return user;
    // } catch (Exception e) {
    // throw new Exception(e.getMessage());
    // }
    // }
    public Users createUser(Users user) throws Exception {
        // Kiểm tra xem email đã tồn tại chưa
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new Exception("Email already exists!");
        }

        // Tạo đối tượng UserEntity và gán giá trị
        UserEntity userEntity = new UserEntity();
        user.setCreateAt(LocalDateTime.now());
        user.setUpdateAt(LocalDateTime.now());

        // **Mã hóa mật khẩu**
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Copy dữ liệu từ DTO sang entity
        BeanUtils.copyProperties(user, userEntity);

        // Lưu user vào database
        userRepository.save(userEntity);

        return user;
    }

    @Override
    public List<UserEntity> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        List<UserEntity> output = new ArrayList<>();

        users.forEach(userEntity -> output.add(userEntity));
        return output;
    }

    @Override
    public Boolean deleteUserByID(Long ID) throws Exception {
        try {
            if (userRepository.findById(ID).isPresent()) {
                userRepository.delete(userRepository.findById(ID).get());
                return true;
            }
            return false;
        } catch (NoSuchElementException e) {
            throw new Exception("User is not found" + ID.toString());
        }
    }

    @Override
    public Users updateUser(Long ID, Users user) throws Exception {
        try {
            UserEntity userEntity = userRepository.findById(ID).isPresent() ? userRepository.findById(ID).get() : null;
            assert userEntity != null;
            userEntity.setName(user.getName());
            userEntity.setGender(user.getGender());
            userEntity.setRole(user.getRole());
            userEntity.setUpdateAt(LocalDateTime.now());
            userRepository.save(userEntity);
            BeanUtils.copyProperties(userEntity, user);
            return user;
        } catch (NoSuchElementException e) {
            throw new Exception("User not found" + ID.toString());
        }
    }

    @Override
    public UserEntity getUserByID(Long ID) throws Exception {
        try {
            UserEntity userEntity = userRepository.findById(ID).isPresent() ? userRepository.findById(ID).get() : null;
            return userEntity;
        } catch (NoSuchElementException e) {
            throw new Exception("User not found" + ID.toString());
        }
    }

    @Override
    public List<UserEntity> getUserByName(String name) throws Exception {
        try {
            List<UserEntity> userEntities = userRepository.findByNameContaining(name);
            if (userEntities.isEmpty()) {
                throw new NoSuchElementException("No users found with name containing: " + name);
            }
            return userEntities;
        } catch (NoSuchElementException e) {
            throw new Exception(e.getMessage());
        }
    }

}
