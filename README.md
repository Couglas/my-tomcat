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

























