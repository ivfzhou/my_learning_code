# 设置版本号、头文件名、代码库名称等。
set(OPENSSL_NAME openssl-release-static)
set(OPENSSL_HEADER_NAME openssl)
set(OPENSSL_LIBRARY_NAME libssl.a)
set(CRYPTO_LIBRARY_NAME libcrypto.a)
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
find_library(
        OPENSSL_LIBRARY
        NAMES ${OPENSSL_LIBRARY_NAME}
        PATHS ${OPENSSL_LIBRARY_DIRECTORY}
        NO_DEFAULT_PATH
)
if (OPENSSL_LIBRARY)
    message(STATUS "found openssl library ${OPENSSL_LIBRARY}")
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
if (NOT (OPENSSL_LIBRARY AND OPENSSL_INCLUDE_DIRECTORY AND CRYPTO_LIBRARY))
    include(ExternalProject)
    set(OPENSSL_BUILD_DIRECTORY ${OPENSSL_DIRECTORY}/build)
    set(OPENSSL_SOURCE_DIRECTORY ${OPENSSL_DIRECTORY}/source)
    get_filename_component(ZLIB_LIBRARY_DIRECTORY ${ZLIB_LIBRARY} DIRECTORY)
    get_filename_component(ZSTD_LIBRARY_DIRECTORY ${ZSTD_LIBRARY} DIRECTORY)
    ExternalProject_Add(
            ${OPENSSL_NAME}
            PREFIX ${OPENSSL_DIRECTORY}
            URL ${OPENSSL_URL}
            URL_HASH SHA256=${OPENSSL_SHA256}
            SOURCE_DIR ${OPENSSL_SOURCE_DIRECTORY}
            BINARY_DIR ${OPENSSL_BUILD_DIRECTORY}
            CONFIGURE_COMMAND perl ${OPENSSL_SOURCE_DIRECTORY}/Configure
                --prefix=${OPENSSL_INSTALL_DIRECTORY}
                --openssldir=${OPENSSL_INSTALL_DIRECTORY}
                --with-zlib-include=${ZLIB_INCLUDE_DIRECTORY}
                --with-zlib-lib=${ZLIB_LIBRARY_DIRECTORY}
                --with-zstd-include=${ZSTD_INCLUDE_DIRECTORY}
                --with-zstd-lib=${ZSTD_LIBRARY_DIRECTORY}
                no-docs
                enable-legacy
                no-module
                no-tests
                zlib
                enable-zstd
                no-shared
                --release
            BUILD_COMMAND make
            INSTALL_COMMAND make install
    )
    set(OPENSSL_LIBRARY ${OPENSSL_LIBRARY_DIRECTORY}/${OPENSSL_LIBRARY_NAME})
    set(CRYPTO_LIBRARY ${OPENSSL_LIBRARY_DIRECTORY}/${CRYPTO_LIBRARY_NAME})
    set(OPENSSL_INCLUDE_DIRECTORY ${OPENSSL_HEADERS_DIRECTORY})
    list(APPEND DEPENDENCIES ${OPENSSL_NAME})
    if (TARGET ${ZSTD_NAME})
        add_dependencies(${OPENSSL_NAME} ${ZSTD_NAME})
    endif ()
    if (TARGET ${ZLIB_NAME})
        add_dependencies(${OPENSSL_NAME} ${ZLIB_NAME})
    endif ()
    unset(OPENSSL_BUILD_DIRECTORY)
    unset(OPENSSL_SOURCE_DIRECTORY)
    unset(ZLIB_LIBRARY_DIRECTORY)
    unset(ZSTD_LIBRARY_DIRECTORY)
endif ()

# 导入头文件文件夹、链接代码库、复制动态代码库。
list(APPEND INCLUDES ${OPENSSL_INCLUDE_DIRECTORY})
list(APPEND LIBRARIES ${CRYPTO_LIBRARY} ${OPENSSL_LIBRARY})

set(OPENSSL_HEADER_NAME)
set(OPENSSL_LIBRARY_NAME)
set(CRYPTO_LIBRARY_NAME)
set(OPENSSL_DIRECTORY)
set(OPENSSL_INSTALL_DIRECTORY)
set(OPENSSL_HEADERS_DIRECTORY)
set(OPENSSL_LIBRARY_DIRECTORY)
