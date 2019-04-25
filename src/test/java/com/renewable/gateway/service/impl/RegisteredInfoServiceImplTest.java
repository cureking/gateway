package com.renewable.gateway.service.impl;

import com.renewable.gateway.BaseSpringTestCase;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.dao.SensorRegisterMapper;
import com.renewable.gateway.pojo.SensorRegister;
import com.renewable.gateway.service.IRegisteredInfoService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.TestCase.assertTrue;

/**
 * RegisteredInfoServiceImpl Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Mar 12, 2019</pre>
 */
@RunWith(SpringJUnit4ClassRunner.class)

public class RegisteredInfoServiceImplTest extends BaseSpringTestCase {

    //    @Autowired
//    private SensorRegisterMapper sensorRegisterMapper;
    @Autowired
    private SensorRegisterMapper sensorRegisterMapper;
    @Autowired
    private IRegisteredInfoService iregisteredInfoService;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getSerialCount()
     */
    @Test
    public void testGetSerialCount() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getSerialList()
     */
    @Test
    public void testGetSerialList() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getSensorList(String port)
     */
    @Test
    public void testGetSensorListPort() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getSensorList()
     */
    @Test
    public void testGetSensorList() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getSensor(String port, String address)
     */
    @Test
    public void testGetSensor() throws Exception {
//TODO: Test goes here...
        ServerResponse<SensorRegister> response = iregisteredInfoService.getSensor("COM1", "01");
        System.out.println(response);
        assertTrue(response.isSuccess());
    }

    /**
     * Method: setNickname(String port, String address, String nickname)
     */
    @Test
    public void testSetNickname() throws Exception {
//TODO: Test goes here... 
    }


} 
