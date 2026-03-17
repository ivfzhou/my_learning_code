# 设置版本号、头文件名、依赖库名称。
set(PUGIXML_VERSION v1.15)

# 设置依赖安装目录。
set(PUGIXML_DIRECTORY ${DEPENDENCIES_DIRECTORY}/pugixml)
file(MAKE_DIRECTORY ${PUGIXML_DIRECTORY})
set(PUGIXML_BUILD_DIRECTORY ${PUGIXML_DIRECTORY}/build)
set(PUGIXML_SOURCE_DIRECTORY ${PUGIXML_DIRECTORY}/source)
set(PUGIXML_INSTALL_DIRECTORY ${PUGIXML_DIRECTORY}/install)

# 设置依赖库路径。
find_library(
        PUGIXML_LIBRARY
        NAMES pugixml.lib
        PATHS ${PUGIXML_INSTALL_DIRECTORY}/lib
        NO_DEFAULT_PATH
)

# 设置依赖头文件路径。
find_path(
        PUGIXML_INCLUDE_DIRECTORY
        NAMES pugixml.hpp
        PATHS ${PUGIXML_INSTALL_DIRECTORY}/include
        NO_DEFAULT_PATH
)

# 如果找到依赖库和头文件，则设置依赖库和头文件的路径。
if (PUGIXML_LIBRARY AND PUGIXML_INCLUDE_DIRECTORY)
    message(STATUS "found pugixml library: ${PUGIXML_LIBRARY}")
    message(STATUS "found pugixml include directory: ${PUGIXML_INCLUDE_DIRECTORY}")
else ()
    include(ExternalProject)
    ExternalProject_Add(
            pugixml
            PREFIX ${PUGIXML_DIRECTORY}
            URL https://github.com/zeux/pugixml/archive/refs/tags/${PUGIXML_VERSION}.zip
            SOURCE_DIR ${PUGIXML_SOURCE_DIRECTORY}
            BINARY_DIR ${PUGIXML_BUILD_DIRECTORY}
            CONFIGURE_COMMAND ${CMAKE_COMMAND} --fresh -S ${PUGIXML_SOURCE_DIRECTORY} -B ${PUGIXML_BUILD_DIRECTORY}
                -DCMAKE_INSTALL_PREFIX=${PUGIXML_INSTALL_DIRECTORY}
                -DCMAKE_CONFIGURATION_TYPES=${CMAKE_BUILD_TYPE}
                -DCMAKE_MSVC_RUNTIME_LIBRARY=${CMAKE_MSVC_RUNTIME_LIBRARY}
                -DCMAKE_CXX_FLAGS=${COMPILER_FLAGS}
            BUILD_COMMAND ${CMAKE_COMMAND} --build ${PUGIXML_BUILD_DIRECTORY} --config ${CMAKE_BUILD_TYPE} --parallel --clean-first
            INSTALL_COMMAND ${CMAKE_COMMAND} --build ${PUGIXML_BUILD_DIRECTORY} --config ${CMAKE_BUILD_TYPE} --target install
    )
    set(PUGIXML_LIBRARY ${PUGIXML_INSTALL_DIRECTORY}/lib/pugixml.lib)
    set(PUGIXML_INCLUDE_DIRECTORY ${PUGIXML_INSTALL_DIRECTORY}/include)
endif ()

include_directories(${PUGIXML_INCLUDE_DIRECTORY})
list(APPEND LIBRARIES ${PUGIXML_LIBRARY})
