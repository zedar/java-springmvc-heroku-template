Template project for API development with Java, Jetty, SpringMVC, Spock, Swagger and Heroku
------------------------------------
The aim of this project is to provide template for standalone java application with:
  * embedded jetty container for HTTP support,
  * SpringMVC for easy development of REST API,
  * lombok for POJO shortcuts (constructors, fields access methods).
  * Spockframework for easy testing of API, for mocking and stubbing of services
  * gradle configuration for easy heroku deployment
  * Swagger API definition

Example app should be accessible under the link [api-springmvc-jetty](https://api-springmvc-jetty.herokuapp.com/api/actions) or as a call:

    $ curl -X GET https://api-springmvc-jetty.herokuapp.com/api/actions

**Table of contents:**

# Gradle plugins

Project depends on two main plugins:
  * java
  * application
  * groovy (for testing with *spockframework*)

# Dependencies

## Embedded jetty
Add aggregate jar that contains all of the Jetty classes to the *build.gradle*

    dependencies {
      compile(
        "org.eclipse.jetty.aggregate:jetty-all:9+"
      )
    }

## SpringMVC
Add Spring web mvc to create REST services.

    dependencies {
      compile(
        "org.springframework:spring-webmvc:4.1.2.RELEASE"
      )
    }

## Jackson JSON
Jackson JSON library converts Java objects JSON format. 
SpringMVC's @ResponseBody annotation converts return value to one of the predefined formats: XML, JSON.
We use 2.+ version of Jackson library.

    dependencies {
      compile(
        "com.fasterxml.jackson.core:jackson-core:2+",
        "com.fasterxml.jackson.core:jackson-annotations:2+",
        "com.fasterxml.jackson.core:jackson-databind:2+"
      )
    }

## Log4j2 with async logging
Log4j2 is async logger. Add *log4j-api* and *log4j-core* libraries as well as bridge to *slf4j* API.

    dependencies {
      compile(
        "org.apache.logging.log4j:log4j-api:2.1",
        "org.apache.logging.log4j:log4j-core:2.1",
        "org.apache.logging.log4j:log4j-slf4j-impl:2.1"
      )
    }

### Log4j configuration
Put log4j configuration in *src/main/resources/log4j2.xml* file

    <?xml version="1.0" encoding="UTF-8"?>
    <Configuration status="WARN">
      <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
          <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
      </Appenders>
      <Loggers>
        <Root level="error">
          <AppenderRef ref="Console"/>
        </Root>

        <Logger name="api" level="debug"></Logger>
      </Loggers>
    </Configuration>
    
If your code is placed not in *src/main/java/api* folder, so change logger name from *api* to your name.

## Lombok
Add lombok library to reduce boilerplate code. It contains annotations for auto generate getters, setters, hash, equals and toString methods,
constructors, logger field (with @Slf4j, very similiar to groovy.util.logging.Slf4j).

    dependencies {
      compile(
        "org.projectlombok:lombok:1.14.8"
      )
    }

## Unit testing

### spring-test
Add mocks for spring web mvc testing.

    dependencies {
      testCompile(
        "org.springframework:spring-test:4.1.2.RELEASE"
      )
    }

### spock BDD testing framework
Add *spock* with *cglib* for java class mocking

    dependencies {
      testCompile(
        "org.spockframework:spock-core:0.7-groovy-2.0",
        "cglib:cglib-nodep:3.1"
      )
    }

# Server launcher
Add main class that starts jetty server.

    package api.server;
    
    public class Launcher {
      ...
    }

## Gradle mainClassName
Gradle *application* plugin requires property *mainClassName* set to the launcher class. In our case it is:

    mainClassName="api.server.Launcher"

# SpringMVC context

## Server Context
Contains spring configuration for jetty server running SpringMVC web context.  
Location: **src/main/resources/serverctx.xml**

If environment has *PORT* variable defined it is used as parameter to *server* bean. If not server runs on default 9090 port.
Web application context has a reference to web.xml with definition of all web assets.

    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:aop="http://www.springframework.org/schema/aop"
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:jdbc="http://www.springframework.org/schema/jdbc"
        xmlns:jee="http://www.springframework.org/schema/jee"
        xmlns:tx="http://www.springframework.org/schema/tx"
        xmlns:jpa="http://www.springframework.org/schema/data/jpa"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-3.1.xsd
                            http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
                            http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.1.xsd
                            http://www.springframework.org/schema/jee http://www.springframework.org/schema/jee/spring-jee-3.1.xsd
                            http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.1.xsd
                            http://www.springframework.org/schema/jdbc http://www.springframework.org/schema/jdbc/spring-jdbc-3.1.xsd
                            http://www.springframework.org/schema/jdbc http://www.springframework.org/schema/jdbc/spring-jdbc-3.1.xsd
                            http://www.springframework.org/schema/data/jpa http://www.springframework.org/schema/data/jpa/spring-jpa.xsd">

      <bean name="webappContext" class="org.eclipse.jetty.webapp.WebAppContext">
        <property name="descriptor" value="src/main/webapp/WEB-INF/web.xml"/>
        <property name="resourceBase" value="src/main/webapp"/>
        <property name="contextPath" value="/"/>
        <property name="parentLoaderPriority" value="true"/>
      </bean>

      <bean name="handlerList" class="org.eclipse.jetty.server.handler.HandlerList">
        <property name="handlers">
          <list value-type="org.eclipse.jetty.server.Handler">
            <ref bean="webappContext"/>
          </list>
        </property>
      </bean>

      <bean name="server" class="org.eclipse.jetty.server.Server">
        <constructor-arg value="#{systemEnvironment['PORT'] ?: 9090}"/>
        <property name="handler" ref="handlerList"/>
      </bean>
    </beans>
    
## web.xml context
Web context wire up web/REST service layers.  
Location: **src/main/webapp/WEB-INF/web.xml**.

Servlet mapping defines root context for REST calls: */api/*. All REST methods has to be prefixed with */api/* context.

*DispatcherServlet* has a reference to *servletctx.xml* with definition of controllers.  
Global *webappctx.xml* should contain beans common for all servlets (for example database conntections).

    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <web-app xmlns="http://java.sun.com/xml/ns/javaee"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
      version="3.0"
      metadata-complete="true">

      <!-- General description of your web application -->
      <display-name>API</display-name>
      <description>
        API WebApp
      </description>

      <welcome-file-list>
        <welcome>index.html</welcome>
      </welcome-file-list>

      <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
      </listener>

      <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:/webappctx.xml</param-value>
      </context-param>

      <servlet>
        <servlet-name>rest</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
          <param-name>contextConfigLocation</param-name>
          <param-value>classpath:/servletctx.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
      </servlet>
        
      <servlet-mapping>
        <servlet-name>rest</servlet-name>
        <url-pattern>/*</url-pattern>
      </servlet-mapping>
        
    </web-app>
    
## Servlet Context
Contains definition of beans available for dispatcher servlet.  
Location: **src/main/resources/servletctx.xml**

    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:aop="http://www.springframework.org/schema/aop"
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:jdbc="http://www.springframework.org/schema/jdbc"
        xmlns:jee="http://www.springframework.org/schema/jee"
        xmlns:tx="http://www.springframework.org/schema/tx"
        xmlns:jpa="http://www.springframework.org/schema/data/jpa"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:util="http://www.springframework.org/schema/util"
        xmlns:mvc="http://www.springframework.org/schema/mvc"
        xsi:schemaLocation="http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-4.1.xsd
                            http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.1.xsd
                            http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.1.xsd
                            http://www.springframework.org/schema/jee http://www.springframework.org/schema/jee/spring-jee-4.1.xsd
                            http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-4.1.xsd
                            http://www.springframework.org/schema/jdbc http://www.springframework.org/schema/jdbc/spring-jdbc-4.1.xsd
                            http://www.springframework.org/schema/data/jpa http://www.springframework.org/schema/data/jpa/spring-jpa.xsd
                            http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-4.1.xsd
                            http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc-4.1.xsd">
      <context:component-scan base-package="api" />
      <mvc:annotation-driven />

      <!-- To enable @RequestMapping process on type level and method level -->
      <bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter">
        <property name="cacheSeconds" value="0" />
        <property name="messageConverters">
          <util:list id="beanList">
            <ref bean="jsonConverter" />
          </util:list>
       </property>
      </bean>

      <bean id="jsonConverter" class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter">
        <property name="supportedMediaTypes" value="application/json"/>
      </bean>
    </beans>

## Web app context
Contains definition of beans shared between servlets.  
Location: **src/main/resources/webappctx.xml**

    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:aop="http://www.springframework.org/schema/aop"
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:jdbc="http://www.springframework.org/schema/jdbc"
        xmlns:jee="http://www.springframework.org/schema/jee"
        xmlns:tx="http://www.springframework.org/schema/tx"
        xmlns:jpa="http://www.springframework.org/schema/data/jpa"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:util="http://www.springframework.org/schema/util"
        xmlns:mvc="http://www.springframework.org/schema/mvc"
        xsi:schemaLocation="http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-4.1.xsd
                            http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.1.xsd
                            http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.1.xsd
                            http://www.springframework.org/schema/jee http://www.springframework.org/schema/jee/spring-jee-4.1.xsd
                            http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-4.1.xsd
                            http://www.springframework.org/schema/jdbc http://www.springframework.org/schema/jdbc/spring-jdbc-4.1.xsd
                            http://www.springframework.org/schema/data/jpa http://www.springframework.org/schema/data/jpa/spring-jpa.xsd
                            http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-4.1.xsd
                            http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc-4.1.xsd">
    </beans>

# Example API
Assume that controller method return java class that is automatically converted to JSON.

## Example domain class
Location: **src/main/api/entity/Action.java**

## Example controller class
Location: **src/main/api/controller/ApiController.java**
Controller class has defined roor *@RequestMapping("/api")*. All requests to urls beginning with */api* are directed to this controller.

# Testing
Location: **src/test/groovy**
Because this project uses *spockframework*, so all unit tests are written in *Groovy* lanugage.

To run tests execute command:

    $ gradle test

API is based on *SpringMVC*, so for testing we use spring mocking funkcionality:

    org.springframework.test.web.servlet.MockMvc
    org.springframework.test.web.servlet.setup.MockMvcBuildes.standaloneSetup
    org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*

Mocked APIs return HTTP status codes defined in

    org.springframework.http.HttpStatus.*

# Swagger API definition
Project contains example definition of API in the format of [Swagger standard](https://github.com/swagger-api/swagger-spec).

## api-docs controller
Swagger defines standard URI for accessing API definition. It is */api-docs* path.
There is *api.controller.ApiDocsController* that returns *json* file with swagger API def.
We use *jackson streaming* classes to generate API. 
API is not automatically generated out of source file. This is consciously decision.
Any change in source code should not violate API def used by the third parties.

Access example api-docs with the following URL:

    https://api-springmvc-jetty.herokuapp.com/api-docs

# Heroku configuration
This project uses gradle build-pack from *Heroku*.  
There are a few requirements:

  * build.gradle has to have stage task defined. It is automatically used by heroku to build deployed application;
  * gradle wrapper is used by heroku to download and install required gradle version;
  * java version shoudl be set to 1.7

## build.gradle

### Java target compatibility
Set it to 1.7 Java version

    targetCompatibility="1.7"
    sourceCompatibility="1.7"

### Define *stage* task
It is automatically run by heroku to build application

    task stage(dependsOn: [clean, installApp]) {
      description "Clean, build and install application"
    }

## settings.gradle
Force heroku gradle to use our own folder name as build output

    rootProject.name="api"

## system.properties
Force heroku to use 1.7 java version (default is set to 1.6).

    java.runtime.version=1.7

## Procfile
Define command to start application when *stage* task is finished.

    web: ./build/install/api/bin/api

# Heroku deployment
Create free Heroku account for developers.
Install heroku tools in your operating system. For OSX download *Heroku Toolbelt*.

After installing execute following command

    $ heroku login
    Enter your Heroku credentials.
    Email: java@example.com
    Password:
    Could not find an existing public key.
    Would you like to generate one? [Yn]
    Generating new SSH public key.
    Uploading ssh public key /Users/java/.ssh/id_rsa.pub

Above steps are very important. Without uploading public key to heroku further steps will not be possible.  
It is possible to upload public key manually through heroku's dashboard.

To deploy your application all changes have to be commited to git (could be local repo).

Create new heroku application

    $ heroku create api-test
    $ git push heroku master

# Test example API
Locally

    $ curl -X GET http://localhost:9090/api/actions

On Heroku

    $ curl -X GET https://api-springmvc-jetty.herokuapp.com/api/actions

As result JSON should be returned:

    [
        {
            "id": "1",
            "name": "a1",
            "description": "Action 1",
            "url": "action_url",
            "tags": "no tags"
        },
        {
            "id": "2",
            "name": "a2",
            "description": "Action 2",
            "url": "action_url",
            "tags": "no tags"
        },
        {
            "id": "3",
            "name": "a3",
            "description": "Action 3",
            "url": "action_url",
            "tags": "no tags"
        },
        {
            "id": "4",
            "name": "a4",
            "description": "Action 4",
            "url": "action_url",
            "tags": "no tags"
        }
    ]

# Miscellaneous

This project's basic structure has been created with command:

    $ lazybones create java-basic java-springmvc-heroku

We use gradle as building subsystem.

[gradle-wrapper](http://www.gradle.org/docs/current/userguide/gradle_wrapper.html) is necessary for heroku deployment.
Automatically downloads gradle, if not accessible.

    Add task wrapper to build.gradle and run:
    $ gradle wrapper

Inside *wrapper* task it is possible to set required gradle version

    task wrapper(type: Wrapper) {
      gradleVersion = "2.0"
    }

[gradle-daemon](http://www.gradle.org/docs/current/userguide/gradle_daemon.html) speeds up startup and execution of gradle.

    Add property to gradle.properties
    org.gradle.daemon=true
