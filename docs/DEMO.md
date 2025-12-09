# FAST 易支付 - Demo 说明文档

## 一、Demo 简介

`fastpay-demo` 是 FAST 易支付的对接演示项目，展示了如何将 FAST 易支付集成到您的系统中。Demo 提供了完整的对接示例，包括：

- **页面跳转支付**：通过表单提交跳转到支付页面
- **API 接口支付**：后端调用 API 创建订单并展示二维码
- **订单查询**：查询订单支付状态
- **回调通知处理**：接收并处理支付成功通知
- **签名生成与验证**：完整的签名算法实现

---

## 二、技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 开发语言 |
| Spring Boot | 3.2.0 | 基础框架 |
| Thymeleaf | - | 模板引擎 |
| Hutool | 5.8+ | 工具类库 |
| Lombok | - | 简化代码 |

---

## 三、项目结构

```
fastpay-demo/
├── src/main/java/com/fastpay/demo/
│   ├── FastPayDemoApplication.java    # 启动类
│   ├── config/
│   │   └── FastPayConfig.java         # 支付配置类
│   ├── controller/
│   │   └── PayController.java         # 支付控制器
│   ├── service/
│   │   └── FastPayService.java        # 支付服务类
│   └── util/
│       └── SignUtil.java              # 签名工具类
│
├── src/main/resources/
│   ├── templates/                      # 页面模板
│   │   ├── index.html                 # 首页
│   │   ├── page_pay.html              # 页面跳转支付
│   │   ├── page_pay_submit.html       # 支付表单提交页
│   │   ├── api_pay.html               # API 接口支付
│   │   └── result.html                # 支付结果页
│   └── application.yml                 # 配置文件
│
└── pom.xml                             # Maven 配置
```

---

## 四、快速开始

### 4.1 环境准备

1. 确保已安装 JDK 17+
2. 确保已安装 Maven 3.8+
3. 确保 FAST 易支付服务端已启动

### 4.2 配置修改

编辑 `src/main/resources/application.yml`：

```yaml
server:
  port: 8080

# FAST 易支付配置
fastpay:
  # 商户编号（在商户平台获取）
  merchant-no: M230117997677
  # API 密钥（在商户平台获取）
  api-secret: 9024dc8cd22d4113ad76ca37dead0817
  # 支付网关地址
  gateway-url: http://localhost:9090/fastpay-server
  # 异步通知地址（需要外网可访问）
  notify-url: http://your-domain.com/pay/notify
  # 同步跳转地址
  return-url: http://localhost:8080/pay/return
```

**配置说明**：

| 配置项 | 说明 | 获取方式 |
|--------|------|----------|
| merchant-no | 商户编号 | 商户平台 → 开发配置 |
| api-secret | API 密钥 | 商户平台 → 开发配置 → 查看密钥 |
| gateway-url | 支付网关地址 | FAST 易支付服务端地址 |
| notify-url | 异步通知地址 | 您的服务器外网地址 + /pay/notify |
| return-url | 同步跳转地址 | 支付成功后跳转的页面地址 |

### 4.3 启动项目

```bash
cd fastpay-demo
mvn spring-boot:run
```

访问地址：http://localhost:8080

---

## 五、功能演示

### 5.1 首页

访问 http://localhost:8080 进入演示首页，可选择：

- **页面跳转支付**：适用于网站接入
- **API 接口支付**：适用于 APP 或自定义页面

### 5.2 页面跳转支付

#### 流程说明

1. 用户填写订单信息（金额、商品名称、支付方式）
2. 点击「立即支付」
3. 自动跳转到 FAST 易支付页面
4. 用户扫码支付
5. 支付成功后跳转回商户结果页

#### 演示步骤

1. 访问 http://localhost:8080/page-pay
2. 填写订单信息：
   - 订单金额：如 `0.01`
   - 商品名称：如 `测试商品`
   - 支付方式：选择微信或支付宝
3. 点击「立即支付」
4. 页面跳转到支付页面，展示收款二维码
5. 使用手机扫码支付
6. 支付成功后自动跳转回结果页

### 5.3 API 接口支付

#### 流程说明

1. 用户填写订单信息
2. 后端调用 API 创建订单
3. 获取二维码并展示给用户
4. 用户扫码支付
5. 轮询查询订单状态
6. 支付成功后展示结果

#### 演示步骤

1. 访问 http://localhost:8080/api-pay
2. 填写订单信息：
   - 订单金额：如 `0.01`
   - 商品名称：如 `测试商品`
   - 支付方式：选择微信或支付宝
3. 点击「创建订单」
4. 页面展示收款二维码
5. 使用手机扫码支付
6. 页面自动检测支付状态并展示结果

### 5.4 回调通知

支付成功后，FAST 易支付会向配置的 `notify-url` 发送 POST 请求。

**回调地址**：`POST /pay/notify`

**处理逻辑**：

```java
@PostMapping("/pay/notify")
@ResponseBody
public String payNotify(@RequestParam Map<String, Object> params) {
    log.info("收到支付回调通知: {}", params);

    // 1. 验证签名
    if (!fastPayService.verifyNotify(params)) {
        log.error("签名验证失败");
        return "fail";
    }

    // 2. 获取订单信息
    String orderNo = (String) params.get("orderNo");
    String outTradeNo = (String) params.get("outTradeNo");
    String amount = (String) params.get("amount");
    Integer status = Integer.parseInt(params.get("status").toString());

    log.info("订单支付成功 - 平台订单号: {}, 商户订单号: {}, 金额: {}", 
             orderNo, outTradeNo, amount);

    // 3. 处理业务逻辑
    // TODO: 在这里处理您的业务逻辑
    // - 验证订单是否存在
    // - 验证订单金额是否一致
    // - 更新订单状态
    // - 发货或开通服务

    // 4. 返回 success 表示处理成功
    return "success";
}
```

---

## 六、核心代码说明

### 6.1 配置类 (FastPayConfig.java)

```java
@Data
@Component
@ConfigurationProperties(prefix = "fastpay")
public class FastPayConfig {
    
    /** 商户编号 */
    private String merchantNo;
    
    /** API 密钥 */
    private String apiSecret;
    
    /** 支付网关地址 */
    private String gatewayUrl;
    
    /** 异步通知地址 */
    private String notifyUrl;
    
    /** 同步跳转地址 */
    private String returnUrl;

    /** 获取页面跳转支付地址 */
    public String getSubmitUrl() {
        return gatewayUrl + "/api/pay/submit";
    }

    /** 获取 API 创建订单地址 */
    public String getCreateUrl() {
        return gatewayUrl + "/api/pay/create";
    }

    /** 获取订单查询地址 */
    public String getQueryUrl() {
        return gatewayUrl + "/api/pay/query";
    }
}
```

### 6.2 签名工具类 (SignUtil.java)

```java
public class SignUtil {

    /**
     * 生成签名
     *
     * @param params    参数 Map
     * @param apiSecret API 密钥
     * @return 签名字符串
     */
    public static String generateSign(Map<String, Object> params, String apiSecret) {
        // 1. 移除 sign 参数
        Map<String, Object> signParams = new TreeMap<>(params);
        signParams.remove("sign");

        // 2. 按字母顺序拼接参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : signParams.entrySet()) {
            if (entry.getValue() != null && !"".equals(entry.getValue().toString())) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }

        // 3. 拼接密钥
        sb.append("key=").append(apiSecret);

        // 4. MD5 加密并转大写
        return DigestUtil.md5Hex(sb.toString()).toUpperCase();
    }

    /**
     * 验证签名
     *
     * @param params    参数 Map
     * @param apiSecret API 密钥
     * @return 是否验证通过
     */
    public static boolean verifySign(Map<String, Object> params, String apiSecret) {
        String sign = (String) params.get("sign");
        if (sign == null || sign.isEmpty()) {
            return false;
        }
        String mySign = generateSign(params, apiSecret);
        return sign.equals(mySign);
    }
}
```

### 6.3 支付服务类 (FastPayService.java)

#### 构建页面跳转支付参数

```java
public Map<String, Object> buildPagePayParams(String outTradeNo, String amount, 
                                               String subject, String payType) {
    Map<String, Object> params = new HashMap<>();
    params.put("merchantNo", config.getMerchantNo());
    params.put("outTradeNo", outTradeNo);
    params.put("amount", amount);
    params.put("subject", subject);
    params.put("payType", payType);
    params.put("timestamp", System.currentTimeMillis() / 1000);
    params.put("returnUrl", config.getReturnUrl());

    // 生成签名
    String sign = SignUtil.generateSign(params, config.getApiSecret());
    params.put("sign", sign);
    
    return params;
}
```

#### API 创建订单

```java
public JSONObject createOrder(String outTradeNo, String amount, 
                               String subject, String payType) {
    Map<String, Object> params = new HashMap<>();
    params.put("merchantNo", config.getMerchantNo());
    params.put("outTradeNo", outTradeNo);
    params.put("amount", amount);
    params.put("subject", subject);
    params.put("payType", payType);
    params.put("timestamp", System.currentTimeMillis() / 1000);

    // 生成签名
    String sign = SignUtil.generateSign(params, config.getApiSecret());
    params.put("sign", sign);

    // 发送请求
    HttpResponse response = HttpRequest.post(config.getCreateUrl())
            .header("Content-Type", "application/json")
            .body(JSONUtil.toJsonStr(params))
            .timeout(10000)
            .execute();

    return JSONUtil.parseObj(response.body());
}
```

#### 查询订单状态

```java
public JSONObject queryOrder(String outTradeNo) {
    Map<String, Object> params = new HashMap<>();
    params.put("merchantNo", config.getMerchantNo());
    params.put("outTradeNo", outTradeNo);
    params.put("timestamp", System.currentTimeMillis() / 1000);

    // 生成签名
    String sign = SignUtil.generateSign(params, config.getApiSecret());
    params.put("sign", sign);

    // 构建查询 URL
    StringBuilder url = new StringBuilder(config.getQueryUrl());
    url.append("?merchantNo=").append(params.get("merchantNo"));
    url.append("&outTradeNo=").append(params.get("outTradeNo"));

    // 发送请求
    HttpResponse response = HttpRequest.get(url.toString())
            .timeout(10000)
            .execute();

    return JSONUtil.parseObj(response.body());
}
```

---

## 七、页面模板说明

### 7.1 首页 (index.html)

展示两种支付方式的入口：

- 页面跳转支付
- API 接口支付

### 7.2 页面跳转支付 (page_pay.html)

订单信息填写表单：

- 订单金额输入
- 商品名称输入
- 支付方式选择
- 提交按钮

### 7.3 支付表单提交页 (page_pay_submit.html)

自动提交表单到支付网关：

```html
<form id="payForm" action="${submitUrl}" method="POST">
    <input type="hidden" name="merchantNo" value="${params.merchantNo}">
    <input type="hidden" name="outTradeNo" value="${params.outTradeNo}">
    <input type="hidden" name="amount" value="${params.amount}">
    <input type="hidden" name="subject" value="${params.subject}">
    <input type="hidden" name="payType" value="${params.payType}">
    <input type="hidden" name="returnUrl" value="${params.returnUrl}">
    <input type="hidden" name="timestamp" value="${params.timestamp}">
    <input type="hidden" name="sign" value="${params.sign}">
</form>

<script>
    document.getElementById('payForm').submit();
</script>
```

### 7.4 API 接口支付 (api_pay.html)

展示二维码并轮询订单状态：

```javascript
// 创建订单
async function createOrder() {
    const response = await fetch('/api-pay/create', {
        method: 'POST',
        body: new FormData(form)
    });
    const result = await response.json();
    
    if (result.success) {
        // 展示二维码
        showQrcode(result.data.qrcodeUrl);
        // 开始轮询订单状态
        startPolling(result.outTradeNo);
    }
}

// 轮询订单状态
function startPolling(outTradeNo) {
    const timer = setInterval(async () => {
        const response = await fetch('/api-pay/query?outTradeNo=' + outTradeNo);
        const result = await response.json();
        
        if (result.success && result.data.status === 1) {
            clearInterval(timer);
            // 支付成功
            showSuccess();
        }
    }, 3000);
}
```

### 7.5 支付结果页 (result.html)

展示支付结果：

- 支付成功/失败状态
- 订单号信息
- 返回首页按钮

---

## 八、集成到您的项目

### 8.1 添加依赖

```xml
<!-- HTTP 请求 -->
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-http</artifactId>
    <version>5.8.25</version>
</dependency>

<!-- JSON 处理 -->
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-json</artifactId>
    <version>5.8.25</version>
</dependency>

<!-- 加密工具 -->
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-crypto</artifactId>
    <version>5.8.25</version>
</dependency>
```

### 8.2 复制核心类

将以下类复制到您的项目中：

1. `FastPayConfig.java` - 配置类
2. `FastPayService.java` - 服务类
3. `SignUtil.java` - 签名工具类

### 8.3 配置参数

在 `application.yml` 中添加配置：

```yaml
fastpay:
  merchant-no: 您的商户编号
  api-secret: 您的API密钥
  gateway-url: 支付网关地址
  notify-url: 您的回调通知地址
  return-url: 支付成功跳转地址
```

### 8.4 调用示例

```java
@Autowired
private FastPayService fastPayService;

// 创建订单
public void createOrder() {
    String outTradeNo = "ORDER" + System.currentTimeMillis();
    JSONObject result = fastPayService.createOrder(
        outTradeNo, 
        "10.00", 
        "测试商品", 
        "wxpay"
    );
    
    if (result.getInt("code") == 200) {
        JSONObject data = result.getJSONObject("data");
        String qrcodeUrl = data.getStr("qrcodeUrl");
        String payPageUrl = data.getStr("payPageUrl");
        // 处理支付信息...
    }
}

// 处理回调
@PostMapping("/pay/notify")
public String handleNotify(@RequestParam Map<String, Object> params) {
    // 验证签名
    if (!fastPayService.verifyNotify(params)) {
        return "fail";
    }
    
    // 处理业务逻辑
    String outTradeNo = (String) params.get("outTradeNo");
    // 更新订单状态...
    
    return "success";
}
```

---

## 九、常见问题

### 9.1 创建订单失败

**可能原因**：
1. 商户编号或 API 密钥错误
2. 支付网关地址配置错误
3. 签名生成错误

**解决方法**：
1. 检查商户平台的配置信息
2. 确认支付网关地址正确
3. 检查签名算法实现

### 9.2 回调收不到

**可能原因**：
1. notify-url 不是外网可访问地址
2. 服务器防火墙拦截
3. 回调处理返回非 success

**解决方法**：
1. 使用外网可访问的地址（可用 ngrok 测试）
2. 检查防火墙设置
3. 确保回调处理返回 "success" 字符串

### 9.3 签名验证失败

**可能原因**：
1. API 密钥不一致
2. 参数排序错误
3. 编码问题

**解决方法**：
1. 确认使用正确的 API 密钥
2. 检查参数是否按字母顺序排序
3. 确保字符编码一致（UTF-8）

---

## 十、测试建议

### 10.1 本地测试

1. 使用小金额测试（如 0.01 元）
2. 先测试页面跳转支付
3. 再测试 API 接口支付
4. 最后测试回调通知

### 10.2 回调测试

使用 ngrok 等工具将本地服务暴露到外网：

```bash
ngrok http 8080
```

将生成的外网地址配置为 notify-url。

### 10.3 日志查看

Demo 项目会输出详细的请求和响应日志，方便调试：

```
INFO  - API 创建订单请求参数: {merchantNo=M230117997677, ...}
INFO  - API 创建订单响应状态码: 200, 响应内容: {...}
INFO  - 收到支付回调通知: {orderNo=P202512050001, ...}
```

---

## 十一、技术支持

如有问题，请联系：

- **开发者**：大熊 Bigbear
- **项目地址**：https://github.com/your-repo/bigbear-pay
