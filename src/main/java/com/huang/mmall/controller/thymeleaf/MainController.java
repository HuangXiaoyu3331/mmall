package com.huang.mmall.controller.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


/**
 * MainController class
 *
 * @author hxy
 * @date 2019/1/22
 */
@Controller
@RequestMapping("/thymeleaf")
public class MainController {

    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("/index");
        modelAndView.addObject("name", "黄晓宇");
        modelAndView.addObject("age", "18");
        return modelAndView;
    }

    @RequestMapping("/error")
    public ModelAndView serverError(ModelAndView modelAndView){
        modelAndView.setViewName("index");
        int a = 1/0;
        return modelAndView;
    }
}
