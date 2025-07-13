#Requires AutoHotkey v2.0
#SingleInstance Force

; 创建托盘菜单
Tray := A_TrayMenu
Tray.Delete() ; 删除默认菜单项
Tray.Add("打开应用程序", ShowAppMenu)
Tray.Add("退出", (*) => ExitApp())
Tray.Default := "打开应用程序"

; 创建应用程序菜单
AppMenu := Menu()
AppMenu.Add("记事本", (*) => Run("notepad.exe"))
AppMenu.Add("计算器", (*) => Run("calc.exe"))
AppMenu.Add() ; 分隔线
AppMenu.Add("文件资源管理器", (*) => Run("explorer.exe"))
AppMenu.Add() ; 分隔线
; AppMenu.Add("自定义程序1", (*) => Run("C:\Path\To\Your\Program1.exe"))
; AppMenu.Add("自定义程序2", (*) => Run("C:\Path\To\Your\Program2.exe"))
AppMenu.Add() ; 分隔线
AppMenu.Add("配置菜单", OpenConfig)

ShowAppMenu(*) {
    AppMenu.Show()
}

OpenConfig(*) {
    MsgBox "在这里可以添加配置菜单的代码", "配置", "OK"
}

; 设置热键 Win+Alt+A 打开应用程序菜单
#!a::ShowAppMenu()