set(LIBZIP_VERSION v1.11.3)
set(LIBZIP_DIRECTORY ${DEPENDENCIES_DIRECTORY}/libzip/${LIBZIP_VERSION})

find_path(
    LIBZIP_INCLUDE_DIRECTORY
    NAMES zip.h
    PATHS ${LIBZIP_DIRECTORY}/include
    NO_DEFAULT_PATH
)
find_library(
    LIBZIP_LIBRARY
    NAMES libzip.a
    PATHS ${LIBZIP_DIRECTORY}/lib
    NO_DEFAULT_PATH
)

if(LIBZIP_LIBRARY AND LIBZIP_INCLUDE_DIRECTORY)
    message(STATUS "found libzip include directory: ${LIBZIP_INCLUDE_DIRECTORY}")
    message(STATUS "found libzip library: ${LIBZIP_LIBRARY}")
else()
    ExternalProject_Add(
        libzip
        PREFIX ${LIBZIP_DIRECTORY}
        URL https://github.com/nih-at/libzip/archive/refs/tags/${LIBZIP_VERSION}.zip
        CONFIGURE_COMMAND cd ${LIBZIP_DIRECTORY}/src && rm -rf libzip-build && mkdir -p libzip-build
        BUILD_COMMAND cd ${LIBZIP_DIRECTORY}/src/libzip-build && ${CMAKE_COMMAND} -DCMAKE_INSTALL_PREFIX=${LIBZIP_DIRECTORY} -DCMAKE_BUILD_TYPE=${CMAKE_BUILD_TYPE} -DBUILD_SHARED_LIBS=OFF ../libzip
        INSTALL_COMMAND cd ${LIBZIP_DIRECTORY}/src/libzip-build && ${CMAKE_COMMAND} --build . --target install
    )
    set(LIBZIP_INCLUDE_DIRECTORY ${LIBZIP_DIRECTORY}/include)
    set(LIBZIP_LIBRARY ${LIBZIP_DIRECTORY}/lib/libzip.a)
endif()

include_directories(${LIBZIP_INCLUDE_DIRECTORY})
list(APPEND LIBRARIES ${LIBZIP_LIBRARY})
