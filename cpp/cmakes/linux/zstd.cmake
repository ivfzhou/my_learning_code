# 设置版本号、头文件名、依赖库名称。
set(ZSTD_VERSION v1.5.7)
set(ZSTD_HEADER_NAME zstd.h)
set(ZSTD_LIBRARY_NAME libzstd.a)

# 设置依赖安装目录。
set(ZSTD_DIRECTORY ${DEPENDENCIES_DIRECTORY}/zstd)
file(MAKE_DIRECTORY ${ZSTD_DIRECTORY})
set(ZSTD_BUILD_DIRECTORY ${ZSTD_DIRECTORY}/build)
set(ZSTD_SOURCE_DIRECTORY ${ZSTD_DIRECTORY}/source)
set(ZSTD_INSTALL_DIRECTORY ${ZSTD_DIRECTORY}/install)

# 设置依赖库路径。
find_library(
        ZSTD_LIBRARY
        NAMES ${ZSTD_LIBRARY_NAME}
        PATHS ${ZSTD_INSTALL_DIRECTORY}/lib
        NO_DEFAULT_PATH
)

# 设置依赖头文件路径。
find_path(
        ZSTD_INCLUDE_DIRECTORY
        NAMES ${ZSTD_HEADER_NAME}
        PATHS ${ZSTD_INSTALL_DIRECTORY}/include
        NO_DEFAULT_PATH
)

# 如果找到依赖库和头文件，则设置依赖库和头文件的路径。
if (ZSTD_LIBRARY AND ZSTD_INCLUDE_DIRECTORY)
    message(STATUS "found zstd library: ${ZSTD_LIBRARY}")
    message(STATUS "found zstd include directory: ${ZSTD_INCLUDE_DIRECTORY}")
else ()
    include(ExternalProject)
    ExternalProject_Add(
            zstd
            PREFIX ${ZSTD_DIRECTORY}
            URL https://github.com/facebook/zstd/archive/refs/tags/${ZSTD_VERSION}.zip
            SOURCE_DIR ${ZSTD_SOURCE_DIRECTORY}
            BINARY_DIR ${ZSTD_BUILD_DIRECTORY}
            CONFIGURE_COMMAND ${CMAKE_COMMAND} --fresh -S ${ZSTD_SOURCE_DIRECTORY}/build/cmake -B ${ZSTD_BUILD_DIRECTORY}
                -DCMAKE_INSTALL_PREFIX=${ZSTD_INSTALL_DIRECTORY}
                -DCMAKE_BUILD_TYPE=${CMAKE_BUILD_TYPE}
                -DCMAKE_CXX_FLAGS=${LIBRARY_COMPILE_CXX_FLAGS}
                -DCMAKE_CXX_FLAGS_DEBUG=${LIBRARY_COMPILE_CXX_FLAGS_DEBUG}
                -DCMAKE_CXX_FLAGS_RELEASE=${LIBRARY_COMPILE_CXX_FLAGS_RELEASE}
                -DCMAKE_C_FLAGS=${LIBRARY_COMPILE_C_FLAGS}
                -DCMAKE_C_FLAGS_DEBUG=${LIBRARY_COMPILE_C_FLAGS_DEBUG}
                -DCMAKE_C_FLAGS_RELEASE=${LIBRARY_COMPILE_C_FLAGS_RELEASE}
                -DZSTD_BUILD_TESTS=OFF
            BUILD_COMMAND ${CMAKE_COMMAND} --build ${ZSTD_BUILD_DIRECTORY} --config ${CMAKE_BUILD_TYPE} --parallel --clean-first
            INSTALL_COMMAND ${CMAKE_COMMAND} --build ${ZSTD_BUILD_DIRECTORY} --config ${CMAKE_BUILD_TYPE} --target install
    )
    set(ZSTD_LIBRARY ${ZSTD_INSTALL_DIRECTORY}/lib/${ZSTD_LIBRARY_NAME})
    set(ZSTD_INCLUDE_DIRECTORY ${ZSTD_INSTALL_DIRECTORY}/include)
endif ()

include_directories(${ZSTD_INCLUDE_DIRECTORY})
list(APPEND LIBRARIES ${ZSTD_LIBRARY})
