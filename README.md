# Young-IM

![](/assets/logo.png)

<div align="center">
 Young-IM是基于Netty的高性能分布式即时通讯系统 </br>
 </div>
<div align="center"> 
  <a href=##一、项目介绍>介绍</a> |  <a href=##二、亮点>亮点</a> |  <a href=##三、快速开始>快速开始</a> |  <a href=##四、架构设计>结构设计</a>
</div>



## 一、项目介绍

Young-IM 是一个DDD架构设计的基于 Netty 实现的高性能分布式即时通讯系统。系统实现了消息的四大特性（实时、有序、可靠、幂等），同时吸收并应用了业界先进技术和成熟产品实现，拥有以下特点:

1. 高性能：使用 Netty 网络框架，支持高并发，单机支持万级别在线用户，并支持横向扩展。
2. 分布式：Young-IM支持多端同步在线，多端内容实时同步。
3. 可扩展性强：策略模式解耦合，开发者可以很方便的单独定制command。
4. 多租户设计：可以0代码侵入为租户开启一套崭新IM系统。
5. 丰富的功能和特性： 支持点对点消息、群组聊天、消息撤回、消息已读等常用特性，对开发者提供回调等机制方便第三方系统接入。
6. 易于学习与使用: 简洁易懂的接口设计和完善的开发文档，使得该系统易于学习与使用，并且具有较好的可扩展性。

我的目标是为广大开发者提供一个高质量的 IM 通讯框架，以及丰富的相应技术支持和社区资源，在更新迭代和完善逐步实现中，降低用户的开发学习成本，提高开发效率。

该项目遵循 Apache 许可证2.0 开源，欢迎社区贡献代码或者提交问题。

## 二、亮点

* [x] 设计模式模块解耦合。
  *  使用策略模式重构用户操作指令逻辑。
  *  使用状态模式重构用户自定义多端登录方式。
  *  使用模板模式重构消息接收器。
* [x] 使用 Redisson 发布订阅模式，监听用户登录行为，发送用户下线通知。存储用户多端设备的 Session 信息
* [x] 使用 Rabbitmq 处理分布式消息顺序性, 异步执行历史消息落库持久化等问题, 并且解决线上 MQ 消息积压和消息不一致等问题
* [x] 使用拦截器机制, 通过 HMAC-SHA256 加密算法实现接口加密防刷, 提升系统安全性。
  * 单聊、群聊服务优化改造(实时性、有序性、可靠性、幂等性)
  * 实时性: 使用线程池、MQ 异步持久化、RPC 解耦合法性校验大幅提升消息实时性。
  * 可靠性: 通过应用层两次握手, 即发送方接收上、下行 ACK 确保消息可靠性, 解决消息丢失问题。
  * 有序性: 使用 Redis 原子递增 incr 保证消息有序性, 解决消息乱序问题。
  * 幂等性: 通过防重 ID, 服务端、客户端缓存消息等幂等性手段遏制消息重复现象, 并限制消息的无限制重试。
* [x] 实现单聊、群聊消息已读和已读回执功能。
* [x] 实现服务路由功能，可根据多种路由算法与服务器建立链接。
* [x] 采用读扩散实现单聊、群聊离线消息拉取。
* [x] 自研数据库分库分表starter路由组件，满足业务数据的快速增长。
* [x] 历史消息改造存储MongoDB。
* todo
* [] 连接加入校验，避免恶意攻击，造成创建大量netty连接资源耗尽。

## 三、快速开始

#### 3.1 模块介绍

Young-IM采用DDD架构设计，具体模块内容职责如下：

```text
young-im
├─ im-codec           接入层：负责网关服务配置文件集成、私有协议包结构定义、消息编解码以及需要发送给 TCP 服务的数据包定义
├─ im-common          基础层：负责定义整个 IM 架构所有常量、状态码、错误码、基础数据模型
├─ im-domain          领域层：负责定义用户、好友、群组等多个领域的逻辑，以及消息的发送服务
├─ im-message-store   消息存储层：通过 MQ 将消息异步持久化落库
├─ im-infrastructure  基础层：负责定义底层组件如 redis、zk、mq 的配置逻辑，回调机制和基类消息发送
└─ im-tcp             网关层：负责定义心跳机制、监控用户消息读取以及上线下线、Netty 消息通道以及 WebSocket 全双工通道
```

#### 3.1 先决条件

1、Young-IM的领域层需要在ZK注册，所以需要有ZK的环境，这里列举docker下启动命令(注意映射目录).

```bash
docker run -d --name zookeeper --privileged=true -p 2181:2181  -v /mydata/zookeeper/data:/data -v /mydata/zookeeper/conf:/conf -v /mydata/zookeeper/logs:/datalog zookeeper:3.5.7/
```

2、Young-IM的收发消息都是基于RabbitMq，所以需要有RabbitMq的环境。

```bash
docker run -d -p 5672:5672 -p 15672:15672 --name rabbitmq rabbitmq
```

3、Young-IM的用户session以及部分缓存都在Redis存储，所以需要有Redis环境（注意配置文件映射和密码）

```bash
docker run --restart=always --log-opt max-size=100m --log-opt max-file=2 -p 6379:6379 --name myredis -v /root/redis/config/myredis.conf:/etc/redis/redis.conf -v /root/redis/data:/data -d redis redis-server /etc/redis/redis.conf  --appendonly yes  --requirepass YOURPASSWORD!
```

4、Young-IM的消息记录已经好友关系等都存储在MySQL中，所以需要Mysql环境（注意修改密码和开启远程访问）

```bash
docker run --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=YOURPASSWROD -d mysql:8.0.21 
```

执行assets/sql下的young-im创建所需表

#### 3.3 启动服务

需要启动的服务有im-tcp、im-domain、im-message-store。

1、im-tcp启动时需要在启动参数添加配置文件的路径，配置文件在resources下的config.yml，只需要修改RabbitMq、ZK、Redis即可，其他配置项可以忽略。

此外在JDK9以上的版本中，由于引入了模块化设计导致netty不能够正常启动 需要再启动选项添加JVM参数即可解决

```bash
 --add-opens java.base/jdk.internal.misc=ALL-UNNAMED -Dio.netty.tryReflectionSetAccessible=true
```

2、im-domain、im-message-store都为springboot项目，修改spring的配置文件的RabbitMq、ZK、Redis地址后即可启动

三个服务启动没有先后顺序。

## 四、架构设计

![](/assets/im架构图.png)

## 五、功能模块设计

### 5.1 消息可靠性 ack机制
Young-IM为了保证消息传递的可靠性采用ack机制，即当客户端发送一条消息给服务端以后，只有收到了服务端的ack通知才会认为消息传递投递成功。
![](/assets/im时序.png)

### Netty私有协议

### 登录行为

### 负载均衡

### 回调机制

### 多端登录

### 收发消息

### 数据同步

### 消息持久化

### 消息已读

### 消息拉取
正常情况Young-IM是没有消息拉取的，只有当用户掉线、断网等与服务端的连接断开之后造成消息无法投递成功，这个时候需要客户端主动与服务端拉取消息同步记录。消息在传递的时候会记录着一个sequenceId，这个id是有序自增的，当消息成功投递到客户端，客户端会更改该会话的sequenceId，当失败时候客户端进行重连时候会拿自己记录的sequenceId与服务端最新的该会话的sequenceId进行对比然后拉取丢失的消息进行同步。
![](/assets/拉取消息.png)
### 消息检索

