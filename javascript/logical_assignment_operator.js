let x = 0
x ||= 4 // 100。如果第一个值为 false，则分配第二个值。
console.log(x)

x &&= 5 // 5。如果第一个值为 true，则分配第二个值。
console.log(x)

x = null
x ??= 8 // 如果第一个值 undefined 或为 null，则分配第二个值。
console.log(x)
