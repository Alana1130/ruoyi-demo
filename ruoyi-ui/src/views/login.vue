<template>
  <div class="login-container">
    <div class="login-bg"></div>
    <div class="login-form-wrapper">
      <div class="login-form-card">
        <div class="login-header">
          <h2 class="login-title">{{ title }}</h2>
          <p class="login-subtitle">欢迎回来，管理员</p>
        </div>
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="请输入账号"
              class="login-input"
            >
              <svg-icon slot="prefix" icon-class="user" class="input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              auto-complete="off"
              placeholder="请输入密码"
              class="login-input"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="password" class="input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="code" v-if="captchaEnabled">
            <el-input
              v-model="loginForm.code"
              auto-complete="off"
              placeholder="请输入验证码"
              class="login-input captcha-input"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="validCode" class="input-icon" />
            </el-input>
            <div class="login-code">
              <img :src="codeUrl" @click="getCode" class="login-code-img"/>
            </div>
          </el-form-item>
          <div class="login-options">
            <el-checkbox v-model="loginForm.rememberMe" class="remember-me">记住密码</el-checkbox>
            <div v-if="register" class="register-link">
              <router-link class="link-type" :to="'/register'">立即注册</router-link>
            </div>
          </div>
          <el-form-item class="login-button-container">
            <el-button
              :loading="loading"
              size="large"
              type="primary"
              class="login-button"
              @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">登录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <!-- 底部 -->
    <div class="login-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from '@/utils/jsencrypt'
import defaultSettings from '@/settings'

export default {
  name: "Login",
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
      footerContent: defaultSettings.footerContent,
      codeUrl: "",
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: false,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      const username = Cookies.get("username")
      const password = Cookies.get("password")
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe),
        code: this.loginForm.code,
        uuid: this.loginForm.uuid
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 })
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove("username")
            Cookies.remove("password")
            Cookies.remove('rememberMe')
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{})
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.login-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  position: relative;
  overflow: hidden;
  
  .login-bg {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, #1a365d, #2c5282, #4299e1);
    z-index: 0;
    
    &:before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MCIgaGVpZ2h0PSI2MCIgdmlld0JveD0iMCAwIDYwIDYwIj48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiPjxwYXRoIGQ9Ik0zNiAxOGMzLjMxNCAwIDYtMi42ODYgNi02cy0yLjY4Ni02LTYtNi02IDIuNjg2LTYgNiAyLjY4NiA2IDYgNnptMC0yYzQtLjAwMSA3Ljk5OS0uMDAxIDgtMCAuMDAxIDQgLjAwMSA4IDAgOCA0IDAgOCA0IDggMCAuMDAxLTRuMC04eiIgZmlsbD0icmdiYSgyNTUsMjU1LDI1NSwwLjEpIi8+PHBhdGggZD0iTTMwIDEyYzMuMzE0IDAgNi0yLjY4NiA2LTZzLTIuNjg2LTYtNi02LTYgMi42ODYtNiA2IDIuNjg2IDYgNiA2em0wLTJjNC0uMDAxIDcuOTk5LS4wMDEgOC0wIC4wMDEgNC4wMDEgOCAwIDggNCAwIDggNCA4IDAgLjAwMS00em0wLTJjMy4zMTQgMCA2LTIuNjg2IDYtNnMtMi42ODYtNi02LTYtNiAyLjY4Ni02IDYgMi42ODYgNiA2IDZ6IiBmaWxsPSJyZ2JhKDI1NSwyNTUsMjU1LDAuMSkiLz48cGF0aCBkPSJNNjAgMGMwIDUuMDMyLTMuNDY4IDkuNS04LjUgOS41cy04LjUtNC40NjgtOC41LTkuNSAwLTUuMDMyIDMuNDY4LTkuNSA4LjUtOS41IDUuMDMyIDAgOS41IDMuNDY4IDkuNSA4LjUgMCA1LjAzMi0zLjQ2OCA5LjUtOC41IDkuNXptLTUyIDBjMCA1LjAzMi0zLjQ2OCA5LjUtOC41IDkuNS01LjAzMiAwLTkuNS0zLjQ2OC05LjUtOS41IDAtNS4wMzIgMy40NjgtOS41IDguNS05LjUgNS4wMzIgMCA5LjUgMy40NjggOS41IDguNSAwIDUuMDMyLTMuNDY4IDkuNS04LjUgOS41em0wLTYwYzAtNS4wMzIgMy40NjgtOS41IDguNS05LjUgNS4wMzIgMCA5LjUgMy40NjggOS41IDguNSAwIDUuMDMyLTMuNDY4IDkuNS04LjUgOS41LTUuMDMyIDAtOS41LTMuNDY4LTkuNS04LjV6IiBmaWxsPSJyZ2JhKDI1NSwyNTUsMjU1LDAuMSkiLz48cGF0aCBkPSJNMTggMzZjMCA1LjAzMi0zLjQ2OCA5LjUtOC41IDkuNS01LjAzMiAwLTkuNS0zLjQ2OC05LjUtOS41IDAtNS4wMzIgMy40NjgtOS41IDguNS05LjUgNS4wMzIgMCA5LjUgMy40NjggOS41IDguNSAwIDUuMDMyLTMuNDY4IDkuNS04LjUgOS41eiIgZmlsbD0icmdiYSgyNTUsMjU1LDI1NSwwLjEpIi8+PHBhdGggZD0iTTQyIDM2YzAgNS4wMzItMy40NjggOS41LTguNSA5LjUtNS4wMzIgMC05LjUtMy40NjgtOS41LTkuNSAwLTUuMDMyIDMuNDY4LTkuNSA4LjUtOS41IDUuMDMyIDAgOS41IDMuNDY4IDkuNSA4LjUgMCA1LjAzMi0zLjQ2OCA5LjUtOC41IDkuNXoiIGZpbGw9InJnYmEoMjU1LDI1NSwyNTUsMC4xKSIvPjwvZz48L3N2Zz4=');
    }
  }
  
  .login-form-wrapper {
    z-index: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    max-width: 450px;
    padding: 0 20px;
  }
  
  .login-form-card {
    background: rgba(255, 255, 255, 0.95);
    border-radius: 12px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    padding: 40px;
    width: 100%;
    animation: fadeInUp 0.6s ease-out;
    
    .login-header {
      text-align: center;
      margin-bottom: 30px;
      
      .login-title {
        font-size: 24px;
        font-weight: 600;
        color: #1a365d;
        margin-bottom: 8px;
      }
      
      .login-subtitle {
        font-size: 14px;
        color: #718096;
      }
    }
    
    .login-form {
      .login-input {
        height: 48px;
        border-radius: 8px;
        border: 1px solid #e2e8f0;
        transition: all 0.3s ease;
        
        input {
          height: 48px;
          font-size: 14px;
        }
        
        &:focus-within {
          border-color: #4299e1;
          box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
        }
      }
      
      .captcha-input {
        width: 65%;
      }
      
      .input-icon {
        color: #a0aec0;
        font-size: 16px;
      }
    }
    
    .login-options {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin: 20px 0;
      
      .remember-me {
        font-size: 14px;
        color: #718096;
      }
      
      .register-link {
        font-size: 14px;
        
        .link-type {
          color: #4299e1;
          text-decoration: none;
          transition: color 0.3s ease;
          
          &:hover {
            color: #3182ce;
          }
        }
      }
    }
    
    .login-button-container {
      margin-top: 10px;
    }
    
    .login-button {
      width: 100%;
      height: 48px;
      font-size: 16px;
      font-weight: 500;
      border-radius: 8px;
      background: linear-gradient(135deg, #4299e1, #3182ce);
      border: none;
      transition: all 0.3s ease;
      
      &:hover {
        background: linear-gradient(135deg, #3182ce, #2c5282);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(66, 153, 225, 0.3);
      }
      
      &:active {
        transform: translateY(0);
      }
    }
  }
  
  .login-footer {
    position: fixed;
    bottom: 20px;
    width: 100%;
    text-align: center;
    color: rgba(255, 255, 255, 0.8);
    font-size: 14px;
    z-index: 1;
  }
}

.login-code {
  width: 30%;
  height: 48px;
  float: right;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  
  img {
    cursor: pointer;
    vertical-align: middle;
    border-radius: 8px;
  }
}

.login-code-img {
  height: 48px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  
  &:hover {
    transform: scale(1.05);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .login-form-card {
    padding: 30px 20px;
  }
  
  .login-header {
    .login-title {
      font-size: 20px;
    }
  }
}
</style>
