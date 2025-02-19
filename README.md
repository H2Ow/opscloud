Welcome to the opscloud wiki!

# OpsCloud简介
<img src="https://img.shields.io/badge/version-3.0.1-brightgreen.svg"></img>
<img src="https://img.shields.io/badge/java-8-brightgreen.svg"></img> 
<img src="https://img.shields.io/badge/springboot-2.2.2.RELEASE-brightgreen.svg"></img> 
<img src="https://img.shields.io/badge/mysql-8-brightgreen.svg"></img> 

<br>
OpsCloud4.0 IaC基础架构即代码 版本预告

<img src="https://opscloud-res.oss-cn-hangzhou.aliyuncs.com/opscloud4/github/oc4-1.png"></img>
<img src="https://opscloud-res.oss-cn-hangzhou.aliyuncs.com/opscloud4/github/oc4-2.png"></img>
<img src="https://opscloud-res.oss-cn-hangzhou.aliyuncs.com/opscloud4/github/oc4-3.png"></img>
<img src="https://opscloud-res.oss-cn-hangzhou.aliyuncs.com/opscloud4/github/oc4-4.png"></img>
<img src="https://opscloud-res.oss-cn-hangzhou.aliyuncs.com/opscloud4/github/oc4-5.png"></img>
<img src="https://opscloud-res.oss-cn-hangzhou.aliyuncs.com/opscloud4/github/oc4-6.png"></img>
<img src="https://opscloud-res.oss-cn-hangzhou.aliyuncs.com/opscloud4/github/oc4-7.png"></img>
<img src="https://opscloud-res.oss-cn-hangzhou.aliyuncs.com/opscloud4/github/oc4-8.png"></img>
<img src="https://opscloud-res.oss-cn-hangzhou.aliyuncs.com/opscloud4/github/oc4-9.png"></img>
<img src="https://opscloud-res.oss-cn-hangzhou.aliyuncs.com/opscloud4/github/oc4-10.png"></img>
<img src="https://opscloud-res.oss-cn-hangzhou.aliyuncs.com/opscloud4/github/oc4-11.png"></img>

#### 4.0版本核心设计（前后端全部重构）
+ 以事件驱动
  + 监听外部事件
    + KubernetesEvent
    + Zabbix问题
    + AliyunEvent
    + GitlabHook
    + 发布平台事件
    + 内部事件
  + 事件处理器订阅、消费事件
    + Zabbix问题事件触发告警，问题事件投影至Ssh服务器（用户快速登录并处理故障）
    + Gitlab事件触发资产同步并通知CI/CD平台
+ 万物皆资产
  + 支持多外部数据源（同类型允许多实例）
  + 抽象数据源实例资产
+ 远程控制集成
  + 远程桌面RDP,VNC(需安装apache-guacamole)
  + Web终端（支持多开，会话复制，命令同步）
  + Ssh服务器
    + 支持快速登录（使用Gitlab公钥认证）
    + 展示服务器环境，标签，授权账户
    + 支持Kubernetes容器登录
    + 支持查看Kubernetes容器日志
    + 支持事件投影，通过事件ID快速登录服务器






开源协议：<a style="color:#2b669a" href="http://www.gnu.org/licenses/old-licenses/gpl-2.0.html" target="_blank">GNU General Public License v2</a>

### 帮助文档

<a style="color:#2b669a" href="https://www.kancloud.cn/ixrjog/opscloud/1780645" target="_blank">传送门:https://www.kancloud.cn/ixrjog/opscloud/1780645</a>

+ API接口文档
  + example.org/oc3/doc.html

+ 平台特性
  + 基于角色的访问控制RBAC(Role-Based Access Control)+LDAP完全管理
  + 配置文件加密（密码, AK），数据库敏感字段加密
  + 代码即文档，全接口API
  + 前后端分离，支持跨域部署
  
+ 核心功能(自动化配置)
  + 多云主机管理
    + ECS,EC2,CVM,ESXi/VM
  + 阿里云
    + LogService,RAM
  + 堡垒机
    + Jumpserver
    + Web终端
      + 支持多窗口批量操作（看日志利器）
      + 支持低权限/高权限登录（工单中申请权限）
  + 配置管理
    + Ansible,配置文件订阅
  + 监控
    + Zabbix
  + Kubernetes
  
### 前端代码
+ https://github.com/ixrjog/opscloud-web

#### 欢迎提供更为详细的安装文档
+ ixrjog@qq.com