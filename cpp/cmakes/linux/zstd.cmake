set(ZSTD_VERSION v1.5.7)
set(ZSTD_DIRECTORY ${DEPENDENCIES_DIRECTORY}/zstd/${ZSTD_VERSION})

find_path(
    ZSTD_INCLUDE_DIRECTORY
    NAMES zstd.h
    PATHS ${ZSTD_DIRECTORY}/include
    NO_DEFAULT_PATH
)
find_library(
    ZSTD_LIBRARY
    NAMES libzstd.a
    PATHS ${ZSTD_DIRECTORY}/lib
    NO_DEFAULT_PATH
)

if(ZSTD_LIBRARY AND ZSTD_INCLUDE_DIRECTORY)
    message(STATUS "found zstd include directory: ${ZSTD_INCLUDE_DIRECTORY}")
    message(STATUS "found zstd library: ${ZSTD_LIBRARY}")
else()
    ExternalProject_Add(
        zstd
        PREFIX ${ZSTD_DIRECTORY}
        URL https://github.com/facebook/zstd/archive/refs/tags/${ZSTD_VERSION}.zip
        CONFIGURE_COMMAND cd ${ZSTD_DIRECTORY}/src/zstd
        BUILD_COMMAND cd ${ZSTD_DIRECTORY}/src/zstd && make -j
        INSTALL_COMMAND cd ${ZSTD_DIRECTORY}/src/zstd && make install PREFIX=${ZSTD_DIRECTORY}
    )
    set(ZSTD_INCLUDE_DIRECTORY ${ZSTD_DIRECTORY}/include)
    set(ZSTD_LIBRARY ${ZSTD_DIRECTORY}/lib/libzstd.a)
endif()

include_directories(${ZSTD_INCLUDE_DIRECTORY})
list(APPEND LIBRARIES ${ZSTD_LIBRARY})
