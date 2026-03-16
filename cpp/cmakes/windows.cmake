# 设置编译参数。
# MT：静态链接运行时库（Release 模式）
# MTd：静态链接运行时库（Debug 模式）
# MD：动态链接运行时库（Release 模式）
# MDd：动态链接运行时库（Debug 模式）
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} /utf-8 /MT")
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} /utf-8 /MTd")
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} /utf-8 /MT")
set(COMPILER_FLAGS_RELEASE "/MT")
set(COMPILER_FLAGS_DEBUG "/MTd")
set(CMAKE_MSVC_RUNTIME_LIBRARY MultiThreaded)
if(CMAKE_BUILD_TYPE STREQUAL "Debug")
    set(CMAKE_MSVC_RUNTIME_LIBRARY MultiThreadedDebug)
endif()

# 添加依赖。
include(cmakes/windows/argparse.cmake)
include(cmakes/windows/bzip2.cmake)
include(cmakes/windows/cpp-httplib.cmake)
include(cmakes/windows/pugixml.cmake)
include(cmakes/windows/xz.cmake)
include(cmakes/windows/yaml-cpp.cmake)
include(cmakes/windows/zlib.cmake)
include(cmakes/windows/zstd.cmake)
include(cmakes/windows/openssl.cmake)
include(cmakes/windows/jwt-cpp.cmake)
include(cmakes/windows/libzip.cmake)

add_definitions(-DWINDOWS)
