set(CPP_HTTPLIB_VERSION v0.20.0)
set(CPP_HTTPLIB_DIRECTORY ${DEPENDENCIES_DIRECTORY}/cpp-httplib/${CPP_HTTPLIB_VERSION})

find_path(
    CPP_HTTPLIB_INCLUDE_DIRECTORY
    NAMES httplib.h
    PATHS ${CPP_HTTPLIB_DIRECTORY}/include
    NO_DEFAULT_PATH
)

if(CPP_HTTPLIB_INCLUDE_DIRECTORY)
    message(STATUS "found cpp-httplib include directory: ${CPP_HTTPLIB_INCLUDE_DIRECTORY}")
else()
    ExternalProject_Add(
        cpp-httplib
        PREFIX ${CPP_HTTPLIB_DIRECTORY}
        URL https://github.com/yhirose/cpp-httplib/archive/refs/tags/${CPP_HTTPLIB_VERSION}.zip
        CONFIGURE_COMMAND cd ${CPP_HTTPLIB_DIRECTORY}/src && rd /s /q cpp-httplib-build && md cpp-httplib-build
        BUILD_COMMAND cd ${CPP_HTTPLIB_DIRECTORY}/src/cpp-httplib-build && ${CMAKE_COMMAND} -DCMAKE_INSTALL_PREFIX=${CPP_HTTPLIB_DIRECTORY} -DCMAKE_BUILD_TYPE=${CMAKE_BUILD_TYPE} -DBUILD_SHARED_LIBS=OFF ../cpp-httplib
        INSTALL_COMMAND cd ${CPP_HTTPLIB_DIRECTORY}/src/cpp-httplib-build && ${CMAKE_COMMAND} --build . --target install --config ${CMAKE_BUILD_TYPE}
    )
    set(CPP_HTTPLIB_INCLUDE_DIRECTORY ${CPP_HTTPLIB_DIRECTORY}/include)
endif()

include_directories(${CPP_HTTPLIB_INCLUDE_DIRECTORY})
