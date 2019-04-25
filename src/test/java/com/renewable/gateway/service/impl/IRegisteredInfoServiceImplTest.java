package com.renewable.gateway.service.impl;

import com.renewable.gateway.BaseSpringTestCase;
import com.renewable.gateway.service.IRegisteredInfoService;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;

/** 
* IRegisteredInfoServiceImpl Tester. 
* 
* @author <Authors name> 
* @since <pre>Apr 15, 2019</pre> 
* @version 1.0 
*/ 
public class IRegisteredInfoServiceImplTest extends BaseSpringTestCase {

    @Autowired
    private IRegisteredInfoService iRegisteredInfoService;

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: text() 
* 
*/ 
@Test
public void testText() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getSerialCount() 
* 
*/ 
@Test
public void testGetSerialCount() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getSerialList() 
* 
*/ 
@Test
public void testGetSerialList() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getSensorCount() 
* 
*/ 
@Test
public void testGetSensorCount() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getSensorList() 
* 
*/ 
@Test
public void testGetSensorList() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getSensorListByPort(String port) 
* 
*/ 
@Test
public void testGetSensorListByPort() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getSensor(String port, String address) 
* 
*/ 
@Test
public void testGetSensorForPortAddress() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getSensor(int id) 
* 
*/ 
@Test
public void testGetSensorId() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: setNickname(int id, String nickname) 
* 
*/ 
@Test
public void testSetNickname() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: insertSensor(SensorRegister sensorRegister) 
* 
*/ 
@Test
public void testInsertSensor() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: updateSensor(SensorRegister sensorRegister) 
* 
*/ 
@Test
public void testUpdateSensor() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: deleteSensor(int id) 
* 
*/ 
@Test
public void testDeleteSensor() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: initTotalAngleCal(double[][] initMeasureArray, double R) 
* 
*/ 
@Test
public void testInitTotalAngleCal() throws Exception { 
//TODO: Test goes here... 
} 


/** 
* 
* Method: checkValid(String str, String type) 
* 
*/ 
@Test
public void testCheckValid() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = IRegisteredInfoServiceImpl.getClass().getMethod("checkValid", String.class, String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: checkValidPortAndAddress(String port, String address) 
* 
*/ 
@Test
public void testCheckValidPortAndAddress() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = IRegisteredInfoServiceImpl.getClass().getMethod("checkValidPortAndAddress", String.class, String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: initPlaneAngle(double p1h, double p1angel, double p2h, double p2angel, double p3h, double p3angel, double R) 
* 
*/ 
@Test
public void testInitPlaneAngle() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = IRegisteredInfoServiceImpl.getClass().getMethod("initPlaneAngle", double.class, double.class, double.class, double.class, double.class, double.class, double.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/

double[][] testArray = {{0,2},{120,3},{240,4},{280,3}};

} 

} 
