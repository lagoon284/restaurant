package com.project.restaurant;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class Index {

	@GetMapping(value = "/")
	public String index(Model model) {
		return "/index";
	}
}
