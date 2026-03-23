#ifndef CPP_FORWARD_REFERENCE_HPP
#define CPP_FORWARD_REFERENCE_HPP

#include <chrono>
#include <format>
#include <iostream>
#include <ostream>
#include <sstream>
#include <string>
#include <utility>

namespace gitee::com::ivfzhou::cpp {

    /*
    template<typename... T>
    void log(T&&... args);
    // 显式实例化声明。
    extern template void log<char const (&)[10]>(char const (&)[10]);
    */

    const static auto& one_line_string = []<typename T>(T&& arg) {
        std::ostringstream temp;
        temp << std::forward<T>(arg);
        std::string escaped;
        for (const char c : temp.str()) {
            if (c == '\n')
                escaped += "\\n";
            else if (c == '\r')
                escaped += "\\r";
            else
                escaped += c;
        }
        return escaped;
    };
    static std::string now() {
        return std::format("{:%Y-%m-%d %H:%M:%S}",
                           std::chrono::current_zone()->to_local(
                               std::chrono::floor<std::chrono::milliseconds>(std::chrono::system_clock::now())));
    }
    static void print() { std::cout << std::endl; }
    template<typename T, typename... U>
    void print(T&& t, U&&... args) {
        std::cout << one_line_string(std::forward<T>(t)) << " ";
        print(std::forward<U>(args)...);
    }
    template<typename... T>
    void log(T&&... args) {
        std::cout << now() << " ";
        print(std::forward<T>(args)...);
    }

    // 测试转发引用与模板递归效果。
    void forward_reference();

#define LOG(...)                                                                                                       \
    {                                                                                                                  \
        auto fileName = std::string(__FILE__);                                                                         \
        auto index = fileName.find_first_of("cpp");                                                                    \
        if (index != std::string::npos) fileName = fileName.substr(index + 2);                                         \
        fileName.append(":").append(std::format("{}", __LINE__));                                                      \
        log(fileName, __VA_ARGS__);                                                                                    \
    }

}

#endif
