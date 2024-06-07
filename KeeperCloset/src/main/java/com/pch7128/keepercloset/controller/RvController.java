package com.pch7128.keepercloset.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pch7128.keepercloset.dto.BoxSizeDTO;
import com.pch7128.keepercloset.dto.PrincipalDetails;
import com.pch7128.keepercloset.dto.Reservation;
import com.pch7128.keepercloset.dto.StorageDTO;
import com.pch7128.keepercloset.oauth.OAuth2UserInfo;
import com.pch7128.keepercloset.repository.RvMapper;
import com.pch7128.keepercloset.repository.RvRepository;
import com.pch7128.keepercloset.svc.RvSvc;

@Controller
@RequestMapping("/rv")
public class RvController {
	
	@Autowired
	private RvSvc rsvc;
	
	@GetMapping("/keeping")
	public String bookingPage(@AuthenticationPrincipal PrincipalDetails principal,Model m) {
		System.out.println(principal.getMember().getUnum());
		if(principal!=null) {
			m.addAttribute("unum", principal.getMember().getUnum());
		} else {
			m.addAttribute("unum", "");
		}

		return "kc/rv/booking";
	}
	
	
	@PostMapping("/keeping")
	public String reservation(Reservation rv,Model m,@AuthenticationPrincipal PrincipalDetails principal,
			@RequestParam("stnum") int stnum) {
		int unum = principal.getMember().getUnum();
		System.out.println(stnum);
		
		Reservation result=rsvc.saveRv(unum, rv,stnum);
		if(result==null) {
			return "kc/rv/bookingFail";
		}
		return "kc/rv/bookingResult";
	}
	
	
	

}
