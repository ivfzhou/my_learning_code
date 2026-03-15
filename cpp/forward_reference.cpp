#include <format>
#include <iostream>

#include "forward_reference.hpp"

namespace gitee::com::ivfzhou::cpp {
    /*
    void log() { std::cout << std::endl; }

    template<typename T, typename... U>
    void log(T&& t, U&&... args) {
        std::cout << t << " ";
        log(std::forward<U>(args)...);
    }

    // 显式实例化定义。
    template void log<char const (&)[10]>(char const (&)[10]);
    */

    // 测试转发引用与模板递归效果。
    void forward_reference_and_recursive_template() {
        std::cout << "[pugixml] 开始 - 测试转发引用与模板递归效果" << std::endl;
        try {
            log("INFO", "hello logger", 1, 1.1, false);
            LOG("ERROR", "macro logger", 1, 1.1, false);
        } catch (std::exception& e) {
            std::cout << "发生错误：" << e.what() << std::endl;
        }
        std::cout << "[pugixml] 结束 - 测试转发引用与模板递归效果" << std::endl << std::endl;
    }

}
