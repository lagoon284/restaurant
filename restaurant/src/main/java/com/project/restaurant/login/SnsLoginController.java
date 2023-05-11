package com.project.restaurant.login;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.project.restaurant.user.User;
import com.project.restaurant.user.UserServiceImpl;

@RestController
@RequestMapping("/snsLogin")
public class SnsLoginController {
	
	@Autowired
	private UserServiceImpl userService;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private HttpServletResponse response;

//	private String apiResult = null;
	
	/* NaverLoginBO */
	private NaverLoginBO naverLoginBO;
	
	/* KakaoLoginBO*/
	private KakaoLoginBO kakaoLoginBO;

	/* googleLoginBO*/
	private GoogleLoginBO googleLoginBO;
	
	@Autowired
	private void setNaverLoginBO(NaverLoginBO naverLoginBO) {
		this.naverLoginBO = naverLoginBO;
	}
	
	@Autowired
	private void setKakaoLoginBO(KakaoLoginBO kakaoLoginBO) {
		this.kakaoLoginBO = kakaoLoginBO;
	}
	
	@Autowired
	private void setGoogleLoginBO(GoogleLoginBO googleLoginBO) {
		this.googleLoginBO = googleLoginBO;
	}
	
	
	/**
	 * SNS로그인 (카카오)
	 * @param 	user - 사용자
	 * @return	index
	 * @throws IOException 
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping(value= "/kakao", method = RequestMethod.GET)
	public ModelAndView kakaoLogin(User user, Model model, @RequestParam String code, @RequestParam String state) 
			throws Exception {
		
		ModelAndView mav = new ModelAndView("/index");

		HttpSession session = request.getSession();
		
		OAuth2AccessToken oauthToken = kakaoLoginBO.getAccessToken(session, code, state);
		System.out.println("oauthToken	::	" + oauthToken);
		
		//로그인 사용자 정보를 읽어온다.
		String apiResult = kakaoLoginBO.getUserProfile(oauthToken);
	    
        // apiResult값을 JSON형태로 변환
	    JSONParser jsonParser = new JSONParser();
	    JSONObject jsonObj = (JSONObject) jsonParser.parse(apiResult);
		JSONObject response_obj = (JSONObject) jsonObj.get("kakao_account");			
		JSONObject profile = (JSONObject)response_obj.get("profile");
		
		System.out.println("kakaoLogin	apiResult		::		" + apiResult);
		System.out.println("kakaoLogin	Result		::		" + response_obj);
		System.out.println("profile		::		" + profile);		
		
		// response의 데이터 파싱
		String name = (String) profile.get("nickname");
		String email = (String) response_obj.get("email");
		String age = (String) response_obj.get("age");
		String mobile = (String) response_obj.get("mobile");
		String gender = (String) response_obj.get("gender");
		String birthDay = (String) response_obj.get("birthday");
		
		System.out.println("name	::	" + name);
		System.out.println("email	::	" + email);
		System.out.println("age		::	" + age);
		System.out.println("mobile	::	" + mobile);
		System.out.println("gender	::	" + gender);
		System.out.println("birthDay	::	" + birthDay);
		
		int result = userService.duplicationUser(email, "kakao");
		
		if (result <= 0) {
			user.setUserId(email);
			user.setName(name);
			user.setTel(mobile);
			
			if ("male".equals(gender)) {
				user.setGender("male");
			} else if ("female".equals(gender)) {
				user.setGender("female");
			} else {
				user.setGender("none");
			}
			
			user.setBirthday(birthDay);
			user.setUserType("normal");
			user.setLoginType("kakao");
			
			userService.insertUser(user);
		}
		
		session.setAttribute("userId", email);
		session.setAttribute("name", name);
		session.setAttribute("userType", user.getUserType());
		session.setAttribute("loginType", user.getLoginType());
		session.setMaxInactiveInterval(60 * 10 * 1);
		
		return mav;
		
	}
	
	
	
	/**
	 * SNS로그인 (네이버)
	 * @param user
	 * @param model
	 * @param request
	 * @param code
	 * @param state
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="/naver", method = RequestMethod.GET)
	public ModelAndView naverLogin(User user, Model model, @RequestParam String code, @RequestParam String state) 
			throws Exception {
		
		ModelAndView mav = new ModelAndView("/index");
		
		System.out.println("NaverLogin Start!!!!!!!");
		
		HttpSession session = request.getSession();
		
		OAuth2AccessToken oauthToken = naverLoginBO.getAccessToken(session, code, state);
		System.out.println("oauthToken	::	" + oauthToken);
		
        //로그인 사용자 정보를 읽어온다.
		String apiResult = naverLoginBO.getUserProfile(oauthToken);
		System.out.println("apiResult	::	" + apiResult);
	    
        // apiResult값을 JSON형태로 변환
	    JSONParser jsonParser = new JSONParser();
	    JSONObject jsonObj = (JSONObject) jsonParser.parse(apiResult);
		JSONObject response_obj = (JSONObject) jsonObj.get("response");	
		
		System.out.println("jsonObj   ::   " + jsonObj);
		
		// response의 데이터 파싱
		String name = (String) response_obj.get("name");
		String email = (String) response_obj.get("email");
		String age = (String) response_obj.get("age");
		String mobile = (String) response_obj.get("mobile");
		String gender = (String) response_obj.get("gender");
		String birthyear = (String)response_obj.get("birthyear");
		String birthday = (String) response_obj.get("birthday");

		/** 네이버로 회원가입 시 중복회원 검사 */
		int result = userService.duplicationUser(email, "naver");
		
		if (result <= 0) {
			user.setUserId(email);
			user.setName(name);
			user.setTel(mobile);
			
			if ("M".equals(gender)) {
				user.setGender("male");
			} else if ("W".equals(gender)) {
				user.setGender("female");
			} else {
				user.setGender("none");
			}
			
			user.setBirthday(birthyear + "-" + birthday);
			user.setUserType("normal");
			user.setLoginType("naver");
			
			userService.insertUser(user);
		}
		
		session.setAttribute("userId", email);
		session.setAttribute("name", name);
		session.setAttribute("userType", user.getUserType());
		session.setAttribute("loginType", user.getLoginType());
		session.setMaxInactiveInterval(60 * 10 * 1);
		
		return mav;
        
	}
	
	
	/**
	 * 스크립트 사용 시
	 * SNS로그인 (네이버)
	 * @param user
	 * @param request
	 * @param state
	 * @param user_id
	 * @param name
	 * @param birthYear
	 * @param birthday
	 * @param gender
	 * @param tel
	 * @return
	 */
//	@RequestMapping("/naver")
//	public String naverLogin(User user, HttpServletRequest request, @RequestParam(value = "user_id") String user_id, 
//							 @RequestParam(value = "user_pw") String user_pw, @RequestParam(value = "name") String name,
//							 @RequestParam(value = "birthyear") String birthYear, @RequestParam(value = "birthday") String birthday,
//							 @RequestParam(value = "gender") String gender, @RequestParam(value = "tel") String tel,
//							 @RequestParam(value = "login_type") String login_type) {
//		
//		System.out.println("Naver Login Start!!!!!");
//		
//		HttpSession session = request.getSession();
//		int result = userServiceImpl.duplicationUser(user_id, login_type);
//		
//		System.out.println("result	::	" + result);
//		System.out.println("user	::	" + user);
//		System.out.println("user_id	::	" + user_id);
//		System.out.println("name	::	" + name);
//		System.out.println("birthYear	::	" + birthYear);
//		System.out.println("birthday	::	" + birthday);
//		System.out.println("gender	::	" + gender);
//		System.out.println("tel	::	" + tel);
//		
//		if (result <= 0) {
//			user.setUser_id(user_id);
//			user.setName(name);
//			user.setBirthday(birthYear + "-" + birthday);
//			
//			if ("M".equals(gender)) {
//				user.setGender("male");
//			} else {
//				user.setGender("female");
//			}
//			
//			user.setTel(tel);
//			user.setUser_type("normal");
//			user.setLogin_type(login_type);
//			
//			userServiceImpl.insertUser(user);
//		}
//		
//		session.setAttribute("user_id", user_id);
//		session.setAttribute("name", name);
//		session.setAttribute("user_type", user.getUser_type());
//		session.setAttribute("login_type", user.getLogin_type());
//		session.setMaxInactiveInterval(60 * 10 * 1);
//		
//		System.out.println("Naver Login End!!!!!");
//		
//		return "redirect:/";
//	}
	
	
	@ResponseBody
	@RequestMapping(value="/google", method = RequestMethod.GET)
	public ModelAndView googleLogin(User user,  Model model, @RequestParam String code, @RequestParam String state)
				throws Exception {
		
			ModelAndView mav = new ModelAndView("/index");
			
			System.out.println("GoogleLogin Start!!!!!!!");
			
			HttpSession session = request.getSession();
			
			OAuth2AccessToken oauthToken = googleLoginBO.getAccessToken(session, code, state);
			System.out.println("oauthToken	::	" + oauthToken);
			
	        //로그인 사용자 정보를 읽어온다.
			String apiResult = googleLoginBO.getUserProfile(oauthToken);
//			System.out.println("apiResult	::	" + apiResult);
			
		    
			
	        // apiResult값을 JSON형태로 변환
		    JSONParser jsonParser = new JSONParser();
		    JSONObject jsonObj = (JSONObject) jsonParser.parse(apiResult);
			JSONObject response_obj = (JSONObject) jsonObj.get("response");	
			
			// response의 데이터 파싱
			String name = (String) response_obj.get("name");
			String email = (String) response_obj.get("email");
			String age = (String) response_obj.get("age");
			String mobile = (String) response_obj.get("mobile");
			String gender = (String) response_obj.get("gender");
			String birthyear = (String)response_obj.get("birthyear");
			String birthday = (String) response_obj.get("birthday");

			/** 네이버로 회원가입 시 중복회원 검사 */
			int result = userService.duplicationUser(email, "google");
			
			if (result <= 0) {
				user.setUserId(email);
				user.setName(name);
				user.setTel(mobile);
				
				if ("M".equals(gender)) {
					user.setGender("male");
				} else if ("W".equals(gender)) {
					user.setGender("female");
				} else {
					user.setGender("none");
				}
				
				user.setBirthday(birthyear + "-" + birthday);
				user.setUserType("normal");
				user.setLoginType("google");
				
				userService.insertUser(user);
			}
			
			session.setAttribute("userId", email);
			session.setAttribute("name", name);
			session.setAttribute("userType", user.getUserType());
			session.setAttribute("loginType", user.getLoginType());
			session.setMaxInactiveInterval(60 * 10 * 1);
			
			return mav;
	}
	
}
