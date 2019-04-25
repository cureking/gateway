package com.renewable.gateway; /**
 * @Description：
 * @Author: jarry
 */

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author arganzheng
 */
@RunWith(SpringJUnit4ClassRunner.class) //指定执行类
@ContextConfiguration(locations = {"classpath:applicationContext.xml"}) //引入spring配置。
//@link:https://blog.csdn.net/HELLOMRP/article/details/79736502
//@Transactional
public class BaseSpringTestCase {
}

