if (CMAKE_SYSTEM_NAME STREQUAL "Windows")
    include(${CMAKE_SOURCE_DIR}/cmakes/common_windows.cmake)
elseif (CMAKE_SYSTEM_NAME STREQUAL "Linux")
    include(${CMAKE_SOURCE_DIR}/cmakes/common_linux.cmake)
endif ()

# 跨平台动态库辅助函数：复制/安装动态库（平台无关逻辑）。
# 平台相关的 get_dynamic_library_soname() 定义在：
#   - cmakes/linux/common.cmake  (readelf / SONAME)
#   - cmakes/windows/common.cmake (dumpbin / DLL 内部名)

# 复制动态库到目标编译输出目录，并将其重命名为内部名称
# 参数：
#   TARGET_NAME - 目标可执行文件名
#   LIB_PATH    - 要复制的动态库文件路径
function(copy_dynamic_library_to_target TARGET_NAME LIB_PATH)
    get_filename_component(LIB_FILE_NAME ${LIB_PATH} NAME)
    get_dynamic_library_soname(${LIB_PATH} LIB_SONAME)

    # 如果内部名称存在且与文件名不同，则复制时使用内部名称作为目标文件名
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

# 安装动态库到指定目录，并将其重命名为内部名称
# 参数：
#   LIB_PATH       - 动态库文件路径
#   DESTINATION_DIR - 安装目标目录
function(install_dynamic_library_with_soname LIB_PATH DESTINATION_DIR)
    get_filename_component(LIB_FILE_NAME ${LIB_PATH} NAME)
    get_dynamic_library_soname(${LIB_PATH} LIB_SONAME)

    # 如果内部名称存在且与文件名不同，则安装时重命名
    if (LIB_SONAME AND (NOT LIB_SONAME STREQUAL LIB_FILE_NAME))
        install(FILES ${LIB_PATH} DESTINATION ${DESTINATION_DIR} RENAME ${LIB_SONAME})
    else ()
        # 否则按原样安装
        install(FILES ${LIB_PATH} DESTINATION ${DESTINATION_DIR})
    endif ()
endfunction()
