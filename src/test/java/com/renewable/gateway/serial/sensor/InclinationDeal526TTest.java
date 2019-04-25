package com.renewable.gateway.serial.sensor;

import com.renewable.gateway.BaseSpringTestCase;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.Inclination;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import static junit.framework.TestCase.assertTrue;

/** 
* InclinationDeal526T Tester. 
* 
* @author <Authors name> 
* @since <pre>Apr 12, 2019</pre> 
* @version 1.0 
*/ 
public class InclinationDeal526TTest extends BaseSpringTestCase {

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: origin2Object(byte[] originBuffer) 
* 
*/ 
@Test
public void testOrigin2Object() throws Exception { 
//TODO: Test goes here...
    byte[] test3 = {104, 13, 0, -124, -16, 0, -87, 16, 37, -106, 3, 25, 37, 4};
    ServerResponse<Inclination> serverResponse = InclinationDeal526T.origin2Object(test3);
    Inclination inclination = serverResponse.getData();
    assertTrue(inclination.getAngleX() == -0.109 && inclination.getAngleY() == -2.596 && inclination.getTemperature() == 31.925);
} 

/** 
* 
* Method: command2Origin(int address, InclinationConst.InclinationSensor1Enum inclinationSensor1Enum) 
* 
*/ 
@Test
public void testCommand2OriginForAddressInclinationSensor1Enum() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: command2Origin(int address, InclinationConst.InclinationSensor1Enum inclinationSensor1Enum, String data) 
* 
*/ 
@Test
public void testCommand2OriginForAddressInclinationSensor1EnumData() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: main(String[] args) 
* 
*/ 
@Test
public void testMain() throws Exception { 
//TODO: Test goes here... 
} 


/** 
* 
* Method: originData2Object(byte[] originData) 
* 
*/ 
@Test
public void testOriginData2Object() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = InclinationDeal526T.getClass().getMethod("originData2Object", byte[].class); 
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
* Method: list2OriginData(List<Integer> list) 
* 
*/ 
@Test
public void testList2OriginData() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = InclinationDeal526T.getClass().getMethod("list2OriginData", List<Integer>.class); 
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
* Method: unsignedShortToByte2(int s) 
* 
*/ 
@Test
public void testUnsignedShortToByte2S() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = InclinationDeal526T.getClass().getMethod("unsignedShortToByte2", int.class); 
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
* Method: int2byte(int data) 
* 
*/ 
@Test
public void testInt2byte() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = InclinationDeal526T.getClass().getMethod("int2byte", int.class); 
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
* Method: int2byteNoChange(int data) 
* 
*/ 
@Test
public void testInt2byteNoChange() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = InclinationDeal526T.getClass().getMethod("int2byteNoChange", int.class); 
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
* Method: objece2ListData(int command, int address) 
* 
*/ 
@Test
public void testObjece2ListDataForCommandAddress() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = InclinationDeal526T.getClass().getMethod("objece2ListData", int.class, int.class); 
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
* Method: objece2ListData(int command, int address, Integer data) 
* 
*/ 
@Test
public void testObjece2ListDataForCommandAddressData() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = InclinationDeal526T.getClass().getMethod("objece2ListData", int.class, int.class, Integer.class); 
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
* Method: origin2Double(byte[] originData) 
* 
*/ 
@Test
public void testOrigin2Double() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = InclinationDeal526T.getClass().getMethod("origin2Double", byte[].class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

} 
