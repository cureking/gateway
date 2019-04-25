//package com.renewable.gateway.common;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerExceptionResolver;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.view.json.MappingJacksonJsonView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * @Description：
// * @Author: jarry
// * @Date: 1/19/2019 0:11
// */
//@Slf4j
//@Component
////@Component注解，用于非Controller,Service,Repository的组件中
////都表示其对应文件是Spring的Bean组件，要注入到Spring容器中
////HandlerExceptionResolver接口的实现，表示要进行全局异常处理
//public class ExceptionResolver implements HandlerExceptionResolver {
//    @Override
//    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
//        log.error("{} Exception ",httpServletRequest.getRequestURI(),e);
//        //这里不希望返回一个ModelAndView，而是希望返回JsonView
//        //之所以不使用MappingJackson2JsonView，是因为我们porm文件中引入的是Jackson1.9.0，而不是2.x。故无法支持MappingJackson2JsonView。
//        ModelAndView modelAndView = new ModelAndView(new MappingJacksonJsonView());
//
//        //模仿ServerResponse这一通用返回格式（但是由于该函数只能返回ModelAndView，所以不可以直接返回ServerResponse）
//        modelAndView.addObject("status",ResponseCode.ERROR.getCode());  //既然报错了，那必然是异常
//        modelAndView.addObject("msg","接口异常，详情请查看服务器日志");
//        modelAndView.addObject("data",e.toString());
//        return modelAndView;
//    }
//}
