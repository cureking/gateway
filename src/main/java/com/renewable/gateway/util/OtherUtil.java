package com.renewable.gateway.util;

import com.renewable.gateway.common.sensor.InclinationConst;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
public class OtherUtil {

    //计算合倾角（不管是否包含初始变量）
    public static double[] calAngleTotal(double angleX, double angleY, double sensorDirect, int installModeEnum) {
        double radianTotal = 0;
        double angleTotal = 0;

        double radianDirect = 0;
        double angleDirect = 0;

        if (angleX == 0 && angleY == 0) {    //这样的条件看起来较为更为清晰，即计算公式无法计算X=Y=0的情况。
            //可以不做处理
        } else {
            radianTotal = Math.acos(1 / Math.sqrt(Math.pow(Math.tan(angle2radian(angleX)), 2) + Math.pow(Math.tan(angle2radian(angleY)), 2) + 1));
        }
        angleTotal = radian2angle(radianTotal);

        //方位角计算（这里默认采用了第一种安装方式，之后可以和算法三集成，封装八种封装方式的不同 情况）。但是我想说，写这个算法文档的写文档水平真的有点低。呵呵。

        //日后扩展点，对不同安装方式，进行不同的调整
        if (installModeEnum == 1) {
            angleX = angleX;
            angleY = angleY;
        }

        //计算方位角     //同样默认传感器方位角sensorDirect为0。我还是忍不住吐槽，难道就不可以简单点嘛。就一个安装方位角度，就提高了至少一倍的编码量，真的值得嘛（而且，我觉得本身就存在一定问题）。
        if (angleY > 0) {
            radianDirect = sensorDirect + Math.atan(Math.tan(angleX) / Math.tan(angleY));
        } else if (angleY == 0) {
            if (angleX < 0) {
                radianDirect = sensorDirect + Math.PI / 2;
            } else if (angleX > 0) {
                radianDirect = sensorDirect - Math.PI / 2;
            }
            //再次吐槽，虽然当倾斜为零，确实没有倾斜方向了。但是无论是从程序，还是从数学严谨角度，都应该设置一个初始值  //由于存在初始化值，这里不做处理
        } else {
            radianDirect = sensorDirect + Math.atan(Math.tan(angleX) / Math.tan(angleY)) + Math.PI;
        }
        //转换弧度至角度
        angleDirect = radian2angle(radianDirect);

        //组装结果
        double[] resultTDArray = {angleTotal, angleDirect};

        return resultTDArray;
    }

    //计算合倾角（包含初始变量）
    //由于不同传感器对应不同的安装情况。安装的方向有八种情况（水平为y的四种与水平与x的四种)。该模块日后可能提取到专门的包中
    public static double[] calInitAngleTotal(double angleX, double angleY, double angleInit, double X, double Y, InclinationConst.InclinationInstallModeEnum installModeEnum) {
        double param1 = angleX;
        double param2 = angleY;

        switch (installModeEnum) {
            case ONE:
                param1 = angleX;
                param2 = angleY;
                break;

            case TWO:
                param1 = angleX;
                param2 = -angleY;
                break;

            case THIRD:
                param1 = -angleX;
                param2 = angleY;
                break;

            case FOUR:
                param1 = -angleX;
                param2 = -angleY;
                break;

            case FIVE:
                param1 = angleY;
                param2 = angleX;
                break;

            case SIX:
                param1 = angleY;
                param2 = -angleX;
                break;

            case SEVEN:
                param1 = -angleY;
                param2 = angleX;
                break;

            case EIGHT:
                param1 = -angleY;
                param2 = -angleX;
                break;

            default:
                log.error("Inclination Install model Enum appear an error:" + installModeEnum.getCode());
        }

        //代入通用公式    计算经过初始角度的修正值
        double x = Math.atan(Math.cos(angleInit) * param1 + Math.sin(angleInit) * param2);
        double y = Math.atan(Math.cos(angleInit) * param1 - Math.sin(angleInit) * param2);


//        System.out.println("x:"+x+" y:"+y);

        //调用已有方法，计算总倾斜合倾角
        return calAngleTotal(x + X, y + Y, 0, 1);
//
//        return 11;
    }


    public static double angle2radian(double angle) {
        double radian = angle * Math.PI / 180;
        return radian;
    }

    public static double radian2angle(double radian) {
        double angle = radian / (Math.PI / 180);
        return angle;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        try {
            System.arraycopy(src, begin, bs, 0, count);
        } catch (Exception e) {
            System.out.println("OtherUtil/subBytes执行异常：" + e.toString());
        }

        return bs;
    }

    public static int[] subIntegers(int[] src, int begin, int count) {
        int[] bs = new int[count];
        try {
            System.arraycopy(src, begin, bs, 0, count);
        } catch (Exception e) {
            System.out.println("OtherUtil/subInteger执行异常：" + e.toString());
        }

        return bs;
    }

    public static byte[] arraySplitByByte(byte[] src) {
        int length = src.length;
        byte[] bs = new byte[length * 2];
        for (int i = 0; i < length; i++) {
            bs[i * 2] = (byte) ((src[i] / 16) & 0xff);
            bs[i * 2 + 1] = (byte) (src[i] % 16 & 0xff);
        }
        return bs;
    }

    public static byte[] arraySplitByInt(int[] src) {
        int length = src.length;
        byte[] bs = new byte[length * 2];
        for (int i = 0; i < length; i++) {
            bs[i * 2] = (byte) ((src[i] / 16) & 0xff);
            bs[i * 2 + 1] = (byte) (src[i] % 16 & 0xff);
        }
        return bs;
    }

    public static int getUnsignedByte(byte data) {      //将data字节型数据转换为0~255 (0xFF 即BYTE)。
        return data & 0x0FF;
    }   //该方法在Byte对象中存在

    public static int[] bcdArray2intArray(byte[] bcdData) {
        int[] resultArray = new int[bcdData.length];

        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = Byte.toUnsignedInt(bcdData[i]);
        }

        return resultArray;
    }

    public static int[] bcdArray2HexArray(byte[] bcdData) {
        int[] resultArray = new int[bcdData.length];

//        System.out.println(Arrays.toString(bcdData));
        String hexStr = HexUtil.bytesToHexString(bcdData);
//        System.out.println(hexStr);

        for (int i = 0; i < resultArray.length; i++) {

//            resultArray[i] = hexStr.charAt(i);
            System.out.println(resultArray[i]);
        }

        return resultArray;
    }

    public static double[] bubbleSort(double[] numbers) {
        double temp = 0;
        int size = numbers.length;
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1 - i; j++) {
                if (numbers[j] > numbers[j + 1])  //交换两数位置
                {
                    temp = numbers[j];
                    numbers[j] = numbers[j + 1];
                    numbers[j + 1] = temp;
                }
            }
        }
        return numbers;
    }

    public static double[][] bubbleSort(double[][] numbers, int sortIndex) {
        double[] temp;
        int size = numbers.length;
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1 - i; j++) {
                if (numbers[j][sortIndex] > numbers[j + 1][sortIndex])  //交换两数位置
                {
                    temp = numbers[j];
                    numbers[j] = numbers[j + 1];
                    numbers[j + 1] = temp;
                }
            }
        }
        return numbers;
    }

    public static double[][] bubbleSort(double[][] numbers) {
        return bubbleSort(numbers, 0);
    }

    /**
     * 将参数组装成matlabUtil中calcul方法所需的数组
     *
     * @param h1
     * @param h2
     * @param h3
     * @param h4
     * @param angle1
     * @param angle2
     * @param angle3
     * @param angle4
     * @return
     */
    public static double[][] assembleMatlabArray(double h1, double h2, double h3, double h4,
                                                 double angle1, double angle2, double angle3, double angle4) {
        double[][] resultArray = new double[4][2];

        resultArray[0][0] = h1;
        resultArray[1][0] = h1;
        resultArray[2][0] = h1;
        resultArray[3][0] = h1;
        resultArray[0][1] = angle1;
        resultArray[1][1] = angle1;
        resultArray[2][1] = angle1;
        resultArray[3][1] = angle1;

        return resultArray;
    }

    public static String getSubStrByCharAfter(String str, char ch) {
        return getSubStrByCharAfter(str, ch, 1);
    }

    public static String getSubStrByCharAfter(String str, char ch, int offset) {
        return str.substring(str.lastIndexOf(ch) + offset);
        //这是从后面开始找的。还可以从前面开始找，indexof()。
    }

    public static void main(String[] args) {
//        byte[] test3 ={104, 13, 0, -124, 16, 0, 83, 16, 37, 114, 3, 25, 3, -70};
//
//        System.out.println(Arrays.toString(bcdArray2intArray(test3)));
//
//        System.out.println(Arrays.toString(arraySplitByInt(bcdArray2intArray(test3))));
//
//        System.out.println(Arrays.toString(arraySplitByByte(test3)));

//        byte[] tst = {68, 10, 00, 84, 00, 00, 20, 18, 10, 00, 20, 28};
//
//        System.out.println(Arrays.toString(subBytes(tst,0,4)));
////        byte[] tst = {68,10};
//        System.out.println(Arrays.toString(arraySplit(tst)));

        System.out.println(getSubStrByCharAfter("abcedfadgfasdfadf", 'g'));
    }
}
