set(OPENSSL_VERSION openssl-3.4.1)
set(OPENSSL_DIRECTORY ${DEPENDENCIES_DIRECTORY}/openssl/${OPENSSL_VERSION})

find_path(
    OPENSSL_INCLUDE_DIRECTORY
    NAMES openssl
    PATHS ${OPENSSL_DIRECTORY}/include
    NO_DEFAULT_PATH
)
if(CMAKE_SIZEOF_VOID_P EQUAL 4)
    set(OPENSSL_LIBRARY_NAME lib)
elseif(CMAKE_SIZEOF_VOID_P EQUAL 8)
    set(OPENSSL_LIBRARY_NAME lib64)
endif()
set(OPENSSL_LIBRARY_DIRECTORY ${OPENSSL_DIRECTORY}/${OPENSSL_LIBRARY_NAME})
find_library(
    OPENSSL_LIBRARY
    NAMES libssl.a
    PATHS ${OPENSSL_LIBRARY_DIRECTORY}
    NO_DEFAULT_PATH
)
find_library(
    CRYPTO_LIBRARY
    NAMES libcrypto.a
    PATHS ${OPENSSL_LIBRARY_DIRECTORY}
    NO_DEFAULT_PATH
)

if(OPENSSL_LIBRARY AND OPENSSL_INCLUDE_DIRECTORY AND CRYPTO_LIBRARY)
    message(STATUS "found openssl include directory: ${OPENSSL_INCLUDE_DIRECTORY}")
    message(STATUS "found openssl library: ${OPENSSL_LIBRARY}")
    message(STATUS "found crypto library: ${CRYPTO_LIBRARY}")
else()
    string(TOLOWER ${CMAKE_BUILD_TYPE} OPENSSL_BUILD_TYPE)
    ExternalProject_Add(
        openssl
        PREFIX ${OPENSSL_DIRECTORY}
        URL https://github.com/openssl/openssl/archive/refs/tags/${OPENSSL_VERSION}.zip
        CONFIGURE_COMMAND cd ${OPENSSL_DIRECTORY}/src/openssl && ./config no-shared no-deprecated --prefix=${OPENSSL_DIRECTORY} --${OPENSSL_BUILD_TYPE}
        BUILD_COMMAND cd ${OPENSSL_DIRECTORY}/src/openssl && make -j
        INSTALL_COMMAND cd ${OPENSSL_DIRECTORY}/src/openssl && sudo make install
    )
    set(OPENSSL_LIBRARY ${OPENSSL_LIBRARY_DIRECTORY}/libssl.a)
    set(CRYPTO_LIBRARY ${OPENSSL_LIBRARY_DIRECTORY}/libcrypto.a)
    set(OPENSSL_INCLUDE_DIRECTORY ${OPENSSL_DIRECTORY}/include)
endif()

include_directories(${OPENSSL_INCLUDE_DIRECTORY})
list(APPEND LIBRARIES ${OPENSSL_LIBRARY})
list(APPEND LIBRARIES ${CRYPTO_LIBRARY})
