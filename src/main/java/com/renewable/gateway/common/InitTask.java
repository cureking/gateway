package com.renewable.gateway.common;

/**
 * @Description：
 * @Author: jarry
 */
//@Component
//@Slf4j
//@ContextConfiguration(locations = {"classpath:applicationContext.xml"}) //引入spring配置。
//@Transactional
//public class InitTask {
//
//    @Autowired
//    private IRegisteredInfoService iInclinationService;
//
//
//    @PostConstruct
//    public void init() {
////        System.out.println("initTask_start");
////
////
////        this.initLocalRegister();
////        System.out.println("initTask_end");
//    }
//
//    //在系统启动之初，将物理串口信息扫描，加入数据库   //暂时抛弃，现如今还不是自动注册，单靠软件进行自动化，太过费力，不合理。（硬件确定通信钱，先在多频段发送注册信息）
//    private void getPhysicalRegister(){
//
//    }
//
//
//    //初始化注册表的本地文件   //预设标准数据库数据
//    public void initLocalRegister(){
//        ServerResponse serverResponse = iInclinationService.getSensorList();
//        if (!serverResponse.isSuccess()){
//            log.error("initLocalRegister error:Couldn't get relational data from database!");
//        }
//        List<SensorRegister> sensorRegisterList = (List<SensorRegister>)serverResponse.getData();
//        //将数据写入redis    //这么做的目的是能够快速比对数据差异性
//        RedisPoolUtil.set(Const.SENSOR_REGISTER_PREFIX+"List",JsonUtil.obj2StringPretty(sensorRegisterList));
//        System.out.println("initLocalRegister finished!");
//        System.out.println(RedisPoolUtil.get(Const.SENSOR_REGISTER_PREFIX+"List"));
//    }
//
//    //更新注册表     //放在这里合适吗？      //也许这个可以做出切面什么的。毕竟每次对IRegister数据库的数据操作后，都要执行这个操作。
//    public void updateLocalRegister(){
//
//    }
//
//
//
//}
