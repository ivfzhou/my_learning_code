# 设置版本号、头文件路径。
set(ARGPARSE_VERSION v3.2)
set(ARGPARSE_HEADER_NAME argparse/argparse.hpp)

# 设置依赖安装目录。
set(ARGPARSE_DIRECTORY ${DEPENDENCIES_DIRECTORY}/argparse)
file(MAKE_DIRECTORY ${ARGPARSE_DIRECTORY})
set(ARGPARSE_BUILD_DIRECTORY ${ARGPARSE_DIRECTORY}/build)
set(ARGPARSE_SOURCE_DIRECTORY ${ARGPARSE_DIRECTORY}/source)
set(ARGPARSE_INSTALL_DIRECTORY ${ARGPARSE_DIRECTORY}/install)

# 设置依赖头文件路径。
find_path(
        ARGPARSE_INCLUDE_DIRECTORY
        NAMES ${ARGPARSE_HEADER_NAME}
        PATHS ${ARGPARSE_INSTALL_DIRECTORY}/include
        NO_DEFAULT_PATH
)

# 如果找到依赖库和头文件，则设置依赖库和头文件的路径。
if (ARGPARSE_INCLUDE_DIRECTORY)
    message(STATUS "found argparse include directory: ${ARGPARSE_INCLUDE_DIRECTORY}")
else ()
    include(ExternalProject)
    ExternalProject_Add(
            argparse
            PREFIX ${ARGPARSE_DIRECTORY}
            URL https://github.com/p-ranav/argparse/archive/refs/tags/${ARGPARSE_VERSION}.zip
            SOURCE_DIR ${ARGPARSE_SOURCE_DIRECTORY}
            BINARY_DIR ${ARGPARSE_BUILD_DIRECTORY}
            CONFIGURE_COMMAND ${CMAKE_COMMAND} --fresh -S ${ARGPARSE_SOURCE_DIRECTORY} -B ${ARGPARSE_BUILD_DIRECTORY}
                -DCMAKE_INSTALL_PREFIX=${ARGPARSE_INSTALL_DIRECTORY}
                -DCMAKE_CONFIGURATION_TYPES=${CMAKE_BUILD_TYPE}
                -DCMAKE_MSVC_RUNTIME_LIBRARY=${CMAKE_MSVC_RUNTIME_LIBRARY}
                -DCMAKE_CXX_FLAGS=${COMPILER_FLAGS}
                -DARGPARSE_BUILD_TESTS=OFF
            BUILD_COMMAND ${CMAKE_COMMAND} --build ${ARGPARSE_BUILD_DIRECTORY} --parallel --config ${CMAKE_BUILD_TYPE} --clean-first
            INSTALL_COMMAND ${CMAKE_COMMAND} --build ${ARGPARSE_BUILD_DIRECTORY} --config ${CMAKE_BUILD_TYPE} --target install
    )
    set(ARGPARSE_INCLUDE_DIRECTORY ${ARGPARSE_INSTALL_DIRECTORY}/include)
endif ()

include_directories(${ARGPARSE_INCLUDE_DIRECTORY})
