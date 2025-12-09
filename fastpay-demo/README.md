# FAST 易支付 - 对接演示项目

## 简介

这是 FAST 易支付的对接演示项目，展示了如何将 FAST 易支付集成到您的系统中。

## 功能演示

- ✅ 页面跳转支付
- ✅ API 接口支付
- ✅ 订单状态查询
- ✅ 回调通知处理
- ✅ 签名生成与验证

## 快速开始

### 1. 配置修改

编辑 `src/main/resources/application.yml`：

```yaml
fastpay:
  merchant-no: 您的商户编号
  api-secret: 您的API密钥
  gateway-url: http://localhost:9090/fastpay-server
  notify-url: http://your-domain.com/pay/notify
  return-url: http://localhost:8080/pay/return
```

### 2. 启动项目

```bash
mvn spring-boot:run
```

### 3. 访问演示

打开浏览器访问：http://localhost:8080

## 项目结构

```
src/main/java/com/fastpay/demo/
├── config/
│   └── FastPayConfig.java      # 支付配置
├── controller/
│   └── PayController.java      # 支付控制器
├── service/
│   └── FastPayService.java     # 支付服务
└── util/
    └── SignUtil.java           # 签名工具

src/main/resources/
├── templates/
│   ├── index.html              # 首页
│   ├── page_pay.html           # 页面跳转支付
│   ├── api_pay.html            # API接口支付
│   └── result.html             # 支付结果
└── application.yml             # 配置文件
```

## 详细文档

请查看 [Demo 说明文档](../docs/DEMO.md)

## 技术支持

- **开发者**：大熊 Bigbear
