package com.rhy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: Herion Lemon
 * @date: 2021年05月11日 13:21:00
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description:
 */
@Controller
public class AnnotationController {
	@RequestMapping("/annotation")
	@ResponseBody
	public String test(HttpServletRequest request, HttpServletResponse response,String param){
		return "annotation";
	}
}
