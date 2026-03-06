set(JWT_CPP_VERSION v0.7.1)
set(JWT_CPP_DIRECTORY ${DEPENDENCIES_DIRECTORY}/jwt-cpp/${JWT_CPP_VERSION})

find_path(
    JWT_CPP_INCLUDE_DIRECTORY
    NAMES jwt-cpp/jwt.h
    PATHS ${JWT_CPP_DIRECTORY}/include
    NO_DEFAULT_PATH
)

if(JWT_CPP_INCLUDE_DIRECTORY)
    message(STATUS "found jwt-cpp include directory: ${JWT_CPP_INCLUDE_DIRECTORY}")
else()
    ExternalProject_Add(
        jwt-cpp
        PREFIX ${JWT_CPP_DIRECTORY}
        URL https://github.com/Thalhammer/jwt-cpp/archive/refs/tags/${JWT_CPP_VERSION}.zip
        CONFIGURE_COMMAND cd ${JWT_CPP_DIRECTORY}/src && rd /s /q jwt-cpp-build && md jwt-cpp-build
        BUILD_COMMAND cd ${JWT_CPP_DIRECTORY}/src/jwt-cpp-build && ${CMAKE_COMMAND} -DCMAKE_INSTALL_PREFIX=${JWT_CPP_DIRECTORY} -DBUILD_SHARED_LIBS=OFF -DCMAKE_BUILD_TYPE=${CMAKE_BUILD_TYPE} -DOPENSSL_ROOT_DIR=${OPENSSL_DIRECTORY} -DJWT_BUILD_EXAMPLES=OFF ../jwt-cpp
        INSTALL_COMMAND cd ${JWT_CPP_DIRECTORY}/src/jwt-cpp-build && ${CMAKE_COMMAND} --build . --target install --config ${CMAKE_BUILD_TYPE}
    )
    set(JWT_CPP_INCLUDE_DIRECTORY ${JWT_CPP_DIRECTORY}/include)
endif()

include_directories(${JWT_CPP_INCLUDE_DIRECTORY})
