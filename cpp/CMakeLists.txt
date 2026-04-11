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

# 定义依赖版本信息。
set(ARGPARSE_URL https://github.com/p-ranav/argparse/archive/refs/tags/v3.2.zip)
set(ARGPARSE_SHA256 14c1a0e975d6877dfeaf52a1e79e54f70169a847e29c7e13aa7fe68a3d0ecbf1)
set(BZIP2_URL https://github.com/libarchive/bzip2/archive/1ea1ac188ad4b9cb662e3f8314673c63df95a589.zip)
set(BZIP2_SHA256 b803cdb2d044a53bde3b89cbbe811486035c885994e75f2879116ca7be0f613b)
set(CPP_HTTPLIB_URL https://github.com/yhirose/cpp-httplib/archive/refs/tags/v0.20.0.zip)
set(CPP_HTTPLIB_SHA256 0bf6bae6d95c5b0ea34ebd9be81005412009b56785dbca1978872090fd769d36)
set(JWT_CPP_URL https://github.com/Thalhammer/jwt-cpp/archive/refs/tags/v0.7.2.zip)
set(JWT_CPP_SHA256 1d288deca2252183aa927f135b83a312882d1a69732f6789c066a868c27681a4)
set(LIBZIP_URL https://github.com/nih-at/libzip/archive/refs/tags/v1.11.4.zip)
set(LIBZIP_SHA256 3f78eaac85d98d5ac41cc640834dafba7257eb81989854aeb456ed91a177d7c2)
set(OPENSSL_URL https://github.com/openssl/openssl/archive/refs/tags/openssl-3.6.1.zip)
set(OPENSSL_SHA256 b5fb172237ed3b1b47a9f7f15d3a40f9e9563f59f544b7078780ee27279a3c0f)
set(PUGIXML_URL https://github.com/zeux/pugixml/archive/refs/tags/v1.15.zip)
set(PUGIXML_SHA256 0dd4dc16e38bbc1c889d3a59660072450127bac7d19415f3cf5d5d6b25c58b35)
set(XZ_URL https://github.com/tukaani-project/xz/archive/refs/tags/v5.8.2.zip)
set(XZ_SHA256 f31af924df81d645720eee558a00394d3f8a6c59b97c315a541a6598a9aea3d2)
set(YAML_CPP_URL https://github.com/jbeder/yaml-cpp/archive/refs/tags/yaml-cpp-0.9.0.zip)
set(YAML_CPP_SHA256 1c22709eb1fcde200c87ef4e878ce2c9477cc05eae84ebf1f72ec5b356468fee)
set(ZLIB_URL https://github.com/madler/zlib/archive/refs/tags/v1.3.2.zip)
set(ZLIB_SHA256 31fd9fee98812abcf147d0e103bc4d2f983c35a8d7a807a328a299f3a74e0050)
set(ZSTD_URL https://github.com/facebook/zstd/archive/refs/tags/v1.5.7.zip)
set(ZSTD_SHA256 7897bc5d620580d9b7cd3539c44b59d78f3657d33663fe97a145e07b4ebd69a4)

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
file(TO_CMAKE_PATH "${DEPENDENCIES_DIRECTORY}" DEPENDENCIES_DIRECTORY)
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
include(${CMAKE_SOURCE_DIR}/cmakes/common.cmake)
if (CMAKE_SYSTEM_NAME STREQUAL "Windows")
    include(${CMAKE_SOURCE_DIR}/cmakes/windows.cmake)
elseif (CMAKE_SYSTEM_NAME STREQUAL "Linux")
    include(${CMAKE_SOURCE_DIR}/cmakes/linux.cmake)
else ()
    message(FATAL_ERROR "unsupported operating system: ${CMAKE_SYSTEM_NAME}")
endif ()

# 被依赖者要放后面。
list(REVERSE LIBRARIES)

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
