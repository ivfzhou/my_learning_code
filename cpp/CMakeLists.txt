cmake_minimum_required(VERSION 3.25)

project(cpp LANGUAGES CXX VERSION 0.0.1)

# 设置语言版本。
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# 打印变量信息。
message("打印变量信息：")
message("PROJECT_NAME is ${PROJECT_NAME}")
message("CMAKE_SYSTEM_NAME is ${CMAKE_SYSTEM_NAME}")
message("CMAKE_SOURCE_DIR is ${CMAKE_SOURCE_DIR}")
message("CMAKE_BINARY_DIR is ${CMAKE_BINARY_DIR}")
message("CMAKE_CURRENT_BINARY_DIR is ${CMAKE_CURRENT_BINARY_DIR}")
message("CMAKE_SIZEOF_VOID_P is ${CMAKE_SIZEOF_VOID_P}")
message("CMAKE_COMMAND is ${CMAKE_COMMAND}")
message("CMAKE_BUILD_TYPE is ${CMAKE_BUILD_TYPE}")
message("CMAKE_THREAD_PREFER_PTHREAD is ${CMAKE_THREAD_PREFER_PTHREAD}")
message("CMAKE_CXX_COMPILER is ${CMAKE_CXX_COMPILER}")
message("CMAKE_CXX_COMPILER_VERSION is ${CMAKE_CXX_COMPILER_VERSION}")
message("CMAKE_CXX_FLAGS is ${CMAKE_CXX_FLAGS}")
message("CMAKE_CXX_FLAGS_DEBUG is ${CMAKE_CXX_FLAGS_DEBUG}")
message("CMAKE_CXX_FLAGS_RELEASE is ${CMAKE_CXX_FLAGS_RELEASE}")
message("CMAKE_C_COMPILER is ${CMAKE_C_COMPILER}")
message("CMAKE_C_FLAGS is ${CMAKE_C_FLAGS}")
message("CMAKE_C_FLAGS_DEBUG is ${CMAKE_C_FLAGS_DEBUG}")
message("CMAKE_C_FLAGS_RELEASE is ${CMAKE_C_FLAGS_RELEASE}")
message("THREADS_PREFER_PTHREAD_FLAG is ${THREADS_PREFER_PTHREAD_FLAG}")
message("MSVC is ${MSVC}")
message("CMAKE_MSVC_RUNTIME_LIBRARY is ${CMAKE_MSVC_RUNTIME_LIBRARY}")
message("CMAKE_HOST_SYSTEM_PROCESSOR is ${CMAKE_HOST_SYSTEM_PROCESSOR}")
message("CMAKE_SYSTEM_PROCESSOR is ${CMAKE_SYSTEM_PROCESSOR}")
message("CMAKE_RUNTIME_OUTPUT_DIRECTORY is ${CMAKE_RUNTIME_OUTPUT_DIRECTORY}")

# 定义全局变量。
set(SOURCES)
set(HEADERS)
set(DEPENDENCIES)
set(DEFINITIONS)
set(DYNAMIC_LIBRARIES)
set(LIBRARIES)
set(INCLUDES ${CMAKE_SOURCE_DIR})

# 添加版本信息宏。
string(TIMESTAMP DATETIME "%Y%m%d%H%M%S")
list(APPEND DEFINITIONS "-DBUILD_DATETIME=\"${DATETIME}\"")
unset(DATETIME)
if (NOT DEFINED PROJECT_VERSION)
    list(APPEND DEFINITIONS "-DBUILD_VERSION=\"unknown\"")
else ()
    list(APPEND DEFINITIONS "-DBUILD_VERSION=\"${PROJECT_VERSION}\"")
endif ()
execute_process(
        COMMAND git rev-parse --short HEAD
        WORKING_DIRECTORY ${CMAKE_SOURCE_DIR}
        OUTPUT_VARIABLE GIT_COMMIT_ID
        OUTPUT_STRIP_TRAILING_WHITESPACE
        ERROR_QUIET
)
if (NOT DEFINED GIT_COMMIT_ID OR GIT_COMMIT_ID STREQUAL "")
    list(APPEND DEFINITIONS "-DGIT_COMMIT_ID=\"unknown\"")
else ()
    list(APPEND DEFINITIONS "-DGIT_COMMIT_ID=\"${GIT_COMMIT_ID}\"")
    unset(GIT_COMMIT_ID)
endif ()

# 设置构建类型。
if (NOT DEFINED CMAKE_BUILD_TYPE)
    set(CMAKE_BUILD_TYPE "Debug")
elseif ((NOT CMAKE_BUILD_TYPE STREQUAL "Debug") AND (NOT CMAKE_BUILD_TYPE STREQUAL "Release"))
    set(CMAKE_BUILD_TYPE "Debug")
endif ()
message("Actual CMAKE_BUILD_TYPE is ${CMAKE_BUILD_TYPE}")

# 是否编译成动态库。
option(COMPILE_DYNAMIC_MODE "compile dynamic mode" OFF)
if (COMPILE_DYNAMIC_MODE)
    list(APPEND DEFINITIONS "-DCOMPILE_DYNAMIC_MODE")
    message("compile dynamic mode is on")
endif ()

# 设置依赖编译安装路径。
if (NOT DEFINED DEPENDENCIES_PREFIX)
    set(DEPENDENCIES_PREFIX ${CMAKE_SOURCE_DIR})
endif ()
math(EXPR BIT_SIZE "${CMAKE_SIZEOF_VOID_P} * 8")
if (COMPILE_DYNAMIC_MODE)
    set(DEPENDENCIES_DIRECTORY ${DEPENDENCIES_PREFIX}/dependencies/${CMAKE_SYSTEM_NAME}_${CMAKE_SYSTEM_PROCESSOR}_${CMAKE_BUILD_TYPE}_${BIT_SIZE}_Dynamic)
else ()
    set(DEPENDENCIES_DIRECTORY ${DEPENDENCIES_PREFIX}/dependencies/${CMAKE_SYSTEM_NAME}_${CMAKE_SYSTEM_PROCESSOR}_${CMAKE_BUILD_TYPE}_${BIT_SIZE}_Static)
endif ()
file(MAKE_DIRECTORY ${DEPENDENCIES_DIRECTORY})

# 设置源代码文件（排除测试文件）。
file(GLOB SOURCES
        ${CMAKE_SOURCE_DIR}/*.cpp
        ${CMAKE_SOURCE_DIR}/inheritance_usage/*.cpp
        ${CMAKE_SOURCE_DIR}/template_usage/*.cpp
        ${CMAKE_SOURCE_DIR}/yaml-cpp/*.cpp
        ${CMAKE_SOURCE_DIR}/pugixml/*.cpp
        ${CMAKE_SOURCE_DIR}/http_usage/*.cpp
        ${CMAKE_SOURCE_DIR}/jwt_usage/*.cpp
        ${CMAKE_SOURCE_DIR}/zip_usage/*.cpp
)

# 设置头文件。
file(GLOB HEADERS
        ${CMAKE_SOURCE_DIR}/*.hpp
        ${CMAKE_SOURCE_DIR}/inheritance_usage/*.hpp
        ${CMAKE_SOURCE_DIR}/template_usage/*.hpp
        ${CMAKE_SOURCE_DIR}/yaml-cpp/*.hpp
        ${CMAKE_SOURCE_DIR}/pugixml/*.hpp
        ${CMAKE_SOURCE_DIR}/http_usage/*.hpp
        ${CMAKE_SOURCE_DIR}/jwt_usage/*.hpp
        ${CMAKE_SOURCE_DIR}/zip_usage/*.hpp
        ${CMAKE_SOURCE_DIR}/*.tpp
        ${CMAKE_SOURCE_DIR}/template_usage/*.tpp
        ${CMAKE_SOURCE_DIR}/inheritance_usage/*.tpp
)

# 包含其它脚本。
include(cmakes/common.cmake)
if (CMAKE_SYSTEM_NAME STREQUAL "Windows")
    include(cmakes/windows.cmake)
elseif (CMAKE_SYSTEM_NAME STREQUAL "Linux")
    include(cmakes/linux.cmake)
else ()
    message(FATAL_ERROR "unsupported operating system: ${CMAKE_SYSTEM_NAME}")
endif ()

# 打印全局变量信息。
message("SOURCES is ${SOURCES}")
message("HEADERS is ${HEADERS}")
message("DEPENDENCIES is ${DEPENDENCIES}")
message("DEFINITIONS is ${DEFINITIONS}")
message("DYNAMIC_LIBRARIES is ${DYNAMIC_LIBRARIES}")
message("LIBRARIES is ${LIBRARIES}")
message("INCLUDES is ${INCLUDES}")

# 设置可执行程序目标。
add_executable(${PROJECT_NAME} ${SOURCES})
#foreach(v ${SOURCES})
#    get_filename_component(FILE_NAME ${v} NAME_WE)
#    add_library(${FILE_NAME} OBJECT ${v})
#    target_sources(${PROJECT_NAME} PRIVATE $<TARGET_OBJECTS:${FILE_NAME}>)
#endforeach()

# 添加依赖。
foreach (v ${DEPENDENCIES})
    add_dependencies(${PROJECT_NAME} ${v})
endforeach ()

# 配置可执行程序目标。
target_link_libraries(${PROJECT_NAME} ${LIBRARIES})
target_include_directories(${PROJECT_NAME} PRIVATE ${INCLUDES})
target_compile_definitions(${PROJECT_NAME} PRIVATE ${DEFINITIONS})
set_target_properties(
        ${PROJECT_NAME}
        PROPERTIES
        BUILD_RPATH "\$ORIGIN"
        INSTALL_RPATH "\$ORIGIN"
)

# 复制动态链接库文件到可执行程序目标。
if (COMPILE_DYNAMIC_MODE)
    foreach (v ${DYNAMIC_LIBRARIES})
        copy_dynamic_library_to_target(${PROJECT_NAME} ${v})
    endforeach ()
endif ()

# 安装可执行程序目标。
install(TARGETS ${PROJECT_NAME} RUNTIME DESTINATION bin)

# 安装工程中的头文件到 include 目录，并保留相对目录结构。
foreach (HEADER ${HEADERS})
    file(RELATIVE_PATH HEADER_RELATIVE_PATH ${CMAKE_SOURCE_DIR} ${HEADER})
    get_filename_component(HEADER_DIR ${HEADER_RELATIVE_PATH} DIRECTORY)
    if (HEADER_DIR STREQUAL "")
        install(FILES ${HEADER} DESTINATION include)
    else ()
        install(FILES ${HEADER} DESTINATION include/${HEADER_DIR})
    endif ()
endforeach ()

# 安装动态链接库。
if (COMPILE_DYNAMIC_MODE)
    foreach (v ${DYNAMIC_LIBRARIES})
        install_dynamic_library_with_soname(${v} bin)
    endforeach ()
endif ()
