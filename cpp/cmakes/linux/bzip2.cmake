# 设置版本号、头文件名、依赖库名称。
set(BZIP2_VERSION 1ea1ac188ad4b9cb662e3f8314673c63df95a589)
set(BZIP2_HEADER_NAME bzlib.h)
set(BZIP2_LIBRARY_NAME libbz2_static.a)

# 设置依赖安装目录。
set(BZIP2_DIRECTORY ${DEPENDENCIES_DIRECTORY}/bzip2)
file(MAKE_DIRECTORY ${BZIP2_DIRECTORY})
set(BZIP2_BUILD_DIRECTORY ${BZIP2_DIRECTORY}/build)
set(BZIP2_SOURCE_DIRECTORY ${BZIP2_DIRECTORY}/source)
set(BZIP2_INSTALL_DIRECTORY ${BZIP2_DIRECTORY}/install)

# 设置依赖库路径。
find_library(
        BZIP2_LIBRARY
        NAMES ${BZIP2_LIBRARY_NAME}
        PATHS ${BZIP2_INSTALL_DIRECTORY}/lib
        NO_DEFAULT_PATH
)

# 设置依赖头文件路径。
find_path(
        BZIP2_INCLUDE_DIRECTORY
        NAMES ${BZIP2_HEADER_NAME}
        PATHS ${BZIP2_INSTALL_DIRECTORY}/include
        NO_DEFAULT_PATH
)

# 如果找到依赖库和头文件，则设置依赖库和头文件的路径。
if (BZIP2_LIBRARY AND BZIP2_INCLUDE_DIRECTORY)
    message(STATUS "found bzip2 library: ${BZIP2_LIBRARY}")
    message(STATUS "found bzip2 include directory: ${BZIP2_INCLUDE_DIRECTORY}")
else ()
    include(ExternalProject)
    ExternalProject_Add(
            bzip2
            PREFIX ${BZIP2_DIRECTORY}
            URL https://github.com/libarchive/bzip2/archive/${BZIP2_VERSION}.zip
            SOURCE_DIR ${BZIP2_SOURCE_DIRECTORY}
            BINARY_DIR ${BZIP2_BUILD_DIRECTORY}
            CONFIGURE_COMMAND ${CMAKE_COMMAND} --fresh -S ${BZIP2_SOURCE_DIRECTORY} -B ${BZIP2_BUILD_DIRECTORY}
                -DCMAKE_INSTALL_PREFIX=${BZIP2_INSTALL_DIRECTORY}
                -DCMAKE_BUILD_TYPE=${CMAKE_BUILD_TYPE}
                -DCMAKE_C_FLAGS=${COMPILER_FLAGS}
                -DENABLE_EXAMPLES=OFF
                -DENABLE_SHARED_LIB=OFF
                -DENABLE_DOCS=OFF
                -DENABLE_STATIC_LIB=ON
            BUILD_COMMAND ${CMAKE_COMMAND} --build ${BZIP2_BUILD_DIRECTORY} --config ${CMAKE_BUILD_TYPE} --parallel --clean-first
            INSTALL_COMMAND ${CMAKE_COMMAND} --build ${BZIP2_BUILD_DIRECTORY} --config ${CMAKE_BUILD_TYPE} --target install
    )
    set(BZIP2_LIBRARY ${BZIP2_INSTALL_DIRECTORY}/lib/${BZIP2_LIBRARY_NAME})
    set(BZIP2_INCLUDE_DIRECTORY ${BZIP2_INSTALL_DIRECTORY}/include)
endif ()

include_directories(${BZIP2_INCLUDE_DIRECTORY})
list(APPEND LIBRARIES ${BZIP2_LIBRARY})
