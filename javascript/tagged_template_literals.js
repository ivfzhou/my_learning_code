function safeHTML(strings, ...values) {
    console.log(strings.raw.join('{}'))
    return strings.reduce((result, str, i) => {
        const value = values[i]
            ? String(values[i])
                .replace(/&/g, '&amp;')
                .replace(/</g, '&lt;')
                .replace(/>/g, '&gt;')
                .replace(/"/g, '&quot;')
            : ''
        return result + str + value
    }, '')
}

const userInput = '<script>alert("xss")</script>';
const html = safeHTML`<div>${userInput}</div>`
console.log(html)
