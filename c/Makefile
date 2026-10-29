CC := gcc

# 打印变量值。
$(info $$(CC) $(CC))
$(info $$(MAKE) $(MAKE))
$(info $$(SHELL) $(SHELL))
$(info $$(OS) $(OS))

# -g0：不生成调试信息。
# -g1：生成基本的调试信息，通常是最小的调试信息。
# -g3：生成更详细的调试信息，包括宏定义和其他信息。
# -Wall：启用所有的警告信息。
# -Winline：启用关于内联函数的警告。
# -O2：优化级别设置。
# -Dxxx=xxx：定义预处理宏。
# -Wno-unused：禁用所有未使用的警告（包括函数、参数等）。
# -I /usr/local/include/ -L /usr/local/lib/ -l bitwise
CFLAGS_COMMON := -std=c18 -finput-charset=UTF-8 -fexec-charset=UTF-8 --all-warnings -pedantic -Winline -Wall \
                 -Wno-unused-variable -Wno-unused-function -Wno-unused-local-typedefs
CFLAGS_DEBUG := $(CFLAGS_COMMON) -O0 -g3
CFLAGS_RELEASE := $(CFLAGS_COMMON) -O2 -g0 -DNDEBUG
TARGET := c
SOURCES := $(shell ls | grep -E '(\.c$$)|(\.C$$)')
SOURCES += bitwise/bitwise.c
DEBUG_OBJECTS := $(addprefix debug/, $(SOURCES:.c=.o))
RELEASE_OBJECTS := $(addprefix release/, $(SOURCES:.c=.o))
CLEAN_COMMAND := rm -rf debug/ release/ $(TARGET)-debug $(TARGET)-debug.exe $(TARGET)-release $(TARGET)-release.exe
$(info $$(CFLAGS_DEBUG) $(CFLAGS_DEBUG))
$(info $$(CFLAGS_RELEASE) $(CFLAGS_RELEASE))
$(info $$(SOURCES) $(SOURCES))
$(info $$(DEBUG_OBJECTS) $(DEBUG_OBJECTS))
$(info $$(RELEASE_OBJECTS) $(RELEASE_OBJECTS))
$(info $$(CLEAN_COMMAND) $(CLEAN_COMMAND))


.PHONY: debug release all clean format-code


# 编译每个源代码（debug 版本）。
debug/%.o: %.c
	@mkdir -p $(dir $@)
	@$(CC) $(CFLAGS_DEBUG) -c -o $@ $<


# 编译每个源代码（release 版本）。
release/%.o: %.c
	@mkdir -p $(dir $@)
	@$(CC) $(CFLAGS_RELEASE) -c -o $@ $<


# 链接代码（debug 版本）。
$(TARGET)-debug: $(DEBUG_OBJECTS)
	@$(CC) $(CFLAGS_DEBUG) -o $@ $^
	@echo 'build debug done'


# 链接代码（release 版本）。
$(TARGET)-release: $(RELEASE_OBJECTS)
	@$(CC) $(CFLAGS_RELEASE) -o $@ $^
	@echo 'build release done'


clean:
	@$(CLEAN_COMMAND)
	@echo 'clean done'


# 格式化所有源文件（-i 原地修改）。依赖 clang-format 工具。
format-code:
	@clang-format -i $(SOURCES)
	@echo 'format-code done'


all: $(TARGET)-release
debug: $(TARGET)-debug
release: $(TARGET)-release
