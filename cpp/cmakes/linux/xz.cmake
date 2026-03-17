# 设置版本号、头文件名、依赖库名称。
set(XZ_VERSION v5.8.2)
set(XZ_HEADER_NAME lzma.h)
set(XZ_LIBRARY_NAME liblzma.a)

# 设置依赖安装目录。
set(XZ_DIRECTORY ${DEPENDENCIES_DIRECTORY}/xz)
file(MAKE_DIRECTORY ${XZ_DIRECTORY})
set(XZ_BUILD_DIRECTORY ${XZ_DIRECTORY}/build)
set(XZ_SOURCE_DIRECTORY ${XZ_DIRECTORY}/source)
set(XZ_INSTALL_DIRECTORY ${XZ_DIRECTORY}/install)

# 设置依赖库路径。
find_library(
        XZ_LIBRARY
        NAMES ${XZ_LIBRARY_NAME}
        PATHS ${XZ_INSTALL_DIRECTORY}/lib
        NO_DEFAULT_PATH
)

# 设置依赖头文件路径。
find_path(
        XZ_INCLUDE_DIRECTORY
        NAMES ${XZ_HEADER_NAME}
        PATHS ${XZ_INSTALL_DIRECTORY}/include
        NO_DEFAULT_PATH
)

# 如果找到依赖库和头文件，则设置依赖库和头文件的路径。
if (XZ_LIBRARY AND XZ_INCLUDE_DIRECTORY)
    message(STATUS "found xz library: ${XZ_LIBRARY}")
    message(STATUS "found xz include directory: ${XZ_INCLUDE_DIRECTORY}")
else ()
    include(ExternalProject)
    ExternalProject_Add(
            xz
            PREFIX ${XZ_DIRECTORY}
            URL https://github.com/tukaani-project/xz/archive/refs/tags/${XZ_VERSION}.zip
            SOURCE_DIR ${XZ_SOURCE_DIRECTORY}
            BINARY_DIR ${XZ_BUILD_DIRECTORY}
            CONFIGURE_COMMAND ${CMAKE_COMMAND} --fresh -S ${XZ_SOURCE_DIRECTORY} -B ${XZ_BUILD_DIRECTORY}
                -DCMAKE_INSTALL_PREFIX=${XZ_INSTALL_DIRECTORY}
                -DCMAKE_BUILD_TYPE=${CMAKE_BUILD_TYPE}
                -DCMAKE_C_FLAGS=${COMPILER_FLAGS}
                -DXZ_DOC=OFF
            BUILD_COMMAND ${CMAKE_COMMAND} --build ${XZ_BUILD_DIRECTORY} --config ${CMAKE_BUILD_TYPE} --parallel --clean-first
            INSTALL_COMMAND ${CMAKE_COMMAND} --build ${XZ_BUILD_DIRECTORY} --config ${CMAKE_BUILD_TYPE} --target install
    )
    set(XZ_LIBRARY ${XZ_INSTALL_DIRECTORY}/lib/${XZ_LIBRARY_NAME})
    set(XZ_INCLUDE_DIRECTORY ${XZ_INSTALL_DIRECTORY}/include)
endif ()

include_directories(${XZ_INCLUDE_DIRECTORY})
list(APPEND LIBRARIES ${XZ_LIBRARY})
