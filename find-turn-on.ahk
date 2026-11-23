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
^!d::Diagnose()

Diagnose(*) {
    hwnd := WinExist("A")
    path := A_Temp "\vscode-allow-" A_Now ".txt"
    name := WinGetProcessName("ahk_id " hwnd)
    cls := WinGetClass("ahk_id " hwnd)
    FileAppend("process=" name " class=" cls "`r`n", path, "UTF-8")
    EnvSet("AHK_ALLOW_REPORT", path)
    try {
        code := ScanWindows(hwnd, true)
        FileAppend("exit=" code "`r`n", path, "UTF-8")
    } catch Error as err {
        FileAppend("launch error=" err.Message "`r`n", path, "UTF-8")
    }
    Run('notepad.exe "' path '"')
}

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
            if !Mod(count, 256) && count >= 256 && count <= 25600
                ShowTip("已点击 " (count // 256) " 个确认按钮")
        }
    } finally {
        busy := false
    }
}

ScanWindows(handles, diagnostic := false) {
script := "$handles = @(" handles ")`n"
script .= "$diagnostic = " (diagnostic ? "$true" : "$false") "`n"
script .= "
(
$ErrorActionPreference = 'Stop'
try { Add-Type -AssemblyName UIAutomationClient } catch {
    if ($diagnostic) { Add-Content -LiteralPath $env:AHK_ALLOW_REPORT -Encoding UTF8 -Value $_.Exception.Message }
    exit 121
}
$class = [Windows.Automation.AutomationElement]::ClassNameProperty
$type = [Windows.Automation.AutomationElement]::ControlTypeProperty
$card = [Windows.Automation.PropertyCondition]::new($class,'chat-confirmation-widget-container')
$button = [Windows.Automation.PropertyCondition]::new($type,[Windows.Automation.ControlType]::Button)
$menuItem = [Windows.Automation.PropertyCondition]::new($type,[Windows.Automation.ControlType]::MenuItem)
$actions = [Windows.Automation.OrCondition]::new($button,$menuItem)
$allowOnce = '^(?:Allow\s+once|允许一次|仅允许一次)(?:\s*[(（][^()（）]*[)）])?$'
$allowAll = '^(?:Allow\s+all|全部允许|允许所有|允许全部)(?:\s*[(（][^()（）]*[)）])?$'
$turnOn = '^(?:Turn\s+on|开启|启用|打开)(?:\s*[(（][^()（）]*[)）])?$'
$count = 0
foreach ($hwnd in $handles) {
    try {
        $window = [Windows.Automation.AutomationElement]::FromHandle([IntPtr]$hwnd)
        if ($diagnostic) {
            $all = $window.FindAll(4,[Windows.Automation.Condition]::TrueCondition)
            $rows = @("UIA elements=" + $all.Count)
            foreach ($element in $all) {
                $state = $element.Current
                if ($state.Name -match '^\s*(allow once|allow all|turn on|允许一次|仅允许一次|全部允许|允许所有|允许全部|启用|开启)(?:\s|$|[(（])' -and $state.Name.Length -lt 100) {
                    $rows += "$($state.Name) | $($state.ControlType.ProgrammaticName) | enabled=$($state.IsEnabled) offscreen=$($state.IsOffscreen) | patterns=$(($element.GetSupportedPatterns().ProgrammaticName) -join ',')"
                }
            }
            Add-Content -LiteralPath $env:AHK_ALLOW_REPORT -Encoding UTF8 -Value $rows
            continue
        }
        $panels = $window.FindAll(4,$card)
        $groups = @($panels | ForEach-Object { $_ })
        $groups += $window
        $handledCard = $false
        foreach ($group in $groups) {
            $wholeWindow = $group -eq $window
            if ($wholeWindow -and $handledCard) { continue }
            $patterns = if ($wholeWindow) { $allowOnce,$allowAll } else { $allowOnce,$turnOn,$allowAll }
            $buttons = $group.FindAll(4,$actions)
            foreach ($pattern in $patterns) {
                $invoked = $false
                foreach ($item in $buttons) {
                    try {
                        $state = $item.Current
                        if (!$state.IsEnabled -or $state.IsOffscreen -or $state.Name.Trim() -notmatch $pattern) { continue }
                        $item.GetCurrentPattern([Windows.Automation.InvokePattern]::Pattern).Invoke()
                        $count++
                        $invoked = $true
                        if (!$wholeWindow) { $handledCard = $true }
                        break
                    } catch { continue }
                }
                if ($invoked) { break }
            }
        }
    } catch { if ($diagnostic) { Add-Content -LiteralPath $env:AHK_ALLOW_REPORT -Encoding UTF8 -Value $_.Exception.Message }; continue }
}
exit (256 * [Math]::Min($count,100))
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


