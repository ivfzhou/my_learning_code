set(JWT_CPP_VERSION v0.7.2)

set(JWT_CPP_DIRECTORY ${DEPENDENCIES_DIRECTORY}/jwt-cpp)
file(MAKE_DIRECTORY ${JWT_CPP_DIRECTORY})
set(JWT_CPP_BUILD_DIRECTORY ${JWT_CPP_DIRECTORY}/build)
set(JWT_CPP_SOURCE_DIRECTORY ${JWT_CPP_DIRECTORY}/source)
set(JWT_CPP_INSTALL_DIRECTORY ${JWT_CPP_DIRECTORY}/install)

find_path(
        JWT_CPP_INCLUDE_DIRECTORY
        NAMES jwt-cpp/jwt.h
        PATHS ${JWT_CPP_INSTALL_DIRECTORY}/include
        NO_DEFAULT_PATH
)

if (JWT_CPP_INCLUDE_DIRECTORY)
    message(STATUS "found jwt-cpp include directory: ${JWT_CPP_INCLUDE_DIRECTORY}")
else ()
    include(ExternalProject)
    ExternalProject_Add(
            jwt-cpp
            PREFIX ${JWT_CPP_DIRECTORY}
            URL https://github.com/Thalhammer/jwt-cpp/archive/refs/tags/${JWT_CPP_VERSION}.zip
            SOURCE_DIR ${JWT_CPP_SOURCE_DIRECTORY}
            BINARY_DIR ${JWT_CPP_BUILD_DIRECTORY}
            CONFIGURE_COMMAND ${CMAKE_COMMAND} --fresh -S ${JWT_CPP_SOURCE_DIRECTORY} -B ${JWT_CPP_BUILD_DIRECTORY}
                -DCMAKE_INSTALL_PREFIX=${JWT_CPP_INSTALL_DIRECTORY}
                -DCMAKE_BUILD_TYPE=${CMAKE_BUILD_TYPE}
                -DCMAKE_CXX_FLAGS_RELEASE=${COMPILER_FLAGS}
                -DBUILD_SHARED_LIBS=OFF
                -DOPENSSL_LIBRARIES=${CRYPTO_LIBRARY}
                -DOPENSSL_ROOT_DIR=${OPENSSL_INSTALL_DIRECTORY}
                -DJWT_BUILD_EXAMPLES=OFF
            BUILD_COMMAND ${CMAKE_COMMAND} --build ${JWT_CPP_BUILD_DIRECTORY} --parallel --config ${CMAKE_BUILD_TYPE} --clean-first
            INSTALL_COMMAND ${CMAKE_COMMAND} --build ${JWT_CPP_BUILD_DIRECTORY} --config ${CMAKE_BUILD_TYPE} --target install
    )
    set(JWT_CPP_INCLUDE_DIRECTORY ${JWT_CPP_INSTALL_DIRECTORY}/include)
endif ()

include_directories(${JWT_CPP_INCLUDE_DIRECTORY})
