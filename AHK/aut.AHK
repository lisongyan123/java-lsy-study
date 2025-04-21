#Requires AutoHotkey v2.0

; 创建主窗口
AppLauncher := Gui(, "应用程序启动器")

; 设置窗口大小
AppLauncher.SetFont("s10", "Segoe UI")

; 添加标题
AppLauncher.Add("Text", "w300 Center", "选择要启动的应用程序")

; 添加分隔线
AppLauncher.Add("Text", "w300 0x10")  ; 0x10 是水平分隔线的样式

; 创建应用程序列表（可自行修改添加更多应用）
apps := Map(
    "记事本", "notepad.exe",
    "计算器", "calc.exe",
    "画图", "mspaint.exe",
    "浏览器", "chrome.exe",  ; 或 "msedge.exe"
    "资源管理器", "explorer.exe",
    "命令提示符", "cmd.exe",
    "任务管理器", "taskmgr.exe"
)

; 添加列表框显示应用程序
lbApps := AppLauncher.Add("ListBox", "w300 h150")
for name in apps
    lbApps.Add([name])

; 添加启动按钮
btnLaunch := AppLauncher.Add("Button", "Default w100", "启动")
btnLaunch.OnEvent("Click", LaunchApp)

; 添加退出按钮
btnExit := AppLauncher.Add("Button", "x+10 w100", "退出")
btnExit.OnEvent("Click", (*) => ExitApp())

; 显示 GUI
AppLauncher.Show()

; 启动应用程序的函数
LaunchApp(*)
{
    selected := lbApps.Text
    if !selected
    {
        MsgBox("请先选择一个应用程序", "提示", "Icon!")
        return
    }
    
    try
    {
        Run(apps[selected])
    }
    catch as e
    {
        MsgBox("无法启动应用程序: " e.Message, "错误", "Iconx")
    }
}

; 当窗口关闭时退出脚本
AppLauncher.OnEvent("Close", (*) => ExitApp())