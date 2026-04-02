import 'dart:math' as math;

void main() {
  test28();
}

void test1(String? s) {
  print(s ?? 'print out if s is null');
}

String test2(String? s) {
  s ??= 'assign if s is null';
  return s;
}

void test3(String s) {
  print('interpolation s is ${s}'); // indicate calling s.toString()
  print('interpolation s.length is ${s.length}');
  print('3 + 2 = ${3 + 2}');
  print('call uppercase ${"ivfzhou".toUpperCase()}');
}

int? test4(String? s) {
  return s?.length; // call length if s is not null.
}

bool test5(String? s) {
  return s!.isEmpty; // throw an exception if s is null.
}

class AClass1 {
  late final String aField; // late field must be assigned before it is used.
}

void test6() {
  var c = AClass1();
  c.aField = 'late field value'; // commenting line will throw an exception.
  print(c.aField);
}

void test7() {
  final map = {1: 1};
  final set = {1, 2};
  final list = [1, 1];
  final mapUninitial = <int, int>{};
  final setUninitial = <int>{};
  final listUninitial = <int>[];
}

void test8() {
  var fn = (s) => s.toString(); // arrow syntax
}

void test9() {
  String? s = 'hello cascade';
  //s
  //  ?..toString()
  //  ..length
  //  ..isEmpty; // can nullable cascade.
  s
    ..isEmpty
    ..length; // can not nullable cascade.
}

void test10(int a,
    [int? b,
    int? c = 0,
    List<int> d = const [
      1,
      2
    ]]) {} // optional positional parameter. call test10(1)

void test11({int a = 0}) {} // named parameters

void test12({required int a}) {} // named parameters

void test13({int? a}) {} // named parameters

void test14() {
  try {
    throw 'can anything';
  } on NullThrownError catch (e, st) {
    print(e);
    print(st);
  } on Exception catch (e) {
    print(e);
  } catch (e) {
    // catch all exception
    print(e);
  } finally {
    // do finally
    print('final');
  }
}

class AClass2 {
  int red;
  int blue;

  AClass2(this.red, this.blue)
      : assert(red > 0); // constructor & initializer list

  AClass2.init()
      : this.red = 255,
        blue = 0; // named constructor, executes before the superclass is called

  factory AClass2.build() {
    // factory constructor.
    return AClass2.init();
  }

  AClass2.ref() : this.init(); // redirecting constructor

  set green(int red) => this.red = red; // setter property

  int get green => red; // getter property
}

class AClass3 {
  final int x;

  const AClass3(this.x); // const constructor

  AClass3.init() : this.x = 0 {
    print('class3');
  }
}

void test15() {
  Iterable<int> list = [1, 2, 3];
  for (final item in list) {
    print(item);
  }
}

Future<int> test16() {
  return Future.delayed(Duration(seconds: 1), () => 1);
}

Future<int> test17() async {
  // work it as asynchronous.
  var res = await test16(); // must within async function.
  return res;
}

enum AEnum {
  Enum1(1);

  final int a;

  const AEnum(this.a);
}

abstract class AClass4 {
  int? a;

  int doPost();
}

class AClass5 extends AClass4 with AClass6 {
  @override
  int doPost() {
    throw UnimplementedError();
  }
}

mixin AClass6 {
  int? a;
  int? _b; // private field.
}

class AClass7 implements AClass4 {
  // providing the APIs required by the interfaces
  @override
  int? a;

  @override
  int doPost() {
    throw UnimplementedError();
  }

  @override // results in a NoSuchMethodError
  void noSuchMethod(Invocation invocation) {
    print(invocation);
  }
}

void test18() {
  var s = """
  multi line string
  """;
  var s2 = '''
  multi line string
  ''';
  var s3 = '' + '';
  var s4 = '' '';
  var s5 = r'';
}

void test19() {
  Object? obj; // any value is allowed.
  Enum e;
  void vi;
  Never ne;
  dynamic dy;
  Stream st;
  num um;
  Function f;
  assert(true);
  late String s = test16.toString(); // Lazily initialized.
}

void test20() {
  var m = Map();
  var s = Set();
  var sy = Symbol('');
  var r = Runes('');
}

void test21() {
  var list = [];
  List<int>? l;
  var list1 = [...list];
  var list2 = [...?l]; // null-aware spread operator
  var list3 = [
    if (true) 1,
    for (final i in list) '$i'
  ]; // collection if & collection for.
  var list4 = const []; // constant
}

void test22() {
  var s = '';
  var sy = #s;
  print(sy);
}

void test23() {
  var i = 9;
  var iCast = i as int;
}

void test24() {
  switch (1) {
    case 1:
      print('ok');
      continue label;
    label:
    case 2:
      print('fall through');
  }
}

class AClass8 extends AClass3 {
  // AClass8(super.x);

  AClass8() : super.init() {
    print('Class8');
  }

  AClass8 operator +(AClass8 a) => AClass8();
}

void test25() {
  AClass8();
}

void test26() {
  var a = AClass3(1);
  var b = AClass3(1);
  print(a.hashCode == b.hashCode);
}

void test27() {
  var c = AClass2.init();
  c.green = 9;
  print(c.green);
}

enum AEnum2 implements Comparable<AEnum2> {
  E1(1);

  final int a; // must be final

  const AEnum2(this.a); // must be const

  @override
  int compareTo(AEnum2 other) => this.a - other.a;
}

void test28() {
  print(AEnum2.values);
  print(AEnum2.E1.index);
  print(AEnum2.E1.name);
}

mixin AClass9 on Object /*required superclass*/ {}
