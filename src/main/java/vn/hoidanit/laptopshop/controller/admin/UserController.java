package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.UploadService;
import vn.hoidanit.laptopshop.service.UserService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final UserService userService;
    private final UploadService uploadService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, UploadService uploadService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.uploadService = uploadService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping("/")
    public String getHomePage(Model model) {
        List<User> arrUsers = this.userService.getAllUsersByEmail("1@gmail.com");
        System.out.println(arrUsers);
        model.addAttribute("eric", "test");
        return "hello";
    }

    @RequestMapping("/admin/user")
    public String getUserPage(Model model) {
        List<User> users = this.userService.getAllUsers();
        model.addAttribute("users1", users);
        // users: Giá trị gán vào
        // users1: Giá trị nhận được bên view
        return "admin/user/show";
    }

    @GetMapping("/admin/user/create")
    public String getCreateUserPage(Model model) {
        model.addAttribute("newUser", new User());
        return "admin/user/create";
    }

    @RequestMapping("/admin/user/update/{id}")
    public String getUpdateUserPage(Model model, @PathVariable long id) {
        User currentUser = this.userService.getUserById(id);
        model.addAttribute("newUser", currentUser);
        return "admin/user/update";
    }

    @GetMapping("/admin/user/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable long id) {
        // User user = new User();
        // user.setId(id);
        model.addAttribute("newUser", new User());
        model.addAttribute("id", id);
        return "admin/user/delete";
    }

    @RequestMapping("/admin/user/{id}")
    public String getUserDetailPage(Model model, @PathVariable long id) {
        User user = this.userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("id", id);
        return "admin/user/detail";
    }

    @PostMapping("/admin/user/create")
    public String createUserPage(Model model, @ModelAttribute("newUser") @Valid User trandat,
            BindingResult newUserBindingResult,
            @RequestParam("tiendatFile") MultipartFile file) {

        // List<FieldError> errors = newUserBindingResult.getFieldErrors();
        // for (FieldError error : errors) {
        // System.out.println(">>>>" + error.getField() + " - " +
        // error.getDefaultMessage());
        // }

        // validate
        if (newUserBindingResult.hasErrors()) {
            return "admin/user/create";
        }
        // hop le
        String avatar = this.uploadService.handleSaveUploadFile(file, "avatar");
        String hashPassword = this.passwordEncoder.encode(trandat.getPassword());
        trandat.setAvatar(avatar);
        trandat.setPassword(hashPassword);
        trandat.setRole(this.userService.getRoleByName(trandat.getRole().getName()));
        this.userService.handleSaveUser(trandat);
        return "redirect:/admin/user"; // quay luon lai trang
    }

    @PostMapping("/admin/user/update")
    public String postUpdateUser(Model model, @ModelAttribute("newUser") User trandat) {
        User currentUser = this.userService.getUserById(trandat.getId());
        if (currentUser != null) {
            currentUser.setAddress(trandat.getAddress());
            currentUser.setFullName(trandat.getFullName());
            currentUser.setPhone(trandat.getPhone());
            this.userService.handleSaveUser(currentUser);
        }
        return "redirect:/admin/user";
    }

    @PostMapping("/admin/user/delete")
    public String postDeleteUser(Model model, @ModelAttribute("newUser") User trandat) {
        this.userService.deleteAUser(trandat.getId());
        return "redirect:/admin/user";
    }

}
// @RestController
// public class UserController {
// // DI
// private UserService userService;

// public UserController(UserService userService) {
// this.userService = userService;
// }

// @GetMapping("")
// public String getHomePage() {
// return this.userService.handleHome();
// }
// }
