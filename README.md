tomcat的框架
server -> service -> engine -> host -> context -> wrapper -> servlet。
server：一个server包含多个service
service：一个service包含多个connector和一个engine
engine：tomcat的顶层容器，一个engine代表一个完整的servlet引擎，接收connector的请求，交给servlet来处理
host：表示一个主机，一个tomcat可以管理多个虚拟主机
context：表示一个web应用，一个host下可以有多个context
wrapper：具体的servlet，一个context可以有多个wrapper

通过这个结构可以看出，请求的大概流程如下：
1. 用户通过浏览器发送请求到服务，即socket发送被connector监听到
2. connector解析socket，交给对应的engine处理
3. engine解析url，找到对应host 
4. host获得请求串，找到对应的context
5. context找出对应的servlet
6. 构造HttpServletRequest和HttpServletResponse作为参数调用servlet.service方法
7. context返回HttpServletResponse给host
8. host返回给engine
9. engine返回给connector
10. connector将HttpServletResponse序列化给浏览器

# 实现简单HttpServer
一个常见的请求格式如下，第一行是请求方法、请求uri和协议版本，其他行则是具体的请求头：
```http request
GET /hello.txt HTTP/1.1
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
Accept-Encoding: gzip, deflate, br
Accept-Language: zh-CN,zh;q=0.9
Cache-Control: max-age=0
Connection: keep-alive
Host: localhost:8080
Sec-Fetch-Dest: document
Sec-Fetch-Mode: navigate
Sec-Fetch-Site: none
Sec-Fetch-User: ?1
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36
sec-ch-ua: "Google Chrome";v="113", "Chromium";v="113", "Not-A.Brand";v="24"
```
简单的服务就包含三个部分：
1. HttpServer：创建socket，监听8080端口，构造request、response处理请求
2. Request：使用InputStream读取请求，解析请求uri
3. Response：使用OutputStream处理响应
# 规范Response
常见的响应格式如下，包含状态行、请求头、空行、响应体：
```http request
HTTP/1.1 200 OK
Content-Type: text/html
Content-Length: 12

Hello World!
```
1. 引入Servlet：支持动态资源
2. 提取处理静态资源和动态资源的组件，ServletProcessor和StaticResourceProcessor
3. HttpServer通过request.uri判断处理静态资源或动态资源
# 拆分HttpServer
HttpServer包含连接请求、调用servlet、封装响应，工作太多，秉持单一原则，拆分成专门的类做相应的处理
1. HttpConnector：处理连接
2. HttpProcessor：处理请求分发，调用servlet
3. 规范Request和Response，分别实现ServletRequest和ServletResponse
# 异步化改造Processor
接收到请求之后每次新建processor然后同步处理，这样服务的开销较多且无法同时处理多个请求。
改进的思路是，processor引入池化技术，减少构造对象的开销，然后引入多线程技术，使用不同的线程执行connector和processor，即主线程启动connector并监听端口获取socket，获取到后分配给一个processor去处理该请求；每个线程启动一个processor等待socket到来，然后处理请求，执行完后回收当前processor，当前线程挂起继续等待下个socket到来。需要注意的点是要要仔细处理线程同步代码，防止死锁等问题发生。
1. processor实现Runnable接口，添加标志位用于判断当前是否有socket就位
2. connector实现Runnable接口，添加一个队列的processor当做处理池，启动时初始化一定数量的processor，并新建线程启动它。每次从中获取一个processor
这样一来，一个connector服务多个processor，且都是异步处理，提升并发访问的能力。
# 适配Servlet规范
1. 新增HttpRequest实现HttpServletRequest
2. 新增HttpResponse实现HttpServletResponse
3. 引入SocketInputStream，按行读取请求信息，解析requestLine和header信息
4. 新增HttpRequestFacade与HttpResponseFacade，封装内部方法。
# 解析请求参数和Header
1. 根据get和post请求参数格式，在request中实现解析方法，且在真正获取参数时才调用解析方法。解析的结果放到request中的map中
2. 支持cookie和session：
   1. cookie放在header中，格式固定是：```Cookie: userName=xxxx;password=pwd;```。解析header时发现名称是Cookie，就解析其值，request中可以包含多个cookie，因此用数组存放
   2. 服务给每个请求创建一个Session，存储用户状态，用一个map存储，放到connector中管理。jsessionid一般存在Cookie或Url中，浏览器和服务的交互都携带这个sessionid。
   3. response设置生成的sessionid到响应头```jsessionid=6DB16341D13D83D72B725A0C3A16C4DB```
3. 模拟实现长连接。
   http1.1的实现采用Connection:keep-alive和Transfer-encoding:chunked头来表明数据通过chunked块来传输数据，块的格式是固定的```[chunk size][\r\n][chunk data][\r\n][chunk size][\r\n][chunk data][\r\n] …… [chunk size = 0][\r\n][\r\n]```。根据这个格式，只需要在HttpProcessor中判断keep-alive判断，以及chunk块大小是否为0来决定是否关闭socket。
# 拆分Connector
connector应该只关注连接的管理和分发，具体的Servlet管理交给专门的角色来处理，分工更明确
1. 新增ServletContainer：使用类加载器加载实例化servlet，用一个map管理已经实例化的servlet，对外提供根据请求uri调用对应servlet的方法invoke
2. 新增ServletWrapper：直接使用Container管理Servlet，相对比较繁琐，每个servlet都需要处理它的声明周期等，引入ServletWrapper去处理具体Servlet的生成和加载，ServletContainer只管理这些wrapper即可。
# 构造多层容器
参照tomcat的框架层级，一个server对外提供http服务，内部支持多个虚拟主机，每个主机又有多个应用，每个应用包含多个servlet。简单起见，只实现context和wrapper两层，也足够弄清tomcat多层容器的概念
1. 抽象Container接口：对外提供get、set父、子容器和处理请求的invoke方法等
2. 新增ContainerBase类，实现Container接口：提供容器的默认实现，内部用map维护多个子容器，一个容器属性作为父容器
3. 修改ServletContainer名称为StandardContext、StandardWrapper，继承ContainerBase类，实现相应方法，改造为相应的两个容器
# 实现容器间的互相调用
当服务器要调用某个具体的servlet的时候，是先经过这些container的invoke()方法一层一层调用。每个container执行本层具体任务之前，会先执行一连串的valve，这些valve用于给每层container做一些操作，如日志打印等。 

具体来说，每一层container都有一个pipeline，它是由多个valve组成的。调用某个container.invoke，就是调用它的pipeline的第一个valve，每个valve都会调用下一个valve，直到最后一个basic valve（每个容器都默认存在），然后调用下一层容器，直到最后。
1. 新增Valve接口：提供获取container和invoke等方法
2. 新增ValveContext接口：提供invokeNext方法，调用下一个valve
3. 新增Pipeline接口：提供增删查basic valve和valve的方法以及invoke方法
4. 新增ValveBase：实现基础Valve，内部依赖一个container
5. 新增StandardPipeline：实现Pipeline，内部维护Valve数组和一个basic valve，实现相应增删改方法。其invoke依赖实现了ValveContext的内部类，调用ValveContext.invokeNext，具体实现就是按顺序执行valve数组中的valve.invoke，最后一个执行basic valve.invoke
6. 新增StandardContextValve：实现ValveBase，context容器级别的basic valve，作用是根据请求获取容器中对应的wrapper并调用wrapper.invoke
7. 新增StandardWrapperValve：实现ValveBase，wrapper容器级别的basic valve，作用是获取wrapper中的servlet并调用servlet.service
6. ContainerBase实现Pipeline，其实现的Pipeline方法依赖其内部的StandardPipeline，invoke直接调用pipeline.invoke
7. StandardContext和StandardWrapper初始化时设置对应的basic valve，如果有需要可以添加自定义的valve去处理相关逻辑

总结一下流程，如下：

HttpConnector -> HttpProcessor -> ServletProcessor -> StandardContext.invoke -> ContainerBase.invoke
-> StandardPipeline.invoke -> StandardPipeline内部类StandardPipelineValveContext.invokeNext -> for(Valve : valves) {valve.invoke}
-> other context valve.invoke -> StandardContextValve.invoke -> other wrapper valve.invoke -> StandardWrapperValve.invoke 
-> servlet.service -> controller -> service
# 实现filter
filter的调用是在真正的servlet之前，也就是StandardWrapperValve.invoke中先调用这些filter，完事再调用servlet.service。

根据经验，这些filter应该是先配置在什么地方，然后加载到某个容器中，在需要的时候（url或servletName匹配到的filter）实例化成具体的类，存到filterChain的list中，然后调用第一个filter.doFilter，filter.doFilter又会调用filterChain.doFilter，直到所有filter都执行完成，最后执行servlet.service。
1. 新增FilterDef：filter定义类，存储filter类名、名称等
2. 新增FilterMap：存储filter与URL/Servlet的映射关系
3. 新增ApplicationFilterConfig：实现FilterConfig，filter配置类，也是容器中加载的filter配置，对外提供getFilter实例化具体的filter（FilterDef -> Filter）
4. 新增ApplicationFilterChain：实现FilterChain，内部维护List<ApplicationFilterConfig> filters，具体的doFilter实现就是通过迭代器调用第一个filter，完成后调用servlet.service
5. 修改StandardContext，维护filterMap映射、FilterDef定义以及FilterConfig配置等
6. 修改StandardWrapperValve，根据StandardContext中的filterMap或Servlet匹配并实例化FilterConfig，构建filterChain，执行filterChain.doFilter
# 实现listener
listener和filter类似，都是通过配置加载到容器中，当触发某个事件时，相应的监听器执行相关逻辑。监听器和事件两者共同存在
1. 新增ContainerEvent事件：实现EventObject
2. 新增ContainerListener接口：提供触发事件方法
3. StandardContext内部维护List<ContainerListener>，创建容器后触发事件监听
# 支持多应用
大致思路是使用不同的类加载器去加载类，对JVM来说判断一个类是否相同是根据类加载器名称+类型，这样从底层就隔离开不同的应用了。解析url，根据路径创建不同的类加载器，加载对应路径下的类，也就是做到了区分不同的应用。

为了实现这个逻辑，需要一个容器来管理StandardContext，并为每个StandardContext创建其对应的类加载器，之后用到该context时直接从容器中获取即可，之后的流程就和之前是相同的了。
1. 新增StandardHost：实现ContainerBase，内部维护StandardContext，根据需要创建
2. 新增StandardHostValve：继承ValveBase，实现根据url解析的路径创建相应的StandardContext，然后调用context.invoke
3. 新增WebappClassLoader：实现加载不同路径下的类的类加载器
4. 修改Bootstrap，启动时创建StandardHost，由它来作为整个容器流程的起点，即StandardHost -> StandardHostValve -> StandardContext -> StandardContextValve -> StandardWrapper -> StandardWrapperValve -> servlet.service






















