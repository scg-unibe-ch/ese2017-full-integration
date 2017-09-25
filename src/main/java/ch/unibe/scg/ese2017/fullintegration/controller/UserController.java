package ch.unibe.scg.ese2017.fullintegration.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(path = "/user")
public class UserController {

	@Autowired
	private UserDetailsManager userDetailsManager;

	@RequestMapping("/greeting")
	public String greeting(@RequestParam(value = "name", required = false, defaultValue = "World") String name,
			Model model) {
		model.addAttribute("name", name);
		return "greeting";
	}

	@PreAuthorize("@userSecurityService.canCreate()")
	@PostMapping(path = "/")
	public UserDetails create(@RequestParam String name, @RequestParam String password) {
		User user = new User(name, password, Collections.emptyList());
		userDetailsManager.createUser(user);
		return user;
	}

	@PreAuthorize("@userSecurityService.canRead(#name)")
	@GetMapping(path = "/{name}")
	public UserDetails read(@PathVariable(value = "name") String name) {
		return userDetailsManager.loadUserByUsername(name);
	}

	@PreAuthorize("@userSecurityService.canUpdate(#name)")
	@PutMapping(path = "/{name}")
	public UserDetails update(@PathVariable(value = "name") String name, @RequestParam String password) {
		String oldPassword = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getPassword();
		userDetailsManager.changePassword(oldPassword, password);
		return userDetailsManager.loadUserByUsername(name);
	}

	@PreAuthorize("@userSecurityService.canDelete(#name)")
	@DeleteMapping(path = "/{name}")
	public void delete(@PathVariable(value = "name") String name) {
		userDetailsManager.deleteUser(name);
	}

}
