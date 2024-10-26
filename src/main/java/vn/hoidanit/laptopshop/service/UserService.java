package vn.hoidanit.laptopshop.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    public String handleHome() {
        return "Welcome to Service";
    }
}
