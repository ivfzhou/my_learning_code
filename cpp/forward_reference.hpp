#ifndef CPP_FORWARD_REFERENCE_HPP
#define CPP_FORWARD_REFERENCE_HPP

namespace gitee::com::ivfzhou::cpp {

    /*
    void log();
    template<typename T, typename... U>
    void log(T&& t, U&&... args);
    // 显式实例化声明。
    extern template void log<char const (&)[10]>(char const (&)[10]);
    */


    static void log() { std::cout << std::endl; }
    template<typename T, typename... U>
    void log(T&& t, U&&... args) {
        std::cout << t << " ";
        log(std::forward<U>(args)...);
    }

    // 测试转发引用与模板递归效果。
    void forward_reference_and_recursive_template();

#define LOG(level, ...) log(level, __FILE__, ":", __LINE__, __VA_ARGS__)

}

#endif
