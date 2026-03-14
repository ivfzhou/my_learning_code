# 设置版本号、头文件名、依赖库名称。
set(OPENSSL_VERSION openssl-3.6.1)
set(OPENSSL_HEADER_NAME openssl)
set(OPENSSL_LIBRARY_NAME libssl.a)
set(CRYPTO_LIBRARY_NAME libcrypto.a)
set(OPENSSL_BUILD_TYPE --release)
if (CMAKE_BUILD_TYPE STREQUAL "Debug")
    set(OPENSSL_BUILD_TYPE --debug)
endif ()

# 设置依赖安装目录。
set(OPENSSL_DIRECTORY ${DEPENDENCIES_DIRECTORY}/openssl)
file(MAKE_DIRECTORY ${OPENSSL_DIRECTORY})
set(OPENSSL_BUILD_DIRECTORY ${OPENSSL_DIRECTORY}/build)
set(OPENSSL_SOURCE_DIRECTORY ${OPENSSL_DIRECTORY}/source)
set(OPENSSL_INSTALL_DIRECTORY ${OPENSSL_DIRECTORY}/install)

# 设置依赖库路径。
if (CMAKE_SIZEOF_VOID_P EQUAL 4)
    set(OPENSSL_LIBRARY_DIRECTORY lib)
elseif (CMAKE_SIZEOF_VOID_P EQUAL 8)
    set(OPENSSL_LIBRARY_DIRECTORY lib64)
endif ()
find_library(
        OPENSSL_LIBRARY
        NAMES ${OPENSSL_LIBRARY_NAME}
        PATHS ${OPENSSL_INSTALL_DIRECTORY}/${OPENSSL_LIBRARY_DIRECTORY}
        NO_DEFAULT_PATH
)
find_library(
        CRYPTO_LIBRARY
        NAMES ${CRYPTO_LIBRARY_NAME}
        PATHS ${OPENSSL_INSTALL_DIRECTORY}/${OPENSSL_LIBRARY_DIRECTORY}
        NO_DEFAULT_PATH
)

# 设置依赖头文件路径。
find_path(
        OPENSSL_INCLUDE_DIRECTORY
        NAMES ${OPENSSL_HEADER_NAME}
        PATHS ${OPENSSL_INSTALL_DIRECTORY}/include
        NO_DEFAULT_PATH
)

# 如果找到依赖库和头文件，则设置依赖库和头文件的路径。
if (OPENSSL_LIBRARY AND OPENSSL_INCLUDE_DIRECTORY AND CRYPTO_LIBRARY)
    message(STATUS "found openssl library: ${OPENSSL_LIBRARY}")
    message(STATUS "found crypto library: ${CRYPTO_LIBRARY}")
    message(STATUS "found openssl include directory: ${OPENSSL_INCLUDE_DIRECTORY}")
else ()
    include(ExternalProject)
    get_filename_component(ZLIB_LIBRARY_DIRECTORY ${ZLIB_LIBRARY} DIRECTORY)
    get_filename_component(ZSTD_LIBRARY_DIRECTORY ${ZSTD_LIBRARY} DIRECTORY)
    ExternalProject_Add(
            openssl
            PREFIX ${OPENSSL_DIRECTORY}
            URL https://github.com/openssl/openssl/archive/refs/tags/${OPENSSL_VERSION}.zip
            SOURCE_DIR ${OPENSSL_SOURCE_DIRECTORY}
            BINARY_DIR ${OPENSSL_BUILD_DIRECTORY}
            CONFIGURE_COMMAND perl ${OPENSSL_SOURCE_DIRECTORY}/Configure
                --prefix=${OPENSSL_INSTALL_DIRECTORY}
                --openssldir=${OPENSSL_INSTALL_DIRECTORY}
                --with-zlib-include=${ZLIB_INCLUDE_DIRECTORY}
                --with-zlib-lib=${ZLIB_LIBRARY_DIRECTORY}
                --with-zstd-include=${ZSTD_INCLUDE_DIRECTORY}
                --with-zstd-lib=${ZSTD_LIBRARY_DIRECTORY}
                ${OPENSSL_BUILD_TYPE}
                no-docs
                no-shared
                no-deprecated
                no-tests
                zlib
                enable-zstd
            BUILD_COMMAND make
            INSTALL_COMMAND make install
    )
    set(OPENSSL_LIBRARY ${OPENSSL_INSTALL_DIRECTORY}/${OPENSSL_LIBRARY_DIRECTORY}/${OPENSSL_LIBRARY_NAME})
    set(CRYPTO_LIBRARY ${OPENSSL_INSTALL_DIRECTORY}/${OPENSSL_LIBRARY_DIRECTORY}/${CRYPTO_LIBRARY_NAME})
    set(OPENSSL_INCLUDE_DIRECTORY ${OPENSSL_INSTALL_DIRECTORY}/include)
endif ()

include_directories(${OPENSSL_INCLUDE_DIRECTORY})
list(APPEND LIBRARIES ${CRYPTO_LIBRARY} ${OPENSSL_LIBRARY})
