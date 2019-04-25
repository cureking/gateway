package com.renewable.gateway.util;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
public class SensorDealUtil {

    //TODO 之后将这些方法提取出通用方法（就是参数数量不限制+泛型）。

//    private static Inclination originData2Object(byte[] originData){
//        Inclination inclination = new Inclination();
////        Inclination.setAngleX();
//        //这里暂时先利用硬编码实现，但很明显这并不优雅，之后优化处理。而且这里代码的健壮性极差，完全无法应对任何意料之外的情况，如outofarrayrangeexception.etc
//        double origin_X = origin2Double(originData[4],originData[5],originData[6],originData[7]);
//        double origin_Y = origin2Double(originData[8],originData[9],originData[10],originData[11]);
//        double origin_T = origin2Double(originData[12],originData[13],originData[14],originData[15]);
//        inclination.setAngleX(origin_X);
//        inclination.setAngleY(origin_Y);
//        inclination.setTemperature(origin_T);
//        //该数据的create_time由mybatis的mapper中now()函数控制
//
//        return inclination;
//    }
//
//    /**
//     * List<Integer>转为对应2字节Byte[]类型 （容量扩大一倍）
//     * @param list
//     * @return
//     */
//    private static byte[] list2OriginData(List<Integer> list){
//        byte originData[] = new byte[list.size()*2];
//        for(int j=0;j<list.size();j+=2){
//            originData[j]=unsignedShortToByte2(list.get(j))[0];
//            originData[j+1]=unsignedShortToByte2(list.get(j))[1];
//        }
//        return originData;
//    }
//
//    /**
//     * short整数转换为2字节的byte数组
//     *
//     * @param s   short整数
//     * @return byte数组
//     */
//    private static Byte[] unsignedShortToByte2(int s) {
//        Byte[] targets = new Byte[2];
//        targets[0] = (byte) (s >> 8 & 0xFF);
//        targets[1] = (byte) (s & 0xFF);
//        return targets;
//    }
//
//    private static String int2Byte(int data){
//        String hex = Integer.toHexString(data);
//        return hex;
//    }
//
//    private static List<Integer> objece2ListData(int command,int address){
//
//        //数据先以Integer类型保存，之后转为Byte
//        List<Integer> originArray = Lists.newArrayList();
//
//        //这里可以解耦
//        int data_0 = 68;
//        int data_1 = 4;
//        int data_2 = address;
//        int data_3 = command;
//        int data_5 = 0;
//
//        originArray.add(data_0);
//        originArray.add(data_1);
//        originArray.add(data_2);
//        originArray.add(data_3);
//
//        //设置数据长度（+1是因为数据校验位还没有添加，需要计算）
//        originArray.set(1,originArray.size()+1);
//
//        //计算校验和     校验和是不计算标示符（不计算第一位）与自身的（不计算最后一位）
//        for(int j=1;j<originArray.size()-1;j++){
//            data_5+=originArray.get(j);
//        }
//        originArray.add(data_5);
//        return originArray;
//    }
//
//    /**
//     *     但是我改完又后悔了。先不提改后有点难看，这样写就等于将判断data是否为空交由上层处理，上层必须完成函数的挑选。
//     *     不过上层还要完成data的建立，所以这里的统一整合，放在之后再细致优化吧。
//     * @param command
//     * @param address
//     * @param data
//     * @return
//     */
//    //todo_finished 这里可以对方法进行重载，毕竟有的命令是不存在数据域的。
//    private static List<Integer> objece2ListData(int command,int address,Integer data){
//
//        //数据先以Integer类型保存，之后转为Byte
//        List<Integer> originArray = Lists.newArrayList();
//
//        //这里可以解耦
//        int data_0 = 68;
//        int data_1 = 4;
//        int data_2 = address;
//        int data_3 = command;
//        int data_4 = data;
//        int data_5 = 0;
//
//        originArray.add(data_0);
//        originArray.add(data_1);
//        originArray.add(data_2);
//        originArray.add(data_3);
//        originArray.add(data_4);
//
//        //设置数据长度（+1是因为数据校验位还没有添加，需要计算）
//        originArray.set(1,originArray.size()+1);
//
//        //计算校验和     校验和是不计算标示符（不计算第一位）与自身的（不计算最后一位）
//        for(int j=1;j<originArray.size()-1;j++){
//            data_5+=originArray.get(j);
//        }
//        originArray.add(data_5);
//        return originArray;
//    }
//
//    /**
//     * 完成三个（也许应该扩展为多个，再做成工具Util）十六进制转double（包含合并）
//     * @param data_1
//     * @param data_2
//     * @param data_3
//     * @param data_4
//     * @return
//     */
//    private static Double origin2Double(Byte data_1,Byte data_2,Byte data_3,Byte data_4){
//        double result =0;
//        int dataI_1 = Byte.toUnsignedInt(data_1);
//        int dataI_2 = Byte.toUnsignedInt(data_2);
//        int dataI_3 = Byte.toUnsignedInt(data_3);
//        int dataI_4 = Byte.toUnsignedInt(data_4);
//
//        int sig;
//        //这只是针对目前的情况，也许之后会改变这种传输格式
//        if (dataI_1/16 == 0){
//            sig = 1;
//        }else if (dataI_1/16 == 1){
//            sig = -1;
//        }else{
//            sig=1;  //为保证之后的代码正确运行，我们给它一个default_value :1;
////            System.out.println(data_1+"传输的数据符号位出现问题，出现0/1之外的情况，其高位为： "+dataI_1/16);
//            log.error("传输的数据符号位出现问题，出现0/1之外的情况，其高位为： "+dataI_1/16);
//        }
//
//        int first = dataI_1%16;
//        result = sig*first*100+dataI_2*1+dataI_3*(-0.01)+dataI_4*(-0.0001);
//
////        System.out.println(Double.longBitsToDouble(data_2)*1+Double.longBitsToDouble(data_3)*(-10));
////        System.out.println(Double.longBitsToDouble(data_3));
//        return result;
//    }
}
