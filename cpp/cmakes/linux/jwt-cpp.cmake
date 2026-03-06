set(JWT_CPP_VERSION v0.7.2)
set(JWT_CPP_DIRECTORY ${DEPENDENCIES_DIRECTORY}/jwt-cpp/${JWT_CPP_VERSION})

find_path(
    JWT_CPP_INCLUDE_DIRECTORY
    NAMES jwt
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
        CONFIGURE_COMMAND cd ${JWT_CPP_DIRECTORY}/src &&rm -rf jwt-cpp-build && mkdir -p jwt-cpp-build
        BUILD_COMMAND cd ${JWT_CPP_DIRECTORY}/src/jwt-cpp-build && ${CMAKE_COMMAND} -DCMAKE_INSTALL_PREFIX=${JWT_CPP_DIRECTORY} -DCMAKE_BUILD_TYPE=${CMAKE_BUILD_TYPE} -DBUILD_SHARED_LIBS=OFF -DOPENSSL_INCLUDE_DIRS=${OPENSSL_INCLUDE_DIRECTORY} -DOPENSSL_LIBRARY_DIRS=${OPENSSL_LIBRARY_DIRECTORY} -DOPENSSL_LIBRARIES=${CRYPTO_LIBRARY} ../jwt-cpp
        INSTALL_COMMAND cd ${JWT_CPP_DIRECTORY}/src/jwt-cpp-build && ${CMAKE_COMMAND} --build . --target install
    )
    set(JWT_CPP_INCLUDE_DIRECTORY ${JWT_CPP_DIRECTORY}/include)
endif()

include_directories(${JWT_CPP_INCLUDE_DIRECTORY})
