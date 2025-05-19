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






















