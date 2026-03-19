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
    cmake.exe -DCMAKE_BUILD_TYPE=Debug -G "Visual Studio 17 2022" --fresh -S . -B .\build-debug-static
    cmake.exe --build .\build-debug-static --config Debug --clean-first --parallel --target cpp

    cmake.exe -DCMAKE_BUILD_TYPE=Debug -DCOMPILE_DYNAMIC_MODE=ON -G "Visual Studio 17 2022" --fresh -S . -B .\build-debug-dynamic
    cmake.exe --build .\build-debug-dynamic --config Debug --clean-first --parallel --target cpp
    ```

- Release 模式：
    ```cmd
    cmake.exe -DCMAKE_BUILD_TYPE=Release -G "Visual Studio 17 2022" --fresh -S . -B .\build-release-static
    cmake.exe --build .\build-release-static --config Release --clean-first --parallel --target cpp

    cmake.exe -DCMAKE_BUILD_TYPE=Release -DCOMPILE_DYNAMIC_MODE=ON -G "Visual Studio 17 2022" --fresh -S . -B .\build-release-dynamic
    cmake.exe --build .\build-release-dynamic --config Release --clean-first --parallel --target cpp
    ```

## 1.2 Linux

|  工具   |   版本   |
|:-----:|:------:|
|  C++  |   20   |
| CMake | 4.2.3  |
|  g++  | 14.2.0 |

- Debug 模式：
    ```shell
    cmake -DCMAKE_BUILD_TYPE=Debug --fresh -G "Unix Makefiles" -S . -B ./build-debug-static
    cmake --build ./build-debug-static --config Debug --clean-first --parallel --target cpp

    cmake -DCMAKE_BUILD_TYPE=Debug -DCOMPILE_DYNAMIC_MODE=ON --fresh -G "Unix Makefiles" -S . -B ./build-debug-dynamic
    cmake --build ./build-debug-dynamic --config Debug --clean-first --parallel --target cpp
    ```
- Release 模式：
    ```shell
    cmake -DCMAKE_BUILD_TYPE=Release --fresh -G "Unix Makefiles" -S . -B ./build-release-static
    cmake --build ./build-release-static --config Release --clean-first --parallel --target cpp

    cmake -DCMAKE_BUILD_TYPE=Release -DCOMPILE_DYNAMIC_MODE=ON --fresh -G "Unix Makefiles" -S . -B ./build-release-dynamic
    cmake --build ./build-release-dynamic --config Release --clean-first --parallel --target cpp
    ```
