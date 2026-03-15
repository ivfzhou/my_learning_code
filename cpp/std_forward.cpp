#include <iostream>

#include "std_forward.hpp"

namespace gitee::com::ivfzhou::cpp {
    template<typename T>
    static void receive(T& t) {
        std::cout << "left_reference_receive: " << t << std::endl;
    }

    template<typename T>
    static void receive(T&& t) {
        std::cout << "right_reference_receive: " << t << std::endl;
    }

    template<typename T>
    static void forward(T&& t) {
        receive(std::forward<T>(t));
    }

    // 测试转发表现。
    void test_std_forward() {
        std::cout << "开始 - 测试转发表现" << std::endl;
        try {
            int value = 1;
            int& leftValue = value;
            const int& constLeftValue = value;
            forward(leftValue);
            forward(constLeftValue);
            forward(1);
        } catch (std::exception& e) {
            std::cout << "发生错误: " << e.what() << std::endl;
        }

        std::cout << "结束 - 测试转发表现" << std::endl << std::endl;
    }

    static void modify_value(int&& value) { value = value + 1; }

    // 测试 move 后的值修改表现。
    void test_std_move() {
        std::cout << "开始 - 测试 move 后的值修改表现" << std::endl;
        try {
            int value = 1;
            int& leftValue = value;
            modify_value(std::move(leftValue));
            modify_value(std::move(value));
            std::cout << leftValue << std::endl;
            std::cout << value << std::endl;
        } catch (std::exception& e) {
            std::cout << "发生错误: " << e.what() << std::endl;
        }

        std::cout << "结束 - 测试 move 后的值修改表现" << std::endl << std::endl;
    }

    template<typename T>
    static std::remove_reference<T>::type&& move(T&& value) {
        return static_cast<std::remove_reference<T>::type&&>(value);
    }

    // 测试自定义实现的 move 效果。
    void test_diy_move() {
        std::cout << "开始 - 测试自定义实现的 move 效果" << std::endl;
        try {
            int value = 1;
            int& leftValue = value;
            modify_value(move(leftValue));
            modify_value(move(value));
            std::cout << leftValue << std::endl;
            std::cout << value << std::endl;
        } catch (std::exception& e) {
            std::cout << "发生错误: " << e.what() << std::endl;
        }

        std::cout << "结束 - 测试自定义实现的 move 效果" << std::endl << std::endl;
    }
}
