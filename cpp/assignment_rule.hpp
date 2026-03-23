#ifndef CPP_ASSIGNMENT_RULE_HPP
#define CPP_ASSIGNMENT_RULE_HPP

#include <ostream>

namespace gitee::com::ivfzhou::cpp {
    class type {
        int x = 0;

        friend std::ostream& operator<<(std::ostream& out, const type& t);

      public:
        // 普通构造。
        explicit type(const int& x) noexcept;

        // 复制构造。
        type(const type& t) noexcept;

        // 移动构造。
        type(type&&) noexcept;

        // 赋值运算符。
        type& operator=(const type& t) noexcept;
    };

    // 测试各种赋值效果表现。
    void assignment_rule();
}

#endif
