package com.fernandomantoan.tcc.oauth.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.fernandomantoan.tcc.oauth.domain.entity.credentials.Role;
import com.fernandomantoan.tcc.oauth.domain.entity.credentials.User;
import com.fernandomantoan.tcc.oauth.domain.service.UserService;

@Controller
@RequestMapping("user")
@SessionAttributes("userBean")
public class UserController
{
	@Autowired
	private UserService userService;
	
	// ------------------------
	// Binding
	public User createFormUser()
	{
		User user = new User();
		user.setEnabled(true);
		user.setRole(Role.ROLE_USER);
		return user;
	}
	
	// -------------------------
	// Request
	@RequestMapping("login")
	public ModelAndView loginForm()
	{
		return new ModelAndView("user/login");
	}
	
	@RequestMapping
	public ModelAndView indexPage()
	{
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("users", this.userService.findAll());
		return new ModelAndView("user/index", "model", model);
	}
	
	@RequestMapping("add")
	public ModelAndView addForm(User userBean)
	{
		userBean.setEnabled(true);
		return new ModelAndView("user/form", "userBean", userBean);
	}
	
	@RequestMapping(value="edit/{id}",method=RequestMethod.GET)
	public ModelAndView editForm(@PathVariable Long id)
	{
		User userBean = this.userService.findById(id);
		return new ModelAndView("user/form", "userBean", userBean);
	}
	
	@RequestMapping(value="profile",method=RequestMethod.GET)
	public ModelAndView profileForm()
	{
		User userBean = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return new ModelAndView("user/form", "userBean", userBean);
	}
	
	// --------------------------
	// Processing
	@RequestMapping(value="add",method=RequestMethod.POST)
	public String doAdd(@ModelAttribute("userBean") User user, BindingResult result)
	{
		try {
			if (result.hasErrors())
				throw new Exception("Formulário inválido");
			
			this.userService.insert(user);
			return "redirect:/user";
		} catch (Exception e) {
			e.printStackTrace();
			return "user/form";
		}
	}
	
	@RequestMapping(value="edit/{id}",method=RequestMethod.POST)
	public String doEdit(@ModelAttribute("userBean") User user, BindingResult result)
	{
		try {
			if (result.hasErrors())
				throw new Exception("Formulário inválido");
			
			this.userService.update(user);
			return "redirect:/user";
		} catch (Exception e) {
			e.printStackTrace();
			return "user/form";
		}
	}
	
	@RequestMapping(value="profile",method=RequestMethod.POST)
	public String doChangeProfile(@ModelAttribute("userBean") User user, BindingResult result)
	{
		try {
			if (result.hasErrors())
				throw new Exception("Formulário inválido");
			
			this.userService.update(user);
			return "redirect:/user/profile";
		} catch (Exception e) {
			e.printStackTrace();
			return "user/form";
		}
	}
	
	@RequestMapping(value="delete/{id}",method=RequestMethod.GET)
	public String doDelete(@PathVariable Long id)
	{
		this.userService.remove(id);
		return "redirect:/user";
	}
	
	@RequestMapping(value="info",method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> doGetUserInfo(HttpServletRequest request) throws Exception
	{
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		StringBuilder out = new StringBuilder();
		out.append("{ \"user\" : ");
		out.append(String.format("{ \"id\" : \"%s\" , \"realname\" : \"%s\" , \"username\" : \"%s\"}", 
				user.getId(), user.getRealname(), user.getUsername()));
		out.append("}");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		return new ResponseEntity<String>(out.toString(), headers, HttpStatus.OK);
	}
}