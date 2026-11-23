; =====================================================================
; VSCode Auto Allow —— 自动点击 VSCode 聊天/Agent 的权限确认按钮
;
; 功能：每 3 秒扫描所有 VSCode 窗口（含多个实例），找到
;       “Allow Once / Allow All” 按钮并自动点击。
; 原理：通过 UIA（UI Automation）在后台查找并 Invoke 按钮，
;       不抢焦点、不移动鼠标，不影响你继续打字。
; 依赖：UIA-v2 库（已放在 .\Lib\UIA.ahk）
;       https://github.com/Descolada/UIA-v2
; 用法：双击运行；Ctrl+Alt+A 暂停/恢复，Ctrl+Alt+S 立即扫描。
; =====================================================================
#Requires AutoHotkey v2.0
#SingleInstance Force
#Include <UIA>

; ------------------------------ 配置 ------------------------------
BUTTON_NAMES := [
    "Allow Once",
    "Allow All",
    ; 中文界面请按需追加："允许一次", "全部允许"
    ; 风险提示：不要随意加 "Allow Always"（Always 会长期放行工具，
    ; 自动脚本点它等于永久授权）。
]

; 要扫描的 VSCode 进程名。用 Cursor / VSCodium 等 fork 就追加对应 exe。
PROCESS_NAMES := ["Code.exe", "Code - Insiders.exe"]

; 前缀匹配：按钮名以这些开头就点。VSCode 会在按钮名后拼快捷键提示
; （实测为 "Turn On (Ctrl+Enter)"），精确匹配会失败，所以单独走 StartsWith。
BUTTON_PREFIX_NAMES := ["Turn On"]

SCAN_INTERVAL_MS := 3000      ; 扫描间隔（毫秒）
SHOW_TIP := true              ; 点击成功时显示气泡提示
; ------------------------------------------------------------------

Persistent
A_IconTip := "VSCode Auto Allow 运行中 | Ctrl+Alt+A 暂停/恢复"

A_TrayMenu.Delete()
A_TrayMenu.Add("暂停 / 恢复扫描`tCtrl+Alt+A", (*) => ToggleScan())
A_TrayMenu.Add("立即扫描一次`tCtrl+Alt+S", (*) => ScanAll())
A_TrayMenu.Add()
A_TrayMenu.Add("退出", (*) => ExitApp())
A_TrayMenu.Default := "立即扫描一次`tCtrl+Alt+S"

ScanAll()                      ; 启动时先扫一遍
SetTimer(ScanAll, SCAN_INTERVAL_MS)
return

^!a::ToggleScan()
^!s::ScanAll()

ToggleScan() {
    static running := true
    running := !running
    SetTimer(ScanAll, running ? SCAN_INTERVAL_MS : 0)
    ToolTip(running ? "VSCode Auto Allow：继续扫描" : "VSCode Auto Allow：已暂停")
    SetTimer(() => ToolTip(), -1200)
}

ScanAll() {
    static busy := false       ; 防止上一轮还没扫完就进入下一轮
    if busy
        return
    busy := true
    clicked := 0
    try {
        ; VSCode 是 Electron 应用，窗口类都是 Chrome_WidgetWin_1，
        ; 再用进程名过滤出真正的 VSCode（排除 Chrome/Edge 等）。
        for hwnd in WinGetList("ahk_class Chrome_WidgetWin_1") {
            if !IsTargetProcess(WinGetProcessName("ahk_id " hwnd))
                continue
            clicked += ScanWindow(hwnd)
        }
    } catch {
        ; 个别窗口可能刚好关闭，忽略即可
    }
    busy := false
    if (SHOW_TIP && clicked > 0) {
        ToolTip("VSCode Auto Allow：已点击 " clicked " 个确认按钮")
        SetTimer(() => ToolTip(), -1500)
    }
}

IsTargetProcess(exe) {
    for name in PROCESS_NAMES
        if (StrCompare(exe, name, "Off") = 0)
            return true
    return false
}

ScanWindow(hwnd) {
    clicked := 0
    local root
    try root := UIA.ElementFromHandle(hwnd)   ; 库会自动激活 Chromium 无障碍树
    catch
        return 0
    for name in BUTTON_NAMES {
        local buttons
        try buttons := root.FindElements({Name: name, Type: "Button"})
        catch
            continue
        for btn in buttons
            clicked += InvokeBtn(btn)
    }
    for name in BUTTON_PREFIX_NAMES {
        local prefixed
        try prefixed := root.FindElements({Name: name, Type: "Button", MatchMode: "StartsWith"})
        catch
            continue
        for btn in prefixed
            clicked += InvokeBtn(btn)
    }
    return clicked
}

InvokeBtn(btn) {
    try {
        ; Click() 优先走 InvokePattern（后台触发，不动鼠标）；
        ; 只有按钮不支持 Invoke 时才退化成真实鼠标点击。
        btn.Click(, , , , true)
        return 1
    } catch {
        ; 元素可能刚被界面刷新失效，下一轮再处理
        return 0
    }
}
