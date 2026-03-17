# 设置版本号、头文件名、依赖库名称。
set(ZLIB_VERSION v1.3.2)
set(ZLIB_HEADER_NAME zlib.h)
set(ZLIB_LIBRARY_NAME zs.lib)
if (CMAKE_BUILD_TYPE STREQUAL "Debug")
    set(ZLIB_LIBRARY_NAME zsd.lib)
endif ()

# 设置依赖安装目录。
set(ZLIB_DIRECTORY ${DEPENDENCIES_DIRECTORY}/zlib)
file(MAKE_DIRECTORY ${ZLIB_DIRECTORY})
set(ZLIB_BUILD_DIRECTORY ${ZLIB_DIRECTORY}/build)
set(ZLIB_SOURCE_DIRECTORY ${ZLIB_DIRECTORY}/source)
set(ZLIB_INSTALL_DIRECTORY ${ZLIB_DIRECTORY}/install)

# 设置依赖头文件路径。
find_path(
        ZLIB_INCLUDE_DIRECTORY
        NAMES ${ZLIB_HEADER_NAME}
        PATHS ${ZLIB_INSTALL_DIRECTORY}/include
        NO_DEFAULT_PATH
)

# 设置依赖库路径。
find_library(
        ZLIB_LIBRARY
        NAMES ${ZLIB_LIBRARY_NAME}
        PATHS ${ZLIB_INSTALL_DIRECTORY}/lib
        NO_DEFAULT_PATH
)

# 如果找到依赖库和头文件，则设置依赖库和头文件的路径。
if (ZLIB_INCLUDE_DIRECTORY AND ZLIB_LIBRARY)
    message(STATUS "found zlib include directory: ${ZLIB_INCLUDE_DIRECTORY}")
    message(STATUS "found zlib library: ${ZLIB_LIBRARY}")
else ()
    include(ExternalProject)
    ExternalProject_Add(
            zlib
            PREFIX ${ZLIB_DIRECTORY}
            URL https://github.com/madler/zlib/archive/refs/tags/${ZLIB_VERSION}.zip
            SOURCE_DIR ${ZLIB_SOURCE_DIRECTORY}
            BINARY_DIR ${ZLIB_BUILD_DIRECTORY}
            CONFIGURE_COMMAND ${CMAKE_COMMAND} --fresh -S ${ZLIB_SOURCE_DIRECTORY} -B ${ZLIB_BUILD_DIRECTORY}
                -DCMAKE_INSTALL_PREFIX=${ZLIB_INSTALL_DIRECTORY}
                -DCMAKE_CONFIGURATION_TYPES=${CMAKE_BUILD_TYPE}
                -DCMAKE_MSVC_RUNTIME_LIBRARY=${CMAKE_MSVC_RUNTIME_LIBRARY}
                -DCMAKE_C_FLAGS=${COMPILER_FLAGS}
                -DZLIB_BUILD_TESTING=OFF
                -DZLIB_BUILD_SHARED=OFF
                -DZLIB_BUILD_MINIZIP=OFF
            BUILD_COMMAND ${CMAKE_COMMAND} --build ${ZLIB_BUILD_DIRECTORY} --config ${CMAKE_BUILD_TYPE} --parallel --clean-first
            INSTALL_COMMAND ${CMAKE_COMMAND} --build ${ZLIB_BUILD_DIRECTORY} --config ${CMAKE_BUILD_TYPE} --target install
    )
    set(ZLIB_INCLUDE_DIRECTORY ${ZLIB_INSTALL_DIRECTORY}/include)
    set(ZLIB_LIBRARY ${ZLIB_INSTALL_DIRECTORY}/lib/${ZLIB_LIBRARY_NAME})
endif ()

include_directories(${ZLIB_INCLUDE_DIRECTORY})
list(APPEND LIBRARIES ${ZLIB_LIBRARY})
