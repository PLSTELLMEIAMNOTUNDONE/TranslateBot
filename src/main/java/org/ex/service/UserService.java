package org.ex.service;

import org.ex.models.User;
import org.ex.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private User defaultUser(String chatId) {
        return new User(chatId, "ru", "en", "txt", "txt");
    }

    public User saveUser(User user) {
        if (!userRepository.existsById(user.getId())) userRepository.save(user);
        return user;
    }

    public User setUserLangTo(String chatId, String langTo) {
        if (userRepository.existsById(chatId)) {
            User user = userRepository.getReferenceById(chatId);
            user.setLangTo(langTo);
            return userRepository.save(user);
        }
        User user = defaultUser(chatId);
        user.setLangTo(langTo);
        return userRepository.save(user);
    }

    public User setUserLangFrom(String chatId, String langFrom) {
        if (userRepository.existsById(chatId)) {
            User user = userRepository.getReferenceById(chatId);
            user.setLangFrom(langFrom);
            return userRepository.save(user);
        }
        User user = defaultUser(chatId);
        user.setLangFrom(langFrom);
        return userRepository.save(user);
    }

    public User setUserReqFile(String chatId, String reqFile) {
        if (userRepository.existsById(chatId)) {
            User user = userRepository.getReferenceById(chatId);
            user.setReqFile(reqFile);
            return userRepository.save(user);
        }
        User user = defaultUser(chatId);
        user.setReqFile(reqFile);
        return userRepository.save(user);
    }

    public User setUserResFile(String chatId, String resFile) {
        if (userRepository.existsById(chatId)) {
            User user = userRepository.getReferenceById(chatId);
            user.setResFile(resFile);
            return userRepository.save(user);
        }
        User user = defaultUser(chatId);
        user.setResFile(resFile);
        return userRepository.save(user);
    }

    public User getUser(String chatId) {
        if (userRepository.existsById(chatId)) return userRepository.getReferenceById(chatId);
        return userRepository.save(defaultUser(chatId));
    }
}
