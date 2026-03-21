# 设置版本号、头文件名、代码库名称等。
set(OPENSSL_VERSION openssl-3.6.1)
set(OPENSSL_HEADER_NAME openssl)
set(OPENSSL_LIBRARY_NAME libssl.a)
set(CRYPTO_LIBRARY_NAME libcrypto.a)
set(OPENSSL_DYNAMIC_LIBRARY_NAME libssl.so.3)
set(CRYPTO_DYNAMIC_LIBRARY_NAME libcrypto.so.3)
if (COMPILE_DYNAMIC_MODE)
    set(OPENSSL_LIBRARY_NAME ${OPENSSL_DYNAMIC_LIBRARY_NAME})
    set(CRYPTO_LIBRARY_NAME ${CRYPTO_DYNAMIC_LIBRARY_NAME})
endif ()
set(OPENSSL_DIRECTORY ${DEPENDENCIES_DIRECTORY}/openssl)
set(OPENSSL_INSTALL_DIRECTORY ${OPENSSL_DIRECTORY}/install)
set(OPENSSL_HEADERS_DIRECTORY ${OPENSSL_INSTALL_DIRECTORY}/include)
set(OPENSSL_LIBRARY_DIRECTORY ${OPENSSL_INSTALL_DIRECTORY}/lib)
if (CMAKE_SIZEOF_VOID_P EQUAL 8)
    set(OPENSSL_LIBRARY_DIRECTORY ${OPENSSL_INSTALL_DIRECTORY}/lib64)
endif ()

# 查找头文件所在文件夹。
find_path(
        OPENSSL_INCLUDE_DIRECTORY
        NAMES ${OPENSSL_HEADER_NAME}
        PATHS ${OPENSSL_HEADERS_DIRECTORY}
        NO_DEFAULT_PATH
)
if (OPENSSL_INCLUDE_DIRECTORY)
    message(STATUS "found openssl include directory ${OPENSSL_INCLUDE_DIRECTORY}")
endif ()

# 查找库文件。
find_file(
        OPENSSL_DYNAMIC_LIBRARY
        NAMES ${OPENSSL_DYNAMIC_LIBRARY_NAME}
        PATHS ${OPENSSL_LIBRARY_DIRECTORY}
        NO_DEFAULT_PATH
)
if (OPENSSL_DYNAMIC_LIBRARY)
    message(STATUS "found openssl dynamic library ${OPENSSL_DYNAMIC_LIBRARY}")
endif ()
find_library(
        OPENSSL_LIBRARY
        NAMES ${OPENSSL_LIBRARY_NAME}
        PATHS ${OPENSSL_LIBRARY_DIRECTORY}
        NO_DEFAULT_PATH
)
if (OPENSSL_LIBRARY)
    message(STATUS "found openssl library ${OPENSSL_LIBRARY}")
endif ()
find_file(
        CRYPTO_DYNAMIC_LIBRARY
        NAMES ${CRYPTO_DYNAMIC_LIBRARY_NAME}
        PATHS ${OPENSSL_LIBRARY_DIRECTORY}
        NO_DEFAULT_PATH
)
if (CRYPTO_DYNAMIC_LIBRARY)
    message(STATUS "found crypto dynamic library ${CRYPTO_DYNAMIC_LIBRARY}")
endif ()
find_library(
        CRYPTO_LIBRARY
        NAMES ${CRYPTO_LIBRARY_NAME}
        PATHS ${OPENSSL_LIBRARY_DIRECTORY}
        NO_DEFAULT_PATH
)
if (CRYPTO_LIBRARY)
    message(STATUS "found crypto library ${CRYPTO_LIBRARY}")
endif ()

# 添加外部构建项目。
if (NOT ((COMPILE_STATIC_MODE AND OPENSSL_LIBRARY AND OPENSSL_INCLUDE_DIRECTORY AND CRYPTO_LIBRARY) OR (COMPILE_DYNAMIC_MODE AND CRYPTO_DYNAMIC_LIBRARY AND OPENSSL_DYNAMIC_LIBRARY AND OPENSSL_LIBRARY AND OPENSSL_INCLUDE_DIRECTORY AND CRYPTO_LIBRARY)))
    include(ExternalProject)
    set(OPENSSL_BUILD_DIRECTORY ${OPENSSL_DIRECTORY}/build)
    set(OPENSSL_SOURCE_DIRECTORY ${OPENSSL_DIRECTORY}/source)
    set(OPENSSL_BUILD_TYPE --release)
    if (CMAKE_BUILD_TYPE STREQUAL "Debug")
        set(OPENSSL_BUILD_TYPE --debug)
    endif ()
    set(OPENSSL_NAME openssl-static)
    set(OPENSSL_BUILD_SHARED "no-shared")
    if (COMPILE_DYNAMIC_MODE)
        set(OPENSSL_NAME openssl-dynamic)
        set(OPENSSL_BUILD_SHARED "")
    endif ()
    ExternalProject_Add(
            ${OPENSSL_NAME}
            PREFIX ${OPENSSL_DIRECTORY}
            URL https://github.com/openssl/openssl/archive/refs/tags/${OPENSSL_VERSION}.zip
            SOURCE_DIR ${OPENSSL_SOURCE_DIRECTORY}
            BINARY_DIR ${OPENSSL_BUILD_DIRECTORY}
            CONFIGURE_COMMAND perl ${OPENSSL_SOURCE_DIRECTORY}/Configure
                --prefix=${OPENSSL_INSTALL_DIRECTORY}
                --openssldir=${OPENSSL_INSTALL_DIRECTORY}
                --with-zlib-include=${ZLIB_INCLUDE_DIRECTORY}
                --with-zlib-lib=${ZLIB_LIBRARY}
                --with-zstd-include=${ZSTD_INCLUDE_DIRECTORY}
                --with-zstd-lib=${ZSTD_LIBRARY}
                ${OPENSSL_BUILD_TYPE}
                no-docs
                no-deprecated
                no-tests
                zlib
                enable-zstd
                ${OPENSSL_BUILD_SHARED}
            BUILD_COMMAND make
            INSTALL_COMMAND make install
    )
    set(OPENSSL_LIBRARY ${OPENSSL_LIBRARY_DIRECTORY}/${OPENSSL_LIBRARY_NAME})
    set(CRYPTO_LIBRARY ${OPENSSL_LIBRARY_DIRECTORY}/${CRYPTO_LIBRARY_NAME})
    set(CRYPTO_DYNAMIC_LIBRARY ${OPENSSL_LIBRARY_DIRECTORY}/${CRYPTO_DYNAMIC_LIBRARY_NAME})
    set(OPENSSL_DYNAMIC_LIBRARY ${OPENSSL_LIBRARY_DIRECTORY}/${OPENSSL_DYNAMIC_LIBRARY_NAME})
    set(OPENSSL_INCLUDE_DIRECTORY ${OPENSSL_HEADERS_DIRECTORY})
    list(APPEND DEPENDENCIES ${OPENSSL_NAME})
    if (TARGET ${ZSTD_NAME})
        add_dependencies(${OPENSSL_NAME} ${ZSTD_NAME})
    endif ()
    if (TARGET ${ZLIB_NAME})
        add_dependencies(${OPENSSL_NAME} ${ZLIB_NAME})
    endif ()
endif ()

# 导入头文件文件夹、链接代码库、复制动态代码库。
include_directories(${OPENSSL_INCLUDE_DIRECTORY})
list(APPEND LIBRARIES ${CRYPTO_LIBRARY} ${OPENSSL_LIBRARY})
list(APPEND DYNAMIC_LIBRARIES ${OPENSSL_DYNAMIC_LIBRARY} ${CRYPTO_DYNAMIC_LIBRARY})
