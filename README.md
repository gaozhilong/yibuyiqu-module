## 易步易趋 ##

已经停止维护，如果有兴趣请移步[新的应用](https://github.com/gaozhilong/Yibuyiqu)

**易步易趋（不是错别字，我喜欢这么称呼它）** 是一个模块化的分布式的服务器端应用框架，可以作为多种服务器的后端使用，可以配置为定时推送或者即时消息发送。开发它的初衷是作为我的APP应用的服务器，同时也准备作为手游服务器的基础。

### 主要特性 ###

**模块化的设计**

不同的服务使用消息总线通信。因为使用了[Vert.x](http://vertx.io/)作为底层，所以理论上每个服务都可以用不同的语言来开发。设计的初衷是命令服务由开发者习惯的语言开发。

**灵活的部署方式**

config.json文件是应用的配置文件，里面定义了各种服务的具体实现类，实例的个数以及异步/同步工作方式等参数。根据实际需要可以对服务的实例数量等进行调整以适应具体业务的需要。

**分布式**

因为使用了[Vert.x](http://vertx.io/)所以应用本身天然就是支持分布式部署的。暂时没有提供Vert.x集群的部署方式，正在实践中。

### 使用的技术 ###

- **[Vert.x](http://vertx.io/)** 一个神奇的轻量的消息总线服务
- **[mongodb](https://www.mongodb.org/)** 用来存储日志以及session信息，也可以配置成为主要的数据存储。最开始的初衷是使用**[leveldb](http://leveldb.org/)**因为个人更加喜欢这个更单纯的数据库。


### 后续的改进计划 ###

简化掉日志写入的编码工作，让命令执行服务更加单纯的专注于业务。这也是最开始的设计初衷，只有这样才能让命令服务可以使用各种语言编写

一个简单的APP实例，以便使用者可以熟悉如何使用

迁移至**[Vert.x 3.0](http://vert-x3.github.io/)**并且使用**[groovy](http://www.groovy-lang.org/)**重写,其实已经在进行中，但是3.0版本的Vert.x存在一些问题，所以没有能够开发完成

### 如何运行 ###

想要运行起来，您需要最少安装**[mongodb](https://www.mongodb.org/)**之后创建一个数据库，并创建三个Collection**（users,logs,sessions）**，然后在users中增加一条你的登录用户格式大体上是这样的：**{"username" : "username","password" : "password"}**

更改LogServer.java文件中的数据库链接相关的数据库名称，用户口令等。如果你使用的Postgresql作为主数据库，那么你需要在Postgres数据库中建立一个你的数据库，并且建立一张表

**CREATE TABLE "User"
(
  id serial NOT NULL,
  jdoc jsonb,
  CONSTRAINT "User_pkey" PRIMARY KEY (id)
)**

之后你同样需要插入一条记录id随便你使用什么数值，但是jdoc需要插入类似这样的{"username" : "username","password" : "password"}一个合法的json字符串

都做完以后就可以到项目目录下，执行**mvn clean package vertx:runMod**命令启动了。我假设你已经安装了Maven并且可以正常的使用它。

最后打开浏览器访问**http://localhost:8000/**，如果你没有修改config.json文件里面的proxyserver配置那么应该可以看到一个简单的页面了。之后在输入框中输入**{"command":"login","username" : "username","password" : "password"}**，用户和密码都是您刚才插入到数据库的，不要来问我了！
之后点击按钮会弹出一个登录成功的提示，当然每个proxyserver都可以返回这样的提示，8000，8001，8002默认都会启动服务
