package com.project.restaurant.login;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
	
	/* NaverLoginBO */
	private NaverLoginBO naverLoginBO;
	
	/* kakaoLoginBO */
	private KakaoLoginBO kakaoLoginBO;
	
	@Value("${kakao.auth.requestUrl}")
	private String requestUrl;
	
	@Value("${kakao.auth.grantType}")
	private String grantType;
	
	@Value("${kakao.auth.clientId}")
	private String clientId;
	
	@Value("${kakao.auth.redirectUrl}")
	private String redirectUrl;
	
	@Autowired
	private void setNaverLoginBO(NaverLoginBO naverLoginBO) {
		this.naverLoginBO = naverLoginBO;
	}
	
	@Autowired
	private void setKakaoLoginBO(KakaoLoginBO kakaoLoginBO) {
		this.kakaoLoginBO = kakaoLoginBO;
	}
	

	/**
	 * 로그인 페이지
	 * @return
	 */
	@GetMapping(value = "/loginView")
	public String userLoginView(Model model, HttpSession session) {
		
		/* 네이버 로그인 인증 URL을 생성하기 위하여 naverLoginBO클래스의 getAuthorizationUrl메소드 호출 */
		String naverAuthUrl =  naverLoginBO.getAuthorizationUrl(session);
		/* 카카오 로그인 인증 URL을 생성하기 위하여 kakaoLoginBO클래스의 getAuthorizationUrl메소드 호출 */
		String kakaoAuthUrl =  kakaoLoginBO.getAuthorizationUrl(session);
		
		/* 객체 바인딩 */
		model.addAttribute("naverAuthUrl", naverAuthUrl);
		model.addAttribute("kakaoAuthUrl", kakaoAuthUrl);
		
		System.out.println("naver	::	" + naverAuthUrl);
		System.out.println("kakao	::	" + kakaoAuthUrl);
		
		model.addAttribute("requestUrl", requestUrl);
		model.addAttribute("grantType", grantType);
		model.addAttribute("clientId", clientId);
		model.addAttribute("redirectUrl", redirectUrl);
		
		return "/user/loginView";
	}
	

	
	
	/**
	 * 비밀번호 찾기 페이지
	 * @return
	 */
	@GetMapping(value = "/findPwView")
	public String userFindPwView() {
		return "/user/findPwView";
	}
	
	/**
	@RequestMapping("/callback")
	public ModelAndView naverCallback() {
		
		ModelAndView mav = new ModelAndView("/user/naver_callback");
		
		return mav;
	}
	*/
}
