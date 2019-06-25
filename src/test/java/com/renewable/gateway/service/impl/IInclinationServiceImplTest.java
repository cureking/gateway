package com.renewable.gateway.service.impl;

import com.renewable.gateway.BaseSpringTestCase;
import com.renewable.gateway.common.sensor.InclinationConst;
import com.renewable.gateway.service.IInclinationService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * IInclinationServiceImpl Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Apr 15, 2019</pre>
 */
public class IInclinationServiceImplTest extends BaseSpringTestCase {

	@Autowired
	private IInclinationService iInclinationService;

	@Before
	public void before() throws Exception {
	}

	@After
	public void after() throws Exception {
	}

	/**
	 * Method: getInclinationDataList(int pageNum, int pageSize)
	 */
	@Test
	public void testGetInclinationDataList() throws Exception {
//TODO: Test goes here... 
	}

	/**
	 * Method: getInclinationDataListTime(String startTime, String endTime)
	 */
	@Test
	public void testGetInclinationDataListTime() throws Exception {
//TODO: Test goes here... 
	}

	/**
	 * Method: sendDataInclination(String port, String baudrate, InclinationConst.InclinationSensor1Enum inclinationSensor1Enum)
	 */
	@Test
	public void testSendDataInclination() throws Exception {
//TODO: Test goes here... 
	}

	/**
	 * Method: receiveData(String port, int baudrate, byte[] originBuffer)
	 */
	@Test
	public void testReceiveData() throws Exception {
//TODO: Test goes here... 
	}

	/**
	 * Method: inclinationData2DB(Inclination inclination)
	 */
	@Test
	public void testInclinationData2DB() throws Exception {
//TODO: Test goes here... 
	}

	/**
	 * Method: cleanDataTask(SensorRegister sensorRegister)
	 */
	@Test
	public void testCleanDataTask() throws Exception {
//TODO: Test goes here... 
	}

	/**
	 * Method: initTotalAngleCal(double[][] initMeasureArray, double R)
	 */
	@Test
	public void testInitTotalAngleCal() throws Exception {
//TODO: Test goes here...
	}

/**
 *
 * Method: initAngleTotalCalMatlab(double[][] initMeasureArray, double R)
 *
 */
//@Test
//public void testInitAngleTotalCalMatlab() throws Exception {
////TODO: Test goes here...
//    double[][] testArray1 = {{0,315},{0,225},{1.707,90},{0.1,270}};    //R=1    //Y>0
//    double[][] testArray2 = {{0,315},{0,45},{1.707,180},{0.1,0}};    //R=1    //Y>0
//    double[][] testArray3 = {{1.707,180},{0,315},{0,45},{0.1,0}};    //R=1    //Y>0
//    double[][] testArray4 = {{0,2},{3,120},{4,240},{3,280}};    //R=8    //Y>0
//    double[][] testArray5 = {{3,120},{4,240},{2,0},{3,280}};    //R=9    //Y>0
//    double[][] testArray6 = {{3.1,60},{2.9,180},{3.2,270},{3,225}};    //R=9   //Y>0
//    double[][] testArray7 = {{3.1,50},{2.9,170},{3.2,270},{3,215}};    //R=9    //Y>0
//
//
//    double[] result1 = iInclinationService.initAngleTotalCalMatlab(testArray1,1);
//    double[] result2 = iInclinationService.initAngleTotalCalMatlab(testArray2,1);
//    double[] result3 = iInclinationService.initAngleTotalCalMatlab(testArray3,1);
//    double[] result4 = iInclinationService.initAngleTotalCalMatlab(testArray4,8);
//    double[] result5 = iInclinationService.initAngleTotalCalMatlab(testArray5,9);
//    double[] result6 = iInclinationService.initAngleTotalCalMatlab(testArray6,9);
//    double[] result7 = iInclinationService.initAngleTotalCalMatlab(testArray7,9);
//
////    Assert.assertTrue(Math.abs(result1[0]-45)<0.01&&Math.abs(result1[1]-270)<0.01);
////    Assert.assertTrue(Math.abs(result2[0]-45)<0.01&&Math.abs(result2[1]-0)<0.01);
////    Assert.assertTrue(Math.abs(result3[0]-45)<0.01&&Math.abs(result3[1]-0)<0.01);
//////    Assert.assertTrue(Math.abs(result4[0]-11.915)<0.01&&Math.abs(result4[1]-20)<0.01);
////    Assert.assertTrue(Math.abs(result5[0]-10.623)<0.01&&Math.abs(result5[1]-20)<0.01);
////    Assert.assertTrue(Math.abs(result6[0]-1.7329)<0.01&&Math.abs(result6[1]-173.82)<0.01);
//////    Assert.assertTrue(Math.abs(result7[0]-1.7329)<0.01&&Math.abs(result7[1]-163.82)<0.01);
////
////    System.out.println(Arrays.toString(result1));
////    System.out.println(Arrays.toString(result2));
////    System.out.println(Arrays.toString(result3));
////    System.out.println(Arrays.toString(result4));
////    System.out.println(Arrays.toString(result5));
////    System.out.println(Arrays.toString(result6));
////    System.out.println(Arrays.toString(result7));
//
//}

	/**
	 * Method: calAngleTotal(double angleX, double angleY)
	 */
	@Test
	public void testCalAngleTotal() throws Exception {
//TODO: Test goes here...
/*
try {
   Method method = IInclinationServiceImpl.getClass().getMethod("calAngleTotal", double.class, double.class);
   method.setAccessible(true);
   method.invoke(<Object>, <Parameters>);
} catch(NoSuchMethodException e) {
} catch(IllegalAccessException e) {
} catch(InvocationTargetException e) {
}
*/
	}

	/**
	 * Method: assumbleInclinationListVo(Inclination inclination)
	 */
	@Test
	public void testAssumbleInclinationListVo() throws Exception {
//TODO: Test goes here...
/*
try {
   Method method = IInclinationServiceImpl.getClass().getMethod("assumbleInclinationListVo", Inclination.class);
   method.setAccessible(true);
   method.invoke(<Object>, <Parameters>);
} catch(NoSuchMethodException e) {
} catch(IllegalAccessException e) {
} catch(InvocationTargetException e) {
}
*/
	}

	/**
	 * Method: cleanDataAssort(SensorRegister sensorRegister, Inclination startInclination, Inclination currentInclination)
	 */
	@Test
	public void testCleanDataAssort() throws Exception {
//TODO: Test goes here...
/*
try {
   Method method = IInclinationServiceImpl.getClass().getMethod("cleanDataAssort", SensorRegister.class, Inclination.class, Inclination.class);
   method.setAccessible(true);
   method.invoke(<Object>, <Parameters>);
} catch(NoSuchMethodException e) {
} catch(IllegalAccessException e) {
} catch(InvocationTargetException e) {
}
*/
		System.out.println(System.getProperty("java.library.path"));
	}

	/**
	 * Method: cleanDatabyPeak(SensorRegister sensorRegister, Inclination startInclination, Inclination currentInclination)
	 */
	@Test
	public void testCleanDatabyPeak() throws Exception {
//TODO: Test goes here...
/*
try {
   Method method = IInclinationServiceImpl.getClass().getMethod("cleanDatabyPeak", SensorRegister.class, Inclination.class, Inclination.class);
   method.setAccessible(true);
   method.invoke(<Object>, <Parameters>);
} catch(NoSuchMethodException e) {
} catch(IllegalAccessException e) {
} catch(InvocationTargetException e) {
}
*/
	}

	/**
	 * Method: inclinationDealAssemble(Inclination inclination)
	 */
	@Test
	public void testInclinationDealAssemble() throws Exception {
//TODO: Test goes here...
/*
try {
   Method method = IInclinationServiceImpl.getClass().getMethod("inclinationDealAssemble", Inclination.class);
   method.setAccessible(true);
   method.invoke(<Object>, <Parameters>);
} catch(NoSuchMethodException e) {
} catch(IllegalAccessException e) {
} catch(InvocationTargetException e) {
}
*/
	}

	/**
	 * Method: initPlaneAngle(double p1h, double p1angel, double p2h, double p2angel, double p3h, double p3angel, double R)
	 */
	@Test
	public void testInitPlaneAngle() throws Exception {
//TODO: Test goes here...
/*
try {
   Method method = IInclinationServiceImpl.getClass().getMethod("initPlaneAngle", double.class, double.class, double.class, double.class, double.class, double.class, double.class);
   method.setAccessible(true);
   method.invoke(<Object>, <Parameters>);
} catch(NoSuchMethodException e) {
} catch(IllegalAccessException e) {
} catch(InvocationTargetException e) {
}
*/

		double[][] testArray1 = {{0, 315}, {0, 225}, {1.707, 90}, {0.1, 270}};    //R=1    //Y>0
		double[][] testArray2 = {{0, 315}, {0, 45}, {1.707, 180}, {0.1, 0}};    //R=1    //Y>0
		double[][] testArray3 = {{1.707, 180}, {0, 315}, {0, 45}, {0.1, 0}};    //R=1    //Y>0
		double[][] testArray4 = {{0, 2}, {3, 120}, {4, 240}, {3, 280}};    //R=8    //Y>0
		double[][] testArray5 = {{3, 120}, {4, 240}, {2, 0}, {3, 280}};    //R=9    //Y>0
		double[][] testArray6 = {{3.1, 60}, {2.9, 180}, {3.2, 270}, {3, 225}};    //R=9   //Y>0
		double[][] testArray7 = {{3.1, 50}, {2.9, 170}, {3.2, 270}, {3, 215}};    //R=9    //Y>0


		double[] result1 = iInclinationService.initTotalAngleCal(testArray1, 1);
		double[] result2 = iInclinationService.initTotalAngleCal(testArray2, 1);
		double[] result3 = iInclinationService.initTotalAngleCal(testArray3, 1);
		double[] result4 = iInclinationService.initTotalAngleCal(testArray4, 8);
		double[] result5 = iInclinationService.initTotalAngleCal(testArray5, 9);
		double[] result6 = iInclinationService.initTotalAngleCal(testArray6, 9);
		double[] result7 = iInclinationService.initTotalAngleCal(testArray7, 9);

		Assert.assertTrue(Math.abs(result1[0] - 45) < 0.01 && Math.abs(result1[1] - 270) < 0.01);
		Assert.assertTrue(Math.abs(result2[0] - 45) < 0.01 && Math.abs(result2[1] - 0) < 0.01);
		Assert.assertTrue(Math.abs(result3[0] - 45) < 0.01 && Math.abs(result3[1] - 0) < 0.01);
//    Assert.assertTrue(Math.abs(result4[0]-11.915)<0.01&&Math.abs(result4[1]-20)<0.01);
		Assert.assertTrue(Math.abs(result5[0] - 10.623) < 0.01 && Math.abs(result5[1] - 20) < 0.01);
		Assert.assertTrue(Math.abs(result6[0] - 1.7329) < 0.01 && Math.abs(result6[1] - 173.82) < 0.01);
//    Assert.assertTrue(Math.abs(result7[0]-1.7329)<0.01&&Math.abs(result7[1]-163.82)<0.01);

		System.out.println(Arrays.toString(result1));
		System.out.println(Arrays.toString(result2));
		System.out.println(Arrays.toString(result3));
		System.out.println(Arrays.toString(result4));
		System.out.println(Arrays.toString(result5));
		System.out.println(Arrays.toString(result6));
		System.out.println(Arrays.toString(result7));

	}

	/**
	 * Method: calInitAngleTotal(double angleX, double angleY, double angleInit, InclinationConst.InclinationInstallModeEnum installModeEnum)
	 */
	@Test
	public void testCalInitAngleTotal() throws Exception {
//TODO: Test goes here...

		double result2 = iInclinationService.calInitAngleTotal(0.00577, 0.00003, 1.0, 1.0, 90, InclinationConst.InclinationInstallModeEnum.FOUR)[0];
		System.out.println("result2:" + result2);

	}


}
