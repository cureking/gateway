package com.renewable.gateway.util;

import com.mathworks.toolbox.javabuilder.MWException;
import sinfit.Calcul;

import java.util.Arrays;

/**
 * @Description：
 * @Author: jarry
 */
public class MatlabUtil {

    /**
     *
     * @param initMeasureArray
     * @param R
     * @return  返回的数组，包含四个参数，分别是合倾角，方向角，X，Y。
     */
    public static Object[] initAngleTotalCalMatlab(double[][] initMeasureArray, double R){
        Calcul calcul = null;
        try {
            calcul = new Calcul();
        } catch (MWException e) {
            e.printStackTrace();
        }
        Object[] resultObjectArray = null;
        Object object=null;
        try {
            resultObjectArray = calcul.sinfit(4,initMeasureArray[0][0],initMeasureArray[0][1],initMeasureArray[0][0],initMeasureArray[0][1],initMeasureArray[0][0],initMeasureArray[0][1],initMeasureArray[0][0],initMeasureArray[0][1],R);
            System.out.println(Arrays.toString(resultObjectArray));
        } catch (MWException e) {
            e.printStackTrace();
        }

//        double[] resultArray = null;
//        for (int i =0;i<resultObjectArray.length;i++){
////            resultArray[i] = Double.parseDouble(resultObjectArray[i].toString());     //无法将9.0000e+01 - 3.9520e+02i这样的字符串转为对应数值，包括Bigdecimbel也不可以。
////            resultArray[i] = (double) resultObjectArray[i];             //java.lang.ClassCastException: com.mathworks.toolbox.javabuilder.MWNumericArray cannot be cast to java.lang.Double     //实在想不通为什么MWNumericArray的数据结构信息还在，这是matlab的数据结构
//
//            //现在两个思路，一个我去追根溯源，寻找数据类型与MWNumericArray的信息，另一个我去写一个函数用来解析9.0000e+01 - 3.9520e+02i这样的信息。（目前怀疑matlab出来的信息被强（或是主动）转为了String。（他们简单了。我们辛苦了。。。）
//            //可以考虑后者，但在那之前需要构建一个复数类，并了解C#与C++是如何正确解析复数到double的。
//        }
//        MWNumericArray

        return resultObjectArray;
    }
}
