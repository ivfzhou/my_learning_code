import { ref } from 'vue'

export default {
    props: ['title'],
    setup(props, ctx) {
        ctx.emits('fn')
    },
    template: `#app`
}
