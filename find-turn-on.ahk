#Requires AutoHotkey v2.0
#SingleInstance Off
#Warn All, StdOut

try {
    onceRequested := false
    for argument in A_Args {
        if argument != "--once"
            throw Error("Supported argument: --once")
        onceRequested := true
    }
    InspectPopups(onceRequested)
} catch Error as fatalError {
    try {
        LogLine("ERROR: " fatalError.Message, true)
    } catch Error as logError {
        MsgBox(fatalError.Message "`nOutput error: " logError.Message,
            "VS Code read-only inspector", "Iconx")
    }
    ExitApp(1)
}

InspectPopups(once) {
    uia := ComObject("{FF48DBA4-60EF-4201-AA87-54103EEF594E}",
        "{30CBE57D-D9D0-452A-AB13-7AC5AC4825EE}")
    condition := 0
    ComCall(21, uia, "ptr*", &condition)
    try {
        LogLine("READ ONLY. No clicks or approvals. Only fixed labels are reported, not arbitrary text.")
        LogLine("Best-effort UIA candidates, not guaranteed coverage. Ctrl+C or tray Exit to stop.")
        loop {
            matched := 0
            windows := 0
            failures := 0
            LogLine("--- " FormatTime(, "yyyy-MM-dd HH:mm:ss") " snapshot ---")
            for exe in ["Code.exe", "Code - Insiders.exe", "VSCodium.exe"] {
                for hwnd in WinGetList("ahk_exe " exe) {
                    if WinGetMinMax("ahk_id " hwnd) = -1
                        continue
                    windows += 1
                    try {
                        matched += InspectWindow(uia, condition, hwnd)
                    } catch Error as scanError {
                        failures += 1
                        LogLine("SCAN ERROR hwnd=" hwnd ": " scanError.Message, true)
                    }
                }
            }
            LogLine("windows=" windows " candidates=" matched " failed_windows=" failures)
            if once
                ExitApp(failures ? 1 : 0)
            Sleep(2000)
        }
    } finally {
        ObjRelease(condition)
    }
}

InspectWindow(uia, condition, hwnd) {
    root := 0
    ComCall(6, uia, "ptr", hwnd, "ptr*", &root)
    try {
        elements := 0
        ComCall(6, root, "int", 7, "ptr", condition, "ptr*", &elements)
        try {
            length := 0
            ComCall(3, elements, "int*", &length)
            if length > 10000
                throw Error("UIA subtree exceeds 10000 nodes; snapshot incomplete.")
            candidates := []
            buttonLabels := Map()
            loop length {
                element := 0
                ComCall(4, elements, "int", A_Index - 1, "ptr*", &element)
                try {
                    if ReadProperty(element, 30022) || ReadProperty(element, 30019)
                        continue
                    controlType := ReadProperty(element, 30003)
                    kind := ClassifyPopup(controlType,
                        ReadProperty(element, 30012), ReadProperty(element, 30101))
                    label := ""
                    if controlType = 50000 || controlType = 50011 || controlType = 50005
                        label := MatchPromptLabel(ReadProperty(element, 30005))
                    if label != "" {
                        buttonLabels[label] := true
                        candidates.Push("hwnd=" hwnd " snapshot_node=" A_Index
                            . " kind=prompt-label label=" label " control_type=" controlType
                            . " enabled=" ReadProperty(element, 30010))
                    }
                    if kind != ""
                        candidates.Push("hwnd=" hwnd " snapshot_node=" A_Index " kind=" kind)
                } finally {
                    ObjRelease(element)
                }
            }
            for candidate in candidates
                LogLine(candidate)
            summary := ""
            for label in ["Allow once", "Allow all", "Turn on"] {
                if buttonLabels.Has(label)
                    summary .= (summary = "" ? "" : " | ") label
            }
            LogLine("hwnd=" hwnd " Buttons: " (summary = "" ? "(none detected)" : summary))
            LogLine("hwnd=" hwnd " accessible_nodes=" length)
            return candidates.Length
        } finally {
            ObjRelease(elements)
        }
    } finally {
        ObjRelease(root)
    }
}

ClassifyPopup(controlType, className, role) {
    if className = "#32770"
        return "native-dialog"
    if controlType = 50009
        return "menu"
    if controlType = 50022
        return "tooltip"
    for token in StrSplit(StrLower(role), A_Space) {
        if token ~= "^(dialog|alertdialog|alert|menu|tooltip)$"
            return "role-" token
    }
    for token in StrSplit(className, A_Space) {
        if token ~= "^(notification-toast|notifications-center|monaco-dialog-box|quick-input-widget|context-view|suggest-widget|parameter-hints-widget|monaco-hover|chat-confirmation-widget-container)$"
            return token
    }
    return ""
}

MatchPromptLabel(name) {
    name := Trim(RegExReplace(StrReplace(name, "&", ""), "\s+", " "))
    for label, pattern in Map(
        "Allow once", "i)^(Allow\s+once|允许一次|仅允许一次)",
        "Allow all", "i)^(Allow\s+all|全部允许|允许所有|允许全部)",
        "Turn on", "i)^(Turn\s+on|启用|开启)"
    ) {
        if RegExMatch(name, pattern "(\s*[(（][^()（）]*[)）])?([.\x{2026}]*)$")
            return label
    }
    return ""
}

ReadProperty(element, id) {
    value := Buffer(A_PtrSize = 8 ? 24 : 16, 0)
    ComCall(10, element, "int", id, "ptr", value)
    try {
        switch NumGet(value, 0, "ushort") {
            case 0, 1: return ""
            case 3: return NumGet(value, 8, "int")
            case 8:
                address := NumGet(value, 8, "ptr")
                return address ? StrGet(address, "UTF-16") : ""
            case 11: return NumGet(value, 8, "short") != 0
            default: throw Error("Unsupported UIA property type for id " id)
        }
    } finally {
        DllCall("OleAut32\VariantClear", "ptr", value, "int")
    }
}

LogLine(text, isError := false) {
    handle := DllCall("GetStdHandle", "int", isError ? -12 : -11, "ptr")
    if handle && handle != -1 && DllCall("GetFileType", "ptr", handle, "uint") {
        FileAppend(text "`n", isError ? "**" : "*", "UTF-8")
        return
    }
    if !DllCall("GetConsoleWindow", "ptr") {
        if !DllCall("AllocConsole", "int")
            throw OSError(A_LastError, , "Cannot create output console.")
    }
    FileAppend(text "`n", "CONOUT$", "UTF-8")
}
