# forge-gradle-kts 插件使用指南
本插件是为gradle的各个子模块提供共用逻辑的插件。
### 基础功能
1. 读取插件扩展的配置文件
2. 提供基于约定大于配置的源配置
3. 提供jeiVersion、mcVersion和forgeVersion的定义
4. 提供便捷的定义测试目标的方法
5. 提供其他的便捷方法
### 1. 读取插件扩展的配置文件
本功能主要实现了从local.properties、local.yml、local.yaml、gradle.yml以及gradle.yaml中读取配置。local.yml和local.properties已经在.gitignore中对git屏蔽，利用该功能可以使得这几个文件储存每个开发人员个性化的配置，比如使用那些预定的源配置（如国内服务器和原源服务器）。
### 2. 提供基于约定大于配置的源配置
