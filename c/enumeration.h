#ifndef COM_GITEE_IVFZHOU_C_ENUMERATION_H
#define COM_GITEE_IVFZHOU_C_ENUMERATION_H

// 枚举的声明
enum week {
  MON, /* 值为0 */
  TUES,
  WED,
  THURS,
  FRI,
  SAT,
  SUN
};

enum level {
  LOW = 100,
  MEDIUM = 500,
  HIGH = 2000,
  HIGHEST /* 值为2001 */
};

enum week test_enumeration(void);

#endif
