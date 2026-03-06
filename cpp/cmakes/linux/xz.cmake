set(XZ_VERSION v5.6.4)
set(XZ_DIRECTORY ${DEPENDENCIES_DIRECTORY}/xz/${XZ_VERSION})

find_path(
    XZ_INCLUDE_DIRECTORY
    NAMES lzma.h
    PATHS ${XZ_DIRECTORY}/include
    NO_DEFAULT_PATH
)
find_library(
    XZ_LIBRARY
    NAMES liblzma.a
    PATHS ${XZ_DIRECTORY}/lib
    NO_DEFAULT_PATH
)

if(XZ_LIBRARY AND XZ_INCLUDE_DIRECTORY)
    message(STATUS "found xz include directory: ${XZ_INCLUDE_DIRECTORY}")
    message(STATUS "found xz library: ${XZ_LIBRARY}")
else()
    ExternalProject_Add(
        xz
        PREFIX ${XZ_DIRECTORY}
        URL https://github.com/tukaani-project/xz/archive/refs/tags/${XZ_VERSION}.zip
        CONFIGURE_COMMAND cd ${XZ_DIRECTORY}/src && rm -rf xz-build && mkdir -p xz-build
        BUILD_COMMAND cd ${XZ_DIRECTORY}/src/xz-build && ${CMAKE_COMMAND} -DCMAKE_INSTALL_PREFIX=${XZ_DIRECTORY} -DCMAKE_BUILD_TYPE=${CMAKE_BUILD_TYPE} -DBUILD_SHARED_LIBS=OFF -DBUILD_TESTING=OFF ../xz
        INSTALL_COMMAND cd ${XZ_DIRECTORY}/src/xz-build && ${CMAKE_COMMAND} --build . --target install
    )
    set(XZ_LIBRARY ${XZ_DIRECTORY}/lib/liblzma.a)
endif()

list(APPEND LIBRARIES ${XZ_LIBRARY})
