<template>
  <div class="app-layout">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <div class="logo-section">
          <img src="@/assets/image.png" alt="郑州大学医院助手" class="logo-image" />
          <h1 class="logo-text">医院助手</h1>
        </div>
        <button class="new-chat-btn" @click="newChat">
          <i class="fa-solid fa-plus"></i>
          <span>新对话</span>
        </button>
      </div>
        <!-- 功能区域 -->
      <div class="sidebar-features">
        <div class="feature-item" @click="sendSymptomConsultation">
          <i class="fa-solid fa-stethoscope"></i>
          <span>症状咨询</span>
        </div>
        <div class="feature-item" @click="sendMedicationGuidance">
          <i class="fa-solid fa-pills"></i>
          <span>用药指导</span>
        </div>
        <div class="feature-item" @click="sendAppointmentService">
          <i class="fa-solid fa-calendar-check"></i>
          <span>预约挂号</span>
        </div>
        <div class="feature-item" @click="sendReportInterpretation">
          <i class="fa-solid fa-file-medical"></i>
          <span>报告解读</span>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 顶部导航栏 -->
      <div class="top-navbar">
        <div class="navbar-title">
          <h2>郑州大学医院智能助手</h2>
          <p>专业医疗咨询，24小时在线服务</p>
        </div>
        <div class="navbar-actions">
          <button class="action-btn">
            <i class="fa-solid fa-gear"></i>
          </button>
          <button class="action-btn">
            <i class="fa-solid fa-question-circle"></i>
          </button>
        </div>
      </div>

      <!-- 聊天容器 -->
      <div class="chat-container">
        <!-- 欢迎界面 -->
        <div v-if="messages.length === 0" class="welcome-screen">
          <div class="welcome-content">
            <div class="welcome-icon">
              <i class="fa-solid fa-user-doctor"></i>
            </div>
            <h3>欢迎使用郑州大学医院助手</h3>
            <p>我是您的专业医疗顾问，可以为您提供：</p>
            <div class="welcome-features">
              <div class="welcome-feature">
                <i class="fa-solid fa-stethoscope"></i>
                <span>症状分析与建议</span>
              </div>
              <div class="welcome-feature">
                <i class="fa-solid fa-pills"></i>
                <span>用药咨询与指导</span>
              </div>
              <div class="welcome-feature">
                <i class="fa-solid fa-calendar-check"></i>
                <span>预约挂号服务</span>
              </div>
              <div class="welcome-feature">
                <i class="fa-solid fa-file-medical"></i>
                <span>检查报告解读</span>
              </div>
            </div>
            <div class="quick-questions">
              <h4>常见问题</h4>
              <div class="question-buttons">
                <button class="question-btn" @click="sendRequest('如何预约挂号？')">
                  如何预约挂号？
                </button>
                <button class="question-btn" @click="sendRequest('头痛应该看哪个科室？')">
                  头痛应该看哪个科室？
                </button>
                <button class="question-btn" @click="sendRequest('血压正常范围是多少？')">
                  血压正常范围是多少？
                </button>
                <button class="question-btn" @click="sendRequest('如何解读血常规报告？')">
                  如何解读血常规报告？
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 消息列表 -->
        <div v-else class="message-list" ref="messaggListRef">
          <div
            v-for="(message, index) in messages"
            :key="index"
            :class="['message-wrapper', message.isUser ? 'user-wrapper' : 'bot-wrapper']"
          >
            <div class="message-avatar">
              <div v-if="message.isUser" class="user-avatar">
                <i class="fa-solid fa-user"></i>
              </div>
              <div v-else class="bot-avatar">
                <i class="fa-solid fa-user-doctor"></i>
              </div>
            </div>
            <div class="message-content">
              <div class="message-bubble">
                <span v-html="message.content"></span>
                <!-- 加载动画 -->
                <div v-if="message.isThinking || message.isTyping" class="typing-indicator">
                  <span class="typing-dot"></span>
                  <span class="typing-dot"></span>
                  <span class="typing-dot"></span>
                </div>
              </div>
              <div class="message-time">
                {{ new Date().toLocaleTimeString() }}
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="input-area">
          <div class="input-wrapper">
            <div class="input-container">
              <input
                v-model="inputMessage"
                @keyup.enter="sendMessage"
                placeholder="输入您的问题，我来为您解答..."
                class="message-input"
                :disabled="isSending"
              />
              <button 
                @click="sendMessage" 
                :disabled="isSending || !inputMessage.trim()" 
                class="send-button"
              >
                <i class="fa-solid fa-paper-plane"></i>
              </button>
            </div>
            <div class="input-tips">
              <span>AI助手正在为您服务，请详细描述您的问题</span>
              <div class="input-actions">
                <button class="action-icon" title="上传图片">
                  <i class="fa-solid fa-image"></i>
                </button>
                <button class="action-icon" title="语音输入">
                  <i class="fa-solid fa-microphone"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import axios from 'axios'
import { v4 as uuidv4 } from 'uuid'

const messaggListRef = ref()
const isSending = ref(false)
const uuid = ref()
const inputMessage = ref('')
const messages = ref([])

onMounted(() => {
  initUUID()
  // 移除 setInterval，改用手动滚动
  watch(messages, () => scrollToBottom(), { deep: true })
  hello()
})

const scrollToBottom = () => {
  if (messaggListRef.value) {
    messaggListRef.value.scrollTop = messaggListRef.value.scrollHeight
  }
}

const hello = () => {
  // 移除自动发送"你好"消息，让用户主动开始对话
  // sendRequest('你好')
}

const sendMessage = () => {
  if (inputMessage.value.trim()) {
    sendRequest(inputMessage.value.trim())
    inputMessage.value = ''
  }
}

const sendRequest = (message) => {
  isSending.value = true
  const userMsg = {
    isUser: true,
    content: message,
    isTyping: false,
    isThinking: false,
  }
  //第一条默认发送的用户消息”你好“不放入会话列表
  if(messages.value.length > 0){
    messages.value.push(userMsg)
  }

  // 添加机器人加载消息
  const botMsg = {
    isUser: false,
    content: '', // 增量填充
    isTyping: true, // 显示加载动画
    isThinking: false,  }
  messages.value.push(botMsg)
  const lastMsg = messages.value[messages.value.length - 1]
  scrollToBottom()
  axios
    .post(
      '/api/xiaozheng/chat',
      { memoryId: uuid.value, message },
      {
        responseType: 'stream',
        timeout: 30000, // 30秒超时
        onDownloadProgress: (e) => {
          const fullText = e.event.target.responseText // 累积的完整文本
          let newText = fullText.substring(lastMsg.content.length)
          lastMsg.content += newText //增量更新
          console.log(lastMsg)
          scrollToBottom() // 实时滚动
        },
      }
    )
    .then(() => {
      // 流结束后隐藏加载动画
      messages.value.at(-1).isTyping = false
      isSending.value = false
    })    .catch((error) => {
      console.error('流式错误:', error)
      let errorMessage = '请求失败，请重试'
      
      if (error.code === 'ECONNREFUSED') {
        errorMessage = '无法连接到服务器，请检查后端服务是否启动'
      } else if (error.code === 'ETIMEDOUT') {
        errorMessage = '请求超时，请重试'
      } else if (error.response) {
        errorMessage = `服务器错误: ${error.response.status}`
      }
      
      messages.value.at(-1).content = errorMessage
      messages.value.at(-1).isTyping = false
      isSending.value = false
    })
}

// 初始化 UUID
const initUUID = () => {
  let storedUUID = localStorage.getItem('user_uuid')
  if (!storedUUID) {
    storedUUID = uuidToNumber(uuidv4())
    localStorage.setItem('user_uuid', storedUUID)
  }
  uuid.value = storedUUID
}

const uuidToNumber = (uuid) => {
  let number = 0
  for (let i = 0; i < uuid.length && i < 6; i++) {
    const hexValue = uuid[i]
    number = number * 16 + (parseInt(hexValue, 16) || 0)
  }
  return number % 1000000
}

// 转换特殊字符
const convertStreamOutput = (output) => {
  return output
    .replace(/\n/g, '<br>')
    .replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;')
    .replace(/&/g, '&amp;') // 新增转义，避免 HTML 注入
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
}

// 侧边栏功能项点击处理函数
const sendSymptomConsultation = () => {
  const message = "我想咨询症状相关问题，请问您能帮我分析一下身体不适的症状吗？比如头痛、发热、咳嗽等症状的可能原因和建议。"
  sendRequest(message)
}

const sendMedicationGuidance = () => {
  const message = "我需要用药指导，请问您能帮我了解药物的正确使用方法、注意事项、副作用以及药物相互作用等相关信息吗？"
  sendRequest(message)
}

const sendAppointmentService = () => {
  const message = "我想了解预约挂号服务，请问如何预约门诊？需要准备哪些材料？预约流程是怎样的？"
  sendRequest(message)
}

const sendReportInterpretation = () => {
  const message = "我需要报告解读服务，请问您能帮我解读医学检查报告吗？比如血常规、生化检查、影像学报告等的含义和注意事项。"
  sendRequest(message)
}

const newChat = () => {
  // 这里添加新会话的逻辑
  console.log('开始新会话')
  localStorage.removeItem('user_uuid')
  window.location.reload()
}

</script>

<style scoped>
/* ===== 全局布局 ===== */
.app-layout {
  display: flex;
  height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif;
}

/* ===== 侧边栏样式 ===== */
.sidebar {
  width: 280px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-right: 1px solid rgba(228, 228, 231, 0.8);
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.sidebar-header {
  padding: 24px;
  border-bottom: 1px solid rgba(228, 228, 231, 0.6);
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.logo-image {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  margin-bottom: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
  text-align: center;
}

.new-chat-btn {
  width: 100%;
  padding: 12px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.new-chat-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.sidebar-features {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.feature-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 8px;
  border-radius: 10px;
  color: #6b7280;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  gap: 12px;
}

.feature-item:hover {
  background: rgba(99, 102, 241, 0.1);
  color: #6366f1;
  transform: translateX(4px);
}

.feature-item i {
  font-size: 16px;
  width: 20px;
  text-align: center;
}

/* ===== 主内容区样式 ===== */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: transparent;
}

.top-navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(228, 228, 231, 0.6);
}

.navbar-title h2 {
  margin: 0 0 4px 0;
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.navbar-title p {
  margin: 0;
  font-size: 14px;
  color: #6b7280;
}

.navbar-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 10px;
  background: rgba(243, 244, 246, 0.8);
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-btn:hover {
  background: rgba(99, 102, 241, 0.1);
  color: #6366f1;
  transform: scale(1.05);
}

/* ===== 聊天容器样式 ===== */
.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0 24px 24px 24px;
}

/* ===== 欢迎界面样式 ===== */
.welcome-screen {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.welcome-content {
  text-align: center;
  max-width: 600px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 48px 40px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.welcome-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  color: white;
  font-size: 32px;
}

.welcome-content h3 {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 16px 0;
}

.welcome-content p {
  font-size: 16px;
  color: #6b7280;
  margin: 0 0 32px 0;
  line-height: 1.6;
}

.welcome-features {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 32px;
}

.welcome-feature {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba(243, 244, 246, 0.6);
  border-radius: 12px;
  color: #374151;
  font-size: 14px;
  font-weight: 500;
}

.welcome-feature i {
  font-size: 18px;
  color: #6366f1;
}

.quick-questions h4 {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 16px 0;
}

.question-buttons {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.question-btn {
  padding: 12px 16px;
  background: rgba(243, 244, 246, 0.8);
  border: 1px solid rgba(228, 228, 231, 0.8);
  border-radius: 10px;
  color: #374151;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: left;
}

.question-btn:hover {
  background: rgba(99, 102, 241, 0.1);
  border-color: #6366f1;
  color: #6366f1;
  transform: translateY(-1px);
}

/* ===== 消息列表样式 ===== */
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 24px 0;
  scroll-behavior: smooth;
}

.message-wrapper {
  display: flex;
  margin-bottom: 24px;
  animation: fadeInUp 0.3s ease;
}

.user-wrapper {
  justify-content: flex-end;
}

.bot-wrapper {
  justify-content: flex-start;
}

.message-avatar {
  margin: 0 12px;
  flex-shrink: 0;
}

.user-avatar, .bot-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: white;
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.bot-avatar {
  background: linear-gradient(135deg, #74b9ff 0%, #0984e3 100%);
}

.message-content {
  max-width: 70%;
  display: flex;
  flex-direction: column;
}

.user-wrapper .message-content {
  align-items: flex-end;
}

.bot-wrapper .message-content {
  align-items: flex-start;
}

.message-bubble {
  padding: 16px 20px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.5;
  position: relative;
  word-wrap: break-word;
}

.user-wrapper .message-bubble {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 6px;
}

.bot-wrapper .message-bubble {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  color: #374151;
  border: 1px solid rgba(228, 228, 231, 0.6);
  border-bottom-left-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.message-time {
  font-size: 11px;
  color: #9ca3af;
  margin-top: 4px;
  padding: 0 4px;
}

/* ===== 打字动画 ===== */
.typing-indicator {
  display: flex;
  gap: 4px;
  margin-top: 8px;
}

.typing-dot {
  width: 6px;
  height: 6px;
  background: #9ca3af;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-dot:nth-child(1) { animation-delay: 0ms; }
.typing-dot:nth-child(2) { animation-delay: 200ms; }
.typing-dot:nth-child(3) { animation-delay: 400ms; }

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-10px); }
}

/* ===== 输入区域样式 ===== */
.input-area {
  margin-top: auto;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(228, 228, 231, 0.6);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.input-container {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 12px;
}

.message-input {
  flex: 1;
  padding: 16px 20px;
  border: 1px solid rgba(228, 228, 231, 0.8);
  border-radius: 24px;
  font-size: 14px;
  background: rgba(249, 250, 251, 0.8);
  transition: all 0.2s ease;
  outline: none;
}

.message-input:focus {
  border-color: #6366f1;
  background: white;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.send-button {
  padding: 12px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 50%;
  width: 48px;
  height: 48px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.send-button:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.send-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.input-tips {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #9ca3af;
  font-size: 12px;
}

.input-actions {
  display: flex;
  gap: 8px;
}

.action-icon {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  color: #9ca3af;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-icon:hover {
  background: rgba(156, 163, 175, 0.1);
  color: #6b7280;
}

/* ===== 动画效果 ===== */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ===== 响应式设计 ===== */
@media (max-width: 768px) {
  .app-layout {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    height: auto;
    flex-direction: row;
    padding: 12px;
    border-right: none;
    border-bottom: 1px solid rgba(228, 228, 231, 0.6);
  }
  
  .sidebar-header {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 0;
    border: none;
    flex: 1;
  }
  
  .logo-section {
    flex-direction: row;
    margin: 0;
  }
  
  .logo-image {
    width: 40px;
    height: 40px;
    margin: 0 8px 0 0;
  }
  
  .logo-text {
    font-size: 16px;
  }
  
  .new-chat-btn {
    width: auto;
    padding: 8px 16px;
  }
  
  .sidebar-features {
    display: none;
  }
  
  .top-navbar {
    padding: 16px 20px;
  }
  
  .navbar-title h2 {
    font-size: 20px;
  }
  
  .welcome-content {
    padding: 32px 24px;
  }
  
  .welcome-features {
    grid-template-columns: 1fr;
  }
  
  .question-buttons {
    grid-template-columns: 1fr;
  }
  
  .chat-container {
    padding: 0 16px 16px 16px;
  }
  
  .message-content {
    max-width: 85%;
  }
}

@media (max-width: 480px) {
  .welcome-content {
    padding: 24px 16px;
  }
  
  .input-area {
    padding: 16px;
  }
  
  .input-tips {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
}
</style>
