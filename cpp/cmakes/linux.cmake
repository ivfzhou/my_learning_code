# 设置编译参数
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -finput-charset=UTF-8 -fexec-charset=UTF-8 -O3 -g0")
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -finput-charset=UTF-8 -fexec-charset=UTF-8 --all-warnings -pedantic -Winline -O0 -Wall -g3 -D_GLIBCXX_DEBUG -Wno-unused-variable -Wno-unused-but-set-variable -Wno-unused-function")

# 包含其他脚本。
include(cmakes/linux/argparse.cmake)
include(cmakes/linux/bzip2.cmake)
include(cmakes/linux/cpp-httplib.cmake)
include(cmakes/linux/pugixml.cmake)
include(cmakes/linux/xz.cmake)
include(cmakes/linux/yaml-cpp.cmake)
include(cmakes/linux/zlib.cmake)
include(cmakes/linux/zstd.cmake)
include(cmakes/linux/openssl.cmake)
include(cmakes/linux/jwt-cpp.cmake)
include(cmakes/linux/libzip.cmake)

add_definitions(-DLINUX)
