# idea开发的技巧

1. idea下面显示类名-方法名：view——Active Editor——Show Breadcrumbs
2. 点击方法名字： ctrl + alt + H 可以看出方法的调用链路
3. 点击参数然后 Analyze + data flow to here 就是看参数的调用链路
4. 编译调优去掉 [重复代码检查](https://www.cnblogs.com/danhuai/p/11341018.html)
5. 多行缩进ctrl+[或者]
6. 使用 Analyz 扫描
7. ctrl + j 快速生成代码
8. idea 快捷键 alt + shift + C 查看最近修改的代码
9. idea 的插件 String Mainpulation 怎么做
10. 选中一个目录下所有文件, shift + 文件开头 + 文件结束
11. code review 要加上 findbugs, checkstyle, fortify, sonarlite 这些插件, code coverage 90% 以上
12. 多行光标: alt + shift + a 鼠标点击
13. alt + f7 查看方法的所有引用 find usages
14. 计算机技术
```angular2html
// 1. 此命令只会下载最后一次提交记录 (按需使用)
git clone --depth=1 https://github.com/qtgolang/SunnyNet.git
// 2. 此命令会下载所有提交记录,大小约 1.1GB - 1.3GB (按需使用)
git clone https://github.com/qtgolang/SunnyNet.git
```