#include <chrono>
#include <exception>
#include <format>
#include <iostream>
#include <ostream>
#include <sstream>
#include <utility>

#include "forward_reference.hpp"

namespace gitee::com::ivfzhou::cpp {
    /*
    void print() { std::cout << std::endl; }

    std::string now() {
        return std::format("{:%Y-%m-%d %H:%M:%S}",
                           std::chrono::current_zone()->to_local(
                               std::chrono::floor<std::chrono::milliseconds>(std::chrono::system_clock::now())));
    }

    const auto& one_line_string = []<typename T>(T&& arg) {
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

    template<typename T, typename... U>
    void print(T&& t, U&&... args) {
        std::cout << t << " ";
        print(std::forward<U>(args)...);
    }

    template<typename... U>
    void log(U&&... args) {
        std::cout << now() << " ";
        print(std::forward<U>(args)...);
    }

    // 显式实例化定义。
    template void log<char const (&)[10]>(char const (&)[10]);
    */

    // 测试转发引用与模板递归效果。
    void forward_reference() {
        std::cout << "开始 - 测试转发引用与模板递归效果" << std::endl;
        try {
            log("INFO", "hello logger", 1, 1.1, false);
            LOG("INFO", "macro logger", 1, 1.1, false);
        } catch (std::exception& e) {
            LOG("发生错误：", e.what());
        }
        std::cout << "结束 - 测试转发引用与模板递归效果" << std::endl << std::endl;
    }

}
