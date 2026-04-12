# 使用 dumpbin 读取 DLL 内部名称。
# MSVC 自带 dumpbin，通常位于 VC 工具链目录下
find_program(DUMPBIN_EXECUTABLE dumpbin PATHS "$ENV{VCINSTALLDIR}/Tools/MSVC" "$ENV{PATH}")

# 获取 DLL 的内部名称。
function(get_dynamic_library_soname LIB_PATH OUT_VAR)
    # 如果库文件不存在，则返回空值（库可能在编译阶段才生成）
    if (NOT EXISTS ${LIB_PATH})
        set(${OUT_VAR} "" PARENT_SCOPE)
        return()
    endif ()
    
    if (NOT DUMPBIN_EXECUTABLE)
        set(${OUT_VAR} "" PARENT_SCOPE)
        return()
    endif ()
    
    execute_process(
            COMMAND ${DUMPBIN_EXECUTABLE} /HEADERS ${LIB_PATH}
            OUTPUT_VARIABLE LIB_DUMPBIN_OUTPUT
            RESULT_VARIABLE LIB_DUMPBIN_RESULT
            OUTPUT_STRIP_TRAILING_WHITESPACE
            ERROR_QUIET
    )
    if (NOT LIB_DUMPBIN_RESULT EQUAL 0)
        set(${OUT_VAR} "" PARENT_SCOPE)
        return()
    endif ()
    # 匹配 "           0        name : xxx.dll"
    string(REGEX MATCH "name[ \t]*:[ \t]*([^ \r\n]+\\.dll)" LIB_DLL_NAME_MATCH "${LIB_DUMPBIN_OUTPUT}")
    if (LIB_DLL_NAME_MATCH)
        set(${OUT_VAR} "${CMAKE_MATCH_1}" PARENT_SCOPE)
    else ()
        set(${OUT_VAR} "" PARENT_SCOPE)
    endif ()
endfunction()
