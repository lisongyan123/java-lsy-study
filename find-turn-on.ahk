#Requires AutoHotkey v2.0
#SingleInstance Force
DetectHiddenWindows(false)
enabled := true
busy := false
A_IconTip := "VS Code 自动确认"
A_TrayMenu.Add("暂停 / 恢复扫描", ToggleScan)
A_TrayMenu.Add("立即扫描", ScanAll)
SetTimer(ScanAll, 3000)
SetTimer(ScanAllAtStart, -1)
^!a::ToggleScan()
^!s::ScanAll()

ScanAllAtStart() => ScanAll()

ToggleScan(*) {
    global enabled
    enabled := !enabled
    SetTimer(ScanAll, enabled ? 3000 : 0)
    ShowTip(enabled ? "已恢复扫描" : "已暂停扫描")
}

ShowTip(message) {
    ToolTip(message)
    SetTimer(() => ToolTip(), -1200)
}

ScanAll(*) {
    global busy
    if busy
        return
    busy := true
    try {
        handles := ""
        for hwnd in WinGetList("ahk_class Chrome_WidgetWin_1") {
            try name := WinGetProcessName("ahk_id " hwnd)
            catch
                continue
            if name = "Code.exe" || name = "Code - Insiders.exe"
                handles .= (handles ? "," : "") hwnd
        }
        if handles {
            count := ScanWindows(handles)
            if count >= 1 && count <= 100
                ShowTip("已点击 " count " 个确认按钮")
        }
    } finally {
        busy := false
    }
}

ScanWindows(handles) {
script := "$handles = @(" handles ")`n"
script .= "
(
Add-Type -AssemblyName UIAutomationClient
$class = [Windows.Automation.AutomationElement]::ClassNameProperty
$type = [Windows.Automation.AutomationElement]::ControlTypeProperty
$card = [Windows.Automation.PropertyCondition]::new($class,'chat-confirmation-widget-container')
$button = [Windows.Automation.PropertyCondition]::new($type,[Windows.Automation.ControlType]::Button)
$labels = '允许一次','Allow once','启用','开启','打开','Turn on','全部允许','允许所有','Allow all'
$count = 0
    foreach ($hwnd in $handles) {
        try {
            $window = [Windows.Automation.AutomationElement]::FromHandle([IntPtr]$hwnd)
            foreach ($panel in $window.FindAll(4,$card)) {
                $buttons = $panel.FindAll(4,$button)
                foreach ($label in $labels) {
                    $match = $buttons | Where-Object {($_.Current.Name -eq $label -or ($label -eq 'Turn on' -and $_.Current.Name.StartsWith('Turn On (',[StringComparison]::OrdinalIgnoreCase))) -and $_.Current.IsEnabled -and !$_.Current.IsOffscreen} | Select-Object -First 1
                    if ($match) {
                        $match.GetCurrentPattern([Windows.Automation.InvokePattern]::Pattern).Invoke()
                        $count++
                        break
                    }
                }
            }
        } catch { continue }
    }
exit ([Math]::Min($count,100))
)"

byteCount := StrLen(script) * 2
flags := 0x40000001  ; Base64 without line breaks
charCount := 0
encode := "Crypt32\CryptBinaryToStringW"
if !DllCall(encode, "Ptr", StrPtr(script), "UInt", byteCount, "UInt", flags,
    "Ptr", 0, "UInt*", &charCount)
    throw OSError()
encoded := Buffer(charCount * 2)
if !DllCall(encode, "Ptr", StrPtr(script), "UInt", byteCount, "UInt", flags,
    "Ptr", encoded.Ptr, "UInt*", &charCount)
    throw OSError()
return RunWait(A_WinDir "\System32\WindowsPowerShell\v1.0\powershell.exe -NoProfile -NonInteractive -WindowStyle Hidden -EncodedCommand "
    StrGet(encoded, "UTF-16"), , "Hide")
}
