#ifndef CPP_ASSIGNMENT_RULE_HPP
#define CPP_ASSIGNMENT_RULE_HPP

#include <ostream>

namespace gitee::com::ivfzhou::cpp {
    class type {
        int x = 0;

        friend std::ostream& operator<<(std::ostream& out, const type& t);

        // 隐式生成的条件。
        // 1. 什么都没声明，默认都隐式生成。
        // 2. 声明任意构造，默认构造不再隐式生成。
        // 3. 声明复制构造，默认构造、移动构造、移动赋值不再隐式生成。
        // 4. 声明复制赋值，移动构造、移动赋值不再隐式生成。
        // 5. 声明移动构造，默认构造、复制构造、复制赋值、移动赋值不再隐式生成。
        // 6. 声明移动赋值，复制构造、复制赋值、移动构造不再隐式生成。
        // 7. 声明构造，复制赋值、移动构造不再隐式生成。

      public:
        // 默认构造。
        // 默认行为：对每个成员执行默认初始化（基本类型不初始化，类类型调用其默认构造）。
        type() = default;

        // 析构。
        // 默认行为：对每个成员执行析构（逆序）。
        ~type() = default;

        // 构造。
        explicit type(const int& x);

        // 复制构造。
        // 默认行为：对每个成员执行复制构造。
        type(const type& t);

        // 移动构造。
        // 默认行为：对每个成员执行 std::move 后移动构造。
        type(type&&) noexcept;

        // 复制赋值。
        // 默认行为：对每个成员执行复制赋值。
        type& operator=(const type& t);

        // 移动赋值。
        // 默认行为：对每个成员执行 std::move 后移动赋值。
        type& operator=(type&&) noexcept;
    };

    // 测试各种赋值效果表现。
    void assignment_rule();
}

#endif
