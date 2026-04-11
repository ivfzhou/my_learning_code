# Linux: 使用 readelf 读取共享库 SONAME。

find_program(READELF_EXECUTABLE readelf)

# 获取动态库的 SONAME（库的真实名称）
# 参数：
#   LIB_PATH - 动态库文件路径
#   OUT_VAR  - 输出变量名，用于返回 SONAME
function(get_dynamic_library_soname LIB_PATH OUT_VAR)
    if (NOT READELF_EXECUTABLE)
        set(${OUT_VAR} "" PARENT_SCOPE)
        return()
    endif ()
    execute_process(
            COMMAND ${READELF_EXECUTABLE} -d ${LIB_PATH}
            OUTPUT_VARIABLE LIB_DYNAMIC_SECTION
            RESULT_VARIABLE LIB_READELF_RESULT
            OUTPUT_STRIP_TRAILING_WHITESPACE
    )
    if (NOT LIB_READELF_RESULT EQUAL 0)
        set(${OUT_VAR} "" PARENT_SCOPE)
        return()
    endif ()
    string(REGEX MATCH "SONAME[^[]*\\[([^]]+)\\]" LIB_SONAME_MATCH "${LIB_DYNAMIC_SECTION}")
    if (LIB_SONAME_MATCH)
        set(${OUT_VAR} "${CMAKE_MATCH_1}" PARENT_SCOPE)
    else ()
        set(${OUT_VAR} "" PARENT_SCOPE)
    endif ()
endfunction()
