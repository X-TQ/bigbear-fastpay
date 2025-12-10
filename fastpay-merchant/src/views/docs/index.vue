<template>
  <div class="docs-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">开发文档</h1>
      <p class="page-desc">FAST 易支付 API 接入指南</p>
    </div>

    <el-row :gutter="16">
      <!-- 左侧导航 -->
      <el-col :span="5">
        <div class="dev-card doc-nav">
          <div class="card-body">
            <el-menu :default-active="activeSection" @select="scrollToSection">
              <el-menu-item index="quick-start">
                <el-icon><Promotion /></el-icon>
                <span>快速开始</span>
              </el-menu-item>
              <el-menu-item index="page-pay">
                <el-icon><Link /></el-icon>
                <span>页面跳转支付</span>
              </el-menu-item>
              <el-menu-item index="api-pay">
                <el-icon><Connection /></el-icon>
                <span>API接口支付</span>
              </el-menu-item>
              <el-menu-item index="query-order">
                <el-icon><Search /></el-icon>
                <span>查询订单</span>
              </el-menu-item>
              <el-menu-item index="callback">
                <el-icon><Bell /></el-icon>
                <span>回调通知</span>
              </el-menu-item>
              <el-menu-item index="websocket">
                <el-icon><Connection /></el-icon>
                <span>WebSocket监听</span>
              </el-menu-item>
              <el-menu-item index="sign">
                <el-icon><Key /></el-icon>
                <span>签名算法</span>
              </el-menu-item>
              <el-menu-item index="error-code">
                <el-icon><Warning /></el-icon>
                <span>错误码</span>
              </el-menu-item>
            </el-menu>
          </div>
        </div>
      </el-col>

      <!-- 右侧内容 -->
      <el-col :span="19">
        <!-- 快速开始 -->
        <div id="quick-start" class="dev-card doc-section">
          <div class="card-header">
            <span class="card-title">快速开始</span>
          </div>
          <div class="card-body">
            <p>FAST 易支付提供简单易用的支付接口，支持两种接入方式：</p>
            <el-row :gutter="16" style="margin-top: 16px;">
              <el-col :span="12">
                <div class="method-card">
                  <div class="method-icon">
                    <el-icon :size="24"><Link /></el-icon>
                  </div>
                  <h4>页面跳转支付</h4>
                  <p>使用 form 表单提交跳转到支付页面，适合 Web 网站接入</p>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="method-card">
                  <div class="method-icon">
                    <el-icon :size="24"><Connection /></el-icon>
                  </div>
                  <h4>API 接口支付</h4>
                  <p>后端调用接口获取二维码，前端展示供用户扫码，适合 APP 接入</p>
                </div>
              </el-col>
            </el-row>
            <el-alert type="info" :closable="false" style="margin-top: 16px;">
              <template #title>
                <strong>接入前准备：</strong>请先在「开发配置」页面获取您的 API Key 和 API Secret
              </template>
            </el-alert>
          </div>
        </div>

        <!-- 页面跳转支付 -->
        <div id="page-pay" class="dev-card doc-section">
          <div class="card-header">
            <span class="card-title">页面跳转支付</span>
          </div>
          <div class="card-body">
            <p>适用于 Web 网站接入，使用 form 表单提交跳转到支付页面，用户扫码支付后自动跳转回商户页面。</p>
            <div class="api-info">
              <el-tag type="success">POST</el-tag>
              <code>/api/pay/submit</code>
            </div>
            <h4>请求参数（表单字段）</h4>
            <el-table :data="pagePayParams" border size="small">
              <el-table-column prop="name" label="参数名" width="140" />
              <el-table-column prop="required" label="必填" width="80">
                <template #default="{ row }">
                  <el-tag :type="row.required ? 'danger' : 'info'" size="small">
                    {{ row.required ? '是' : '否' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="type" label="类型" width="100" />
              <el-table-column prop="desc" label="说明" />
            </el-table>

            <h4 style="margin-top: 24px;">调用示例（HTML Form 表单提交）</h4>
            <div class="code-block">
              <pre>{{ pagePayFormExample }}</pre>
            </div>

            <h4 style="margin-top: 24px;">代码示例</h4>
            <el-tabs type="border-card" class="code-tabs">
              <el-tab-pane label="PHP">
                <div class="code-block">
                  <pre>{{ pagePayPhpCode }}</pre>
                </div>
              </el-tab-pane>
              <el-tab-pane label="Java">
                <div class="code-block">
                  <pre>{{ pagePayJavaCode }}</pre>
                </div>
              </el-tab-pane>
              <el-tab-pane label="Python">
                <div class="code-block">
                  <pre>{{ pagePayPythonCode }}</pre>
                </div>
              </el-tab-pane>
            </el-tabs>

            <el-alert type="info" :closable="false" style="margin-top: 16px;">
              <template #title>
                <strong>说明：</strong>提交后会自动跳转到支付页面，用户支付成功后会跳转到 returnUrl（携带 order_no、out_trade_no、status 参数）
              </template>
            </el-alert>
          </div>
        </div>

        <!-- API接口支付 -->
        <div id="api-pay" class="dev-card doc-section">
          <div class="card-header">
            <span class="card-title">API接口支付</span>
          </div>
          <div class="card-body">
            <p>适用于 APP 或自定义支付页面接入，后端调用接口获取收款二维码，前端展示供用户扫码。</p>
            <div class="api-info">
              <el-tag type="success">POST</el-tag>
              <code>/api/pay/create</code>
              <span class="api-desc">Content-Type: application/json</span>
            </div>
            <h4>请求参数</h4>
            <el-table :data="apiPayParams" border size="small">
              <el-table-column prop="name" label="参数名" width="140" />
              <el-table-column prop="required" label="必填" width="80">
                <template #default="{ row }">
                  <el-tag :type="row.required ? 'danger' : 'info'" size="small">
                    {{ row.required ? '是' : '否' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="type" label="类型" width="100" />
              <el-table-column prop="desc" label="说明" />
            </el-table>

            <h4 style="margin-top: 24px;">响应参数</h4>
            <el-table :data="apiPayResponse" border size="small">
              <el-table-column prop="name" label="参数名" width="160" />
              <el-table-column prop="type" label="类型" width="100" />
              <el-table-column prop="desc" label="说明" />
            </el-table>
          </div>
        </div>

        <!-- 查询订单 -->
        <div id="query-order" class="dev-card doc-section">
          <div class="card-header">
            <span class="card-title">查询订单接口</span>
          </div>
          <div class="card-body">
            <div class="api-info">
              <el-tag>GET</el-tag>
              <code>/api/pay/query</code>
            </div>
            <h4>请求参数</h4>
            <el-table :data="queryOrderParams" border size="small">
              <el-table-column prop="name" label="参数名" width="140" />
              <el-table-column prop="required" label="必填" width="80">
                <template #default="{ row }">
                  <el-tag :type="row.required ? 'danger' : 'info'" size="small">
                    {{ row.required ? '是' : '否' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="type" label="类型" width="100" />
              <el-table-column prop="desc" label="说明" />
            </el-table>
          </div>
        </div>

        <!-- 回调通知 -->
        <div id="callback" class="dev-card doc-section">
          <div class="card-header">
            <span class="card-title">回调通知</span>
          </div>
          <div class="card-body">
            <p>支付成功后，系统会向商户配置的 <code>notify_url</code> 发送 POST 请求（application/json）：</p>
            <h4 style="margin-top: 16px;">通知参数</h4>
            <el-table :data="callbackParams" border size="small">
              <el-table-column prop="name" label="参数名" width="140" />
              <el-table-column prop="type" label="类型" width="100" />
              <el-table-column prop="desc" label="说明" />
            </el-table>
            <el-alert type="warning" :closable="false" style="margin-top: 16px;">
              <template #title>
                <strong>重要：</strong>收到通知后请返回字符串 <code>"success"</code>，否则系统会在 24 小时内重复通知（最多 8 次）
              </template>
            </el-alert>
          </div>
        </div>

        <!-- WebSocket监听 -->
        <div id="websocket" class="dev-card doc-section">
          <div class="card-header">
            <span class="card-title">WebSocket 实时监听</span>
          </div>
          <div class="card-body">
            <p>除了轮询查询和异步回调，您还可以通过 WebSocket 实时监听订单支付状态，获得更好的用户体验。</p>
            
            <el-alert type="success" :closable="false" style="margin: 16px 0;">
              <template #title>
                <strong>推荐场景：</strong>前端展示二维码后，通过 WebSocket 实时监听支付结果，用户支付成功后立即跳转，无需轮询
              </template>
            </el-alert>

            <h4>连接地址</h4>
            <div class="api-info">
              <el-tag type="primary">WebSocket</el-tag>
              <code>ws://your-domain/fastpay-server/ws/pay/{merchantNo}/{outTradeNo}</code>
            </div>

            <h4 style="margin-top: 20px;">连接参数</h4>
            <el-table :data="wsConnectParams" border size="small">
              <el-table-column prop="name" label="参数名" width="140" />
              <el-table-column prop="required" label="必填" width="80">
                <template #default="{ row }">
                  <el-tag :type="row.required ? 'danger' : 'info'" size="small">
                    {{ row.required ? '是' : '否' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="type" label="类型" width="100" />
              <el-table-column prop="desc" label="说明" />
            </el-table>

            <h4 style="margin-top: 20px;">消息格式</h4>
            <p>服务端推送的消息为 JSON 格式：</p>
            <div class="code-block">
              <pre>{{ wsMessageFormat }}</pre>
            </div>

            <h4 style="margin-top: 20px;">消息类型说明</h4>
            <el-table :data="wsMessageTypes" border size="small">
              <el-table-column prop="type" label="类型" width="120" />
              <el-table-column prop="desc" label="说明" />
              <el-table-column prop="action" label="建议操作" />
            </el-table>

            <h4 style="margin-top: 20px;">心跳保活</h4>
            <p>为保持连接稳定，建议客户端每 <strong>30 秒</strong> 发送一次心跳消息：</p>
            <div class="code-block">
              <pre>ping</pre>
            </div>
            <p style="margin-top: 8px;">服务端收到后会回复：</p>
            <div class="code-block">
              <pre>pong</pre>
            </div>

            <h4 style="margin-top: 24px;">前端代码示例（JavaScript）</h4>
            <div class="code-block">
              <pre>{{ wsJsCode }}</pre>
            </div>

            <h4 style="margin-top: 24px;">后端代码示例</h4>
            <el-tabs type="border-card" class="code-tabs">
              <el-tab-pane label="Java">
                <div class="code-block">
                  <pre>{{ wsJavaCode }}</pre>
                </div>
              </el-tab-pane>
              <el-tab-pane label="Python">
                <div class="code-block">
                  <pre>{{ wsPythonCode }}</pre>
                </div>
              </el-tab-pane>
              <el-tab-pane label="Node.js">
                <div class="code-block">
                  <pre>{{ wsNodeCode }}</pre>
                </div>
              </el-tab-pane>
            </el-tabs>

            <el-alert type="info" :closable="false" style="margin-top: 16px;">
              <template #title>
                <strong>注意事项：</strong>
                <ul style="margin: 8px 0 0 20px; line-height: 1.8;">
                  <li>WebSocket 连接仅用于监听支付结果，不能替代异步回调通知</li>
                  <li>建议同时实现异步回调，作为 WebSocket 的备用方案</li>
                  <li>连接断开后应自动重连，最多重试 3 次</li>
                  <li>订单支付成功或超时后，服务端会主动关闭连接</li>
                </ul>
              </template>
            </el-alert>
          </div>
        </div>

        <!-- 签名算法 -->
        <div id="sign" class="dev-card doc-section">
          <div class="card-header">
            <span class="card-title">签名算法</span>
          </div>
          <div class="card-body">
            <div class="sign-steps">
              <div class="sign-step">
                <div class="step-num">1</div>
                <div class="step-content">
                  <h4>参数排序</h4>
                  <p>将所有非空参数（不包括 sign）按参数名 ASCII 码从小到大排序</p>
                </div>
              </div>
              <div class="sign-step">
                <div class="step-num">2</div>
                <div class="step-content">
                  <h4>拼接字符串</h4>
                  <p>使用 URL 键值对格式拼接成字符串：key1=value1&key2=value2...</p>
                </div>
              </div>
              <div class="sign-step">
                <div class="step-num">3</div>
                <div class="step-content">
                  <h4>追加密钥</h4>
                  <p>在拼接好的字符串末尾追加 &key=API_SECRET</p>
                </div>
              </div>
              <div class="sign-step">
                <div class="step-num">4</div>
                <div class="step-content">
                  <h4>MD5 加密</h4>
                  <p>对最终字符串进行 MD5 加密，并将结果转为大写</p>
                </div>
              </div>
            </div>

            <h4 style="margin-top: 24px;">代码示例</h4>
            <el-tabs v-model="codeTab" type="border-card">
              <el-tab-pane label="PHP" name="php">
                <div class="code-block">
                  <pre>{{ phpCode }}</pre>
                </div>
              </el-tab-pane>
              <el-tab-pane label="Java" name="java">
                <div class="code-block">
                  <pre>{{ javaCode }}</pre>
                </div>
              </el-tab-pane>
              <el-tab-pane label="Python" name="python">
                <div class="code-block">
                  <pre>{{ pythonCode }}</pre>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>

        <!-- 错误码 -->
        <div id="error-code" class="dev-card doc-section">
          <div class="card-header">
            <span class="card-title">错误码</span>
          </div>
          <div class="card-body">
            <el-table :data="errorCodes" border size="small">
              <el-table-column prop="code" label="错误码" width="120" />
              <el-table-column prop="message" label="错误信息" />
              <el-table-column prop="solution" label="解决方案" />
            </el-table>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
/**
 * Fast 易支付 - 开发文档
 */
import { ref } from 'vue'

const activeSection = ref('quick-start')
const codeTab = ref('php')

const scrollToSection = (index) => {
  activeSection.value = index
  const el = document.getElementById(index)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

// 页面跳转支付参数
const pagePayParams = [
  { name: 'merchantNo', required: true, type: 'String', desc: '商户编号' },
  { name: 'outTradeNo', required: true, type: 'String', desc: '商户订单号（唯一）' },
  { name: 'shopNo', required: true, type: 'String', desc: '店铺编号' },
  { name: 'amount', required: true, type: 'BigDecimal', desc: '订单金额（元），最小0.01' },
  { name: 'subject', required: true, type: 'String', desc: '商品名称/订单描述' },
  { name: 'payType', required: true, type: 'String', desc: '支付类型：wxpay-微信，alipay-支付宝' },
  { name: 'extParam', required: false, type: 'String', desc: '扩展参数（JSON格式，回调时原样返回）' },
  { name: 'returnUrl', required: false, type: 'String', desc: '支付成功跳转地址' },
  { name: 'timestamp', required: true, type: 'Long', desc: '时间戳（秒）' },
  { name: 'sign', required: true, type: 'String', desc: '签名（MD5）' }
]

// API接口支付参数
const apiPayParams = [
  { name: 'merchantNo', required: true, type: 'String', desc: '商户编号' },
  { name: 'outTradeNo', required: true, type: 'String', desc: '商户订单号（唯一）' },
  { name: 'shopNo', required: true, type: 'String', desc: '店铺编号' },
  { name: 'amount', required: true, type: 'BigDecimal', desc: '订单金额（元），最小0.01' },
  { name: 'subject', required: true, type: 'String', desc: '商品名称/订单描述' },
  { name: 'payType', required: true, type: 'String', desc: '支付类型：wxpay-微信，alipay-支付宝' },
  { name: 'extParam', required: false, type: 'String', desc: '扩展参数（JSON格式，回调时原样返回）' },
  { name: 'timestamp', required: true, type: 'Long', desc: '时间戳（秒）' },
  { name: 'sign', required: true, type: 'String', desc: '签名（MD5）' }
]

// API接口支付响应
const apiPayResponse = [
  { name: 'code', type: 'Integer', desc: '状态码，200 表示成功' },
  { name: 'message', type: 'String', desc: '提示信息' },
  { name: 'data.orderNo', type: 'String', desc: '平台订单号' },
  { name: 'data.outTradeNo', type: 'String', desc: '商户订单号' },
  { name: 'data.amount', type: 'BigDecimal', desc: '订单金额' },
  { name: 'data.payType', type: 'String', desc: '支付类型' },
  { name: 'data.qrcodeUrl', type: 'String', desc: '收款二维码内容（用于生成二维码图片）' },
  { name: 'data.expireTime', type: 'Long', desc: '订单过期时间（时间戳，秒）' }
]

// 查询订单参数
const queryOrderParams = [
  { name: 'merchantNo', required: true, type: 'String', desc: '商户编号' },
  { name: 'outTradeNo', required: true, type: 'String', desc: '商户订单号' }
]

// WebSocket 连接参数
const wsConnectParams = [
  { name: 'outTradeNo', required: true, type: 'String', desc: '商户订单号（URL 路径参数）' }
]

// WebSocket 消息类型
const wsMessageTypes = [
  { type: 'PAY_SUCCESS', desc: '支付成功', action: '关闭连接，跳转到成功页面' },
  { type: 'PAY_TIMEOUT', desc: '订单超时', action: '关闭连接，提示用户订单已过期' },
  { type: 'PAY_CLOSED', desc: '订单关闭', action: '关闭连接，提示用户订单已关闭' },
  { type: 'HEARTBEAT', desc: '心跳响应', action: '无需处理' }
]

// WebSocket 消息格式
const wsMessageFormat = `// 支付成功消息
{
  "type": "PAY_SUCCESS",
  "data": {
    "orderNo": "FP202512060001",      // 平台订单号
    "outTradeNo": "ORDER123456",       // 商户订单号
    "amount": "10.00",                 // 订单金额
    "payAmount": "10.00",              // 实付金额
    "payTime": "2025-12-06 10:30:00"   // 支付时间
  }
}`

// WebSocket JavaScript 代码示例
const wsJsCode = `// JavaScript WebSocket 示例
const outTradeNo = 'ORDER123456';  // 商户订单号
const wsUrl = \`ws://your-domain/fastpay-server/ws/pay/\${merchantNo}\/\${outTradeNo}\`;

let ws = null;
let heartbeatTimer = null;
let reconnectCount = 0;
const MAX_RECONNECT = 3;

function connectWebSocket() {
  ws = new WebSocket(wsUrl);
  
  ws.onopen = () => {
    console.log('WebSocket 连接成功');
    reconnectCount = 0;
    // 启动心跳
    heartbeatTimer = setInterval(() => {
      if (ws.readyState === WebSocket.OPEN) {
        ws.send('ping');
      }
    }, 30000);
  };
  
  ws.onmessage = (event) => {
    const data = event.data;
    
    // 心跳响应
    if (data === 'pong') {
      console.log('收到心跳响应');
      return;
    }
    
    // 解析 JSON 消息
    try {
      const message = JSON.parse(data);
      
      if (message.type === 'PAY_SUCCESS') {
        console.log('支付成功:', message.data);
        // 关闭连接
        ws.close();
        // 跳转到成功页面
        window.location.href = '/pay/success?orderNo=' + message.data.orderNo;
      } else if (message.type === 'PAY_TIMEOUT') {
        console.log('订单超时');
        ws.close();
        alert('订单已超时，请重新下单');
      }
    } catch (e) {
      console.error('消息解析失败:', e);
    }
  };
  
  ws.onclose = (event) => {
    console.log('WebSocket 连接关闭:', event.code, event.reason);
    clearInterval(heartbeatTimer);
    
    // 自动重连（非正常关闭时）
    if (event.code !== 1000 && reconnectCount < MAX_RECONNECT) {
      reconnectCount++;
      console.log(\`尝试重连 (\${reconnectCount}/\${MAX_RECONNECT})...\`);
      setTimeout(connectWebSocket, 3000);
    }
  };
  
  ws.onerror = (error) => {
    console.error('WebSocket 错误:', error);
  };
}

// 开始连接
connectWebSocket();

// 页面关闭时断开连接
window.addEventListener('beforeunload', () => {
  if (ws) {
    ws.close();
  }
});`

// WebSocket Java 代码示例
const wsJavaCode = `// Java WebSocket 客户端示例（使用 Java-WebSocket 库）
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

public class PayWebSocketClient extends WebSocketClient {
    private Timer heartbeatTimer;
    private String merchantNo;
    private String outTradeNo;
    
    public PayWebSocketClient(String merchantNo, String outTradeNo) throws Exception {
        super(new URI("ws://your-domain/fastpay-server/ws/pay/" + merchantNo + outTradeNo));
        this.merchantNo = merchantNo;
        this.outTradeNo = outTradeNo;
    }
    
    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("WebSocket 连接成功");
        // 启动心跳
        heartbeatTimer = new Timer();
        heartbeatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isOpen()) {
                    send("ping");
                }
            }
        }, 30000, 30000);
    }
    
    @Override
    public void onMessage(String message) {
        if ("pong".equals(message)) {
            System.out.println("收到心跳响应");
            return;
        }
        
        try {
            JsonObject json = JsonParser.parseString(message).getAsJsonObject();
            String type = json.get("type").getAsString();
            
            if ("PAY_SUCCESS".equals(type)) {
                JsonObject data = json.getAsJsonObject("data");
                System.out.println("支付成功: " + data);
                // 处理支付成功逻辑
                close();
            } else if ("PAY_TIMEOUT".equals(type)) {
                System.out.println("订单超时");
                close();
            }
        } catch (Exception e) {
            System.err.println("消息解析失败: " + e.getMessage());
        }
    }
    
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("连接关闭: " + code + " - " + reason);
        if (heartbeatTimer != null) {
            heartbeatTimer.cancel();
        }
    }
    
    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket 错误: " + ex.getMessage());
    }
    
    public static void main(String[] args) throws Exception {
        PayWebSocketClient client = new PayWebSocketClient("ORDER123456");
        client.connect();
    }
}`

// WebSocket Python 代码示例
const wsPythonCode = `# Python WebSocket 客户端示例（使用 websocket-client 库）
# pip install websocket-client

import websocket
import json
import threading
import time

class PayWebSocketClient:
    def __init__(self, out_trade_no):
        self.out_trade_no = out_trade_no
        self.ws_url = f"ws://your-domain/fastpay-server/ws/pay/{merchant_no}{out_trade_no}"
        self.ws = None
        self.heartbeat_thread = None
        self.running = False
    
    def on_message(self, ws, message):
        if message == "pong":
            print("收到心跳响应")
            return
        
        try:
            data = json.loads(message)
            msg_type = data.get("type")
            
            if msg_type == "PAY_SUCCESS":
                print(f"支付成功: {data.get('data')}")
                # 处理支付成功逻辑
                self.close()
            elif msg_type == "PAY_TIMEOUT":
                print("订单超时")
                self.close()
        except json.JSONDecodeError as e:
            print(f"消息解析失败: {e}")
    
    def on_error(self, ws, error):
        print(f"WebSocket 错误: {error}")
    
    def on_close(self, ws, close_status_code, close_msg):
        print(f"连接关闭: {close_status_code} - {close_msg}")
        self.running = False
    
    def on_open(self, ws):
        print("WebSocket 连接成功")
        self.running = True
        # 启动心跳线程
        self.heartbeat_thread = threading.Thread(target=self.heartbeat)
        self.heartbeat_thread.start()
    
    def heartbeat(self):
        while self.running:
            time.sleep(30)
            if self.running and self.ws:
                self.ws.send("ping")
    
    def connect(self):
        self.ws = websocket.WebSocketApp(
            self.ws_url,
            on_open=self.on_open,
            on_message=self.on_message,
            on_error=self.on_error,
            on_close=self.on_close
        )
        self.ws.run_forever()
    
    def close(self):
        self.running = False
        if self.ws:
            self.ws.close()

# 使用示例
if __name__ == "__main__":
    client = PayWebSocketClient("ORDER123456")
    client.connect()`

// WebSocket Node.js 代码示例
const wsNodeCode = `// Node.js WebSocket 客户端示例（使用 ws 库）
// npm install ws

const WebSocket = require('ws');

class PayWebSocketClient {
  constructor(outTradeNo) {
    this.outTradeNo = outTradeNo;
    this.wsUrl = \`ws://your-domain/fastpay-server/ws/pay/\${merchantNo}\/\${outTradeNo}\`;
    this.ws = null;
    this.heartbeatTimer = null;
    this.reconnectCount = 0;
    this.maxReconnect = 3;
  }
  
  connect() {
    this.ws = new WebSocket(this.wsUrl);
    
    this.ws.on('open', () => {
      console.log('WebSocket 连接成功');
      this.reconnectCount = 0;
      // 启动心跳
      this.heartbeatTimer = setInterval(() => {
        if (this.ws.readyState === WebSocket.OPEN) {
          this.ws.send('ping');
        }
      }, 30000);
    });
    
    this.ws.on('message', (data) => {
      const message = data.toString();
      
      if (message === 'pong') {
        console.log('收到心跳响应');
        return;
      }
      
      try {
        const json = JSON.parse(message);
        
        if (json.type === 'PAY_SUCCESS') {
          console.log('支付成功:', json.data);
          // 处理支付成功逻辑
          this.close();
        } else if (json.type === 'PAY_TIMEOUT') {
          console.log('订单超时');
          this.close();
        }
      } catch (e) {
        console.error('消息解析失败:', e);
      }
    });
    
    this.ws.on('close', (code, reason) => {
      console.log(\`连接关闭: \${code} - \${reason}\`);
      clearInterval(this.heartbeatTimer);
      
      // 自动重连
      if (code !== 1000 && this.reconnectCount < this.maxReconnect) {
        this.reconnectCount++;
        console.log(\`尝试重连 (\${this.reconnectCount}/\${this.maxReconnect})...\`);
        setTimeout(() => this.connect(), 3000);
      }
    });
    
    this.ws.on('error', (error) => {
      console.error('WebSocket 错误:', error);
    });
  }
  
  close() {
    if (this.ws) {
      this.ws.close();
    }
  }
}

// 使用示例
const client = new PayWebSocketClient('ORDER123456');
client.connect();`

// 页面跳转支付表单示例
const pagePayFormExample = `<!-- HTML Form 表单提交 -->
<form action="https://your-domain/api/pay/submit" method="POST">
  <input type="hidden" name="merchantNo" value="YOUR_MERCHANT_NO">
  <input type="hidden" name="outTradeNo" value="ORDER202512050001">
  <input type="hidden" name="amount" value="10.00">
  <input type="hidden" name="subject" value="测试商品">
  <input type="hidden" name="payType" value="wxpay">
  <input type="hidden" name="extParam" value='{"userId":"123"}'>
  <input type="hidden" name="returnUrl" value="https://your-site.com/pay/result">
  <input type="hidden" name="timestamp" value="1733400000">
  <input type="hidden" name="sign" value="签名值">
  <button type="submit">立即支付</button>
</form>`

// 页面跳转支付 - PHP 代码示例
const pagePayPhpCode = `// PHP 示例 - 生成签名并自动提交表单
$params = [
    'merchantNo' => 'YOUR_MERCHANT_NO',
    'outTradeNo' => 'ORDER' . time(),
    'amount' => '10.00',
    'subject' => '测试商品',
    'payType' => 'wxpay',
    'extParam' => json_encode(['userId' => '123']),
    'returnUrl' => 'https://your-site.com/pay/result',
    'timestamp' => time()
];

// 生成签名（参数按字母排序后拼接）
ksort($params);
$signStr = '';
foreach ($params as $k => $v) {
    if ($v !== '' && $v !== null) {
        $signStr .= $k . '=' . $v . '&';
    }
}
$signStr .= 'key=' . 'YOUR_API_SECRET';
$params['sign'] = strtoupper(md5($signStr));

// 输出表单并自动提交
echo '<form id="payForm" action="https://your-domain/api/pay/submit" method="POST">';
foreach ($params as $k => $v) {
    echo '<input type="hidden" name="'.$k.'" value="'.htmlspecialchars($v).'">';
}
echo '</form>';
// 自动提交表单
echo '<scr' + 'ipt>document.getElementById("payForm").submit();</scr' + 'ipt>';`

// 页面跳转支付 - Java 代码示例
const pagePayJavaCode = `// Java 示例 - 生成签名并构建表单
import java.util.*;
import java.security.MessageDigest;
import java.net.URLEncoder;

public class PagePayDemo {
    public static String buildPayForm() {
        TreeMap params = new TreeMap();
        params.put("merchantNo", "YOUR_MERCHANT_NO");
        params.put("outTradeNo", "ORDER" + System.currentTimeMillis());
        params.put("amount", "10.00");
        params.put("subject", "测试商品");
        params.put("payType", "wxpay");
        params.put("returnUrl", "https://your-site.com/pay/result");
        params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        
        // 生成签名
        StringBuilder sb = new StringBuilder();
        for (Object key : params.keySet()) {
            sb.append(key).append("=").append(params.get(key)).append("&");
        }
        sb.append("key=").append("YOUR_API_SECRET");
        String sign = md5(sb.toString()).toUpperCase();
        params.put("sign", sign);
        
        // 构建自动提交表单
        StringBuilder form = new StringBuilder();
        form.append("<form id='payForm' action='https://your-domain/api/pay/submit' method='POST'>");
        for (Object key : params.keySet()) {
            form.append("<input type='hidden' name='").append(key)
                .append("' value='").append(params.get(key)).append("'>");
        }
        form.append("</form>");
        // 添加自动提交脚本
        form.append("<scr" + "ipt>document.getElementById('payForm').submit();</scr" + "ipt>");
        return form.toString();
    }
    
    private static String md5(String str) {
        // MD5 签名实现...
    }
}`

// 页面跳转支付 - Python 代码示例
const pagePayPythonCode = `# Python 示例 - 生成签名并构建表单
import hashlib
import time
import html

params = {
    'merchantNo': 'YOUR_MERCHANT_NO',
    'outTradeNo': f'ORDER{int(time.time())}',
    'amount': '10.00',
    'subject': '测试商品',
    'payType': 'wxpay',
    'returnUrl': 'https://your-site.com/pay/result',
    'timestamp': str(int(time.time()))
}

# 生成签名（参数按字母排序后拼接）
sorted_params = sorted(params.items())
sign_str = '&'.join([f'{k}={v}' for k, v in sorted_params])
sign_str += '&key=YOUR_API_SECRET'
sign = hashlib.md5(sign_str.encode()).hexdigest().upper()
params['sign'] = sign

# 构建自动提交表单
form_html = '<form id="payForm" action="https://your-domain/api/pay/submit" method="POST">'
for k, v in params.items():
    form_html += f'<input type="hidden" name="{k}" value="{html.escape(str(v))}">'
form_html += '</form>'
# 添加自动提交脚本
form_html += '<scr' + 'ipt>document.getElementById("payForm").submit();</scr' + 'ipt>'

# 在 Web 框架中返回此 HTML (如 Flask: return form_html)
print(form_html)`

// 回调参数
const callbackParams = [
  { name: 'merchant_no', type: 'String', desc: '商户编号' },
  { name: 'order_no', type: 'String', desc: '平台订单号' },
  { name: 'out_trade_no', type: 'String', desc: '商户订单号' },
  { name: 'amount', type: 'String', desc: '订单金额' },
  { name: 'pay_amount', type: 'String', desc: '实付金额' },
  { name: 'pay_type', type: 'String', desc: '支付类型' },
  { name: 'status', type: 'Integer', desc: '订单状态：1-已支付' },
  { name: 'pay_time', type: 'String', desc: '支付时间' },
  { name: 'timestamp', type: 'Long', desc: '时间戳' },
  { name: 'sign', type: 'String', desc: '签名' }
]

// 错误码
const errorCodes = [
  { code: '400', message: '参数错误', solution: '检查请求参数是否完整、格式是否正确' },
  { code: '401', message: '签名验证失败', solution: '检查签名算法是否正确、API Secret 是否正确' },
  { code: '402', message: '商户不存在', solution: '检查商户编号是否正确' },
  { code: '403', message: '商户已禁用', solution: '联系管理员启用商户' },
  { code: '404', message: '订单不存在', solution: '检查订单号是否正确' },
  { code: '500', message: '系统错误', solution: '请稍后重试或联系技术支持' }
]

// 代码示例
const phpCode = `// PHP 示例
$params = [
    'merchantNo' => 'YOUR_MERCHANT_NO',
    'outTradeNo' => 'ORDER' . time(),
    'amount' => '10.00',
    'subject' => '测试商品',
    'payType' => 'wxpay',
    'timestamp' => time()
];

// 生成签名
ksort($params);
$str = '';
foreach ($params as $k => $v) {
    $str .= $k . '=' . $v . '&';
}
$str .= 'key=' . 'YOUR_API_SECRET';
$params['sign'] = strtoupper(md5($str));

// 发起请求
$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, 'https://your-domain/api/pay/create');
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($params));
curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
$result = curl_exec($ch);
curl_close($ch);

print_r(json_decode($result, true));`

const javaCode = `// Java 示例
import java.util.*;
import java.security.MessageDigest;

public class PayDemo {
    public static void main(String[] args) {
        // 使用 TreeMap 自动按 key 排序
        TreeMap params = new TreeMap();
        params.put("merchantNo", "YOUR_MERCHANT_NO");
        params.put("outTradeNo", "ORDER" + System.currentTimeMillis());
        params.put("amount", "10.00");
        params.put("subject", "测试商品");
        params.put("payType", "wxpay");
        params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        
        // 生成签名
        StringBuilder sb = new StringBuilder();
        for (Object key : params.keySet()) {
            sb.append(key).append("=").append(params.get(key)).append("&");
        }
        sb.append("key=").append("YOUR_API_SECRET");
        String sign = md5(sb.toString()).toUpperCase();
        params.put("sign", sign);
        
        // 发起 HTTP POST 请求到 /api/pay/create
    }
    
    private static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(str.getBytes("UTF-8"));
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (Exception e) {
            return "";
        }
    }
}`

const pythonCode = `import hashlib
import time
import requests
import json

params = {
    'merchantNo': 'YOUR_MERCHANT_NO',
    'outTradeNo': f'ORDER{int(time.time())}',
    'amount': '10.00',
    'subject': '测试商品',
    'payType': 'wxpay',
    'timestamp': str(int(time.time()))
}

# 生成签名
sorted_params = sorted(params.items())
sign_str = '&'.join([f'{k}={v}' for k, v in sorted_params])
sign_str += '&key=YOUR_API_SECRET'
sign = hashlib.md5(sign_str.encode()).hexdigest().upper()
params['sign'] = sign

# 发起请求
response = requests.post(
    'https://your-domain/api/pay/create',
    json=params,
    headers={'Content-Type': 'application/json'}
)
print(response.json())`
</script>

<style scoped>
.docs-page {
  padding: 0;
}

.doc-nav {
  position: sticky;
  top: 88px;

  :deep(.el-menu) {
    border: none;
  }

  :deep(.el-menu-item) {
    height: 44px;
    line-height: 44px;
  }
}

.doc-section {
  margin-bottom: 16px;

  h4 {
    font-size: 14px;
    font-weight: 600;
    color: #333;
    margin: 16px 0 12px;
  }

  p {
    color: #666;
    line-height: 1.8;
    margin-bottom: 8px;
  }

  code {
    background: #f5f7fa;
    padding: 2px 6px;
    border-radius: 4px;
    font-family: 'Monaco', 'Menlo', monospace;
    font-size: 13px;
    color: #e6a23c;
  }
}

.method-card {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  text-align: center;

  .method-icon {
    width: 48px;
    height: 48px;
    background: #e6f4ff;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 12px;
    color: #1677ff;
  }

  h4 {
    font-size: 15px;
    margin-bottom: 8px;
  }

  p {
    font-size: 13px;
    color: #666;
    margin: 0;
  }
}

.api-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 6px;

  code {
    background: transparent;
    color: #333;
    font-size: 14px;
  }

  .api-desc {
    font-size: 12px;
    color: #909399;
  }
}

.sign-steps {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.sign-step {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;

  .step-num {
    width: 28px;
    height: 28px;
    background: #1677ff;
    color: #fff;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    font-weight: 600;
    flex-shrink: 0;
  }

  .step-content {
    h4 {
      font-size: 14px;
      font-weight: 600;
      color: #333;
      margin: 0 0 4px;
    }

    p {
      font-size: 12px;
      color: #666;
      margin: 0;
    }
  }
}

.code-block {
  background: #1e1e1e;
  border-radius: 8px;
  padding: 16px;
  overflow-x: auto;

  pre {
    margin: 0;
    color: #d4d4d4;
    font-family: 'Monaco', 'Menlo', 'Consolas', monospace;
    font-size: 13px;
    line-height: 1.6;
  }
}
</style>
