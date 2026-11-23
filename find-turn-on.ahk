#Requires AutoHotkey v2.0
#SingleInstance Force
CoordMode("Mouse", "Screen")
points := Map()
A_IconTip := "VS Code 按钮位置点击"
#HotIf WinActive("ahk_exe Code.exe") || WinActive("ahk_exe Code - Insiders.exe")
^!+j::Remember("once", "允许一次")
^!+k::Remember("all", "全部允许")
^!+u::Remember("on", "启用")
^!j::PressAt("once")
^!k::PressAt("all")
^!u::PressAt("on")
#HotIf

Remember(key, label) {
    global points
    hwnd := WinExist("A")
    MouseGetPos(&x, &y, &under)
    if under != hwnd {
        Tip("请把鼠标放在当前 VS Code 窗口内")
        return
    }
    WinGetClientPos(&left, &top, &width, &height, "ahk_id " hwnd)
    points[key] := {hwnd: hwnd, x: x-left, y: y-top, w: width, h: height}
    Tip("已记录“" label "”的位置")
}

PressAt(key) {
    global points
    if !points.Has(key) {
        Tip("先把鼠标放到按钮上，按 Ctrl+Alt+Shift 加对应字母")
        return
    }
    point := points[key]
    if !WinActive("ahk_id " point.hwnd) {
        Tip("请回到记录位置时的 VS Code 窗口")
        return
    }
    WinGetClientPos(&left, &top, &width, &height, "ahk_id " point.hwnd)
    if width != point.w || height != point.h {
        Tip("窗口尺寸已变化，请重新记录按钮位置")
        return
    }
    ; 等待用户松开快捷键，避免产生 Ctrl/Alt 修饰点击。
    KeyWait("Ctrl")
    KeyWait("Alt")
    if !WinActive("ahk_id " point.hwnd)
        return
    MouseGetPos(&oldX, &oldY)
    MouseMove(left+point.x, top+point.y, 0)
    MouseGetPos(,, &under)
    if under != point.hwnd {
        MouseMove(oldX, oldY, 0)
        Tip("目标位置被其他窗口遮挡")
        return
    }
    try Click()
    finally MouseMove(oldX, oldY, 0)
}

Tip(text) {
    ToolTip(text)
    SetTimer(() => ToolTip(), -1800)
}
