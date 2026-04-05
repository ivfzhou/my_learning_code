// 重新声明变量
var x = 10
// 此处 x 为 10
{
    var x = 6
    // 此处 x 为 6
    console.log(x)
}
// 此处 x 为 6
console.log(x)

var y = 10
// 此处 y 为 10
{
    let y = 6
    // 此处 y 为 6
    console.log(y)
}
// 此处 y 为 10
console.log(y)
