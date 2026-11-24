#Requires AutoHotkey v2.0
#SingleInstance Force
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
            "VS Code auto-click", "Iconx")
    }
    ExitApp(1)
}

InspectPopups(once) {
    uia := ComObject("{FF48DBA4-60EF-4201-AA87-54103EEF594E}",
        "{30CBE57D-D9D0-452A-AB13-7AC5AC4825EE}")
    condition := 0
    ComCall(21, uia, "ptr*", &condition)
    try {
        LogLine("Auto-click active: Allow once, Allow in this session, Allow all, Turn on.")
        LogLine("Best-effort UIA candidates, not guaranteed coverage. Ctrl+C or tray Exit to stop.")
        loop {
            scanStart := A_TickCount
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
            Sleep(Max(1, 2000 - (A_TickCount - scanStart)))
        }
    } finally {
        ObjRelease(condition)
    }
}

InspectWindow(uia, condition, hwnd) {
    static previous := Map()
    held := []
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
                    if controlType = 50000 || controlType = 50011 || controlType = 50005 || controlType = 50031 || ReadProperty(element, 30101) = "button"
                        label := MatchPromptLabel(ReadProperty(element, 30005))
                    if label != "" {
                        buttonLabels[label] := true
                        if ReadProperty(element, 30010) && InvokeOnce(uia, element, label, previous.Get(hwnd, [])) {
                            ObjAddRef(element)
                            held.Push({ptr: element, label: label})
                        }
                        candidates.Push("hwnd=" hwnd " snapshot_node=" A_Index
                            . " kind=prompt-label label=" label " control_type=" controlType
                            . " enabled=" ReadProperty(element, 30010))
                    }
                    if RegExMatch(ReadProperty(element, 30012), "i)(chat-confirmation-widget-container|chat-question|chat-elicitation|chat-carousel)") {
                        if HandleBooleanForm(uia, element, condition)
                            return candidates.Length
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
            for label in ["Allow once", "Allow in this session", "Allow all", "Turn on"] {
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
        for item in previous.Get(hwnd, [])
            ObjRelease(item.ptr)
        previous[hwnd] := held
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
        "Allow in this session", "i)^Allow\s+in\s+this\s+session",
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
            default: return ""
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
    return
}

InvokeOnce(uia, element, label, previous) {
    try {
        for item in previous {
            if item.label = label {
                same := 0
                ComCall(3, uia, "ptr", item.ptr, "ptr", element, "int*", &same)
                if same
                    return true
            }
        }
        pattern := 0
        try ComCall(16, element, "int", 10000, "ptr*", &pattern)
        if !pattern
            return ClickVisibleButton(element, label)
        try {
            invoke := ComObjQuery(pattern, "{FB377FBE-8EA6-46D5-9C73-6499642D3059}")
            try ComCall(3, invoke)
            catch
                return ClickVisibleButton(element, label)
            LogLine("Invoked: " label)
            return true
        } finally {
            ObjRelease(pattern)
        }
    } catch Error as err {
        LogLine("Invoke failed for " label ": " err.Message, true)
        return false
    }
}
ClickVisibleButton(element, label) {
    point := Buffer(8)
    available := 0
    ComCall(84, element, "ptr", point, "int*", &available)
    if !available
        return false
    x := NumGet(point, 0, "int")
    y := NumGet(point, 4, "int")
    target := DllCall("WindowFromPoint", "int64", NumGet(point, 0, "int64"), "ptr")
    owner := DllCall("GetAncestor", "ptr", target, "uint", 2, "ptr")
    if !owner || !WinActive("ahk_id " owner)
        return false
    if !(WinGetProcessName("ahk_id " owner) ~= "i)^(Code|Code - Insiders|VSCodium)\.exe$")
        return false
    CoordMode("Mouse", "Screen")
    Click(x, y)
    LogLine("Clicked: " label)
    return true
}
HandleBooleanForm(uia, form, condition) {
    controls := BooleanControls(form, condition)
    if !controls.Has("true") || !controls.Has("false") 
        return false
    choice := controls["true"]
    state := ReadProperty(choice, 30102)
    selected := ReadProperty(choice, 30079) || ReadProperty(choice, 30086) = 1
        || RegExMatch(state, "i)(^|;)\s*(checked|selected|pressed)=true(;|$)")
    if !selected {
        pattern := 0
        try ComCall(16, choice, "int", 10010, "ptr*", &pattern)
        if pattern {
            try {
                select := ComObjQuery(pattern, "{A8EFA66A-0FDA-421A-9194-38021F3578EA}")
                ComCall(3, select)
            } finally {
                ObjRelease(pattern)
            }
        } else if !InvokeOnce(0, choice, "true", [])
            return false
    }
    ; Copilot may draw its checkmark without exposing a UIA selection state.
    loop 8 {
        Sleep(100)
        controls := BooleanControls(form, condition)
        if controls.Has("true") && controls.Has("false") {
            sameChoice := 0
            ComCall(3, uia, "ptr", choice, "ptr", controls["true"], "int*", &sameChoice)
            if !sameChoice
                return true
            for action in ["next", "submit"] {
                if controls.Has(action) && ReadProperty(controls[action], 30010)
                    return InvokeOnce(0, controls[action], action, [])
            }
        }
    }
    return false
}

BooleanControls(form, condition) {
    result := Map()
    nodes := 0
    ComCall(6, form, "int", 4, "ptr", condition, "ptr*", &nodes)
    try {
        count := 0
        ComCall(3, nodes, "int*", &count)
        loop count {
            ptr := 0
            ComCall(4, nodes, "int", A_Index - 1, "ptr*", &ptr)
            node := ComValue(13, ptr, 1)
            if ReadProperty(node, 30022)
                continue
            type := ReadProperty(node, 30003)
            className := ReadProperty(node, 30012)
            if !(className ~= "(^|\s)chat-question-(list-item|submit-button|next-button|nav-next)(\s|$)" || type ~= "^(50000|50002|50005|50007|50011|50013|50019|50024)$")
                continue
            name := StrLower(Trim(ReadProperty(node, 30005)))
            name := RegExReplace(name, "i)^\s*(?:option\s*|选项\s*|選項\s*)?[12][.、):：\s]+", "")
            if name = "yes"
                name := "true"
            else if name = "no"
                name := "false"
            if name ~= "^(submit|提交)(答案|回答| answers?)?(\s*[(（].*?[)）])?$"
                name := "submit"
            else if className ~= "(^|\s)chat-question-nav-next(\s|$)" || name ~= "^(next(?: question| step)?|下一步|下一题|下一題)(\s*[(（].*?[)）])?$"
                name := "next"
            if !(name ~= "^(true|false|submit|next)$")
                continue
            if name = "next" || name = "submit" {
                if !ReadProperty(node, 30010) || result.Has(name)
                    continue
            }
            if result.Has(name)
                return Map()
            result[name] := node
        }
    } finally {
        ObjRelease(nodes)
    }
    return result
}
