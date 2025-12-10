<template>
  <div class="public-docs-page">
    <!-- 顶部导航 -->
    <header class="docs-header">
      <div class="header-container">
        <div class="logo" @click="$router.push('/')">
          <div class="logo-icon">
            <svg viewBox="0 0 40 40" width="36" height="36" xmlns="http://www.w3.org/2000/svg">
              <defs>
                <linearGradient id="logo-grad-docs" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" style="stop-color:#93c5fd"/>
                  <stop offset="50%" style="stop-color:#60a5fa"/>
                  <stop offset="100%" style="stop-color:#3b82f6"/>
                </linearGradient>
              </defs>
              <rect x="2" y="2" width="36" height="36" rx="10" ry="10" fill="url(#logo-grad-docs)"/>
              <circle cx="20" cy="20" r="14" fill="none" stroke="rgba(255,255,255,0.2)" stroke-width="1"/>
              <circle cx="20" cy="20" r="11" fill="none" stroke="rgba(255,255,255,0.15)" stroke-width="0.5"/>
              <circle cx="8" cy="8" r="1.5" fill="rgba(255,255,255,0.4)"/>
              <circle cx="32" cy="8" r="1.5" fill="rgba(255,255,255,0.4)"/>
              <circle cx="8" cy="32" r="1.5" fill="rgba(255,255,255,0.4)"/>
              <circle cx="32" cy="32" r="1.5" fill="rgba(255,255,255,0.4)"/>
              <text x="20" y="26" text-anchor="middle" font-family="STKaiti, KaiTi, SimSun, serif" font-size="20" font-weight="bold" fill="white">易</text>
            </svg>
          </div>
          <span class="logo-text">FAST 易支付</span>
          <span class="logo-badge">开发文档</span>
        </div>
        <div class="header-actions">
          <el-button @click="$router.push('/login')">登录</el-button>
          <el-button type="primary" @click="$router.push('/login')">开始接入</el-button>
        </div>
      </div>
    </header>

    <!-- 文档内容 -->
    <div class="docs-container">
      <el-row :gutter="24">
        <!-- 左侧导航 -->
        <el-col :span="5">
          <div class="docs-nav">
            <el-menu :default-active="activeSection" @select="scrollToSection">
              <el-menu-item index="intro">产品介绍</el-menu-item>
              <el-menu-item index="quick-start">快速开始</el-menu-item>
              <el-menu-item index="page-pay">页面跳转支付</el-menu-item>
              <el-menu-item index="api-pay">API接口支付</el-menu-item>
              <el-menu-item index="query-order">查询订单</el-menu-item>
              <el-menu-item index="callback">回调通知</el-menu-item>
              <el-menu-item index="websocket">WebSocket监听</el-menu-item>
              <el-menu-item index="sign">签名算法</el-menu-item>
              <el-menu-item index="error-code">错误码</el-menu-item>
            </el-menu>
          </div>
        </el-col>

        <!-- 右侧内容 -->
        <el-col :span="19">
          <!-- 产品介绍 -->
          <section id="intro" class="doc-section">
            <h2>产品介绍</h2>
            <p>FAST 易支付是一款<strong>个人免签支付</strong>解决方案，通过普通收款码即可实现收款通知自动回调，支持绝大多数商城系统。</p>
            <div class="feature-list">
              <div class="feature-item">
                <el-icon><Check /></el-icon>
                <span>免签约，无需企业资质</span>
              </div>
              <div class="feature-item">
                <el-icon><Check /></el-icon>
                <span>资金直达个人账户，无需提现</span>
              </div>
              <div class="feature-item">
                <el-icon><Check /></el-icon>
                <span>支持微信、支付宝多种支付方式</span>
              </div>
              <div class="feature-item">
                <el-icon><Check /></el-icon>
                <span>标准接口，兼容性强</span>
              </div>
            </div>
          </section>

          <!-- 快速开始 -->
          <section id="quick-start" class="doc-section">
            <h2>快速开始</h2>
            <div class="steps">
              <div class="step">
                <div class="step-num">1</div>
                <div class="step-content">
                  <h4>注册商户</h4>
                  <p>联系管理员开通商户账号，获取商户编号和 API Secret</p>
                </div>
              </div>
              <div class="step">
                <div class="step-num">2</div>
                <div class="step-content">
                  <h4>配置收款码</h4>
                  <p>登录商户平台，上传您的微信/支付宝收款二维码</p>
                </div>
              </div>
              <div class="step">
                <div class="step-num">3</div>
                <div class="step-content">
                  <h4>配置回调</h4>
                  <p>在开发配置中设置回调通知地址和支付成功跳转地址</p>
                </div>
              </div>
              <div class="step">
                <div class="step-num">4</div>
                <div class="step-content">
                  <h4>对接API</h4>
                  <p>参考本文档完成系统对接，开始收款</p>
                </div>
              </div>
            </div>
            
            <div class="method-intro">
              <h3>两种接入方式</h3>
              <div class="method-cards">
                <div class="method-card">
                  <div class="method-icon"><el-icon :size="24"><Link /></el-icon></div>
                  <h4>页面跳转支付</h4>
                  <p>使用 form 表单提交，跳转到平台支付页面，适合 Web 网站</p>
                  <el-tag type="success" size="small">推荐</el-tag>
                </div>
                <div class="method-card">
                  <div class="method-icon"><el-icon :size="24"><Connection /></el-icon></div>
                  <h4>API接口支付</h4>
                  <p>后端调用接口获取收款二维码，前端展示供用户扫码</p>
                  <el-tag size="small">适合APP</el-tag>
                </div>
              </div>
            </div>
          </section>

          <!-- 页面跳转支付 -->
          <section id="page-pay" class="doc-section">
            <h2>页面跳转支付</h2>
            <p>适用于 Web 网站接入，使用 form 表单提交跳转到支付页面，用户扫码支付后自动跳转回商户页面。</p>
            
            <div class="api-endpoint">
              <el-tag type="success" size="large">POST</el-tag>
              <code>/api/pay/submit</code>
            </div>
            
            <h3>请求参数</h3>
            <el-table :data="pagePayParams" border>
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

            <h3>调用示例（HTML Form 表单提交）</h3>
            <div class="code-block">
              <pre>&lt;!-- HTML Form 表单提交 --&gt;
&lt;form action="https://your-domain/api/pay/submit" method="POST"&gt;
  &lt;input type="hidden" name="merchantNo" value="YOUR_MERCHANT_NO"&gt;
  &lt;input type="hidden" name="outTradeNo" value="ORDER202512050001"&gt;
  &lt;input type="hidden" name="amount" value="10.00"&gt;
  &lt;input type="hidden" name="subject" value="测试商品"&gt;
  &lt;input type="hidden" name="payType" value="wxpay"&gt;
  &lt;input type="hidden" name="extParam" value="{&quot;userId&quot;:&quot;123&quot;}"&gt;
  &lt;input type="hidden" name="returnUrl" value="https://your-site.com/pay/result"&gt;
  &lt;input type="hidden" name="timestamp" value="1733400000"&gt;
  &lt;input type="hidden" name="sign" value="签名值"&gt;
  &lt;button type="submit"&gt;立即支付&lt;/button&gt;
&lt;/form&gt;</pre>
            </div>

            <h3>代码示例</h3>
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
                提交后会自动跳转到支付页面，用户支付成功后会跳转到 returnUrl（携带 order_no、out_trade_no、status 参数）
              </template>
            </el-alert>
          </section>

          <!-- API接口支付 -->
          <section id="api-pay" class="doc-section">
            <h2>API接口支付</h2>
            <p>适用于 APP 或自定义支付页面接入，后端调用接口获取收款二维码，前端展示供用户扫码。</p>
            
            <div class="api-endpoint">
              <el-tag type="success" size="large">POST</el-tag>
              <code>/api/pay/create</code>
              <span class="endpoint-desc">Content-Type: application/json</span>
            </div>
            
            <h3>请求参数</h3>
            <el-table :data="apiPayParams" border>
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

            <h3>响应参数</h3>
            <el-table :data="apiPayResponse" border>
              <el-table-column prop="name" label="参数名" width="160" />
              <el-table-column prop="type" label="类型" width="100" />
              <el-table-column prop="desc" label="说明" />
            </el-table>

            <h3>响应示例</h3>
            <div class="code-block">
              <pre>{
  "code": 200,
  "message": "操作成功",
  "data": {
    "orderNo": "FP20241205123456789",
    "outTradeNo": "ORDER202512050001",
    "amount": 10.00,
    "payType": "wxpay",
    "qrcodeUrl": "wxp://f2f0xxxxx",
    "expireTime": 1733403600
  }
}</pre>
            </div>
          </section>

          <!-- 查询订单 -->
          <section id="query-order" class="doc-section">
            <h2>查询订单接口</h2>
            <div class="api-endpoint">
              <el-tag size="large">GET</el-tag>
              <code>/api/pay/query</code>
            </div>
            
            <h3>请求参数</h3>
            <el-table :data="queryOrderParams" border>
              <el-table-column prop="name" label="参数名" width="140" />
              <el-table-column prop="required" label="必填" width="80">
                <template #default="{ row }">
                  <el-tag type="danger" size="small">是</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="type" label="类型" width="100" />
              <el-table-column prop="desc" label="说明" />
            </el-table>

            <h3>响应示例</h3>
            <div class="code-block">
              <pre>{
  "code": 200,
  "message": "操作成功",
  "data": {
    "orderNo": "FP20241205123456789",
    "outTradeNo": "ORDER202512050001",
    "amount": 10.00,
    "payAmount": 10.00,
    "status": 1,
    "payTime": "2024-12-05 18:30:00"
  }
}</pre>
            </div>
          </section>

          <!-- 回调通知 -->
          <section id="callback" class="doc-section">
            <h2>回调通知</h2>
            <p>支付成功后，系统会向商户配置的 <code>notifyUrl</code> 发送 POST 请求（Content-Type: application/json）：</p>
            
            <h3>通知参数</h3>
            <el-table :data="callbackParams" border>
              <el-table-column prop="name" label="参数名" width="140" />
              <el-table-column prop="type" label="类型" width="100" />
              <el-table-column prop="desc" label="说明" />
            </el-table>

            <h3>通知示例</h3>
            <div class="code-block">
              <pre>{
  "merchantNo": "M12345678",
  "orderNo": "FP20241205123456789",
  "outTradeNo": "ORDER202512050001",
  "amount": "10.00",
  "payAmount": "10.00",
  "payType": "wxpay",
  "status": 1,
  "payTime": "2024-12-05 18:30:00",
  "timestamp": 1733400000,
  "sign": "签名值"
}</pre>
            </div>

            <el-alert type="warning" :closable="false" style="margin-top: 16px;">
              <template #title>
                <strong>重要：</strong>收到通知后请验证签名，验证通过后返回字符串 <code>success</code>，否则系统会重复通知（最多 8 次）
              </template>
            </el-alert>
          </section>

          <!-- WebSocket 监听 -->
          <section id="websocket" class="doc-section">
            <h2>WebSocket 实时监听</h2>
            <p>除了轮询查询和异步回调，您还可以通过 WebSocket 实时监听订单支付状态，获得更好的用户体验。</p>
            
            <el-alert type="success" :closable="false" style="margin: 16px 0;">
              <template #title>
                <strong>推荐场景：</strong>前端展示二维码后，通过 WebSocket 实时监听支付结果，用户支付成功后立即跳转，无需轮询
              </template>
            </el-alert>

            <h3>连接地址</h3>
            <div class="api-endpoint">
              <span class="method ws">WebSocket</span>
              <code>ws://your-domain/fastpay-server/ws/pay/{merchantNo}/{outTradeNo}</code>
            </div>
            <p style="margin-top: 8px; color: #666;">其中 <code>{merchantNo}</code> 为商户编号、 <code>{outTradeNo}</code> 为商户订单号</p>

            <h3>消息格式</h3>
            <p>服务端推送的消息为 JSON 格式：</p>
            <div class="code-block">
              <pre>// 支付成功消息
{
  "type": "PAY_SUCCESS",
  "data": {
    "orderNo": "FP202512060001",      // 平台订单号
    "outTradeNo": "ORDER123456",       // 商户订单号
    "amount": "10.00",                 // 订单金额
    "payAmount": "10.00",              // 实付金额
    "payTime": "2025-12-06 10:30:00"   // 支付时间
  }
}</pre>
            </div>

            <h3>消息类型</h3>
            <el-table :data="wsMessageTypes" border>
              <el-table-column prop="type" label="类型" width="140" />
              <el-table-column prop="desc" label="说明" />
              <el-table-column prop="action" label="建议操作" />
            </el-table>

            <h3>心跳保活</h3>
            <p>为保持连接稳定，建议客户端每 <strong>30 秒</strong> 发送一次心跳消息 <code>ping</code>，服务端会回复 <code>pong</code>。</p>

            <h3>JavaScript 示例</h3>
            <div class="code-block">
              <pre>{{ wsJsExample }}</pre>
            </div>

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
          </section>

          <!-- 签名算法 -->
          <section id="sign" class="doc-section">
            <h2>签名算法</h2>
            <div class="sign-flow">
              <div class="sign-step">
                <div class="step-badge">1</div>
                <div class="step-info">
                  <h4>参数排序</h4>
                  <p>将所有非空参数（不含 sign）按参数名 ASCII 码从小到大排序</p>
                </div>
              </div>
              <div class="sign-step">
                <div class="step-badge">2</div>
                <div class="step-info">
                  <h4>拼接字符串</h4>
                  <p>使用 URL 键值对格式拼接：<code>key1=value1&key2=value2...</code></p>
                </div>
              </div>
              <div class="sign-step">
                <div class="step-badge">3</div>
                <div class="step-info">
                  <h4>追加密钥</h4>
                  <p>在字符串末尾追加 <code>&key=API_SECRET</code></p>
                </div>
              </div>
              <div class="sign-step">
                <div class="step-badge">4</div>
                <div class="step-info">
                  <h4>MD5 加密</h4>
                  <p>对最终字符串进行 MD5 加密，并将结果转为<strong>大写</strong></p>
                </div>
              </div>
            </div>

            <h3>签名示例</h3>
            <div class="code-block">
              <pre>// 原始参数（参数名使用驼峰命名）
{
  "merchantNo": "M12345678",
  "outTradeNo": "ORDER202512050001",
  "amount": "10.00",
  "subject": "测试商品",
  "payType": "wxpay",
  "timestamp": "1733400000"
}

// 步骤1：按参数名ASCII码排序（注意：大写字母排在小写字母前面）
amount, merchantNo, outTradeNo, payType, subject, timestamp

// 步骤2：拼接字符串
amount=10.00&merchantNo=M12345678&outTradeNo=ORDER202512050001&payType=wxpay&subject=测试商品&timestamp=1733400000

// 步骤3：追加密钥
amount=10.00&merchantNo=M12345678&outTradeNo=ORDER202512050001&payType=wxpay&subject=测试商品&timestamp=1733400000&key=YOUR_API_SECRET

// 步骤4：MD5加密并转大写
sign = MD5(str).toUpperCase()</pre>
            </div>
          </section>

          <!-- 错误码 -->
          <section id="error-code" class="doc-section">
            <h2>错误码</h2>
            <el-table :data="errorCodes" border>
              <el-table-column prop="code" label="状态码" width="120" />
              <el-table-column prop="message" label="说明" />
              <el-table-column prop="solution" label="解决方案" />
            </el-table>
          </section>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
/**
 * Fast 易支付 - 公开开发文档
 */
import { ref } from 'vue'
import { Link, Connection, Check } from '@element-plus/icons-vue'

const activeSection = ref('intro')

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
  { name: 'amount', required: true, type: 'String', desc: '订单金额（元），保留两位小数，如 10.00' },
  { name: 'subject', required: true, type: 'String', desc: '商品名称' },
  { name: 'payType', required: true, type: 'String', desc: '支付类型：wxpay（微信）/ alipay（支付宝）' },
  { name: 'returnUrl', required: false, type: 'String', desc: '支付成功后跳转地址' },
  { name: 'extParam', required: false, type: 'String', desc: '扩展参数，回调时原样返回' },
  { name: 'timestamp', required: true, type: 'Long', desc: '时间戳（秒）' },
  { name: 'sign', required: true, type: 'String', desc: '签名' }
]

// 页面跳转支付 - PHP 代码示例
const pagePayPhpCode = `// PHP 示例 - 生成签名并自动提交表单
$params = [
    'merchantNo' => 'YOUR_MERCHANT_NO',
    'outTradeNo' => 'ORDER' . time(),
    'shopNo' => 'YOUR_SHOP_NO',
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

public class PagePayDemo {
    public static String buildPayForm() {
        TreeMap params = new TreeMap();
        params.put("merchantNo", "YOUR_MERCHANT_NO");
        params.put("outTradeNo", "ORDER" + System.currentTimeMillis());
        params.put("shopNo", "YOUR_SHOP_NO");
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
    'shopNo': 'YOUR_SHOP_NO',
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

// API接口支付参数
const apiPayParams = [
  { name: 'merchantNo', required: true, type: 'String', desc: '商户编号' },
  { name: 'outTradeNo', required: true, type: 'String', desc: '商户订单号（唯一）' },
  { name: 'shopNo', required: true, type: 'String', desc: '店铺编号' },
  { name: 'amount', required: true, type: 'String', desc: '订单金额（元），保留两位小数' },
  { name: 'subject', required: true, type: 'String', desc: '商品名称' },
  { name: 'payType', required: true, type: 'String', desc: '支付类型：wxpay / alipay' },
  { name: 'extParam', required: false, type: 'String', desc: '扩展参数，回调时原样返回' },
  { name: 'timestamp', required: true, type: 'Long', desc: '时间戳（秒）' },
  { name: 'sign', required: true, type: 'String', desc: '签名' }
]

// API响应参数
const apiPayResponse = [
  { name: 'orderNo', type: 'String', desc: '平台订单号' },
  { name: 'outTradeNo', type: 'String', desc: '商户订单号' },
  { name: 'amount', type: 'Number', desc: '订单金额' },
  { name: 'payType', type: 'String', desc: '支付类型' },
  { name: 'qrcodeUrl', type: 'String', desc: '收款码内容（用于生成二维码）' },
  { name: 'expireTime', type: 'Long', desc: '订单过期时间戳（秒）' }
]

// 查询订单参数
const queryOrderParams = [
  { name: 'merchantNo', required: true, type: 'String', desc: '商户编号' },
  { name: 'outTradeNo', required: true, type: 'String', desc: '商户订单号' },
  { name: 'timestamp', required: true, type: 'Long', desc: '时间戳（秒）' },
  { name: 'sign', required: true, type: 'String', desc: '签名' }
]

// 回调通知参数
const callbackParams = [
  { name: 'merchantNo', type: 'String', desc: '商户编号' },
  { name: 'orderNo', type: 'String', desc: '平台订单号' },
  { name: 'outTradeNo', type: 'String', desc: '商户订单号' },
  { name: 'amount', type: 'String', desc: '订单金额' },
  { name: 'payAmount', type: 'String', desc: '实付金额' },
  { name: 'payType', type: 'String', desc: '支付类型' },
  { name: 'status', type: 'Integer', desc: '订单状态：1-已支付' },
  { name: 'payTime', type: 'String', desc: '支付时间' },
  { name: 'extParam', type: 'String', desc: '扩展参数（原样返回）' },
  { name: 'timestamp', type: 'Long', desc: '时间戳' },
  { name: 'sign', type: 'String', desc: '签名' }
]

// WebSocket 消息类型
const wsMessageTypes = [
  { type: 'PAY_SUCCESS', desc: '支付成功', action: '关闭连接，跳转到成功页面' },
  { type: 'PAY_TIMEOUT', desc: '订单超时', action: '关闭连接，提示用户订单已过期' },
  { type: 'PAY_CLOSED', desc: '订单关闭', action: '关闭连接，提示用户订单已关闭' },
  { type: 'HEARTBEAT', desc: '心跳响应', action: '无需处理' }
]

// WebSocket JavaScript 示例
const wsJsExample = `const outTradeNo = 'ORDER123456';
const merchantNo = 'M230117997677';

const wsUrl = 'ws://your-domain/fastpay-server/ws/pay/' + merchantNo + outTradeNo;

let ws = new WebSocket(wsUrl);
let heartbeatTimer = null;

ws.onopen = () => {
  console.log('WebSocket 连接成功');
  // 启动心跳（每30秒）
  heartbeatTimer = setInterval(() => {
    if (ws.readyState === WebSocket.OPEN) {
      ws.send('ping');
    }
  }, 30000);
};

ws.onmessage = (event) => {
  if (event.data === 'pong') return;  // 心跳响应
  
  try {
    const message = JSON.parse(event.data);
    if (message.type === 'PAY_SUCCESS') {
      console.log('支付成功:', message.data);
      ws.close();
      // 跳转到成功页面
      window.location.href = '/pay/success?orderNo=' + message.data.orderNo;
    }
  } catch (e) {
    console.error('消息解析失败:', e);
  }
};

ws.onclose = () => {
  clearInterval(heartbeatTimer);
  console.log('连接关闭');
};`

// 错误码
const errorCodes = [
  { code: '200', message: '成功', solution: '-' },
  { code: '400', message: '参数错误', solution: '检查请求参数是否完整、格式是否正确' },
  { code: '401', message: '签名错误', solution: '检查签名算法、API Secret 是否正确' },
  { code: '403', message: '商户已禁用', solution: '联系管理员确认商户状态' },
  { code: '404', message: '订单不存在', solution: '检查订单号是否正确' },
  { code: '500', message: '服务器错误', solution: '稍后重试或联系技术支持' }
]
</script>

<style scoped>
.public-docs-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.docs-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 64px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  z-index: 100;
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.logo-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.logo-text {
  margin-left: 10px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.logo-badge {
  margin-left: 12px;
  padding: 4px 10px;
  background: #f0f5ff;
  color: #1677ff;
  font-size: 12px;
  border-radius: 4px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.docs-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 88px 24px 40px;
}

.docs-nav {
  position: sticky;
  top: 88px;
  background: #fff;
  border-radius: 8px;
  padding: 8px 0;

  :deep(.el-menu) {
    border: none;
  }

  :deep(.el-menu-item) {
    height: 44px;
  }
}

.doc-section {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 16px;

  h2 {
    font-size: 20px;
    font-weight: 600;
    color: #1a1a1a;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #eee;
  }

  h3 {
    font-size: 15px;
    font-weight: 600;
    color: #333;
    margin: 20px 0 12px;
  }

  p {
    color: #666;
    line-height: 1.8;
    margin-bottom: 12px;
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

.feature-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-top: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #52c41a;

  span {
    color: #333;
  }
}

.steps {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.step {
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
      margin-bottom: 4px;
    }

    p {
      font-size: 13px;
      color: #666;
      margin: 0;
    }
  }
}

.api-endpoint {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 6px;
  margin-bottom: 16px;

  code {
    background: transparent;
    color: #333;
    font-size: 14px;
  }

  .method {
    padding: 4px 10px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 600;
    color: #fff;

    &.ws {
      background: #722ed1;
    }
  }
}

.sign-steps {
  padding-left: 20px;
  
  li {
    margin-bottom: 12px;
    color: #666;
    line-height: 1.8;
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

/* 接入方式卡片 */
.method-intro {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #eee;

  h3 {
    margin-bottom: 16px;
  }
}

.method-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.method-card {
  padding: 20px;
  background: linear-gradient(135deg, #f8f9ff 0%, #f0f5ff 100%);
  border-radius: 12px;
  border: 1px solid #e6f0ff;

  .method-icon {
    width: 48px;
    height: 48px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    margin-bottom: 12px;
  }

  h4 {
    font-size: 16px;
    font-weight: 600;
    color: #1a1a1a;
    margin-bottom: 8px;
  }

  p {
    font-size: 13px;
    color: #666;
    margin-bottom: 12px;
    line-height: 1.6;
  }
}

/* 签名流程 */
.sign-flow {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.sign-step {
  text-align: center;
  padding: 20px 16px;
  background: #f8f9fa;
  border-radius: 12px;
  position: relative;

  .step-badge {
    width: 32px;
    height: 32px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 600;
    margin: 0 auto 12px;
  }

  .step-info {
    h4 {
      font-size: 14px;
      font-weight: 600;
      color: #1a1a1a;
      margin-bottom: 6px;
    }

    p {
      font-size: 12px;
      color: #666;
      line-height: 1.5;
      margin: 0;
    }
  }
}

.endpoint-desc {
  font-size: 12px;
  color: #999;
  margin-left: auto;
}

/* 响应式 */
@media (max-width: 992px) {
  .method-cards,
  .sign-flow {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .steps,
  .method-cards {
    grid-template-columns: 1fr;
  }

  .sign-flow {
    grid-template-columns: 1fr;
  }

  .feature-list {
    grid-template-columns: 1fr;
  }
}
</style>
