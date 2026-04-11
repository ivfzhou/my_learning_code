# 在 Linux 上读取共享库的 SONAME，并为可执行文件设置相对运行时搜索路径。
if (CMAKE_SYSTEM_NAME STREQUAL "Linux")
    find_program(READELF_EXECUTABLE readelf)
endif ()

# 获取动态库的 SONAME（库的真实名称）
# 参数：
#   LIB_PATH - 动态库文件路径
#   OUT_VAR  - 输出变量名，用于返回 SONAME
function(get_dynamic_library_soname LIB_PATH OUT_VAR)
    if (NOT CMAKE_SYSTEM_NAME STREQUAL "Linux")
        set(${OUT_VAR} "" PARENT_SCOPE)
        return()
    endif ()
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

# 复制动态库到目标编译输出目录，并将其重命名为 SONAME
# 参数：
#   TARGET_NAME - 目标可执行文件名
#   LIB_PATH    - 要复制的动态库文件路径
function(copy_dynamic_library_to_target TARGET_NAME LIB_PATH)
    get_filename_component(LIB_FILE_NAME ${LIB_PATH} NAME)
    get_dynamic_library_soname(${LIB_PATH} LIB_SONAME)

    # 如果 SONAME 存在且与文件名不同，则复制时使用 SONAME 作为目标文件名
    if (LIB_SONAME AND (NOT LIB_SONAME STREQUAL LIB_FILE_NAME))
        add_custom_command(
                TARGET ${TARGET_NAME}
                POST_BUILD
                COMMAND ${CMAKE_COMMAND} -E copy_if_different "${LIB_PATH}" "$<TARGET_FILE_DIR:${TARGET_NAME}>/${LIB_SONAME}"
        )
    else ()
        # 否则按原样复制
        add_custom_command(
                TARGET ${TARGET_NAME}
                POST_BUILD
                COMMAND ${CMAKE_COMMAND} -E copy_if_different "${LIB_PATH}" "$<TARGET_FILE_DIR:${TARGET_NAME}>/"
        )
    endif ()
endfunction()

# 安装动态库到指定目录，并将其重命名为 SONAME
# 参数：
#   LIB_PATH       - 动态库文件路径
#   DESTINATION_DIR - 安装目标目录
function(install_dynamic_library_with_soname LIB_PATH DESTINATION_DIR)
    get_filename_component(LIB_FILE_NAME ${LIB_PATH} NAME)
    get_dynamic_library_soname(${LIB_PATH} LIB_SONAME)

    # 如果 SONAME 存在且与文件名不同，则安装时重命名为 SONAME
    if (LIB_SONAME AND (NOT LIB_SONAME STREQUAL LIB_FILE_NAME))
        install(FILES ${LIB_PATH} DESTINATION ${DESTINATION_DIR} RENAME ${LIB_SONAME})
    else ()
        # 否则按原样安装
        install(FILES ${LIB_PATH} DESTINATION ${DESTINATION_DIR})
    endif ()
endfunction()
