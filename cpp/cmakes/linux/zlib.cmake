set(ZLIB_VERSION v1.3.1)
set(ZLIB_DIRECTORY ${DEPENDENCIES_DIRECTORY}/zlib/${ZLIB_VERSION})

find_path(
    ZLIB_INCLUDE_DIRECTORY
    NAMES zlib.h
    PATHS ${ZLIB_DIRECTORY}/include
    NO_DEFAULT_PATH
)
find_library(
    ZLIB_LIBRARY
    NAMES libz.a
    PATHS ${ZLIB_DIRECTORY}/lib
    NO_DEFAULT_PATH
)

if(ZLIB_INCLUDE_DIRECTORY AND ZLIB_LIBRARY)
    message(STATUS "found zlib include directory: ${ZLIB_INCLUDE_DIRECTORY}")
    message(STATUS "found zlib library: ${ZLIB_LIBRARY}")
else()
    ExternalProject_Add(
        zlib
        PREFIX ${ZLIB_DIRECTORY}
        URL https://github.com/madler/zlib/archive/refs/tags/${ZLIB_VERSION}.zip
        CONFIGURE_COMMAND cd ${ZLIB_DIRECTORY}/src && rm -rf zlib-build && mkdir -p zlib-build
        BUILD_COMMAND cd ${ZLIB_DIRECTORY}/src/zlib-build && ${CMAKE_COMMAND} -DCMAKE_INSTALL_PREFIX=${ZLIB_DIRECTORY} -DCMAKE_BUILD_TYPE=${CMAKE_BUILD_TYPE} -DBUILD_SHARED_LIBS=OFF -DZLIB_BUILD_EXAMPLES=OFF ../zlib
        INSTALL_COMMAND cd ${ZLIB_DIRECTORY}/src/zlib-build && ${CMAKE_COMMAND} --build . --target install
    )
    set(ZLIB_INCLUDE_DIRECTORY ${ZLIB_DIRECTORY}/include)
    set(ZLIB_LIBRARY ${ZLIB_DIRECTORY}/lib/libz.a)
endif()

include_directories(${ZLIB_INCLUDE_DIRECTORY})
list(APPEND LIBRARIES ${ZLIB_LIBRARY})
