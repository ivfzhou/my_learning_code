# 设置版本号、头文件名、依赖库名称。
set(YAML_CPP_VERSION yaml-cpp-0.9.0)
set(YAML_CPP_HEADER_NAME yaml-cpp/yaml.h)
set(YAML_CPP_LIBRARY_NAME libyaml-cpp.a)
if (CMAKE_BUILD_TYPE STREQUAL "Debug")
    set(YAML_CPP_LIBRARY_NAME libyaml-cppd.a)
endif ()

# 设置依赖安装目录。
set(YAML_CPP_DIRECTORY ${DEPENDENCIES_DIRECTORY}/yaml-cpp)
file(MAKE_DIRECTORY ${YAML_CPP_DIRECTORY})
set(YAML_CPP_BUILD_DIRECTORY ${YAML_CPP_DIRECTORY}/build)
set(YAML_CPP_SOURCE_DIRECTORY ${YAML_CPP_DIRECTORY}/source)
set(YAML_CPP_INSTALL_DIRECTORY ${YAML_CPP_DIRECTORY}/install)

# 设置依赖头文件路径。
find_path(
        YAML_CPP_INCLUDE_DIRECTORY
        NAMES ${YAML_CPP_HEADER_NAME}
        PATHS ${YAML_CPP_INSTALL_DIRECTORY}/include
        NO_DEFAULT_PATH
)

# 设置依赖库路径。
find_library(
        YAML_CPP_LIBRARY
        NAMES ${YAML_CPP_LIBRARY_NAME}
        PATHS ${YAML_CPP_INSTALL_DIRECTORY}/lib
        NO_DEFAULT_PATH
)

# 如果找到依赖库和头文件，则设置依赖库和头文件的路径。
if (YAML_CPP_LIBRARY AND YAML_CPP_INCLUDE_DIRECTORY)
    message(STATUS "found yaml-cpp include directory: ${YAML_CPP_INCLUDE_DIRECTORY}")
    message(STATUS "found yaml-cpp library: ${YAML_CPP_LIBRARY}")
else ()
    include(ExternalProject)
    ExternalProject_Add(
            yaml-cpp
            PREFIX ${YAML_CPP_DIRECTORY}
            URL https://github.com/jbeder/yaml-cpp/archive/refs/tags/${YAML_CPP_VERSION}.zip
            SOURCE_DIR ${YAML_CPP_SOURCE_DIRECTORY}
            BINARY_DIR ${YAML_CPP_BUILD_DIRECTORY}
            CONFIGURE_COMMAND ${CMAKE_COMMAND} --fresh -S ${YAML_CPP_SOURCE_DIRECTORY} -B ${YAML_CPP_BUILD_DIRECTORY}
                -DCMAKE_INSTALL_PREFIX=${YAML_CPP_INSTALL_DIRECTORY}
                -DCMAKE_BUILD_TYPE=${CMAKE_BUILD_TYPE}
                -DYAML_BUILD_SHARED_LIBS=OFF
                -DYAML_CPP_BUILD_TESTS=OFF
            BUILD_COMMAND ${CMAKE_COMMAND} --build ${YAML_CPP_BUILD_DIRECTORY} --config ${CMAKE_BUILD_TYPE} --parallel --clean-first
            INSTALL_COMMAND ${CMAKE_COMMAND} --build ${YAML_CPP_BUILD_DIRECTORY} --config ${CMAKE_BUILD_TYPE} --target install
    )
    set(YAML_CPP_INCLUDE_DIRECTORY ${YAML_CPP_INSTALL_DIRECTORY}/include)
    set(YAML_CPP_LIBRARY ${YAML_CPP_INSTALL_DIRECTORY}/lib/${YAML_CPP_LIBRARY_NAME})
endif ()

add_definitions(-DYAML_CPP_STATIC_DEFINE=1)
include_directories(${YAML_CPP_INCLUDE_DIRECTORY})
list(APPEND LIBRARIES ${YAML_CPP_LIBRARY})
