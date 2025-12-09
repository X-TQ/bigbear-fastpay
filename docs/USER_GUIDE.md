# FAST 易支付 - 使用说明文档

## 一、快速开始

### 1.1 环境要求

| 环境 | 版本要求 |
|------|----------|
| JDK | 17+ |
| Node.js | 18+ |
| MySQL | 8.0+ |
| Maven | 3.8+ |

### 1.2 安装部署

#### 步骤一：克隆项目

```bash
git clone https://github.com/your-repo/bigbear-pay.git
cd bigbear-pay
```

#### 步骤二：初始化数据库

```sql
-- 创建数据库
CREATE DATABASE fastpay DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE fastpay;

-- 执行初始化脚本
source fastpay-server/src/main/resources/db/init.sql
```

#### 步骤三：配置后端服务

编辑 `fastpay-server/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/fastpay?useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password

jwt:
  secret: your-jwt-secret-key-at-least-32-characters
  expiration: 86400000  # Token 有效期（毫秒）

fastpay:
  pay:
    order-timeout-minutes: 60  # 订单超时时间（分钟）
    page-domain: http://localhost:3002  # 支付页面域名
```

#### 步骤四：启动后端服务

```bash
cd fastpay-server
mvn spring-boot:run
```

服务地址：http://localhost:9090

#### 步骤五：启动管理后台

```bash
cd fastpay-admin
npm install
npm run dev
```

访问地址：http://localhost:3001

**默认账号**：`admin` / `admin123`

#### 步骤六：启动商户平台

```bash
cd fastpay-merchant
npm install
npm run dev
```

访问地址：http://localhost:3002

---

## 二、管理后台使用指南

### 2.1 登录系统

1. 访问 http://localhost:3001
2. 输入管理员账号密码
3. 点击登录进入系统

### 2.2 控制台

控制台展示系统整体运营数据：

- **今日订单数**：当日创建的订单总数
- **今日成交额**：当日支付成功的总金额
- **今日成功率**：支付成功订单占比
- **商户总数**：系统注册的商户数量
- **订单趋势图**：近期订单数量变化
- **支付方式占比**：微信/支付宝使用比例
- **活跃商户**：交易量最大的商户列表

### 2.3 商户管理

#### 创建商户

1. 点击「新增商户」按钮
2. 填写商户信息：
   - **商户名称**：商户的显示名称
   - **登录用户名**：商户登录账号
   - **登录密码**：商户登录密码
   - **联系人**：商户联系人姓名
   - **联系电话**：商户联系电话
   - **回调地址**：默认支付回调通知地址
3. 点击确定完成创建

#### 管理商户

- **查看详情**：查看商户完整信息
- **编辑**：修改商户信息
- **启用/禁用**：控制商户状态
- **重置密钥**：重新生成 API 密钥

### 2.4 店铺管理

#### 创建店铺

1. 选择所属商户
2. 点击「新增店铺」
3. 填写店铺信息：
   - **店铺名称**：店铺显示名称
   - **店铺描述**：店铺简介
   - **联系人**：店铺联系人
   - **联系电话**：店铺联系电话

#### 管理二维码

1. 选择店铺
2. 点击「添加二维码」
3. 上传收款二维码图片
4. 系统自动解析二维码内容
5. 选择对应的支付通道
6. 确认添加

### 2.5 订单管理

#### 订单列表

展示所有订单信息：

| 字段 | 说明 |
|------|------|
| 平台订单号 | 系统生成的唯一订单号 |
| 商户订单号 | 商户系统的订单号 |
| 商户 | 所属商户名称 |
| 店铺 | 所属店铺名称 |
| 金额 | 订单金额 |
| 支付类型 | 微信/支付宝 |
| 状态 | 待支付/已支付/已过期/已关闭 |
| 回调状态 | 未通知/成功/失败 |
| 创建时间 | 订单创建时间 |

#### 订单操作

- **查看详情**：查看订单完整信息
- **确认支付**：手动确认订单已支付（待支付订单）
- **关闭订单**：关闭未支付的订单
- **重发通知**：重新发送回调通知（已支付且通知失败的订单）

---

## 三、商户平台使用指南

### 3.1 商户登录

1. 访问 http://localhost:3002
2. 输入商户账号密码（由管理员创建）
3. 点击登录进入系统

### 3.2 控制台

展示商户自己的运营数据：

- **今日订单**：当日订单数量
- **今日成交**：当日成交金额
- **本月成交**：本月累计成交
- **累计成交**：历史总成交额
- **快捷操作**：常用功能入口
- **最近订单**：最新的订单列表

### 3.3 店铺管理

#### 创建店铺

1. 点击「新增店铺」
2. 填写店铺信息
3. 确认创建

#### 管理二维码

1. 选择店铺
2. 切换到「二维码管理」标签
3. 添加收款二维码：
   - 上传二维码图片
   - 系统自动识别支付类型
   - 选择对应通道
   - 确认添加

#### 查看订单

1. 选择店铺
2. 切换到「订单记录」标签
3. 查看该店铺的所有订单

### 3.4 通道管理

#### 创建通道

1. 点击「新增通道」
2. 填写通道信息：
   - **通道名称**：通道显示名称
   - **支付类型**：微信/支付宝
   - **通道描述**：通道说明
3. 确认创建

#### 通道模板

点击「生成模板」可获取通道的监听回调配置，用于配置收款监听服务。

### 3.5 订单管理

查看和管理所有订单：

- **筛选订单**：按订单号、状态、支付类型筛选
- **查看详情**：查看订单完整信息
- **确认支付**：手动确认订单已支付
- **关闭订单**：关闭未支付订单
- **重发通知**：重新发送回调通知

### 3.6 开发配置

#### 查看 API 信息

- **商户编号**：用于 API 调用的商户标识
- **API 密钥**：用于签名的密钥（点击查看）
- **重置密钥**：重新生成 API 密钥

#### 配置回调地址

- **回调通知地址**：支付成功后的通知地址
- 点击「保存配置」生效

### 3.7 开发文档

商户平台内置完整的开发文档：

- **接口说明**：API 接口详细说明
- **签名算法**：签名生成方法和示例代码
- **代码示例**：PHP、Java、Python 等语言示例
- **WebSocket**：实时支付结果推送说明

---

## 四、API 对接指南

### 4.1 接口概览

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/pay/submit` | POST | 页面跳转支付 |
| `/api/pay/create` | POST | API 创建订单 |
| `/api/pay/query` | GET | 查询订单状态 |

### 4.2 页面跳转支付

适用于网站接入，通过表单提交跳转到支付页面。

**请求地址**：`POST /api/pay/submit`

**请求参数**：

| 参数 | 必填 | 类型 | 说明 |
|------|------|------|------|
| merchantNo | 是 | String | 商户编号 |
| outTradeNo | 是 | String | 商户订单号（唯一） |
| amount | 是 | String | 订单金额（元），如 10.00 |
| subject | 是 | String | 商品名称 |
| payType | 是 | String | 支付类型：wxpay / alipay |
| returnUrl | 否 | String | 支付成功后跳转地址 |
| notifyUrl | 否 | String | 异步通知地址（覆盖默认配置） |
| extParam | 否 | String | 扩展参数，回调时原样返回 |
| timestamp | 是 | Long | 时间戳（秒） |
| sign | 是 | String | 签名 |

**HTML 表单示例**：

```html
<form action="https://your-domain/api/pay/submit" method="POST">
  <input type="hidden" name="merchantNo" value="M230117997677">
  <input type="hidden" name="outTradeNo" value="ORDER202512050001">
  <input type="hidden" name="amount" value="10.00">
  <input type="hidden" name="subject" value="测试商品">
  <input type="hidden" name="payType" value="wxpay">
  <input type="hidden" name="returnUrl" value="https://your-site.com/pay/result">
  <input type="hidden" name="timestamp" value="1733400000">
  <input type="hidden" name="sign" value="签名值">
  <button type="submit">立即支付</button>
</form>
```

### 4.3 API 创建订单

适用于 APP 或自定义支付页面。

**请求地址**：`POST /api/pay/create`

**Content-Type**：`application/json`

**请求参数**：

```json
{
  "merchantNo": "M230117997677",
  "outTradeNo": "ORDER202512050001",
  "amount": "10.00",
  "subject": "测试商品",
  "payType": "wxpay",
  "notifyUrl": "https://your-site.com/pay/notify",
  "timestamp": 1733400000,
  "sign": "签名值"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderNo": "P202512050001",
    "outTradeNo": "ORDER202512050001",
    "amount": 10.00,
    "payType": "wxpay",
    "qrcodeUrl": "wxp://xxx",
    "payPageUrl": "https://your-domain/pay?orderNo=xxx",
    "expireTime": "2025-12-05 12:00:00"
  }
}
```

### 4.4 查询订单

**请求地址**：`GET /api/pay/query`

**请求参数**：

| 参数 | 必填 | 说明 |
|------|------|------|
| merchantNo | 是 | 商户编号 |
| outTradeNo | 是 | 商户订单号 |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderNo": "P202512050001",
    "outTradeNo": "ORDER202512050001",
    "amount": "10.00",
    "payAmount": "10.00",
    "status": 1,
    "payTime": "2025-12-05 11:30:00"
  }
}
```

### 4.5 签名算法

#### 签名步骤

1. 将所有非空参数按 **字母顺序** 排序
2. 拼接成 `key=value&key=value` 格式
3. 最后拼接 `&key=API_SECRET`
4. 对整个字符串进行 **MD5** 加密
5. 转换为 **大写**

#### PHP 示例

```php
function generateSign($params, $apiSecret) {
    // 移除 sign 参数
    unset($params['sign']);
    
    // 按字母顺序排序
    ksort($params);
    
    // 拼接参数
    $signStr = '';
    foreach ($params as $k => $v) {
        if ($v !== '' && $v !== null) {
            $signStr .= $k . '=' . $v . '&';
        }
    }
    $signStr .= 'key=' . $apiSecret;
    
    // MD5 加密并转大写
    return strtoupper(md5($signStr));
}
```

#### Java 示例

```java
public static String generateSign(Map<String, Object> params, String apiSecret) {
    // 移除 sign 参数
    params.remove("sign");
    
    // 按字母顺序排序
    List<String> keys = new ArrayList<>(params.keySet());
    Collections.sort(keys);
    
    // 拼接参数
    StringBuilder sb = new StringBuilder();
    for (String key : keys) {
        Object value = params.get(key);
        if (value != null && !"".equals(value.toString())) {
            sb.append(key).append("=").append(value).append("&");
        }
    }
    sb.append("key=").append(apiSecret);
    
    // MD5 加密并转大写
    return DigestUtils.md5Hex(sb.toString()).toUpperCase();
}
```

### 4.6 回调通知

支付成功后，系统会向商户配置的 `notifyUrl` 发送 POST 请求。

**回调参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| merchantNo | String | 商户编号 |
| orderNo | String | 平台订单号 |
| outTradeNo | String | 商户订单号 |
| amount | String | 订单金额 |
| payAmount | String | 实付金额 |
| payType | String | 支付类型 |
| status | String | 订单状态：1-已支付 |
| payTime | String | 支付时间 |
| timestamp | String | 时间戳 |
| extParam | String | 扩展参数 |
| sign | String | 签名 |

**处理流程**：

1. 验证签名
2. 验证订单是否存在
3. 验证订单金额是否一致
4. 更新订单状态
5. 返回 `success` 字符串

**PHP 回调处理示例**：

```php
// 接收回调参数
$params = $_POST;

// 验证签名
$sign = $params['sign'];
unset($params['sign']);
$mySign = generateSign($params, $apiSecret);

if ($sign !== $mySign) {
    echo 'fail';
    exit;
}

// 获取订单信息
$outTradeNo = $params['outTradeNo'];
$amount = $params['amount'];
$status = $params['status'];

// 处理业务逻辑
if ($status == '1') {
    // 更新订单状态
    // 发货或开通服务
}

// 返回成功
echo 'success';
```

### 4.7 WebSocket 实时推送

支付页面可通过 WebSocket 接收实时支付结果。

**连接地址**：`ws://your-domain/fastpay-server/ws/pay/{merchantNo}/{outTradeNo}`

**消息格式**：

```json
{
  "type": "PAY_SUCCESS",
  "orderNo": "P202512050001",
  "outTradeNo": "ORDER202512050001",
  "amount": "10.00"
}
```

**JavaScript 示例**：

```javascript
const ws = new WebSocket('ws://your-domain/fastpay-server/ws/pay/' + merchantNo + outTradeNo);

ws.onmessage = function(event) {
    const data = JSON.parse(event.data);
    if (data.type === 'PAY_SUCCESS') {
        // 支付成功，跳转结果页
        window.location.href = '/pay/result?orderNo=' + data.orderNo;
    }
};

ws.onerror = function(error) {
    console.error('WebSocket 错误:', error);
};
```

---

## 五、常见问题

### 5.1 签名验证失败

**可能原因**：
1. API 密钥不正确
2. 参数排序错误
3. 参数值包含特殊字符未处理
4. 时间戳过期

**解决方法**：
1. 确认使用正确的 API 密钥
2. 检查参数是否按字母顺序排序
3. 确保参数值正确编码
4. 使用当前时间戳

### 5.2 回调通知收不到

**可能原因**：
1. 回调地址不可访问
2. 回调地址返回非 success
3. 服务器防火墙拦截

**解决方法**：
1. 确保回调地址外网可访问
2. 检查回调处理逻辑，确保返回 success
3. 检查服务器防火墙设置
4. 查看订单详情中的回调状态和次数

### 5.3 订单一直待支付

**可能原因**：
1. 二维码未正确配置
2. 支付监听服务未启动
3. 通道配置错误

**解决方法**：
1. 检查二维码是否正确上传和解析
2. 确认支付监听服务正常运行
3. 检查通道配置是否正确

### 5.4 WebSocket 连接失败

**可能原因**：
1. WebSocket 端口未开放
2. Nginx 未配置 WebSocket 代理
3. 跨域问题

**解决方法**：
1. 确保 9090 端口可访问
2. 配置 Nginx WebSocket 代理
3. 检查跨域配置

---

## 六、生产部署

### 6.1 后端打包

```bash
cd fastpay-server
mvn clean package -DskipTests
```

生成的 jar 包位于 `target/fastpay-server-1.0.0.jar`

### 6.2 前端打包

```bash
# 管理后台
cd fastpay-admin
npm run build

# 商户平台
cd fastpay-merchant
npm run build
```

生成的静态文件位于 `dist` 目录

### 6.3 Nginx 配置

```nginx
server {
    listen 80;
    server_name pay.your-domain.com;

    # 后端 API
    location /api {
        proxy_pass http://127.0.0.1:9090;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # WebSocket
    location /ws {
        proxy_pass http://127.0.0.1:9090;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_read_timeout 3600s;
    }

    # 商户平台前端
    location / {
        root /var/www/fastpay-merchant/dist;
        try_files $uri $uri/ /index.html;
    }
}

server {
    listen 80;
    server_name admin.your-domain.com;

    # 后端 API
    location /api {
        proxy_pass http://127.0.0.1:9090;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # 管理后台前端
    location / {
        root /var/www/fastpay-admin/dist;
        try_files $uri $uri/ /index.html;
    }
}
```

### 6.4 启动服务

```bash
# 后台启动
nohup java -jar fastpay-server-1.0.0.jar --spring.profiles.active=prod > /dev/null 2>&1 &
```

---

## 七、技术支持

如有问题，请联系：

- **开发者**：大熊 Bigbear
- **项目地址**：https://github.com/your-repo/bigbear-pay
