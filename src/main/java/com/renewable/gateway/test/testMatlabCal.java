package com.renewable.gateway.test;

import com.mathworks.toolbox.javabuilder.MWException;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;
import sinfit.Calcul;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

/**
 * @Description：
 * @Author: jarry
 */
@Repository
public class testMatlabCal {

    public static void main(String[] args) throws MWException {
        //单机调用  //原来的程序删除了。。。
        Calcul calcul = new Calcul();
        Object[] result = calcul.sinfit(4, 1.1D, 1.1D, 1.1D, 1.1D, 1.1D, 1.1D, 2.1D, 1.1D, 1.1D);
        System.out.println("result:"+Arrays.toString(result));

        double[][] testArray1 = {{0,315},{0,225},{1.707,90},{0.1,270}};
        double[][] testArray2 = {{0,315},{0,45},{1.707,180},{0.1,0}};    //R=1    //Y>0
        double[][] testArray3 = {{1.707,180},{0,315},{0,45},{0.1,0}};

        Object[] result2 = calcul.sinfit(4, 315.0, 0.0, 225.0, 0.0, 90.0, 1.707, 270.0, 0.1, 1.0);
        System.out.println("result2:"+Arrays.toString(result2));
        Object[] result3 = calcul.sinfit(4, 315.0, 0.0, 225.0, 0.0, 90.0, 1.707, 270.0, 0.1, 1.0);

        System.out.println("result3:"+Arrays.toString( result3));
        Object o = result3[2];

        double d = Double.parseDouble(o.toString());
        System.out.println("result3/double-element:"+d);

    }
}
