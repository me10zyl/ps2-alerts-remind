# 介绍

使用ps2-alerts-remind可以在planetside2警报开始的时候，自动发送邮件提醒给所有订阅用户，每次警报每个订阅用户只会发一次，订阅用户邮箱可以自由添加，邮箱数据存储在sqlite文件中（可以自由放置）。
警报查询间隔为5分钟，使用的是ps2.sifu.pw提供的服务。

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
3. 运行
```bash
mvnw spring-boot:run
```

# API接口

1. 添加订阅用户
```bash
curl 'http://localhost:10030/user/add?server=Connery&email=myemail@qq.com'
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
