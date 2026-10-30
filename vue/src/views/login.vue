<template>
  <div class="login">
    <div class="layout">
      <div class="bgLeft"></div>
      <div class="bgRight"></div>
      <h3 class="title">启航小店管家</h3>
      <div class="login-form">
        <div class="zhuce">登录</div>
        <!--star 密码登录-->
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="账号"
            >
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              auto-complete="off"
              placeholder="密码"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
          <el-form-item style="width:100%;">
            <el-button class="btns"
              :loading="loading"
              size="medium"
              type="primary"
              style="width:100%;"
              @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登 录 中...</span>
            </el-button>
          </el-form-item>
        </el-form>
        <!--end 密码登录-->
      </div>
    </div>
    <!--  底部  -->
    <div class="el-login-footer">
      <span>Copyright © 2023-2026 qihangerp.cn All Rights Reserved.</span>
    </div>
  </div>
</template>

<script>
import Cookies from "js-cookie";
import { encrypt, decrypt } from '@/utils/jsencrypt'
export default {
  name: "Login",
  data() {
    return {
      loginForm: {
        username: "",
        password: "",
        rememberMe: false
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ]
      },
      loading: false,
      redirect: undefined,
    };
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  created() {
    this.getCookie();
  },
  methods: {
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 });
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 });
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 });
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove('rememberMe');
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.loading = true;
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{});
          }).catch(() => {
            this.loading = false;
          });
        }
      });
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
.login {
  font-family: Barlow;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 800px;
  height: 100%;
  width: 100%;
  // background-size: cover;
  background-image: url("../assets/images/login-background.png");
  background-attachment: fixed;
  overflow: hidden;
  .layout{
    position: relative;
    z-index: 3;
    height: 100%;
  }
  .bgLeft{
    position: absolute;
    width: 590px;
    height: 590px;
    bottom: 0;
    left: -359px;
    background-image: url("../assets/images/backgroundLeft.png");
    background-size: cover;
  }
  .bgRight{
    position: absolute;
    width: 414px;
    height: 414px;
    top: 0;
    right: -211px;
    background-image: url("../assets/images/backgroundRight.png");
    background-size: cover;
  }
  .title {
    margin: 79px auto 40px;
    text-align: center;
    color: #000000;
    font-size: 24px;
    font-weight: bold;
  }

}
.login-form {
  border-radius: 16px;
  width: 428px;
  min-height: 500px;
  padding-top: 68px;
  position: relative;
  z-index: 9999;
  box-shadow: 0 20px 80px 0 rgba(45,66,119,.10196);
  background: hsla(0, 0%, 100%, .65);
  border: 1px solid #fff;
  .zhuce{
    font-weight: 600;
    height: 20px;
    font-size: 20px;
    line-height: 20px;
    color: #333;
    text-align: center;
  }
  .el-form{
    padding: 0px 48px 99px;
    margin-top: 32px;
    .btns{
      color: #fff;
      background-color: #307dff;
      border-color: #307dff;
      width: 100%;
      border-radius: 8px;
      padding: 15px 0;
      font-weight: 500;
    }
  }
  .el-input {
    height: 46px;
    input {
      height: 46px;
      line-height: 46px;
      border-color: #e4e4e4;
      border-radius: 8px;
      padding-left: 40px;
      color: #000000;
    }
  }
  .input-icon {
    height: 46px;
    width: 14px;
    margin-left: 10px;
  }
}
.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  z-index: 2;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #000000;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}
</style>
