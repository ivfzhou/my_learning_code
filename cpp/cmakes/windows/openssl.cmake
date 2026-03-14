# 设置版本号、头文件名、依赖库名称。
set(OPENSSL_VERSION openssl-3.6.1)
set(OPENSSL_HEADER_NAME openssl)
set(OPENSSL_LIBRARY_NAME libssl.lib)
set(CRYPTO_LIBRARY_NAME libcrypto.lib)
set(OPENSSL_BUILD_TYPE --release)
set(OPENSSL_COMPILE_FLAG "-MT")
if (CMAKE_BUILD_TYPE STREQUAL "Debug")
    set(OPENSSL_BUILD_TYPE --debug)
    set(OPENSSL_COMPILE_FLAG "-MTd")
endif ()

# 设置依赖安装目录。
set(OPENSSL_DIRECTORY ${DEPENDENCIES_DIRECTORY}/openssl)
file(MAKE_DIRECTORY ${OPENSSL_DIRECTORY})
set(OPENSSL_BUILD_DIRECTORY ${OPENSSL_DIRECTORY}/build)
set(OPENSSL_SOURCE_DIRECTORY ${OPENSSL_DIRECTORY}/source)
set(OPENSSL_INSTALL_DIRECTORY ${OPENSSL_DIRECTORY}/install)

# 设置依赖库路径。
find_library(
        OPENSSL_LIBRARY
        NAMES ${OPENSSL_LIBRARY_NAME}
        PATHS ${OPENSSL_INSTALL_DIRECTORY}/lib
        NO_DEFAULT_PATH
)
find_library(
        CRYPTO_LIBRARY
        NAMES ${CRYPTO_LIBRARY_NAME}
        PATHS ${OPENSSL_INSTALL_DIRECTORY}/lib
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
    ExternalProject_Add(
            openssl
            PREFIX ${OPENSSL_DIRECTORY}
            URL https://github.com/openssl/openssl/archive/refs/tags/${OPENSSL_VERSION}.zip
            SOURCE_DIR ${OPENSSL_SOURCE_DIRECTORY}
            BINARY_DIR ${OPENSSL_BUILD_DIRECTORY}
            CONFIGURE_COMMAND call "C:\\Program Files\\Microsoft Visual Studio\\2022\\Community\\VC\\Auxiliary\\Build\\vcvars64.bat" && perl ${OPENSSL_SOURCE_DIRECTORY}/Configure
                --prefix=${OPENSSL_INSTALL_DIRECTORY}
                --openssldir=${OPENSSL_INSTALL_DIRECTORY}
                --with-zlib-include=${ZLIB_INCLUDE_DIRECTORY}
                --with-zlib-lib=${ZLIB_LIBRARY}
                --with-zstd-include=${ZSTD_INCLUDE_DIRECTORY}
                --with-zstd-lib=${ZSTD_LIBRARY}
                ${OPENSSL_BUILD_TYPE}
                no-docs
                no-shared
                no-deprecated
                no-tests
                zlib
                enable-zstd
                ${OPENSSL_COMPILE_FLAG}
            BUILD_COMMAND call "C:\\Program Files\\Microsoft Visual Studio\\2022\\Community\\VC\\Auxiliary\\Build\\vcvars64.bat" && nmake
            INSTALL_COMMAND call "C:\\Program Files\\Microsoft Visual Studio\\2022\\Community\\VC\\Auxiliary\\Build\\vcvars64.bat" && nmake install
    )
    set(OPENSSL_LIBRARY ${OPENSSL_INSTALL_DIRECTORY}/lib/${OPENSSL_LIBRARY_NAME})
    set(CRYPTO_LIBRARY ${OPENSSL_INSTALL_DIRECTORY}/lib/${CRYPTO_LIBRARY_NAME})
    set(OPENSSL_INCLUDE_DIRECTORY ${OPENSSL_INSTALL_DIRECTORY}/include)
endif ()

include_directories(${OPENSSL_INCLUDE_DIRECTORY})
list(APPEND LIBRARIES ${CRYPTO_LIBRARY} ${OPENSSL_LIBRARY} ws2_32 crypt32 bcrypt)
