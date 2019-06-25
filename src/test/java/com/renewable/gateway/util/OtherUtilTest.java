package com.renewable.gateway.util;

import com.renewable.gateway.BaseSpringTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.renewable.gateway.util.OtherUtil.calAngleTotal;
import static junit.framework.TestCase.assertTrue;

/**
 * OtherUtil Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Apr 12, 2019</pre>
 */
public class OtherUtilTest extends BaseSpringTestCase {

	@Before
	public void before() throws Exception {
	}

	@After
	public void after() throws Exception {
	}

	/**
	 * Method: calAngleTotal(double angleX, double angleY)
	 */
	@Test
	public void testCalAngleTotal() throws Exception {
//TODO: Test goes here...
		System.out.println(calAngleTotal(0.00577, 0.00003, 0, 1));
		assertTrue(Math.abs(calAngleTotal(0.00577, 0.00003, 0, 1)[0] - 0.0058) < 0.001);
	}

	/**
	 * Method: angle2radian(double angle)
	 */
	@Test
	public void testAngle2radian() throws Exception {
//TODO: Test goes here... 
	}

	/**
	 * Method: radian2angle(double radian)
	 */
	@Test
	public void testRadian2angle() throws Exception {
//TODO: Test goes here... 
	}

	/**
	 * Method: subBytes(byte[] src, int begin, int count)
	 */
	@Test
	public void testSubBytes() throws Exception {
//TODO: Test goes here... 
	}

	/**
	 * Method: subIntegers(int[] src, int begin, int count)
	 */
	@Test
	public void testSubIntegers() throws Exception {
//TODO: Test goes here... 
	}

	/**
	 * Method: arraySplitByByte(byte[] src)
	 */
	@Test
	public void testArraySplitByByte() throws Exception {
//TODO: Test goes here... 
	}

	/**
	 * Method: arraySplitByInt(int[] src)
	 */
	@Test
	public void testArraySplitByInt() throws Exception {
//TODO: Test goes here... 
	}

	/**
	 * Method: getUnsignedByte(byte data)
	 */
	@Test
	public void testGetUnsignedByte() throws Exception {
//TODO: Test goes here... 
	}

	/**
	 * Method: bcdArray2intArray(byte[] bcdData)
	 */
	@Test
	public void testBcdArray2intArray() throws Exception {
//TODO: Test goes here... 
	}

	/**
	 * Method: bcdArray2HexArray(byte[] bcdData)
	 */
	@Test
	public void testBcdArray2HexArray() throws Exception {
//TODO: Test goes here... 
	}

	/**
	 * Method: main(String[] args)
	 */
	@Test
	public void testMain() throws Exception {
//TODO: Test goes here... 
	}


} 
