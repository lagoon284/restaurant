package com.project.restaurant.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.restaurant.util.WebHelper;


@Controller
@RequestMapping(value = "/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private WebHelper web;
	
	/**
	 * 회원가입 페이지
	 * @return
	 */
	@GetMapping(value = "/userRegistView")
	public String userRegistView(Model model) {
		return "/user/registView";
	}
	
	
	/**
	 * 비밀번호 찾기 페이지
	 * @return
	 */
	@GetMapping(value = "/userFindPwView")
	public String userFindPwView(Model model) {
		return "/user/findPwView";
	}
	
	
	/**
	 * 회원가입 메소드
	 * 회원가입 전 아이디 및 이메일 중복검사 실행
	 * @param userInfo
	 * @return
	 */
	@PostMapping(value = "/userRegist.do")
	public String userRegist(Model model, User user) {

		// 회원가입
		userService.insertUser(user);
		
		return "redirect:/";
	}
	
	
	/**
	 * 회원가입 시 중복 아이디 체크 Ajax
	 * @param userId
	 * @return
	 */
	@PostMapping(value = "/duplicationUserId")
	@ResponseBody
	public int duplicateUserId(Model model, String userId) {
		
		int result = userService.duplicationUserId(userId);
		
		return result;
	}
	
	
	
	/**
	 * 회원가입 시 중복 이메일 체크 Ajax
	 * @param email
	 * @return
	 
	@RequestMapping(value = "/duplicationEmail")
	@ResponseBody
	public int duplicationEmail(String email) {
		
		int result = userServiceImpl.duplicationEmail(email);
		
		return result;
	}
	*/
	
	
	/**
	 * (사용자) 로그인
	 * @param request
	 * @param user_id - 아이디
	 * @param user_pw - 비밀번호
	 * @return - 메인 페이지
	 */
	@PostMapping(value = "/login.do")
	@ResponseBody
	public User userLogin(Model model, HttpServletRequest request, 
						  @RequestParam(value = "userId") String userId, 
						  @RequestParam(value = "userPw") String userPw, 
						  @RequestParam(value = "keep", required = false) String keep) {
		
		User user = userService.loginUserInfo(userId, userPw);
		HttpSession session = request.getSession();
		
		if (user != null && keep.equals("N")) {
			session.setAttribute("userId", user.getUserId());
			session.setAttribute("name", user.getName());
			session.setAttribute("userType", user.getUserType());
			session.setAttribute("loginType", user.getLoginType());
		} else if (user != null && keep.equals("Y")) {
			session.setAttribute("userId", user.getUserId());
			session.setAttribute("name", user.getName());
			session.setAttribute("userType", user.getUserType());
			session.setAttribute("loginType", user.getLoginType());
			session.setMaxInactiveInterval(60 * 10 * 1);
		}
		
		return user;
	}
	
	
	
	/**
	 * (사용자) 로그아웃
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/logout.do")
	public String logout(HttpServletRequest request) {
		
		HttpSession session = request.getSession(false);
		session.removeAttribute("userId");
		session.removeAttribute("userType");
		session.invalidate();
		
		return "redirect:/";
	}
	
	
	

}
