set(YAML_CPP_VERSION yaml-cpp-0.9.0)
set(YAML_CPP_DIRECTORY ${DEPENDENCIES_DIRECTORY}/yaml-cpp/${YAML_CPP_VERSION})

find_path(
    YAML_CPP_INCLUDE_DIRECTORY
    NAMES yaml-cpp
    PATHS ${YAML_CPP_DIRECTORY}/include
    NO_DEFAULT_PATH
)
set(YAML_CPP_LIBRARY_NAME libyaml-cpp.a)
if(CMAKE_BUILD_TYPE STREQUAL "Debug")
    set(YAML_CPP_LIBRARY_NAME libyaml-cppd.a)
endif()
find_library(
    YAML_CPP_LIBRARY
    NAMES ${YAML_CPP_LIBRARY_NAME}
    PATHS ${YAML_CPP_DIRECTORY}/lib
    NO_DEFAULT_PATH
)

if(YAML_CPP_LIBRARY AND YAML_CPP_INCLUDE_DIRECTORY)
    message(STATUS "found yaml-cpp include directory: ${YAML_CPP_INCLUDE_DIRECTORY}")
    message(STATUS "found yaml-cpp library: ${YAML_CPP_LIBRARY}")
else()
    ExternalProject_Add(
        yaml-cpp
        PREFIX ${YAML_CPP_DIRECTORY}
        URL https://github.com/jbeder/yaml-cpp/archive/refs/tags/${YAML_CPP_VERSION}.zip
        CONFIGURE_COMMAND cd ${YAML_CPP_DIRECTORY}/src && rm -rf yaml-cpp-build && mkdir -p yaml-cpp-build
        BUILD_COMMAND cd ${YAML_CPP_DIRECTORY}/src/yaml-cpp-build && ${CMAKE_COMMAND} -DCMAKE_INSTALL_PREFIX=${YAML_CPP_DIRECTORY} -DCMAKE_BUILD_TYPE=${CMAKE_BUILD_TYPE} -DBUILD_SHARED_LIBS=OFF -DYAML_USE_SYSTEM_GTEST=OFF ../yaml-cpp
        INSTALL_COMMAND cd ${YAML_CPP_DIRECTORY}/src/yaml-cpp-build && ${CMAKE_COMMAND} --build . --target install
    )
    set(YAML_CPP_INCLUDE_DIRECTORY ${YAML_CPP_DIRECTORY}/include)
    set(YAML_CPP_LIBRARY ${YAML_CPP_DIRECTORY}/lib/${YAML_CPP_LIBRARY_NAME})
endif()

add_definitions(-DYAML_CPP_STATIC_DEFINE=1)
include_directories(${YAML_CPP_INCLUDE_DIRECTORY})
list(APPEND LIBRARIES ${YAML_CPP_LIBRARY})
