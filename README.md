# 介绍

使用ps2-alerts-remind可以在planetside2警报开始的时候，自动发送邮件和QQ提醒给所有订阅用户，每次警报每个订阅用户只会发一次，订阅用户邮箱/QQ可以自由添加，邮箱数据存储在sqlite文件中（可以自由放置）。
该提醒分为邮箱提醒和QQ提醒，可以同时邮箱提醒和QQ提醒。
警报查询间隔为1分钟，使用的是ps2.sifu.pw/voidwell提供的服务。

# 安装运行步骤

1. 下载
```bash
git clone https://github.com/me10zyl/ps2-alerts-remind.git
```

2. 拷贝数据库到任意地方，并且更改配置文件到该位置
```bash
cp baksql/db.sqlite /var/db.sqlite
vim src/main/resources/application.properties
```
拷贝 baksql/db.sqlite 到 /var/db.sqlite， 将 src/main/resources/application.properties 中的 `spring.datasource.url=jdbc:sqlite:E:\\db.sqlite`
改为 `spring.datasource.url=jdbc:sqlite:/var/db.sqlite`

3. 填写SMTP服务器配置
```bash
vim src/main/resources/smtp.properties
```
更改 src/main/resources/smtp.properties里的配置
```
mail.smtp.server=smtp.163.com
mail.smtp.port=465
mail.user=xxx@163.com
mail.password=yourpassword
```

4. 填写QQ机器人配置(可选配置，如果不使用QQ机器人可跳过，使用的是 [me10zyl/mirai-qqbot-lib](https://github.com/me10zyl/mirai-qqbot-lib) 这个库，了解更详细用法可参见此项目)
```bash
vim src/main/resources/qq.properties
```
更改 src/main/resources/qq.properties里的配置
```
qqbot.verifyCode=myVerifyCode  #mirai http-api插件校验码
qqbot.qqNumber=123456789 #qq号
```

5. 运行
```bash
chmod +x start.sh
chmod +x mvnw
./start.sh 或 ./mvnw spring-boot:run
```

6. 访问添加用户页面
浏览器打开 `http://localhost:10030` ， 可以添加订阅用户

# API接口

1. 添加订阅用户
```bash
curl -X POST 'http://localhost:10030/user/add' --data "{\"server\" : \"Connery\", \"email\" : \"myemail@qq.com\"}"
```
2. 列出所有订阅用户
```bash
curl 'http://localhost:10030/user/list'
```
3. 删除订阅用户
```bash
curl 'http://localhost:10030/user/delete?server=Connery&email=myemail@qq.com'
```

# 更新版本

```bash
git commit -am "1"
git pull origin master
```
