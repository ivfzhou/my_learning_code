#include <exception>
#include <iostream>
#include <ostream>
#include <utility>

#include "assignment_rule.hpp"
#include "forward_reference.hpp"

namespace gitee::com::ivfzhou::cpp {
    std::ostream& operator<<(std::ostream& out, const type& t) {
        out << t.x << std::endl;
        return out;
    }

    type::type(const int& x) noexcept : x(x) { std::cout << "普通构造：x = " << this->x << std::endl; }

    type::type(const type& t) noexcept {
        this->x = t.x;
        std::cout << "复制构造：x = " << this->x << std::endl;
    }

    type::type(type&& t) noexcept {
        this->x = t.x;
        t.x = 0;
        std::cout << "移动构造：x = " << this->x << std::endl;
    }

    type& type::operator=(const type& t) noexcept {
        this->x = t.x;
        std::cout << "赋值运算符：x = " << this->x << std::endl;
        return *this;
    }

    static type return_left_value() {
        type t0(1);
        return t0;
    }

    static type return_right_value() { return type(1); }

    // 内存泄露。
    static type&& return_right_reference() { return type(1); }
    static type& return_left_reference() {
        type t0(1);
        return t0;
    }

    // 测试各种赋值效果表现。
    void assignment_rule() {
        std::cout << "开始 - 测试各种赋值效果表现" << std::endl;
        try {
            {
                std::cout << "只触发普通构造" << std::endl;
                type t0(1);
                type t1 = type(1);
            }

            {
                std::cout << "以左引用接收" << std::endl;
                type t0(1);
                type& t1 = t0;
                const type& t2 = t0;
                const type& t3 = type(2);

                const type& t4 = t3;
                const type& t5 = t2;
                type& t6 = t1;
            }

            {
                std::cout << "以右引用接收" << std::endl;
                type&& t1 = type(1);
                const type&& t2 = type(1);
            }

            {
                std::cout << "以变量接收" << std::endl;
                type t0(1);
                type t1(2);
                t0 = type(3);
                t0 = t1;

                type& t2 = t1;
                t0 = t2;

                t0 = std::move(t1);
            }

            {
                std::cout << "触发复制构造" << std::endl;
                type t0(1);
                type t1(t0);
                type t2 = t0;
            }

            {
                std::cout << "接收函数返回值" << std::endl;
                type t0 = return_left_value();
                type t1 = return_right_value();

                type t2(0);
                t2 = return_left_value();
                t2 = return_right_value();

                const type& t3 = return_left_value();
                const type& t4 = return_right_value();

                type&& t5 = return_left_value();
                type&& t6 = return_right_value();
            }

            {
                std::cout << "接收函数返回的引用值" << std::endl;
                type&& t0 = return_right_reference();
                type t1 = return_right_reference();
                const type& t2 = return_right_reference();

                type& t3 = return_left_reference();
                type t4 = return_left_reference();
                const type& t5 = return_left_reference();
            }
        } catch (const std::exception& e) {
            LOG("发生错误：", e.what());
        }
        std::cout << "结束 - 测试各种赋值效果表现" << std::endl << std::endl;
    }
}
