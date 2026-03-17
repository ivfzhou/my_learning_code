# 一、编译

克隆源码：

```shell
git clone https://gitee.com/ivfzhou/my_learning_code.git
cd my_learning_code/cpp
```

## 1.1 Windows

|  工具   |              版本              |
|:-----:|:----------------------------:|
|  C++  |              20              |
| CMake |            3.31.6            |
| MSVC  | Microsoft Visual Studio 2022 |

- Debug 模式：
    ```cmd
    cmake.exe -DCMAKE_BUILD_TYPE=Debug -G "Visual Studio 17 2022" --fresh -S . -B .\build-debug
    cmake.exe --build .\build-debug --config Debug --clean-first --parallel --target argparse bzip2 cpp-httplib pugixml xz yaml-cpp zlib zstd openssl jwt-cpp libzip
    ```

- Release 模式：
    ```cmd
    cmake.exe -DCMAKE_BUILD_TYPE=Release -G "Visual Studio 17 2022" --fresh -S . -B .\build-release
    cmake.exe --build .\build-release --config Release --clean-first --parallel --target argparse bzip2 cpp-httplib pugixml xz yaml-cpp zlib zstd openssl jwt-cpp libzip
    ```

## 1.2 Linux

|  工具   |   版本   |
|:-----:|:------:|
|  C++  |   20   |
| CMake | 4.2.3  |
|  g++  | 14.2.0 |

- Debug 模式：
    ```shell
    cmake -DCMAKE_BUILD_TYPE=Debug --fresh -G "Unix Makefiles" -S . -B ./build-debug
    cmake --build ./build-debug --config Debug --clean-first --parallel --target argparse bzip2 cpp-httplib pugixml xz yaml-cpp zlib zstd openssl jwt-cpp libzip
    ```
- Release 模式：
    ```shell
    cmake -DCMAKE_BUILD_TYPE=Release --fresh -G "Unix Makefiles" -S . -B ./build-release
    cmake --build ./build-release --config Release --clean-first --parallel --target argparse bzip2 cpp-httplib pugixml xz yaml-cpp zlib zstd openssl jwt-cpp libzip
    ```
