//package org.csits.demo.config;
//
//import com.alibaba.cloud.nacos.registry.NacosAutoServiceRegistration;
//import java.lang.management.ManagementFactory;
//import java.util.Set;
//import javax.management.MBeanServer;
//import javax.management.ObjectName;
//import javax.management.Query;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class NacosConfig implements ApplicationRunner {
//
//  @Autowired(required = false)
//  private NacosAutoServiceRegistration registration;
//
//  @Value("${server.port}")
//  Integer port;
//
//  @Override
//  public void run(ApplicationArguments args) throws Exception {
//
//    if(registration!=null && port!=null){
//
//      Integer tomcatPort = port;
//      try {
//        tomcatPort = new Integer(getTomcatPort());
//      }catch (Exception e){
//        e.printStackTrace();
//      }
//
//      registration.setPort(tomcatPort);
//      registration.start();
//    }
//  }
//
//  /**
//   *	获取外部tomcat端口
//   */
//  public String getTomcatPort() throws Exception {
//    MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
//    Set<ObjectName> objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"), Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
//
//    return objectNames.iterator().next().getKeyProperty("port");
//  }
//}
