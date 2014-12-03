package api.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;

import org.eclipse.jetty.server.Server;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Launcher {
  public static void main(String[] args) throws Exception {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("serverctx.xml");
    Server server = ctx.getBean("server", Server.class);
    server.start();
    log.info("---> Server started: {}", server.getURI().toString());
    server.join();
  }
}
