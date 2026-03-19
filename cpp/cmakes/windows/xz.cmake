# 设置版本号、头文件名、代码库名称等。
set(XZ_VERSION v5.8.2)
set(XZ_HEADER_NAME lzma.h)
set(XZ_LIBRARY_NAME lzma.lib)
set(XZ_DYNAMIC_LIBRARY_NAME liblzma.dll)
set(XZ_DIRECTORY ${DEPENDENCIES_DIRECTORY}/xz)
set(XZ_INSTALL_DIRECTORY ${XZ_DIRECTORY}/install)
set(XZ_LIBRARY_DIRECTORY ${XZ_INSTALL_DIRECTORY}/lib)
set(XZ_HEADERS_DIRECTORY ${XZ_INSTALL_DIRECTORY}/include)
set(XZ_BINARY_DIRECTORY ${XZ_INSTALL_DIRECTORY}/bin)

# 查找头文件所在文件夹。
find_path(
        XZ_INCLUDE_DIRECTORY
        NAMES ${XZ_HEADER_NAME}
        PATHS ${XZ_HEADERS_DIRECTORY}
        NO_DEFAULT_PATH
)
if (XZ_INCLUDE_DIRECTORY)
    message(STATUS "found xz include directory ${XZ_INCLUDE_DIRECTORY}")
endif ()

# 设置依赖库路径。
find_file(
        XZ_DYNAMIC_LIBRARY
        NAMES ${XZ_DYNAMIC_LIBRARY_NAME}
        PATHS ${XZ_BINARY_DIRECTORY}
        NO_DEFAULT_PATH
)
if (XZ_DYNAMIC_LIBRARY)
    message(STATUS "found xz dynamic library ${XZ_DYNAMIC_LIBRARY}")
endif ()
find_library(
        XZ_LIBRARY
        NAMES ${XZ_LIBRARY_NAME}
        PATHS ${XZ_LIBRARY_DIRECTORY}
        NO_DEFAULT_PATH
)
if (XZ_LIBRARY)
    message(STATUS "found xz library ${XZ_LIBRARY}")
endif ()

# 添加外部构建项目。
if (NOT (XZ_LIBRARY AND XZ_INCLUDE_DIRECTORY))
    include(ExternalProject)
    set(XZ_BUILD_DIRECTORY ${XZ_DIRECTORY}/build)
    set(XZ_SOURCE_DIRECTORY ${XZ_DIRECTORY}/source)
    set(XZ_NAME xz-static)
    if (COMPILE_DYNAMIC_MODE)
        set(XZ_NAME xz-dynamic)
    endif ()
    ExternalProject_Add(
            ${XZ_NAME}
            PREFIX ${XZ_DIRECTORY}
            URL https://github.com/tukaani-project/xz/archive/refs/tags/${XZ_VERSION}.zip
            SOURCE_DIR ${XZ_SOURCE_DIRECTORY}
            BINARY_DIR ${XZ_BUILD_DIRECTORY}
            CONFIGURE_COMMAND ${CMAKE_COMMAND} --fresh -S ${XZ_SOURCE_DIRECTORY} -B ${XZ_BUILD_DIRECTORY}
                -DCMAKE_INSTALL_PREFIX=${XZ_INSTALL_DIRECTORY}
                -DCMAKE_CONFIGURATION_TYPES=${CMAKE_BUILD_TYPE}
                -DCMAKE_MSVC_RUNTIME_LIBRARY=${CMAKE_MSVC_RUNTIME_LIBRARY}
                -DCMAKE_C_FLAGS=${LIBRARY_COMPILE_C_FLAGS}
                -DCMAKE_C_FLAGS_DEBUG=${LIBRARY_COMPILE_C_FLAGS_DEBUG}
                -DCMAKE_C_FLAGS_RELEASE=${LIBRARY_COMPILE_C_FLAGS_RELEASE}
                -DCMAKE_WINDOWS_EXPORT_ALL_SYMBOLS:BOOL=${COMPILE_DYNAMIC_MODE}
                -DBUILD_SHARED_LIBS:BOOL=${COMPILE_DYNAMIC_MODE}
                -DXZ_DOC=OFF
            BUILD_COMMAND ${CMAKE_COMMAND} --build ${XZ_BUILD_DIRECTORY} --config ${CMAKE_BUILD_TYPE} --parallel --clean-first
            INSTALL_COMMAND ${CMAKE_COMMAND} --build ${XZ_BUILD_DIRECTORY} --config ${CMAKE_BUILD_TYPE} --target install
    )
    set(XZ_LIBRARY ${XZ_LIBRARY_DIRECTORY}/${XZ_LIBRARY_NAME})
    set(XZ_DYNAMIC_LIBRARY ${XZ_BINARY_DIRECTORY}/${XZ_DYNAMIC_LIBRARY_NAME})
    set(XZ_INCLUDE_DIRECTORY ${XZ_HEADERS_DIRECTORY})
    list(APPEND DEPENDENCIES ${XZ_NAME})
endif ()

# 导入头文件文件夹、链接代码库、复制动态代码库。
include_directories(${XZ_INCLUDE_DIRECTORY})
list(APPEND LIBRARIES ${XZ_LIBRARY})
list(APPEND DYNAMIC_LIBRARIES ${XZ_DYNAMIC_LIBRARY})
