set(BZIP2_VERSION bzip2-1.0.8)
set(BZIP2_DIRECTORY ${DEPENDENCIES_DIRECTORY}/bzip2/${BZIP2_VERSION})

find_path(
    BZIP2_INCLUDE_DIRECTORY
    NAMES bzlib.h
    PATHS ${BZIP2_DIRECTORY}/include
    NO_DEFAULT_PATH
)
find_library(
    BZIP2_LIBRARY
    NAMES libbz2.a
    PATHS ${BZIP2_DIRECTORY}/lib
    NO_DEFAULT_PATH
)

if(BZIP2_LIBRARY AND BZIP2_INCLUDE_DIRECTORY)
    message(STATUS "found bzip2 include directory: ${BZIP2_INCLUDE_DIRECTORY}")
    message(STATUS "found bzip2 library: ${BZIP2_LIBRARY}")
else()
    ExternalProject_Add(
        bzip2
        PREFIX ${BZIP2_DIRECTORY}
        URL https://github.com/libarchive/bzip2/archive/refs/tags/${BZIP2_VERSION}.zip
        CONFIGURE_COMMAND cd ${BZIP2_DIRECTORY}/src
        BUILD_COMMAND cd ${BZIP2_DIRECTORY}/src/bzip2 && make -j
        INSTALL_COMMAND cd ${BZIP2_DIRECTORY}/src/bzip2 && make install PREFIX=${BZIP2_DIRECTORY}
    )
    set(BZIP2_LIBRARY ${BZIP2_DIRECTORY}/lib/libbz2.a)
    set(BZIP2_INCLUDE_DIRECTORY ${BZIP2_DIRECTORY}/include)
endif()

include_directories(${BZIP2_INCLUDE_DIRECTORY})
list(APPEND LIBRARIES ${BZIP2_LIBRARY})
