# 项目说明文档

## 一、项目作用

本项目是一个基于Spring Boot的缓存示例应用，主要展示了多级缓存（本地Guava缓存+Redis分布式缓存）在实际开发中的应用。通过用户信息的查询与更新场景，演示了缓存的读取、写入、失效及一致性维护等核心功能，同时集成了MySQL数据库作为持久化存储，形成完整的数据访问链路。

## 二、项目分层结构

### 1. 核心包结构
```
com.project.three
├── ThreeApplication.java       // 应用启动类
├── cache/                      // 缓存相关组件
├── config/                     // 配置类
├── controller/                 // 控制器层
├── entity/                     // 实体类
├── mapper/                     // 数据访问层
└── service/                    // 业务逻辑层
```

### 2. 各层说明

#### （1）缓存层（cache/）
- **LocalCache.java**：基于Guava实现的本地缓存组件，提供get/put/delete基础操作，带日志记录
- **RedisCache.java**：Redis缓存组件，针对用户信息提供专用缓存操作，支持JSON序列化
- **CacheManager.java**：缓存管理类，封装用户缓存的读写逻辑
- **CacheKeyEnum.java**：缓存键生成工具类
- **CacheRefreshListener.java**：Redis发布订阅监听器，用于缓存刷新通知

#### （2）配置层（config/）
- **LocalCacheConfig.java**：Guava缓存配置类
- **RedisConfig.java**：Redis序列化及模板配置
- **RedisPubSubConfig.java**：Redis发布订阅配置，用于缓存一致性维护

#### （3）控制器层（controller/）
- **UserController.java**：提供用户查询（GET /users/{id}）和更新（PUT /users）接口

#### （4）实体层（entity/）
- **User.java**：用户实体类，包含id、username、email等字段

#### （5）数据访问层（mapper/）
- **UserMapper.java**：MyBatis接口，定义用户查询和更新方法
- **UserMapper.xml**：MyBatis映射文件，实现SQL逻辑

#### （6）业务层（service/）
- 业务逻辑实现（代码未展示，主要负责协调缓存与数据库操作）

## 三、测试方法

### 1. 环境准备
- 确保MySQL数据库已创建`three`库，且用户表结构存在
- 确保Redis集群（192.168.145.128:7001-7006）可正常访问
- 配置文件`application.yml`中修改数据库和Redis连接参数

### 2. 单元测试

#### （1）连接测试
运行`ConnectionTest.java`测试类，验证MySQL和Redis连接是否正常：
```java
// 测试全部连接
@Test
public void testAllConnections() {
    testMysqlConnection();
    testRedisConnection();
}
```

#### （2）缓存功能测试
运行`CacheTest.java`测试类，验证缓存命中情况：
```java
// 测试缓存命中逻辑
@Test
public void testCacheHit() {
    String key = "1"; // 数据库中存在的用户ID
    // 第一次查询：走数据库，同时写入缓存
    String result1 = cacheManager.getData(key);
    // 第二次查询：走本地缓存
    String result2 = cacheManager.getData(key);
    // 验证结果一致性
    assert result1.equals(result2);
}
```

### 3. 接口测试

#### （1）查询用户
通过HTTP GET请求访问：
```
http://localhost:8080/users/{id}
```
- 第一次请求：日志显示从数据库查询并写入缓存
- 第二次请求：日志显示从本地缓存获取

#### （2）更新用户
通过HTTP PUT请求访问：
```
http://localhost:8080/users
```
请求体：
```json
{
    "id": 1,
    "username": "newName",
    "email": "new@example.com",
    "phone": "123456789"
}
```
- 更新成功后，缓存会被自动刷新

## 四、缓存策略说明

1. **多级缓存机制**：
   - 一级缓存：本地Guava缓存（5分钟过期，最大1000条）
   - 二级缓存：Redis分布式缓存（1小时过期）

2. **缓存更新策略**：
   - 查询：先查本地缓存→再查Redis→最后查数据库
   - 更新：更新数据库后，主动删除对应缓存
   - 缓存一致性：通过Redis发布订阅机制，通知其他节点刷新本地缓存

3. **关键配置**：
   - 本地缓存：`LocalCacheConfig.java`中配置过期时间和容量
   - Redis缓存：`application.yml`中配置集群节点和连接池参数
